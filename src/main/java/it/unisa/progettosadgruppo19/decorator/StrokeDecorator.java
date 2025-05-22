package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

/**
 * Decorator che imposta il colore del bordo di una shape.
 */
public class StrokeDecorator extends ShapeDecorator {

    /**
     * Applica il colore di stroke alla shape decorata.
     *
     * @param decorated shape originale
     * @param stroke colore del contorno
     */
    public StrokeDecorator(Shape decorated, Color stroke) {
        super(decorated);
        ((javafx.scene.shape.Shape) decorated.getNode()).setStroke(stroke);
    }
}
