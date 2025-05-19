// shapes/RectangleShape.java
package it.unisa.progettosadgruppo19.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleShape extends AbstractShape {
    private final double startX, startY;

    public RectangleShape(double startX, double startY, Color stroke) {
        super(new Rectangle(startX, startY, 0, 0));
        this.startX = startX;
        this.startY = startY;
        Rectangle r = (Rectangle) node;
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
}
