package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.LineShape;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import it.unisa.progettosadgruppo19.model.shapes.EllipseShape;

/**
 * Factory concreta che restituisce lo ShapeCreator corrispondente al tipo
 * testuale fornito.
 */
public class ConcreteShapeCreator {

    /**
     * Restituisce un ShapeCreator in base al nome del tipo.
     *
     * @param tipo "Linea", "Rettangolo" o "Ellisse"
     * @return riferimento al costruttore appropriato
     * @throws IllegalArgumentException se il tipo non è supportato
     */
    public static ShapeCreator getCreator(String tipo) {
        return switch (tipo) {
            case "Linea" ->
                new LineShapeCreator();
            case "Rettangolo" ->
                new RectangleShapeCreator();
            case "Ellisse" ->
                new EllipseShapeCreator();
            default ->
                throw new IllegalArgumentException("Tipo non supportato: " + tipo);
        };
    }
}
