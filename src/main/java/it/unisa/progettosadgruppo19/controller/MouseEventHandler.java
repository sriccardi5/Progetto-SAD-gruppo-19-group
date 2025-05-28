package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.factory.ConcreteShapeCreator;
import it.unisa.progettosadgruppo19.factory.ShapeCreator;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.command.receiver.ClipboardReceiver;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Gestisce gli eventi mouse sul canvas per creare, selezionare,
 * spostare, ridimensionare, decorare e incollare le Shape.
 * Implementa ClipboardReceiver per supportare copy/paste.
 */
public class MouseEventHandler implements ClipboardReceiver {

    private final Pane drawingPane;
    private final List<AbstractShape> currentShapes;

    private Shape selectedShapeInstance;
    private String selectedShape;
    private Color strokeColor;
    private Color fillColor;
    private Shape tempShape;

    private boolean toolActive = false;
    private ResizeMode currentResizeMode = ResizeMode.NONE;

    private double moveAnchorX, moveAnchorY;
    private double origX1, origY1, origX2, origY2;
    private double origX, origY;
    private double origCenterX, origCenterY;
    private double resizeAnchorX, resizeAnchorY;

    private static final double HANDLE_RADIUS = 6.0;
    private static final double ELLIPSE_BORDER_TOLERANCE = 6.0;

    private Shape shapeToPaste;
    private Shape clipboardBuffer;

    public enum ResizeMode {
        NONE,
        LINE_START, LINE_END,
        RECT_LEFT, RECT_RIGHT, RECT_TOP, RECT_BOTTOM,
        RECT_TOP_LEFT, RECT_TOP_RIGHT, RECT_BOTTOM_LEFT, RECT_BOTTOM_RIGHT,
        ELLIPSE_BORDER
    }

    /**
     * Costruisce un handler per il Pane e la lista di shape correnti.
     *
     * @param drawingPane   il Pane su cui disegnare; non può essere {@code null}.
     * @param currentShapes la lista di shape create; non può essere {@code null}.
     */
    public MouseEventHandler(Pane drawingPane, List<AbstractShape> currentShapes) {
        this.drawingPane = drawingPane;
        this.currentShapes = currentShapes;
        this.shapeToPaste = null;
    }

     /**
     * Seleziona il tipo di shape da disegnare (es. "Linea", "Rettangolo", ...).
     *
     * @param tipo nome del tipo di shape.
     */
    public void setSelectedShape(String selectedShape) {
        this.selectedShape = selectedShape;
    }

     /**
     * Imposta il colore del bordo delle nuove shape.
     *
     * @param strokeColor il colore di contorno.
     */
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

      /**
     * Imposta il colore di riempimento delle nuove shape.
     *
     * @param fillColor il colore di riempimento.
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Restituisce l'istanza di shape selezionata.
     *
     * @return shape selezionata, o {@code null}.
     */
    public void setSelectedShapeInstance(Shape shape) {
        this.selectedShapeInstance = shape;
    }

    /**
     * Salva una shape nel buffer per il paste.
     *
     * @param shape la shape da copiare; {@code null} per svuotare.
     */
    @Override
    public void setClipboard(Shape shape) {
        this.clipboardBuffer = shape;
    }

    /**
     * Restituisce la shape attualmente nel buffer.
     *
     * @return la shape copiata, o {@code null} se vuoto.
     */
    @Override
    public Shape getClipboard() {
        return clipboardBuffer;
    }

    public Shape getSelectedShapeInstance() {
        return selectedShapeInstance;
    }

    /**
     * Attiva o disattiva l'elaborazione degli eventi per creare/modificare shape.
     *
     * @param active {@code true} per abilitare, {@code false} per ignorare.
     */
    public void setToolActive(boolean active) {
        this.toolActive = active;
    }

    private boolean isNearLine(double px, double py, double x1, double y1, double x2, double y2, double tolerance) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1) <= tolerance;
        }

        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double projX = x1 + t * dx;
        double projY = y1 + t * dy;

        return Math.hypot(px - projX, py - projY) <= tolerance;
    }

    /**
     * Evento mouse pressed: inizia creazione, selezione o avvio di drag/resize.
     *
     * @param e evento di pressione del mouse.
     */
    public void onPressed(MouseEvent e) {
        double x = e.getX(), y = e.getY();

        if (selectedShapeInstance != null) {
            Node node = selectedShapeInstance.getNode();

            if (node instanceof Line line) {
                double sx = line.getStartX(), sy = line.getStartY();
                double ex = line.getEndX(), ey = line.getEndY();

                if (Math.hypot(x - sx, y - sy) < HANDLE_RADIUS) {
                    currentResizeMode = ResizeMode.LINE_START;
                    return;
                }
                if (Math.hypot(x - ex, y - ey) < HANDLE_RADIUS) {
                    currentResizeMode = ResizeMode.LINE_END;
                    return;
                }
            } else if (node instanceof Rectangle rect) {
                double rx = rect.getX(), ry = rect.getY();
                double w = rect.getWidth(), h = rect.getHeight();
                boolean left = Math.abs(x - rx) < HANDLE_RADIUS;
                boolean right = Math.abs(x - (rx + w)) < HANDLE_RADIUS;
                boolean top = Math.abs(y - ry) < HANDLE_RADIUS;
                boolean bottom = Math.abs(y - (ry + h)) < HANDLE_RADIUS;

                if (left && top) {
                    currentResizeMode = ResizeMode.RECT_TOP_LEFT;
                } else if (right && top) {
                    currentResizeMode = ResizeMode.RECT_TOP_RIGHT;
                } else if (left && bottom) {
                    currentResizeMode = ResizeMode.RECT_BOTTOM_LEFT;
                } else if (right && bottom) {
                    currentResizeMode = ResizeMode.RECT_BOTTOM_RIGHT;
                } else if (left) {
                    currentResizeMode = ResizeMode.RECT_LEFT;
                } else if (right) {
                    currentResizeMode = ResizeMode.RECT_RIGHT;
                } else if (top) {
                    currentResizeMode = ResizeMode.RECT_TOP;
                } else if (bottom) {
                    currentResizeMode = ResizeMode.RECT_BOTTOM;
                }

                if (currentResizeMode != ResizeMode.NONE) {
                    if (node instanceof Rectangle) {
                        Rectangle r = (Rectangle) node;

                        switch (currentResizeMode) {
                            case RECT_TOP_LEFT -> {
                                resizeAnchorX = r.getX() + r.getWidth();
                                resizeAnchorY = r.getY() + r.getHeight();
                            }
                            case RECT_TOP_RIGHT -> {
                                resizeAnchorX = r.getX();
                                resizeAnchorY = r.getY() + r.getHeight();
                            }
                            case RECT_BOTTOM_LEFT -> {
                                resizeAnchorX = r.getX() + r.getWidth();
                                resizeAnchorY = r.getY();
                            }
                            case RECT_BOTTOM_RIGHT -> {
                                resizeAnchorX = r.getX();
                                resizeAnchorY = r.getY();
                            }
                            case RECT_LEFT, RECT_RIGHT -> {
                                resizeAnchorX = (currentResizeMode == ResizeMode.RECT_LEFT
                                        ? r.getX() + r.getWidth()
                                        : r.getX());
                                resizeAnchorY = y;
                            }
                            case RECT_TOP, RECT_BOTTOM -> {
                                resizeAnchorX = x;
                                resizeAnchorY = (currentResizeMode == ResizeMode.RECT_TOP
                                        ? r.getY() + r.getHeight()
                                        : r.getY());
                            }
                            default -> {
                            }
                        }
                    } else if (node instanceof Ellipse ell) {
                        double cx = ell.getCenterX();
                        double cy = ell.getCenterY();
                        resizeAnchorX = 2 * cx - x;
                        resizeAnchorY = 2 * cy - y;
                    }
                    return;
                }

            } else if (node instanceof Ellipse ell) {
                double cx = ell.getCenterX(), cy = ell.getCenterY();
                double rx = ell.getRadiusX(), ry = ell.getRadiusY();
                double dx = (x - cx) / rx;
                double dy = (y - cy) / ry;
                double d = Math.hypot(dx, dy);
                if (Math.abs(d - 1) < ELLIPSE_BORDER_TOLERANCE / Math.max(rx, ry)) {
                    currentResizeMode = ResizeMode.ELLIPSE_BORDER;
                    return;
                }
            }
        }

        if (currentResizeMode != ResizeMode.NONE) {
            return;
        }

        if (selectedShapeInstance != null) {
            moveAnchorX = x;
            moveAnchorY = y;

            Node node = selectedShapeInstance.getNode();
            if (node instanceof Line line) {
                origX1 = line.getStartX();
                origY1 = line.getStartY();
                origX2 = line.getEndX();
                origY2 = line.getEndY();
            } else if (node instanceof Rectangle rect) {
                origX = rect.getX();
                origY = rect.getY();
            } else if (node instanceof Ellipse ell) {
                origCenterX = ell.getCenterX();
                origCenterY = ell.getCenterY();
            }
            return;
        }

        if (!toolActive || selectedShape == null) {
            return;
        }

        ShapeCreator creator = ConcreteShapeCreator.getCreator(selectedShape);
        AbstractShape baseShape = (AbstractShape) creator.createShape(x, y, strokeColor);
        currentShapes.add(baseShape);

        tempShape = new StrokeDecorator(baseShape, strokeColor);
        tempShape = new FillDecorator(tempShape, fillColor);
        tempShape.getNode().setUserData(tempShape);
        drawingPane.getChildren().add(tempShape.getNode());
    }

    /**
     * Evento mouse dragged: aggiorna posizione o dimensioni durante il drag/resize.
     *
     * @param e evento di trascinamento del mouse.
     */
    public void onDragged(MouseEvent e) {
        double x = Math.min(Math.max(0, e.getX()), drawingPane.getWidth());
        double y = Math.min(Math.max(0, e.getY()), drawingPane.getHeight());

        if (selectedShapeInstance != null && currentResizeMode != ResizeMode.NONE) {
            Node node = selectedShapeInstance.getNode();
            switch (currentResizeMode) {
                case LINE_START -> {
                    javafx.scene.shape.Line l = (javafx.scene.shape.Line) node;
                    l.setStartX(x);
                    l.setStartY(y);
                }
                case LINE_END -> {
                    javafx.scene.shape.Line l = (javafx.scene.shape.Line) node;
                    l.setEndX(x);
                    l.setEndY(y);
                }
                case RECT_TOP_LEFT, RECT_TOP_RIGHT, RECT_BOTTOM_LEFT, RECT_BOTTOM_RIGHT -> {
                    javafx.scene.shape.Rectangle r = (javafx.scene.shape.Rectangle) node;
                    double newX = Math.min(x, resizeAnchorX);
                    double newY = Math.min(y, resizeAnchorY);
                    double newW = Math.max(1, Math.abs(resizeAnchorX - x));
                    double newH = Math.max(1, Math.abs(resizeAnchorY - y));
                    r.setX(newX);
                    r.setY(newY);
                    r.setWidth(newW);
                    r.setHeight(newH);
                }
                case RECT_LEFT, RECT_RIGHT -> {
                    javafx.scene.shape.Rectangle r = (javafx.scene.shape.Rectangle) node;
                    double newX = Math.min(x, resizeAnchorX);
                    double newW = Math.max(1, Math.abs(resizeAnchorX - x));
                    r.setX(newX);
                    r.setWidth(newW);
                }
                case RECT_TOP, RECT_BOTTOM -> {
                    javafx.scene.shape.Rectangle r = (javafx.scene.shape.Rectangle) node;
                    double newY = Math.min(y, resizeAnchorY);
                    double newH = Math.max(1, Math.abs(resizeAnchorY - y));
                    r.setY(newY);
                    r.setHeight(newH);
                }
                case ELLIPSE_BORDER -> {
                    javafx.scene.shape.Ellipse ell = (javafx.scene.shape.Ellipse) node;
                    double cx = (x + resizeAnchorX) / 2;
                    double cy = (y + resizeAnchorY) / 2;
                    double radiusX = Math.max(1, Math.abs(x - resizeAnchorX) / 2);
                    double radiusY = Math.max(1, Math.abs(y - resizeAnchorY) / 2);
                    ell.setCenterX(cx);
                    ell.setCenterY(cy);
                    ell.setRadiusX(radiusX);
                    ell.setRadiusY(radiusY);
                }
                default -> {
                }
            }
            return;
        }

        if (selectedShapeInstance != null) {
            double dx = x - moveAnchorX;
            double dy = y - moveAnchorY;
            Node node = selectedShapeInstance.getNode();

            if (node instanceof javafx.scene.shape.Line line) {
                double newStartX = origX1 + dx;
                double newStartY = origY1 + dy;
                double newEndX = origX2 + dx;
                double newEndY = origY2 + dy;

                newStartX = Math.max(0, Math.min(newStartX, drawingPane.getWidth()));
                newStartY = Math.max(0, Math.min(newStartY, drawingPane.getHeight()));
                newEndX = Math.max(0, Math.min(newEndX, drawingPane.getWidth()));
                newEndY = Math.max(0, Math.min(newEndY, drawingPane.getHeight()));

                line.setStartX(newStartX);
                line.setStartY(newStartY);
                line.setEndX(newEndX);
                line.setEndY(newEndY);
            } else if (node instanceof javafx.scene.shape.Rectangle rect) {
                double newX = Math.max(0, Math.min(origX + dx, drawingPane.getWidth() - rect.getWidth()));
                double newY = Math.max(0, Math.min(origY + dy, drawingPane.getHeight() - rect.getHeight()));
                rect.setX(newX);
                rect.setY(newY);
            } else if (node instanceof javafx.scene.shape.Ellipse ell) {
                double radiusX = ell.getRadiusX();
                double radiusY = ell.getRadiusY();
                double newCX = Math.max(radiusX, Math.min(origCenterX + dx, drawingPane.getWidth() - radiusX));
                double newCY = Math.max(radiusY, Math.min(origCenterY + dy, drawingPane.getHeight() - radiusY));
                ell.setCenterX(newCX);
                ell.setCenterY(newCY);
            }
            return;
        }

        if (toolActive && selectedShape != null && tempShape != null) {
            tempShape.onDrag(x, y);
        }
    }

    /**
     * Evento mouse released: completa creazione, spostamento o resize della shape.
     *
     * @param e evento di rilascio del mouse.
     */
    public void onReleased(MouseEvent e) {
        if (currentResizeMode != ResizeMode.NONE) {
            currentResizeMode = ResizeMode.NONE;
            return;
        }

        if (selectedShapeInstance != null) {
            return;
        }

        if (!toolActive) {
            return;
        }

        if (tempShape != null) {
            tempShape.onRelease();
            tempShape = null;
        }
    }

    /**
     * Evento mouse click: seleziona shape esistenti o gestisce il paste-mode.
     *
     * @param e evento di click del mouse.
     */
    public void onMouseClick(MouseEvent e) {
        // gestione logica incolla
        if (shapeToPaste != null) {
            handlePaste(e.getX(), e.getY());
            shapeToPaste = null;
            return;
        }

        for (Node node : drawingPane.getChildren()) {
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                fxShape.setStrokeWidth(1);
                fxShape.setEffect(null);
            }
        }

        selectedShapeInstance = null;
        for (Node node : drawingPane.getChildren()) {
            if (!(node instanceof javafx.scene.shape.Shape fxShape)) {
                continue;
            }

            boolean isHit;

            if (fxShape instanceof Line line) {
                double sx = line.getStartX(), sy = line.getStartY();
                double ex = line.getEndX(), ey = line.getEndY();
                isHit = isNearLine(e.getX(), e.getY(), sx, sy, ex, ey, HANDLE_RADIUS);
            } else {
                isHit = fxShape.contains(e.getX(), e.getY());
            }

            if (!isHit) {
                continue;
            }

            Object userData = fxShape.getUserData();
            if (userData instanceof Shape shape) {
                selectedShapeInstance = shape;
                DropShadow ds = new DropShadow();
                ds.setRadius(10);
                ds.setColor(Color.BLACK);
                fxShape.setEffect(ds);
                break;
            }
        }
        System.out.println("[CLICK] Selezionato: " + (selectedShapeInstance != null ? selectedShapeInstance.getClass().getSimpleName() : "null"));

        if (selectedShapeInstance == null) {
            if (toolActive) {
                return;
            }

            toolActive = false;
        }

    }

    public void setShapeToPaste(Shape shapeToPaste) {
        this.shapeToPaste = shapeToPaste;
    }

    private void handlePaste(double x, double y) {
        shapeToPaste.setX(x);
        shapeToPaste.setY(y);

        // Imposta UserData sul nodo
        shapeToPaste.getNode().setUserData(shapeToPaste);

        // Aggiungi la figura alla UI
        drawingPane.getChildren().add(shapeToPaste.getNode());

        // Estrai l'AbstractShape per la logica
        AbstractShape baseShape = AbstractShape.unwrapToAbstract(shapeToPaste);
        currentShapes.add(baseShape);

        // (RI)registra il riferimento anche nel MouseEventHandler, se usa una lista o mappa
        setSelectedShapeInstance(shapeToPaste);

        System.out.println("Figura incollata: " + shapeToPaste.getClass().getSimpleName());
    }

    public void unselectShape() {
        if (selectedShapeInstance != null) {
            javafx.scene.shape.Shape fx = (javafx.scene.shape.Shape) selectedShapeInstance.getNode();
            fx.setStrokeWidth(1);
            fx.setEffect(null);
            selectedShapeInstance = null;
        }
    }

}
