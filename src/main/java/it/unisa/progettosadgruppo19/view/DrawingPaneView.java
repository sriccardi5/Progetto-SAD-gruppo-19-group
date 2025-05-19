// view/DrawingPaneView.java
package it.unisa.progettosadgruppo19.view;

import it.unisa.progettosadgruppo19.model.DrawingModelListener;
import it.unisa.progettosadgruppo19.shapes.Shape;
import javafx.scene.layout.Pane;

public class DrawingPaneView implements DrawingModelListener {
    private final Pane pane;

    public DrawingPaneView(Pane pane) {
        this.pane = pane;
    }

    @Override
    public void shapeAdded(Shape newShape) {
        pane.getChildren().add(newShape.getNode());
    }
}