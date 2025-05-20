/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// src/main/java/it/unisa/progettosadgruppo19/factory/ShapeCreator.java
package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

@FunctionalInterface
public interface ShapeCreator {

    Shape create(double startX, double startY, Color stroke);
}
