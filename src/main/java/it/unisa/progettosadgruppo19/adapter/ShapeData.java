/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.adapter;

import java.io.Serializable;

public class ShapeData implements Serializable {
    private String type;
    private double x, y, width, height;

    public ShapeData(String type, double x, double y, double width, double height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    @Override
    public String toString() {
        return type + " at (" + x + ", " + y + ") size " + width + "x" + height;
    }
}
