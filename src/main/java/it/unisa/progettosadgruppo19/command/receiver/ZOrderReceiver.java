package it.unisa.progettosadgruppo19.command.receiver;

import it.unisa.progettosadgruppo19.model.shapes.Shape;

public interface ZOrderReceiver {

    int getZIndex(Shape shape);

    void setZIndex(Shape shape, int index);
}
