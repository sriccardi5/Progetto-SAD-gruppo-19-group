package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.adapter.ShapeAdapter;
import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.factory.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestisce il salvataggio e il caricamento delle shape su/da file binari.
 */
public class ShapeFileManager {

    public void saveToFile(List<AbstractShape> shapes, File file) throws IOException {
        List<ShapeData> dataList = shapes.stream()
                .map(shape -> new ShapeAdapter(shape).getShapeData())
                .collect(Collectors.toList());

        DrawingData drawingData = new DrawingData(dataList);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(drawingData);
        }
    }

    public DrawingData loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof DrawingData data) {
                return data;
            } else {
                throw new IOException("File does not contain valid DrawingData");
            }
        }
    }

    public List<AbstractShape> rebuildShapes(DrawingData drawingData) {
        List<AbstractShape> shapes = new ArrayList<>();

        for (ShapeData data : drawingData.getShapes()) {
            ShapeCreator creator = switch (data.getType()) {
                case "RectangleShape" ->
                    new RectangleShapeCreator();
                case "EllipseShape" ->
                    new EllipseShapeCreator();
                case "LineShape" ->
                    new LineShapeCreator();
                default ->
                    throw new IllegalArgumentException("Tipo non supportato: " + data.getType());
            };

            AbstractShape baseShape = (AbstractShape) creator.createShape(data.getX(), data.getY(), data.getStroke());

            baseShape.onDrag(data.getX() + data.getWidth(), data.getY() + data.getHeight());
            baseShape.onRelease();

            Shape decorated = new StrokeDecorator(baseShape, data.getStroke());
            decorated = new FillDecorator(decorated, data.getFill());
            decorated.getNode().setUserData(decorated);

            shapes.add(baseShape);
        }

        return shapes;
    }

}
