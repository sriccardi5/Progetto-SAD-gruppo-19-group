/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public abstract class AbstractShape implements Shape {

    // qui specifico il tipo JavaFX in maniera pienamente qualificata
    protected final javafx.scene.shape.Shape node;

    public AbstractShape(javafx.scene.shape.Shape node) {
        this.node = node;
        node.setFill(Color.TRANSPARENT);
        node.setStroke(Color.BLACK);
    }

    @Override
    public Node getNode() {
        return node;
    }
    // non serve qui onDrag/onRelease, le lascio astratte e le implementi nelle sottoclassi

    @Override
    public boolean contains(double x, double y) {
        return node.contains(x, y);
    }
    
    public abstract double getX();
    public abstract double getY();
    public abstract double getWidth();
    public abstract double getHeight();
}

    
