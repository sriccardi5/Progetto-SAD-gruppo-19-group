package it.unisa.progettosadgruppo19.model.shapes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * Classe astratta che implementa parzialmente l'interfaccia Shape, fornendo il
 * nodo JavaFX interno e la logica di contains.
 */
public abstract class AbstractShape implements Shape {

    /**
     * Nodo JavaFX deputato al rendering (Line, Rectangle, Ellipse).
     */
    protected final javafx.scene.shape.Shape node;

    /**
     * Costruisce una AbstractShape avvolgendo il nodo specificato.
     *
     * @param node nodo JavaFX della forma
     */
    public AbstractShape(javafx.scene.shape.Shape node) {
        this.node = node;
        node.setFill(Color.TRANSPARENT);
        node.setStroke(Color.BLACK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getNode() {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(double x, double y) {
        return node.contains(x, y);
    }

    /**
     * Restituisce la coordinata X dell'estremo sinistro della shape.
     *
     * @return valore X minimo
     */
    public abstract double getX();

    /**
     * Restituisce la coordinata Y dell'estremo superiore della shape.
     *
     * @return valore Y minimo
     */
    public abstract double getY();

    /**
     * Restituisce la larghezza complessiva della shape.
     *
     * @return differenza orizzontale tra i bordi
     */
    public abstract double getWidth();

    /**
     * Restituisce l'altezza complessiva della shape.
     *
     * @return differenza verticale tra i bordi
     */
    public abstract double getHeight();
    
    
    @Override
    public abstract AbstractShape clone(); // Sarà implementato dalle sottoclassi


    
    public void moveBy(double dx, double dy) {
    javafx.scene.shape.Shape node = (javafx.scene.shape.Shape) getNode();
    node.setTranslateX(node.getTranslateX() + dx);
    node.setTranslateY(node.getTranslateY() + dy);
}

}
