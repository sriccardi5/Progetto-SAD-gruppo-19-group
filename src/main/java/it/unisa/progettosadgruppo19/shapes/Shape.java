// src/main/java/it/unisa/progettosadgruppo19/shapes/Shape.java
package it.unisa.progettosadgruppo19.shapes;

import javafx.scene.Node;

public interface Shape {
    Node getNode();
    void onDrag(double x, double y);
    void onRelease();
    
    boolean contains(double x, double y);
}
