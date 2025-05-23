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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private Pane drawingPane;
    @FXML
    private Button lineButton, rectButton, ellipseButton, saveButton, loadButton, deleteButton;
    @FXML
    private ColorPicker strokePicker, fillPicker;
    @FXML
    private Button copyButton;

    private final List<AbstractShape> currentShapes = new ArrayList<>();
    private MouseEventHandler mouseHandler;
    private ShapeFileManager fileManager = new ShapeFileManager();

    private String selectedShape = "Linea";

    @FXML
    public void initialize() {
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

        drawingPane.setOnMousePressed(mouseHandler::onPressed);
        drawingPane.setOnMouseDragged(mouseHandler::onDragged);
        drawingPane.setOnMouseReleased(mouseHandler::onReleased);
        drawingPane.setOnMouseClicked(mouseHandler::onMouseClick);
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
            // Clona la shape, incluso eventuali decorator
            Shape copied = selected.clone();

            // Sposta leggermente la copia per distinguere visivamente
            copied.moveBy(10, 10);

            // Aggiunge la nuova forma alla UI
            drawingPane.getChildren().add(copied.getNode());

            // Estrai l'AbstractShape base (senza decorator) per la logica
            AbstractShape baseShape = unwrapToAbstract(copied);
            currentShapes.add(baseShape);

            // Se vuoi selezionare subito la copia:
            mouseHandler.setSelectedShapeInstance(copied);
            
                    // 🔍 Stampa di controllo
                System.out.println("Figura copiata: " + copied.getClass().getSimpleName() +
                                   " | X: " + copied.getX() +
                                   " | Y: " + copied.getY());
            } else {
                System.out.println("Nessuna figura selezionata da copiare.");
            }
            
            
            
        }
    }


