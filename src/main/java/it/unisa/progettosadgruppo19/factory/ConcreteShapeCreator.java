// src/main/java/it/unisa/progettosadgruppo19/factory/ConcreteShapeCreator.java
package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.shapes.*;

public class ConcreteShapeCreator {
    public static ShapeCreator getCreator(String tipo) {
        return switch (tipo) {
            case "Linea"      -> LineShape::new;
            case "Rettangolo" -> RectangleShape::new;
            case "Ellisse"    -> EllipseShape::new;
            default           -> throw new IllegalArgumentException("Tipo non supportato");
        };
    }
}
