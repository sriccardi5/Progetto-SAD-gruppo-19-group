// model/DrawingModel.java
package it.unisa.progettosadgruppo19.model;

import it.unisa.progettosadgruppo19.shapes.Shape;
import java.util.*;

public class DrawingModel {
    private final List<Shape> shapes = new ArrayList<>();
    private final List<DrawingModelListener> listeners = new ArrayList<>();

    public void addShape(Shape s) {
        shapes.add(s);
        notifyShapeAdded(s);
    }

    public List<Shape> getShapes() { return Collections.unmodifiableList(shapes); }

    public void addListener(DrawingModelListener l) { listeners.add(l); }
    public void removeListener(DrawingModelListener l) { listeners.remove(l); }

    private void notifyShapeAdded(Shape s) {
        for (DrawingModelListener l : listeners) {
            l.shapeAdded(s);
        }
    }
}
