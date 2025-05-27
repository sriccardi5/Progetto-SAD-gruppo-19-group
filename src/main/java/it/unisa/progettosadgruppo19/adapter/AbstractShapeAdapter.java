package it.unisa.progettosadgruppo19.adapter;

import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public abstract class AbstractShapeAdapter implements ShapeDataAdapter {

    protected final AbstractShape shape;

    public AbstractShapeAdapter(AbstractShape shape) {
        this.shape = shape;
    }

    @Override
    public ShapeData getShapeData() {
        javafx.scene.shape.Shape fxNode = (javafx.scene.shape.Shape) shape.getNode();

        Paint stroke = fxNode.getStroke();
        Paint fill = fxNode.getFill();

        Color strokeColor = (stroke instanceof Color c) ? c : Color.BLACK;
        Color fillColor = (fill instanceof Color c) ? c : Color.TRANSPARENT;

        return new ShapeData(
                shape.getClass().getSimpleName(),
                shape.getX(),
                shape.getY(),
                shape.getWidth(),
                shape.getHeight(),
                strokeColor,
                fillColor
        );
    }
}
