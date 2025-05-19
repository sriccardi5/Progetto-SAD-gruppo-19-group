/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

public class PrimaryController {

    @FXML private Pane drawingPane;
    @FXML private Button lineButton;
    @FXML private Button rectButton;
    @FXML private Button ellipseButtjon;

    private String selectedShape = "Linea"; // Valore di default

    @FXML
    public void initialize() {
        drawingPane.getChildren().clear();   
        
        // Imposta la forma selezionata in base al bottone cliccato
        lineButton.setOnAction(e -> selectedShape = "Linea");
        rectButton.setOnAction(e -> selectedShape = "Rettangolo");
        ellipseButton.setOnAction(e -> selectedShape = "Ellisse");
    }

}

