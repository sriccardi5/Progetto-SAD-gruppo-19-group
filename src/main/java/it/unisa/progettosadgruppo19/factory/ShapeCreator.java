/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// src/main/java/it/unisa/progettosadgruppo19/factory/ShapeCreator.java
package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

/**
 * Interfaccia funzionale per creare shape
 * usando un metodo factory.
 */
@FunctionalInterface
public interface ShapeCreator {

     /**
     * Crea una nuova shape a partire da coordinate e colore.
     * @param startX coordinata X iniziale
     * @param startY coordinata Y iniziale
     * @param stroke colore del contorno
     * @return nuova istanza di Shape
     */
    Shape create(double startX, double startY, Color stroke);
}
