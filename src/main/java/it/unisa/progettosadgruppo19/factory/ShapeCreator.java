/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// src/main/java/it/unisa/progettosadgruppo19/factory/ShapeCreator.java
package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.shapes.Shape;

@FunctionalInterface
public interface ShapeCreator {
    Shape create(double startX, double startY);
}
