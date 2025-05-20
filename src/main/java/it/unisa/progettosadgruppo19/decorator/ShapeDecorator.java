/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.Node;

public abstract class ShapeDecorator implements Shape {

    protected final Shape decorated;

    public ShapeDecorator(Shape decorated) {
        this.decorated = decorated;
    }

    @Override
    public Node getNode() {
        return decorated.getNode();
    }

    @Override
    public void onDrag(double x, double y) {
        decorated.onDrag(x, y);
    }

    @Override
    public void onRelease() {
        decorated.onRelease();
    }

    @Override
    public boolean contains(double x, double y) {
        return decorated.contains(x, y);
    }
}
