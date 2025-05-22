package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Implementazione di un rettangolo che si ridimensiona durante il drag.
 */
public class RectangleShape extends AbstractShape {

    private final double startX, startY;
    private final Rectangle r;

    /**
     * Costruisce un RectangleShape iniziale di dimensione nulla.
     *
     * @param startX coordinata X di partenza
     * @param startY coordinata Y di partenza
     * @param stroke colore del contorno
     */
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
