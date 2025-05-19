// shapes/LineShape.java
package it.unisa.progettosadgruppo19.shapes;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;


public class LineShape extends AbstractShape {
    private final double startX, startY;

    public LineShape(double startX, double startY) {
        super(new Line(startX, startY, startX, startY));
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public void onDrag(double x, double y) {
        Line l = (Line) node;
        l.setEndX(x);
        l.setEndY(y);
    }
}
