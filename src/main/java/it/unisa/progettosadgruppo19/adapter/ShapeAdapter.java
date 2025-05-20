package it.unisa.progettosadgruppo19.adapter;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.Serializable;

public class ShapeAdapter implements Serializable {

    private transient AbstractShape originalShape;
    private ShapeData shapeData;

    public ShapeAdapter(AbstractShape shape) {
        this.originalShape = shape;
        this.shapeData = convertToShapeData(shape);
    }

    private ShapeData convertToShapeData(AbstractShape shape) {
        String type = shape.getClass().getSimpleName();
        javafx.scene.shape.Shape fxNode = (javafx.scene.shape.Shape) shape.getNode();

        // Convert Paint → Color in modo sicuro
        Paint stroke = fxNode.getStroke();
        Paint fill = fxNode.getFill();

        Color strokeColor = (stroke instanceof Color c) ? c : Color.BLACK;
        Color fillColor = (fill instanceof Color c) ? c : Color.TRANSPARENT;

        return new ShapeData(
                type,
                shape.getX(),
                shape.getY(),
                shape.getWidth(),
                shape.getHeight(),
                strokeColor,
                fillColor
        );
    }

    public ShapeData getShapeData() {
        return shapeData;
    }
}
