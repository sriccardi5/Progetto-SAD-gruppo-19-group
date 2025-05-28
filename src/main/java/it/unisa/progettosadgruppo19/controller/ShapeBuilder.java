package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.factory.ConcreteShapeCreator;
import it.unisa.progettosadgruppo19.factory.ShapeCreator;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

/**
 * Classe responsabile della creazione delle shape con stroke e fill.
 */
public class ShapeBuilder {

    /**
     * Crea una nuova shape decorata a partire dal tipo e dalle coordinate.
     *
     * @param tipo tipo della shape ("Linea", "Rettangolo", "Ellisse")
     * @param x coordinata X iniziale
     * @param y coordinata Y iniziale
     * @param stroke colore del bordo
     * @param fill colore di riempimento
     * @return una Shape decorata e pronta all'uso
     */
    public Shape createDecoratedShape(String tipo, double x, double y, Color stroke, Color fill) {
        ShapeCreator creator = ConcreteShapeCreator.getCreator(tipo);
        AbstractShape baseShape = (AbstractShape) creator.createShape(x, y, stroke);

        // Applica i decorator
        Shape decorated = new StrokeDecorator(baseShape, stroke);
        decorated = new FillDecorator(decorated, fill);

        // Ritorna la shape decorata
        return decorated;
    }
}
