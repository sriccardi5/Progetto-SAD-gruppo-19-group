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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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


    @FXML
    public void initialize() {

        saveButton.setOnAction(evt -> onSave());
        loadButton.setOnAction(evt -> onLoad());
        deleteButton.setOnAction(e -> onDelete());


        // Imposta la forma selezionata in base al bottone cliccato
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

        //Default stroke/fill
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

    private void onPressed(MouseEvent e) {    
     
        


    // 1) DRAG di una shape selezionata
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

    // 2) SE NON c’è un tool di disegno selezionato, esco
    if (currentTool == Tool.NONE) {
        return;
    }

    // 3) LOGICA DI CREAZIONE NUOVA SHAPE
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

    private void onDragged(MouseEvent e) {
        
        
        // 1) se sto spostando una shape selezionata
    if (selectedShapeInstance != null) {
        double dx = e.getX() - moveAnchorX;
        double dy = e.getY() - moveAnchorY;

        Node node = selectedShapeInstance.getNode();
        if (node instanceof javafx.scene.shape.Line line) {
            line.setStartX(origX1 + dx);
            line.setStartY(origY1 + dy);
            line.setEndX(origX2 + dx);
            line.setEndY(origY2 + dy);
        } else if (node instanceof javafx.scene.shape.Rectangle rect) {
            rect.setX(origX + dx);
            rect.setY(origY + dy);
        } else if (node instanceof javafx.scene.shape.Ellipse ell) {
            ell.setCenterX(origCenterX + dx);
            ell.setCenterY(origCenterY + dy);
        }
        return;
    }

    // 2) se non ho tool di disegno, esco
    if (currentTool == Tool.NONE) {
        return;
    }

    // 3) altrimenti continuo il drag per la nuova shape
    if (tempShape != null) {
        System.out.println("onDragged: " + e.getX() + "," + e.getY());
        double x = Math.min(Math.max(0, e.getX()), drawingPane.getWidth());
        double y = Math.min(Math.max(0, e.getY()), drawingPane.getHeight());
        tempShape.onDrag(x, y);
    }
    }

    private void onReleased(MouseEvent e) {
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
        // 1) Inizialmente tolgo ogni selezione
        selectedShapeInstance = null;

        boolean hit = false;

        // 2) Scorro tutti i nodi del pane
        for (Node node : drawingPane.getChildren()) {
            // Mi interessano solo i nodi di tipo javafx.scene.shape.Shape
            if (!(node instanceof javafx.scene.shape.Shape fxShape)) {
                continue;
            }
            // Verifico se il click è dentro i confini della shape
            if (!fxShape.contains(e.getX(), e.getY())) {
                continue;
            }

            // Recupero l'oggetto di dominio (Shape) salvato in userData
            Object userData = fxShape.getUserData();
            if (!(userData instanceof Shape shape)) {
                continue;
            }

            // 3) Ho colpito una shape: la seleziono e interrompo il ciclo
            selectedShapeInstance = shape;
            hit = true;
            break;
        }

        // 4) Se non ho colpito nulla, selectedShapeInstance rimane null (già deselezionato)
        if (!hit) {
            selectedShapeInstance = null;
            currentTool = Tool.NONE;
        }
    }


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
