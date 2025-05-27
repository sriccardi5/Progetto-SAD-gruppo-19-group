package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ClipboardReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

public class Copy implements UndoableCommand {

    private final ClipboardReceiver receiver;
    private final Shape shape;

    public Copy(ClipboardReceiver receiver, Shape shape) {
        this.receiver = receiver;
        this.shape = shape;
    }

    @Override
    public void execute() {
        receiver.setClipboard(shape.clone());
        System.out.println("[COPY] Figura copiata: " + shape.getClass().getSimpleName());
    }

    @Override
    public void undo() {
        receiver.setClipboard(null); // svuota il buffer
        System.out.println("[UNDO COPY] Clipboard svuotato");
    }
}
