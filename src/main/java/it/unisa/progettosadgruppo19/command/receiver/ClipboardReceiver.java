package it.unisa.progettosadgruppo19.command.receiver;

import it.unisa.progettosadgruppo19.model.shapes.Shape;

public interface ClipboardReceiver {

    void setClipboard(Shape shape);

    Shape getClipboard();
}
