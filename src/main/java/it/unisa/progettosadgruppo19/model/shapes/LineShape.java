/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class LineShape extends AbstractShape {

    private final double startX, startY;
    private final Line line;

    public LineShape(double startX, double startY, Color stroke) {
        super(new Line(startX, startY, startX, startY));
        this.startX = startX;
        this.startY = startY;
        this.line = (Line) node;
        line.setStroke(stroke);
    }

    @Override
    public void onDrag(double x, double y) {
        Line l = (Line) node;
        l.setEndX(x);
        l.setEndY(y);
    }

    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return Math.min(line.getStartX(), line.getEndX());
    }

    @Override
    public double getY() {
        return Math.min(line.getStartY(), line.getEndY());
    }

    @Override
    public double getWidth() {
        return Math.abs(line.getEndX() - line.getStartX());
    }

    @Override
    public double getHeight() {
        return Math.abs(line.getEndY() - line.getStartY());
    }
}
