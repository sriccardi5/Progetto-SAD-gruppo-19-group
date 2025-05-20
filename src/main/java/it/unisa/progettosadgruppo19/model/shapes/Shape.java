/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.Node;

public interface Shape {
    Node getNode();
    void onDrag(double x, double y);
    void onRelease();
    
    boolean contains(double x, double y);
}
