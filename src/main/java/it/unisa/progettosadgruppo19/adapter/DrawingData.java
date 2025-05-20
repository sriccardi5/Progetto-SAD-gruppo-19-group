/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.adapter;

import java.io.Serializable;
import java.util.List;

public class DrawingData implements Serializable {
    private List<ShapeData> shapes;

    public DrawingData(List<ShapeData> shapes) {
        this.shapes = shapes;
    }

    public List<ShapeData> getShapes() {
        return shapes;
    }
} 
