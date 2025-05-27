package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ZOrderReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando che porta una shape in secondo piano (primo nella lista dei figli).
 */
public class ZLevelsToBack implements UndoableCommand {

    private final ZOrderReceiver receiver;
    private final Shape shape;
    private final int targetIndex;
    private int oldIndex;

    public ZLevelsToBack(ZOrderReceiver receiver, Shape shape, int targetIndex) {
        this.receiver = receiver;
        this.shape = shape;
        this.targetIndex = targetIndex;
    }

    @Override
    public void execute() {
        oldIndex = receiver.getZIndex(shape);
        receiver.setZIndex(shape, targetIndex); // Inserisce sopra la griglia
    }

    @Override
    public void undo() {
        receiver.setZIndex(shape, oldIndex);
    }
}
