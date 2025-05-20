/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.adapter;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
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
        return new ShapeData(
                type,
                shape.getX(),
                shape.getY(),
                shape.getWidth(),
                shape.getHeight()
        );
    }

    public ShapeData getShapeData() {
        return shapeData;
    }
}
