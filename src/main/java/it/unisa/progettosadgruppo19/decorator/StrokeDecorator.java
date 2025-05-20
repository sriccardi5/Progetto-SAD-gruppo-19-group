/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

public class StrokeDecorator extends ShapeDecorator {
    public StrokeDecorator(Shape decorated, Color stroke) {
        super(decorated);
        ((javafx.scene.shape.Shape) decorated.getNode()).setStroke(stroke);
    }
}
