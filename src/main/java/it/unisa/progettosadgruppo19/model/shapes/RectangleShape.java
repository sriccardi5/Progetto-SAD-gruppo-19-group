/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleShape extends AbstractShape {

    private final double startX, startY;
    private final Rectangle r;

    public RectangleShape(double startX, double startY, Color stroke) {
        super(new Rectangle(startX, startY, 0, 0));
        this.startX = startX;
        this.startY = startY;
        this.r = (Rectangle) node;
        r.setStroke(stroke);
    }

    @Override
    public void onDrag(double x, double y) {
        Rectangle r = (Rectangle) node;
        r.setX(Math.min(startX, x));
        r.setY(Math.min(startY, y));
        r.setWidth(Math.abs(x - startX));
        r.setHeight(Math.abs(y - startY));
    }

    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return r.getX();
    }

    @Override
    public double getY() {
        return r.getY();
    }

    @Override
    public double getWidth() {
        return r.getWidth();
    }

    @Override
    public double getHeight() {
        return r.getHeight();
    }
}
