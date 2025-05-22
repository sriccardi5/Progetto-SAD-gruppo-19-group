/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.factory.ShapeCreator;
import it.unisa.progettosadgruppo19.factory.ConcreteShapeCreator;
import it.unisa.progettosadgruppo19.decorator.*;
import it.unisa.progettosadgruppo19.model.shapes.*;
import it.unisa.progettosadgruppo19.adapter.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller principale che gestisce:
 * - selezione dello strumento (Linea, Rettangolo, Ellisse)
 * - creazione, spostamento e ridimensionamento delle shape
 * - salvataggio e caricamento del disegno
 * - eliminazione di shape
 */
public class Controller {

    @FXML
    private Pane drawingPane;
    @FXML
    private Button lineButton;
    @FXML
    private Button rectButton;
    @FXML
    private Button ellipseButton;
    @FXML
    private ColorPicker strokePicker;
    @FXML
    private ColorPicker fillPicker;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button deleteButton;

    private final List<AbstractShape> currentShapes = new ArrayList<>();

    private Shape tempShape;
    private String selectedShape = "Linea"; // Valore di default

    private Shape selectedShapeInstance = null;
    
    // tipo di operazione corrente
    private enum Tool { NONE, LINE, RECTANGLE, ELLIPSE }
    private Tool currentTool = Tool.NONE;
    
    private double moveAnchorX, moveAnchorY;
    // linee
    private double origX1, origY1, origX2, origY2;
    // rettangoli
    private double origX, origY;
    // ellissi
    private double origCenterX, origCenterY;
    
    private static final double HANDLE_RADIUS = 6.0; //arbitrarie
    private static final double ELLIPSE_BORDER_TOLERANCE = 6.0;

    
    private enum ResizeMode {
        NONE,
        LINE_START, LINE_END,
        ELLIPSE_BORDER,
        RECT_LEFT, RECT_RIGHT, RECT_TOP, RECT_BOTTOM,
        RECT_TOP_LEFT, RECT_TOP_RIGHT, RECT_BOTTOM_LEFT, RECT_BOTTOM_RIGHT
    }
    private ResizeMode currentResizeMode = ResizeMode.NONE;
    private double resizeAnchorX, resizeAnchorY;


    /**
     * Imposta i listener sui controlli e inizializza l'ambiente di disegno.
     */
    @FXML
    public void initialize() {
        
        saveButton.setOnAction(evt -> onSave());
        loadButton.setOnAction(evt -> onLoad());
        deleteButton.setOnAction(e -> onDelete());
        
        
        
        lineButton.setOnAction(e -> {
           currentTool = Tool.LINE;
           selectedShape = "Linea";
           selectedShapeInstance = null;           
        });
        rectButton.setOnAction(e -> {
            currentTool = Tool.RECTANGLE;
            selectedShape = "Rettangolo";
            selectedShapeInstance = null;
        });
        ellipseButton.setOnAction(e -> {
            currentTool = Tool.ELLIPSE;
            selectedShape = "Ellisse";
            selectedShapeInstance = null;
        });

        
        strokePicker.setValue(javafx.scene.paint.Color.BLACK);
        fillPicker.setValue(javafx.scene.paint.Color.TRANSPARENT);

        drawingPane.setOnMousePressed(this::onPressed);
        drawingPane.setOnMouseDragged(this::onDragged);
        drawingPane.setOnMouseReleased(this::onReleased);
        drawingPane.setOnMouseClicked(this::onMouseClick);

        fillPicker.setOnAction(e -> {
            if (selectedShapeInstance != null) {
                // Crea un nuovo decorator con il nuovo colore
                Shape decorated = new FillDecorator(selectedShapeInstance, fillPicker.getValue());

                // Sostituisci il nodo nella UI
                int index = drawingPane.getChildren().indexOf(selectedShapeInstance.getNode());
                drawingPane.getChildren().set(index, decorated.getNode());

                // Aggiorna userData e riferimento
                decorated.getNode().setUserData(decorated);
                selectedShapeInstance = decorated;
            }
        });
    }
    
   
    /**
     * Gestisce la pressione del mouse sul canvas per creazione, selezione
     * o inizio di drag/resize.
     * @param e evento MouseEvent
     */
    
    private boolean isNearLine(double px, double py, double x1, double y1, double x2, double y2, double tolerance) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1) <= tolerance;
        }

        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double projX = x1 + t * dx;
        double projY = y1 + t * dy;

        return Math.hypot(px - projX, py - projY) <= tolerance;
    }



    private void onPressed(MouseEvent e) {    
     
     double x = e.getX(), y = e.getY();

    
     // PRIMA di tutto: cerca una linea vicina e selezionala
    for (AbstractShape shape : currentShapes) {
        Node node = shape.getNode();
        if (node instanceof Line line) {
            double sx = line.getStartX(), sy = line.getStartY();
            double ex = line.getEndX(), ey = line.getEndY();
            if (isNearLine(x, y, sx, sy, ex, ey, HANDLE_RADIUS)) {
                selectedShapeInstance = shape;
                moveAnchorX = x;
                moveAnchorY = y;
                origX1 = sx; origY1 = sy;
                origX2 = ex; origY2 = ey;
                return;
            }
        }
    }

     
    if (selectedShapeInstance != null) {
        Node node = selectedShapeInstance.getNode();

        
        if (node instanceof javafx.scene.shape.Line line) {
            double sx = line.getStartX(), sy = line.getStartY();
            double ex = line.getEndX(), ey = line.getEndY();
            
            if (Math.hypot(x - sx, y - sy) < HANDLE_RADIUS) {
                currentResizeMode = ResizeMode.LINE_START;
                resizeAnchorX = ex;  resizeAnchorY = ey;
                return;
            }
            if (Math.hypot(x - ex, y - ey) < HANDLE_RADIUS) {
                currentResizeMode = ResizeMode.LINE_END;
                resizeAnchorX = sx;  resizeAnchorY = sy;
                return;
            }
            
            // NUOVA PARTE: selezione della linea se si clicca vicino al segmento
            if (isNearLine(x, y, sx, sy, ex, ey, HANDLE_RADIUS)) {
                // attiva drag della linea intera (non resize)
                moveAnchorX = x;
                moveAnchorY = y;
                origX1 = sx; origY1 = sy;
                origX2 = ex; origY2 = ey;
                return;
            }
            
        }
        
        

        
        else if (node instanceof javafx.scene.shape.Rectangle rect) {
            double rx = rect.getX(), ry = rect.getY();
            double w = rect.getWidth(), h = rect.getHeight();
            boolean left   = Math.abs(x - rx) < HANDLE_RADIUS;
            boolean right  = Math.abs(x - (rx + w)) < HANDLE_RADIUS;
            boolean top    = Math.abs(y - ry) < HANDLE_RADIUS;
            boolean bottom = Math.abs(y - (ry + h)) < HANDLE_RADIUS;

            // angoli
            if (left && top)    currentResizeMode = ResizeMode.RECT_TOP_LEFT;
            else if (right && top)   currentResizeMode = ResizeMode.RECT_TOP_RIGHT;
            else if (left && bottom) currentResizeMode = ResizeMode.RECT_BOTTOM_LEFT;
            else if (right && bottom)currentResizeMode = ResizeMode.RECT_BOTTOM_RIGHT;
            // lati
            else if (left)   currentResizeMode = ResizeMode.RECT_LEFT;
            else if (right)  currentResizeMode = ResizeMode.RECT_RIGHT;
            else if (top)    currentResizeMode = ResizeMode.RECT_TOP;
            else if (bottom) currentResizeMode = ResizeMode.RECT_BOTTOM;

            if (currentResizeMode != ResizeMode.NONE) {
                // definisco il punto opposto di ancoraggio
                switch (currentResizeMode) {
                    case RECT_TOP_LEFT:
                        resizeAnchorX = rx + w;  resizeAnchorY = ry + h; break;
                    case RECT_TOP_RIGHT:
                        resizeAnchorX = rx;      resizeAnchorY = ry + h; break;
                    case RECT_BOTTOM_LEFT:
                        resizeAnchorX = rx + w;  resizeAnchorY = ry;     break;
                    case RECT_BOTTOM_RIGHT:
                        resizeAnchorX = rx;      resizeAnchorY = ry;     break;
                    case RECT_LEFT:
                    case RECT_RIGHT:
                        resizeAnchorX = (currentResizeMode==ResizeMode.RECT_LEFT? rx + w : rx);
                        resizeAnchorY = y; break;
                    case RECT_TOP:
                    case RECT_BOTTOM:
                        resizeAnchorX = x;
                        resizeAnchorY = (currentResizeMode==ResizeMode.RECT_TOP? ry + h : ry);
                        break;
                    default: break;
                }
                return;
            }
        }

        
        else if (node instanceof javafx.scene.shape.Ellipse ell) {
            double cx = ell.getCenterX(), cy = ell.getCenterY();
            double rx = ell.getRadiusX(), ry = ell.getRadiusY();
            double dx = (x - cx) / rx;
            double dy = (y - cy) / ry;
            double d  = Math.hypot(dx, dy);
            if (Math.abs(d - 1) < ELLIPSE_BORDER_TOLERANCE / Math.max(rx, ry)) {
                currentResizeMode = ResizeMode.ELLIPSE_BORDER;
                // punto opposto (riflesso)
                resizeAnchorX = 2*cx - x;
                resizeAnchorY = 2*cy - y;
                return;
            }
        }
    }

    
    if (currentResizeMode != ResizeMode.NONE) {
        return;
    }   


    
    if (selectedShapeInstance != null) {
        moveAnchorX = e.getX();
        moveAnchorY = e.getY();

        Node node = selectedShapeInstance.getNode();
        if (node instanceof javafx.scene.shape.Line line) {
            origX1 = line.getStartX();
            origY1 = line.getStartY();
            origX2 = line.getEndX();
            origY2 = line.getEndY();
        } else if (node instanceof javafx.scene.shape.Rectangle rect) {
            origX = rect.getX();
            origY = rect.getY();
        } else if (node instanceof javafx.scene.shape.Ellipse ell) {
            origCenterX = ell.getCenterX();
            origCenterY = ell.getCenterY();
        }
        return;
    }

    
    if (currentTool == Tool.NONE) {
        return;
    }

    
    System.out.println("onPressed chiamato");
    ShapeCreator creator = ConcreteShapeCreator.getCreator(selectedShape);
    AbstractShape concrete = (AbstractShape) creator.create(e.getX(), e.getY(), strokePicker.getValue());
    currentShapes.add(concrete);
    System.out.println("Aggiunta shape a currentShapes: " + concrete.getClass().getSimpleName());

    tempShape = new StrokeDecorator(concrete, strokePicker.getValue());
    tempShape = new FillDecorator(tempShape, fillPicker.getValue());
    tempShape.getNode().setUserData(tempShape);
    drawingPane.getChildren().add(tempShape.getNode());
    }

     /**
     * Gestisce il trascinamento del mouse per spostare, ridimensionare
     * o disegnare la shape in costruzione.
     * @param e evento MouseEvent
     */
    private void onDragged(MouseEvent e) {

        double x = Math.min(Math.max(0, e.getX()), drawingPane.getWidth());
        double y = Math.min(Math.max(0, e.getY()), drawingPane.getHeight());

        if (selectedShapeInstance != null && currentResizeMode != ResizeMode.NONE) {
            Node node = selectedShapeInstance.getNode();
            switch (currentResizeMode) {
                case LINE_START -> {
                    javafx.scene.shape.Line l = (javafx.scene.shape.Line) node;
                    l.setStartX(x);
                    l.setStartY(y);
                }
                case LINE_END -> {
                    javafx.scene.shape.Line l = (javafx.scene.shape.Line) node;
                    l.setEndX(x);
                    l.setEndY(y);
                }
                case RECT_TOP_LEFT, RECT_TOP_RIGHT, RECT_BOTTOM_LEFT, RECT_BOTTOM_RIGHT, RECT_LEFT, RECT_RIGHT, RECT_TOP, RECT_BOTTOM -> {
                    javafx.scene.shape.Rectangle r = (javafx.scene.shape.Rectangle) node;
                    double newX = Math.min(x, resizeAnchorX);
                    double newY = Math.min(y, resizeAnchorY);
                    double newW = Math.abs(resizeAnchorX - x);
                    double newH = Math.abs(resizeAnchorY - y);
                    r.setX(newX);
                    r.setY(newY);
                    r.setWidth(newW);
                    r.setHeight(newH);
                }
                case ELLIPSE_BORDER -> {
                    javafx.scene.shape.Ellipse ell = (javafx.scene.shape.Ellipse) node;
                    double cx = (x + resizeAnchorX) / 2;
                    double cy = (y + resizeAnchorY) / 2;
                    ell.setCenterX(cx);
                    ell.setCenterY(cy);
                    ell.setRadiusX(Math.abs(x - resizeAnchorX) / 2);
                    ell.setRadiusY(Math.abs(y - resizeAnchorY) / 2);
                }
                default -> {
                }
            }
            return;
        }

        if (selectedShapeInstance != null) {
            double dx = e.getX() - moveAnchorX;
            double dy = e.getY() - moveAnchorY;

            Node node = selectedShapeInstance.getNode();
            if (node instanceof javafx.scene.shape.Line line) {
                double newStartX = origX1 + dx;
                double newStartY = origY1 + dy;
                double newEndX = origX2 + dx;
                double newEndY = origY2 + dy;

                // Clamp all coordinates to stay within the pane
                double minX = Math.max(0, Math.min(newStartX, newEndX));
                double minY = Math.max(0, Math.min(newStartY, newEndY));
                double maxX = Math.min(drawingPane.getWidth(), Math.max(newStartX, newEndX));
                double maxY = Math.min(drawingPane.getHeight(), Math.max(newStartY, newEndY));

                double deltaX = maxX - Math.max(newStartX, newEndX) + Math.min(newStartX, newEndX) - minX;
                double deltaY = maxY - Math.max(newStartY, newEndY) + Math.min(newStartY, newEndY) - minY;

                newStartX += deltaX;
                newEndX += deltaX;
                newStartY += deltaY;
                newEndY += deltaY;

                newStartX = origX1 + dx;
                newStartY = origY1 + dy;
                newEndX = origX2 + dx;
                newEndY = origY2 + dy;

// Clamp singolarmente i punti per non uscire dal drawingPane
                newStartX = Math.max(0, Math.min(newStartX, drawingPane.getWidth()));
                newStartY = Math.max(0, Math.min(newStartY, drawingPane.getHeight()));
                newEndX = Math.max(0, Math.min(newEndX, drawingPane.getWidth()));
                newEndY = Math.max(0, Math.min(newEndY, drawingPane.getHeight()));

                line.setStartX(newStartX);
                line.setStartY(newStartY);
                line.setEndX(newEndX);
                line.setEndY(newEndY);

            } else if (node instanceof javafx.scene.shape.Rectangle rect) {
                double newX = origX + dx;
                double newY = origY + dy;

                // Clamp inside pane
                newX = Math.max(0, Math.min(newX, drawingPane.getWidth() - rect.getWidth()));
                newY = Math.max(0, Math.min(newY, drawingPane.getHeight() - rect.getHeight()));

                rect.setX(newX);
                rect.setY(newY);
            } else if (node instanceof javafx.scene.shape.Ellipse ell) {
                double newCX = origCenterX + dx;
                double newCY = origCenterY + dy;

                // Clamp: center ± radius must stay within pane
                double radiusX = ell.getRadiusX();
                double radiusY = ell.getRadiusY();

                newCX = Math.max(radiusX, Math.min(newCX, drawingPane.getWidth() - radiusX));
                newCY = Math.max(radiusY, Math.min(newCY, drawingPane.getHeight() - radiusY));

                ell.setCenterX(newCX);
                ell.setCenterY(newCY);
            }
            return;
        }

        if (currentTool == Tool.NONE) {
            return;
        }

        if (tempShape != null) {
            System.out.println("onDragged: " + e.getX() + "," + e.getY());
            x = Math.min(Math.max(0, e.getX()), drawingPane.getWidth());
            y = Math.min(Math.max(0, e.getY()), drawingPane.getHeight());
            tempShape.onDrag(x, y);
        }
    }


     /**
     * Gestisce il rilascio del mouse per completare operazioni
     * di creazione, drag o resize.
     * @param e evento MouseEvent
     */
    private void onReleased(MouseEvent e) {
        
        // se ero in resize, chiudo la modalità
    if (currentResizeMode != ResizeMode.NONE) {
        currentResizeMode = ResizeMode.NONE;
        return;
    }
        
        // se stavo spostando, termino il drag
    if (selectedShapeInstance != null) {
        return;
    }
    // se non c’è tool attivo, esco
    if (currentTool == Tool.NONE) {
        return;
    }
    // altrimenti termino la creazione della shape
    tempShape = null;
    }

    /**
     * Gestisce il click sul drawingPane:
     *  - se clicco sopra una shape la seleziona
     *  - se clicco sullo sfondo deseleziona qualsiasi shape
     */
    @FXML
    private void onMouseClick(MouseEvent e) {
        // 1) Deseleziono visivamente tutte le shape
        for (Node node : drawingPane.getChildren()) {
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                fxShape.setStrokeWidth(1); // Resetta bordo
                fxShape.setEffect(null); // Rimuove effetti (drop shadow, glow, ecc)
            }
        }
        
        // 2) Tologo la selezione logica
        selectedShapeInstance = null;
        boolean hit = false;

        // 3) Scorro tutti i nodi del pane
        for (Node node : drawingPane.getChildren()) {
            // Mi interessano solo i nodi di tipo javafx.scene.shape.Shape
            if (!(node instanceof javafx.scene.shape.Shape fxShape)) {
                continue;
            }
            boolean isHit = false;

            if (fxShape instanceof Line line) {
                double sx = line.getStartX(), sy = line.getStartY();
                double ex = line.getEndX(), ey = line.getEndY();
                isHit = isNearLine(e.getX(), e.getY(), sx, sy, ex, ey, HANDLE_RADIUS);
            } else {
                 isHit = fxShape.contains(e.getX(), e.getY());
            }

            if (!isHit) {
                continue;
            }


            // Recupero l'oggetto di dominio (Shape) salvato in userData
            Object userData = fxShape.getUserData();
            if (!(userData instanceof Shape shape)) {
                continue;
            }

            // 4) Ho colpito una shape: la seleziono
            selectedShapeInstance = shape;
            selectedShapeInstance.getNode().setUserData(selectedShapeInstance);
            hit = true;
            
            // Evidenzio la figura selezionata: bordo + effetto ombra
         
            
            DropShadow ds = new DropShadow();
            ds.setOffsetX(0);
            ds.setOffsetY(0);
            ds.setRadius(10);
            ds.setColor(Color.BLACK); // o un altro colore tenue, tipo Color.GRAY

            fxShape.setEffect(ds);


            break;
        }

        // 4) Se non ho colpito nulla, selectedShapeInstance rimane null (già deselezionato)
        if (!hit) {
            selectedShapeInstance = null;
            currentTool = Tool.NONE;
        }
    }

    
    /**
     * Salva il disegno corrente in un file binario.
     */
    private void onSave() {
        System.out.println("Salvataggio: " + currentShapes.size() + " shape da serializzare");
        Stage stage = (Stage) saveButton.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salva disegno");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin")
        );
        chooser.setInitialFileName("drawing.bin");

        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            try {
                List<ShapeData> dataList = currentShapes.stream()
                        .map(shape -> new ShapeAdapter(shape).getShapeData())
                        .collect(Collectors.toList());

                DrawingData drawingData = new DrawingData(dataList);

                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                    out.writeObject(drawingData);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                // TODO: mostrare un Alert in caso di errore durante il salvataggio
            }
        }
    }

    /**
     * Carica i dati da file e restituisce il DTO.
     * @param file file binario contenente DrawingData
     * @return DrawingData letto dal file, o null in caso di errore
     */
    private DrawingData loadDrawingData(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof DrawingData data) {
                return data;
            } else {
                throw new IOException("File does not contain valid DrawingData");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ricostruisce una shape a partire dal DTO fornito,
     * applicando stroke e fill e inserendola in currentShapes.
     * @param data dati della shape serializzata
     * @return nuova istanza di Shape
     */
    private Shape rebuildShape(ShapeData data) {
        ShapeCreator creator = switch (data.getType()) {
            case "RectangleShape" ->
                RectangleShape::new;
            case "EllipseShape" ->
                EllipseShape::new;
            case "LineShape" ->
                LineShape::new;
            default ->
                throw new IllegalArgumentException("Tipo non supportato: " + data.getType());
        };

        Shape shape = creator.create(data.getX(), data.getY(), data.getStroke());

        shape.onDrag(data.getX() + data.getWidth(), data.getY() + data.getHeight());
        shape.onRelease();

        shape = new StrokeDecorator(shape, data.getStroke());
        shape = new FillDecorator(shape, data.getFill());

        shape.getNode().setUserData(shape);
        return shape;
    }

    /**
     * Apre un FileChooser, carica i dati e aggiorna il canvas.
     */
    private void onLoad() {
        Stage stage = (Stage) loadButton.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Carica disegno");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin")
        );

        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            DrawingData loadedData = loadDrawingData(file);
            if (loadedData != null) {
                System.out.println("Disegno caricato con " + loadedData.getShapes().size() + " forme.");

                drawingPane.getChildren().clear();
                currentShapes.clear();

                for (ShapeData sData : loadedData.getShapes()) {
                    Shape shape = rebuildShape(sData);

                    drawingPane.getChildren().add(shape.getNode());

                    if (shape instanceof AbstractShape concrete) {
                        currentShapes.add(concrete);
                    }
                }
            }
        }
    }
    
    /**
     * Rimuove dal canvas e da currentShapes la shape attualmente selezionata.
     */
    private void onDelete() {
        if (selectedShapeInstance != null) {
            // Nodo JavaFX sottostante
            Node node = selectedShapeInstance.getNode();
        
            // Rimuovi dal Pane
            drawingPane.getChildren().remove(node);
        
            // Rimuovi dall'elenco delle shape “concrete”
            currentShapes.removeIf(shape -> shape.getNode().equals(node));
        
            // Deseleziona
            selectedShapeInstance = null;
        }
    } 


}
