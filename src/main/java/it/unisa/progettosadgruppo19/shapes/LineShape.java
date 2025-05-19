// shapes/LineShape.java
package it.unisa.progettosadgruppo19.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;


public class LineShape extends AbstractShape {
    private final double startX, startY;

    public LineShape(double startX, double startY, Color stroke) {
        super(new Line(startX, startY, startX, startY));
        this.startX = startX;
        this.startY = startY;
        ((Line)node).setStroke(stroke);
    }

    @Override
    public void onDrag(double x, double y) {
        Line l = (Line) node;
        l.setEndX(x);
        l.setEndY(y);
    }
}
