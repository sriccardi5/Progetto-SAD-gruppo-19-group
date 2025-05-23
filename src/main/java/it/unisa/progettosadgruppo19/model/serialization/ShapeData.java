package it.unisa.progettosadgruppo19.model.serialization;

import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 * DTO serializzabile che rappresenta una shape con tipo, posizione, dimensione
 * e colori di stroke/fill.
 */
public class ShapeData implements Serializable {

    private String type;
    private double x, y, width, height;
    private double strokeR, strokeG, strokeB, strokeA;
    private double fillR, fillG, fillB, fillA;

    /**
     * Costruisce un DTO a partire dai parametri forniti.
     */
    public ShapeData(String type, double x, double y, double width, double height, Color stroke, Color fill) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.strokeR = stroke.getRed();
        this.strokeG = stroke.getGreen();
        this.strokeB = stroke.getBlue();
        this.strokeA = stroke.getOpacity();

        this.fillR = fill.getRed();
        this.fillG = fill.getGreen();
        this.fillB = fill.getBlue();
        this.fillA = fill.getOpacity();
    }

    /**
     * Restituisce il tipo della shape.
     */
    public String getType() {
        return type;
    }

    /**
     * Restituisce la coordinata X.
     */
    public double getX() {
        return x;
    }

    /**
     * Restituisce la coordinata Y.
     */
    public double getY() {
        return y;
    }

    /**
     * Restituisce la larghezza.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Restituisce l'altezza.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Restituisce il colore di stroke ricostruito.
     */
    public Color getStroke() {
        return new Color(strokeR, strokeG, strokeB, strokeA);
    }

    /**
     * Restituisce il colore di fill ricostruito.
     */
    public Color getFill() {
        return new Color(fillR, fillG, fillB, fillA);
    }

    @Override
    public String toString() {
        return type + " at (" + x + ", " + y + ") size " + width + "x" + height;
    }
}
