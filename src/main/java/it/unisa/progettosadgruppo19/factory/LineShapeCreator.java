package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.LineShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

public class LineShapeCreator extends ShapeCreator {

    @Override
    public Shape createShape(double x, double y, Color stroke) {
        return new LineShape(x, y, stroke);
    }
}
