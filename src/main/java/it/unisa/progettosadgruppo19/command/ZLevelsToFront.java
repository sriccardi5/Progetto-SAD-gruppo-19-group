package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ZOrderReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

public class ZLevelsToFront implements UndoableCommand {

    private final ZOrderReceiver receiver;
    private final Shape shape;
    private final int maxIndex;
    private int oldIndex;

    public ZLevelsToFront(ZOrderReceiver receiver, Shape shape, int maxIndex) {
        this.receiver = receiver;
        this.shape = shape;
        this.maxIndex = maxIndex;
    }

    @Override
    public void execute() {
        oldIndex = receiver.getZIndex(shape);
        receiver.setZIndex(shape, maxIndex); // Usa maxIndex valido
    }

    @Override
    public void undo() {
        receiver.setZIndex(shape, oldIndex);
    }
}
