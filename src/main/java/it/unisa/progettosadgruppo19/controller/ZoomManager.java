/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.controller;

public class ZoomManager {
    private final double[] levels = {0.5, 0.75, 1.0, 1.5, 2.0};
    private int index = 2; // parte da 1.0x

    public double zoomIn() {
        if (index < levels.length - 1) index++;
        return levels[index];
    }

    public double zoomOut() {
        if (index > 0) index--;
        return levels[index];
    }

    public double getScale() {
        return levels[index];
    }
}
