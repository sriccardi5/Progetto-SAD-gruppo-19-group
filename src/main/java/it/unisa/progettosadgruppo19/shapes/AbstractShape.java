// src/main/java/it/unisa/progettosadgruppo19/shapes/AbstractShape.java
package it.unisa.progettosadgruppo19.shapes;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public abstract class AbstractShape implements Shape {
    // qui specifico il tipo JavaFX in maniera pienamente qualificata
    protected final javafx.scene.shape.Shape node;

    public AbstractShape(javafx.scene.shape.Shape node) {
        this.node = node;
        node.setFill(Color.TRANSPARENT);
        node.setStroke(Color.BLACK);
    }

    @Override
    public Node getNode() {
        return node;
    }
    // non serve qui onDrag/onRelease, le lascio astratte e le implementi nelle sottoclassi
}
