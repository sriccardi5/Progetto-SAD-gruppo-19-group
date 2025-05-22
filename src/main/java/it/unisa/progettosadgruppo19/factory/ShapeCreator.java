package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

/**
 * Interfaccia funzionale per creare shape usando un metodo factory.
 */
@FunctionalInterface
public interface ShapeCreator {

    /**
     * Crea una nuova shape a partire da coordinate e colore.
     *
     * @param startX coordinata X iniziale
     * @param startY coordinata Y iniziale
     * @param stroke colore del contorno
     * @return nuova istanza di Shape
     */
    Shape create(double startX, double startY, Color stroke);
}
