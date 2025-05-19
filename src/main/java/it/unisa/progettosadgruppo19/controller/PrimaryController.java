/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import javafx.scene.input.MouseEvent;

import it.unisa.progettosadgruppo19.factory.ShapeCreator;
import it.unisa.progettosadgruppo19.factory.ConcreteShapeCreator;
import it.unisa.progettosadgruppo19.shapes.Shape;

public class PrimaryController {

    @FXML private Pane drawingPane;
    @FXML private Button lineButton;
    @FXML private Button rectButton;
    @FXML private Button ellipseButton;
   
    private Shape tempShape;
    
    private String selectedShape = "Linea"; // Valore di default

    @FXML
    public void initialize() {
        
        // Imposta la forma selezionata in base al bottone cliccato
        lineButton.setOnAction(e -> selectedShape = "Linea");
        rectButton.setOnAction(e -> selectedShape = "Rettangolo");
        ellipseButton.setOnAction(e -> selectedShape = "Ellisse");
        
        drawingPane.setOnMousePressed(this::onPressed);
        drawingPane.setOnMouseDragged(this::onDragged);
        drawingPane.setOnMouseReleased(this::onReleased);
    }
    
    private void onPressed(MouseEvent e) {
        ShapeCreator creator = ConcreteShapeCreator.getCreator(selectedShape);
        // crea la forma con le coordinate e i colori selezionati
        tempShape = creator.create(
            e.getX(),
            e.getY()
        );

        drawingPane.getChildren().add(tempShape.getNode());
    }
    
    private void onDragged(MouseEvent e) {
        if (tempShape != null) {
            double x = Math.min(Math.max(0,e.getX()),drawingPane.getWidth());
            double y = Math.min(Math.max(0,e.getY()), drawingPane.getHeight());
            tempShape.onDrag(x,y);
        }
    }

    private void onReleased(MouseEvent e) {
       tempShape = null;
    }
    

}

