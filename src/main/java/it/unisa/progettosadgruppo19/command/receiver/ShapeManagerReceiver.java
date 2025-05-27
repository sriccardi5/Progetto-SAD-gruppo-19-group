package it.unisa.progettosadgruppo19.command.receiver;

import it.unisa.progettosadgruppo19.model.shapes.Shape;

public interface ShapeManagerReceiver {

    void addShape(Shape shape);

    void removeShape(Shape shape);

    int getShapeIndex(Shape shape);

    void insertShapeAt(Shape shape, int index);
}
