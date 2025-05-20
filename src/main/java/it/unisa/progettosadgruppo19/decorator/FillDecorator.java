/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

public class FillDecorator extends ShapeDecorator {
    public FillDecorator(Shape decorated, Color fill) {
        super(decorated);
        ((javafx.scene.shape.Shape) decorated.getNode()).setFill(fill);
    }
}
