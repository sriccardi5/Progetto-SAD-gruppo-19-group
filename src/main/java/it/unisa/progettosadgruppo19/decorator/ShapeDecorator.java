package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.Node;

/**
 * Decorator astratto che inoltra tutte le chiamate all'istanza decorata.
 */
public abstract class ShapeDecorator implements Shape {

    protected final Shape decorated;

    /**
     * Costruisce un decorator su una shape esistente.
     *
     * @param decorated shape da avvolgere
     */
    public ShapeDecorator(Shape decorated) {
        this.decorated = decorated;
    }

    @Override
    public Node getNode() {
        return decorated.getNode();
    }

    @Override
    public void onDrag(double x, double y) {
        decorated.onDrag(x, y);
    }

    @Override
    public void onRelease() {
        decorated.onRelease();
    }

    @Override
    public boolean contains(double x, double y) {
        return decorated.contains(x, y);
    }
}
