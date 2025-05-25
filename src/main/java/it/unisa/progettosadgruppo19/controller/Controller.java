package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.ShapeDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.*;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.transform.Scale;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private Pane drawingPane;
    @FXML
    private Button lineButton, rectButton, ellipseButton, saveButton, loadButton, deleteButton, copyButton,
            cutButton, pasteButton, zoomInButton, zoomOutButton, bringToFrontButton, sendToBackButton,
            gridButton;
    @FXML
    private ColorPicker strokePicker, fillPicker;

    private final List<AbstractShape> currentShapes = new ArrayList<>();
    private MouseEventHandler mouseHandler;
    private ShapeFileManager fileManager = new ShapeFileManager();

    private String selectedShape = "Linea";
    private Shape clipboardBuffer = null; // buffer interno
    
    private boolean gridVisible = false;

    private final ZoomManager zoomManager = new ZoomManager();
    private final Scale scaleTransform = new Scale(1, 1, 0, 0);
    
    private GridManager gridManager;



    @FXML
    public void initialize() {
        
        drawingPane.getTransforms().add(scaleTransform);
        
        zoomInButton.setOnAction(e -> onZoomIn());
        zoomOutButton.setOnAction(e -> onZoomOut());
        
        mouseHandler = new MouseEventHandler(drawingPane, currentShapes);
        mouseHandler.setSelectedShape(selectedShape);
        mouseHandler.setToolActive(true);

        strokePicker.setValue(javafx.scene.paint.Color.BLACK);
        fillPicker.setValue(javafx.scene.paint.Color.TRANSPARENT);

        saveButton.setOnAction(evt -> onSave());
        loadButton.setOnAction(evt -> onLoad());
        deleteButton.setOnAction(evt -> onDelete());

        lineButton.setOnAction(e -> setTool("Linea"));
        rectButton.setOnAction(e -> setTool("Rettangolo"));
        ellipseButton.setOnAction(e -> setTool("Ellisse"));
        
        copyButton.setOnAction(evt -> onCopy());
        cutButton.setOnAction(evt -> onCut());
        
      
        
        gridManager = new GridManager(drawingPane);

        gridButton.setOnAction(e -> {
            gridManager.toggleGrid();
            gridButton.setStyle(gridButton.getStyle().isEmpty() ? "-fx-background-color: lightgray;" : "");
        });



        strokePicker.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                Shape decorated = new StrokeDecorator(selected, strokePicker.getValue());
                int index = drawingPane.getChildren().indexOf(selected.getNode());
                drawingPane.getChildren().set(index, decorated.getNode());
                decorated.getNode().setUserData(decorated);
                mouseHandler.setSelectedShapeInstance(decorated);
            }
        });

        fillPicker.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                Shape decorated = new FillDecorator(selected, fillPicker.getValue());
                int index = drawingPane.getChildren().indexOf(selected.getNode());
                drawingPane.getChildren().set(index, decorated.getNode());
                decorated.getNode().setUserData(decorated);
                mouseHandler.setSelectedShapeInstance(decorated);
            }
        });
        
        bringToFrontButton.setOnAction(e -> bringToFront());
        sendToBackButton.setOnAction(e -> sendToBack());


        drawingPane.setOnMousePressed(mouseHandler::onPressed);
        drawingPane.setOnMouseDragged(mouseHandler::onDragged);
        drawingPane.setOnMouseReleased(mouseHandler::onReleased);
        drawingPane.setOnMouseClicked(mouseHandler::onMouseClick);
    }
    
    @FXML
    private void onZoomIn() {
        double s = zoomManager.zoomIn();
        scaleTransform.setX(s);
        scaleTransform.setY(s);
    }
    
    @FXML
    private void onZoomOut() {
        double s = zoomManager.zoomOut();
        scaleTransform.setX(s);
        scaleTransform.setY(s);
    }

    private void setTool(String tipo) {
        selectedShape = tipo;
        mouseHandler.setSelectedShape(tipo);
        mouseHandler.setStrokeColor(strokePicker.getValue());
        mouseHandler.setFillColor(fillPicker.getValue());
        mouseHandler.setToolActive(true);

        // Deseleziona la figura attuale (se presente)
        if (mouseHandler.getSelectedShapeInstance() != null) {
            javafx.scene.shape.Shape fx = (javafx.scene.shape.Shape) mouseHandler.getSelectedShapeInstance().getNode();
            fx.setStrokeWidth(1);
            fx.setEffect(null);
            mouseHandler.setSelectedShapeInstance(null);
        }
    }

    private void onSave() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salva disegno");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin"));
        chooser.setInitialFileName("drawing.bin");

        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            try {
                fileManager.saveToFile(currentShapes, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onLoad() {
        Stage stage = (Stage) loadButton.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Carica disegno");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin"));

        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            try {
                DrawingData data = fileManager.loadFromFile(file);
                List<AbstractShape> shapes = fileManager.rebuildShapes(data);
                drawingPane.getChildren().clear();
                currentShapes.clear();
                for (AbstractShape s : shapes) {
                    currentShapes.add(s);
                    drawingPane.getChildren().add(s.getNode());
                }

                for (ShapeData s : data.getShapes()) {
                    System.out.println("  " + s.getType() + " @ " + s.getX() + "," + s.getY());
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void onDelete() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Node node = selected.getNode();
            drawingPane.getChildren().remove(node);
            currentShapes.removeIf(shape -> shape.getNode().equals(node));
            mouseHandler.setSelectedShapeInstance(null);
        }
    }
    
    private AbstractShape unwrapToAbstract(Shape shape) {
        while (shape instanceof ShapeDecorator) {
            shape = ((ShapeDecorator) shape).getWrapped();
        }
        if (shape instanceof AbstractShape) {
            return (AbstractShape) shape;
        } else {
            throw new IllegalStateException("Shape non è un AbstractShape dopo l'unwrapping");
        }
    }
    
    private void onCopy() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Shape copied = selected.clone(); // Clona la figura (inclusi decorator)
            clipboardBuffer = copied; // Salva nel buffer
            System.out.println("Figura copiata nel buffer.");
        } else {
            System.out.println("Nessuna figura selezionata da copiare.");
        }
    }
    
    private void onCut() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Shape copied = selected.clone(); // Clona la figura (inclusi decorator)
            clipboardBuffer = copied; // Salva nel buffer
            System.out.println("Figura tagliata nel buffer.");
            drawingPane.getChildren().remove(selected.getNode());
        } else {
            System.out.println("Nessuna figura selezionata da tagliare.");
        }
    }

    @FXML
    public void handlePaste() {
        if (clipboardBuffer != null) {
            mouseHandler.setShapeToPaste(clipboardBuffer.clone()); // Clona la figura
            clipboardBuffer = null;
        } else {
            System.out.println("Buffer vuoto: nessuna figura da incollare.");
        }
    }
    
    
    // Porta la shape selezionata in primo piano (in cima allo stack visivo)
    private void bringToFront() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Node node = selected.getNode();
            if (drawingPane.getChildren().remove(node)) {
                drawingPane.getChildren().add(node); // Aggiungi in fondo => primo piano
                System.out.println("Figura portata in primo piano.");
            }
        }
    }

    // Porta la shape selezionata in secondo piano (in fondo allo stack visivo)
    private void sendToBack() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Node node = selected.getNode();
            if (drawingPane.getChildren().remove(node)) {
                int gridCount = gridManager.getGridLayerCount();
                drawingPane.getChildren().add(gridCount, node); // Inserisci dopo la griglia
                System.out.println("Figura portata in secondo piano (sopra la griglia).");
            }
        }
    }

    
 }


