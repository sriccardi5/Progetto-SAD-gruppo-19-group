package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Responsabile della gestione delle shape attive e del loro disegno sul canvas.
 */
public class ShapeManager {

    private final List<AbstractShape> currentShapes;
    private final Pane drawingPane;

    public ShapeManager(List<AbstractShape> currentShapes, Pane drawingPane) {
        this.currentShapes = currentShapes;
        this.drawingPane = drawingPane;
    }

    public void addShape(AbstractShape shape) {
        currentShapes.add(shape);
        drawingPane.getChildren().add(shape.getNode());
    }

    public void removeShape(AbstractShape shape) {
        currentShapes.remove(shape);
        drawingPane.getChildren().remove(shape.getNode());
    }

    public void clearAll() {
        currentShapes.clear();
        drawingPane.getChildren().clear();
    }

    public List<AbstractShape> getCurrentShapes() {
        return currentShapes;
    }

    public Pane getDrawingPane() {
        return drawingPane;
    }

    public AbstractShape findByNode(Node node) {
        return currentShapes.stream()
                .filter(s -> s.getNode().equals(node))
                .findFirst()
                .orElse(null);
    }
    
    System.out.println("PROVA");//PROVA
}
