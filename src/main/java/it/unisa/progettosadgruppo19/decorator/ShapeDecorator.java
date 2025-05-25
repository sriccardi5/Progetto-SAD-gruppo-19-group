package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.Node;

/**
 * Decorator astratto che inoltra tutte le chiamate all'istanza decorata.
 */
public abstract class ShapeDecorator implements Shape{

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

    @Override
    public double getX() {
        return decorated.getX();
    }

    @Override
    public double getY() {
        return decorated.getY();
    }

    @Override
    public double getWidth() {
        return decorated.getWidth();
    }

    @Override
    public double getHeight() {
        return decorated.getHeight();
    }
    
    public Shape getWrapped() {
        return decorated;
    }

    @Override
    public Shape clone() {
        return recreateWith(decorated.clone()); // ricrea la struttura clonando in profondità
    }


    private Shape recreateChainWith(AbstractShape baseClone) {
        if (decorated instanceof ShapeDecorator) {
            ShapeDecorator innerDecorator = (ShapeDecorator) decorated;
            // Ricorsione: ricrea decorator più interni attorno alla forma base clonata
            Shape innerClone = innerDecorator.recreateChainWith(baseClone);
            // Ricrea questo decorator attorno al clone interno
            return recreateWith(innerClone);
        } else {
            // Decorator base è la forma concreta (già clonata)
            return baseClone;
        }
    }


    
    protected abstract Shape recreateWith(Shape newInner);
    
    private AbstractShape unwrapToAbstract(Shape shape) {
        while (shape instanceof ShapeDecorator) {
            shape = ((ShapeDecorator) shape).decorated;
        }
        return (AbstractShape) shape;
    }
    
    @Override
    public void moveBy(double dx, double dy) {
        decorated.moveBy(dx, dy);
    }

}
