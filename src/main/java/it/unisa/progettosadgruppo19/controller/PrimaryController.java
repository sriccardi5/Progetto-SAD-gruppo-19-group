/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import it.unisa.progettosadgruppo19.factory.ShapeCreator;
import it.unisa.progettosadgruppo19.factory.ConcreteShapeCreator;
import it.unisa.progettosadgruppo19.shapes.*;
import it.unisa.progettosadgruppo19.decorator.*;

public class PrimaryController {

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

    private Shape tempShape;
    private String selectedShape = "Linea"; // Valore di default
    
    private Shape selectedShapeInstance = null;


    @FXML
    public void initialize() {

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
        ShapeCreator creator = ConcreteShapeCreator.getCreator(selectedShape);
        tempShape = creator.create(e.getX(), e.getY(), strokePicker.getValue());

        // Applica i decorator per colore bordo e riempimento iniziale
        tempShape = new StrokeDecorator(tempShape, strokePicker.getValue());
        tempShape = new FillDecorator(tempShape, fillPicker.getValue());

        // Collega il nodo all'oggetto Shape
        tempShape.getNode().setUserData(tempShape);

        // Aggiunge la figura al pannello
        drawingPane.getChildren().add(tempShape.getNode());
    }

    private void onDragged(MouseEvent e) {
        if (tempShape != null) {
            double x = Math.min(Math.max(0, e.getX()), drawingPane.getWidth());
            double y = Math.min(Math.max(0, e.getY()), drawingPane.getHeight());
            tempShape.onDrag(x, y);
        }
    }

    private void onReleased(MouseEvent e) {
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


}