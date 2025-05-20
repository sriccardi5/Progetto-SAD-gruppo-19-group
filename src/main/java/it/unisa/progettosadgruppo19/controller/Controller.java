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

    @FXML
    public void initialize() {

        saveButton.setOnAction(evt -> onSave());
        loadButton.setOnAction(evt -> onLoad());
        deleteButton.setOnAction(e -> onDelete());


        // Imposta la forma selezionata in base al bottone cliccato
        lineButton.setOnAction(e -> selectedShape = "Linea");
        rectButton.setOnAction(e -> selectedShape = "Rettangolo");
        ellipseButton.setOnAction(e -> selectedShape = "Ellisse");

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
        System.out.println("onPressed chiamato");

        // Crea la shape concreta (non decorata)
        ShapeCreator creator = ConcreteShapeCreator.getCreator(selectedShape);
        AbstractShape concrete = (AbstractShape) creator.create(e.getX(), e.getY(), strokePicker.getValue());

        // Salva la forma per il salvataggio futuro
        currentShapes.add(concrete);
        System.out.println("Aggiunta shape a currentShapes: " + concrete.getClass().getSimpleName());

        // Applica i decorator per colore e riempimento
        tempShape = new StrokeDecorator(concrete, strokePicker.getValue());
        tempShape = new FillDecorator(tempShape, fillPicker.getValue());

        // Collega il nodo alla shape decorata
        tempShape.getNode().setUserData(tempShape);

        // Aggiungi la figura al canvas
        drawingPane.getChildren().add(tempShape.getNode());
    }

    private void onDragged(MouseEvent e) {
        if (tempShape != null) {
            System.out.println("onDragged: " + e.getX() + "," + e.getY());
            double x = Math.min(Math.max(0, e.getX()), drawingPane.getWidth());
            double y = Math.min(Math.max(0, e.getY()), drawingPane.getHeight());
            tempShape.onDrag(x, y);
        }
    }

    private void onReleased(MouseEvent e) {
        System.out.println("onReleased chiamato");
        tempShape = null;
    }

    private void onMouseClick(MouseEvent e) {
        selectedShapeInstance = null; // Deseleziona la figura precedente

        for (Node node : drawingPane.getChildren()) {
            if (!(node instanceof javafx.scene.shape.Shape fxShape)) {
                continue;
            }
            if (!fxShape.contains(e.getX(), e.getY())) {
                continue;
            }

            Object userData = fxShape.getUserData();
            if (!(userData instanceof Shape shape)) {
                continue;
            }

            selectedShapeInstance = shape; // Seleziona la figura cliccata
            break;
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
