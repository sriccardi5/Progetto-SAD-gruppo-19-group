package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.command.receiver.ClipboardReceiver;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

public class Cut implements UndoableCommand {

    private final ClipboardReceiver clipboard;
    private final ShapeManagerReceiver shapeManager;
    private final Shape shape;
    private final AbstractShape unwrapped;
    private int index;

    public Cut(ClipboardReceiver clipboard, ShapeManagerReceiver shapeManager, Shape shape) {
        this.clipboard = clipboard;
        this.shapeManager = shapeManager;
        this.shape = shape;
        this.unwrapped = unwrapToAbstract(shape);
        this.index = shapeManager.getShapeIndex(unwrapped);
    }

    @Override
    public void execute() {
        if (index < 0) {
            System.out.println("[CUT] Errore: shape non trovata per index");
            return;
        }
        clipboard.setClipboard(shape.clone());
        shapeManager.removeShape(shape);
        System.out.println("[CUT] Taglio figura: " + shape.getClass().getSimpleName());
    }

    @Override
    public void undo() {
        if (index < 0) {
            System.out.println("[UNDO CUT] Errore: indice non valido (" + index + ")");
            return;
        }
        shapeManager.insertShapeAt(shape, index);
        clipboard.setClipboard(null);
        System.out.println("[UNDO CUT] Figura reinserita a indice: " + index + " e clipboard svuotata");
    }

    private AbstractShape unwrapToAbstract(Shape shape) {
        while (shape instanceof it.unisa.progettosadgruppo19.decorator.ShapeDecorator decorator) {
            shape = decorator.getWrapped();
        }
        return (AbstractShape) shape;
    }
}
