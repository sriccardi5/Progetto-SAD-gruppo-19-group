/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.adapter;

import java.io.Serializable;
import javafx.scene.paint.Color;

public class ShapeData implements Serializable {

    private String type;
    private double x, y, width, height;
    private double strokeR, strokeG, strokeB, strokeA;
    private double fillR, fillG, fillB, fillA;

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

    public String getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Color getStroke() {
        return new Color(strokeR, strokeG, strokeB, strokeA);
    }

    public Color getFill() {
        return new Color(fillR, fillG, fillB, fillA);
    }

    @Override
    public String toString() {
        return type + " at (" + x + ", " + y + ") size " + width + "x" + height;
    }
}
