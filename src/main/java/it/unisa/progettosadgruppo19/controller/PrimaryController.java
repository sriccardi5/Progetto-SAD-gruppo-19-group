/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.controller;



import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class PrimaryController {

    @FXML private Pane drawingPane;

    @FXML
    public void initialize() { 
        drawingPane.getChildren().clear();    
    }
}

