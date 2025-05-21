package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

/**
 * Decorator che imposta il colore di riempimento di una shape.
 */
public class FillDecorator extends ShapeDecorator {

    /**
     * Applica il colore di fill alla shape decorata.
     *
     * @param decorated shape originale
     * @param fill colore di riempimento
     */
    public FillDecorator(Shape decorated, Color fill) {
        super(decorated);
        ((javafx.scene.shape.Shape) decorated.getNode()).setFill(fill);
    }
}
