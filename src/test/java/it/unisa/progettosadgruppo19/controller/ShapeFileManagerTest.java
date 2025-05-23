package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShapeFileManagerTest {

    @Test
    void testSaveAndLoad() throws IOException, ClassNotFoundException {
        ShapeFileManager manager = new ShapeFileManager();
        File tempFile = File.createTempFile("drawing", ".bin");
        tempFile.deleteOnExit();

        AbstractShape original = new RectangleShape(10, 20, Color.BLACK);
        original.onDrag(60, 70); // per dare dimensioni
        original.onRelease();

        List<AbstractShape> shapes = List.of(original); // ✅ SOLO AbstractShape, non decorator
        manager.saveToFile(shapes, tempFile);

        DrawingData data = manager.loadFromFile(tempFile);
        List<AbstractShape> loaded = manager.rebuildShapes(data);

        assertEquals(1, loaded.size());
        AbstractShape result = loaded.get(0);
        assertEquals(original.getX(), result.getX());
        assertEquals(original.getY(), result.getY());
        assertEquals(original.getWidth(), result.getWidth());
        assertEquals(original.getHeight(), result.getHeight());
    }

}
