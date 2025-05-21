/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * Implementazione di un'ellisse che si ridimensiona durante il drag.
 */
public class EllipseShape extends AbstractShape {

    private final double startX, startY;
    private final Ellipse e;
    
    /**
     * Costruisce un'EllipseShape iniziale di raggio zero.
     * @param startX coordinata X del centro iniziale
     * @param startY coordinata Y del centro iniziale
     * @param stroke colore del contorno
     */
    public EllipseShape(double startX, double startY, Color stroke) {
        super(new Ellipse(startX, startY, 0, 0));
        this.startX = startX;
        this.startY = startY;
        this.e = (Ellipse) node;
        e.setStroke(stroke);
    }

    @Override
    public void onDrag(double x, double y) {
        Ellipse e = (Ellipse) node;
        e.setCenterX((startX + x) / 2);
        e.setCenterY((startY + y) / 2);
        e.setRadiusX(Math.abs(x - startX) / 2);
        e.setRadiusY(Math.abs(y - startY) / 2);
    }

    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return e.getCenterX() - e.getRadiusX();
    }

    @Override
    public double getY() {
        return e.getCenterY() - e.getRadiusY();
    }

    @Override
    public double getWidth() {
        return e.getRadiusX() * 2;
    }

    @Override
    public double getHeight() {
        return e.getRadiusY() * 2;
    }

}
