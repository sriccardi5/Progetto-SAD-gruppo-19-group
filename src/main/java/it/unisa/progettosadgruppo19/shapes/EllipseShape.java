// shapes/EllipseShape.java
package it.unisa.progettosadgruppo19.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class EllipseShape extends AbstractShape {
    private final double startX, startY;

    public EllipseShape(double startX, double startY, Color stroke) {
        super(new Ellipse(startX, startY, 0, 0));
        this.startX = startX;
        this.startY = startY;
        Ellipse e = (Ellipse) node;
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
}
