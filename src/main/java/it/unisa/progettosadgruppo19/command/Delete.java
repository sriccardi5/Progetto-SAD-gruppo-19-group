package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.decorator.ShapeDecorator;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

public class Delete implements UndoableCommand {

    private final ShapeManagerReceiver shapeManager;
    private final Shape shape;
    private int index;

    public Delete(ShapeManagerReceiver shapeManager, Shape shape) {
        this.shapeManager = shapeManager;
        this.shape = shape;
        Shape baseShape = unwrap(shape);
        this.index = shapeManager.getShapeIndex(baseShape);
        System.out.println("[DELETE] Index salvato: " + index);
    }

    @Override
    public void execute() {
        System.out.println("[DELETE] Rimozione: " + shape.getClass().getSimpleName());
        shapeManager.removeShape(shape);
    }

    @Override
    public void undo() {
        System.out.println("[UNDO DELETE] Inserisco a indice: " + index);
        shapeManager.insertShapeAt(shape, index);
    }

    private Shape unwrap(Shape shape) {
        while (shape instanceof ShapeDecorator decorator) {
            shape = decorator.getWrapped();
        }
        return shape;
    }
}
