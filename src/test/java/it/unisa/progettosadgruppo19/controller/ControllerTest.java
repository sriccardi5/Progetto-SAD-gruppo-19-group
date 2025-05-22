package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.adapter.ShapeAdapter;
import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    private static boolean toolkitInitialized = false;

    @BeforeAll
    static void initJFX() throws InterruptedException {
        if (!toolkitInitialized) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(() -> {
                    toolkitInitialized = true;
                    latch.countDown();
                });
                latch.await();
            } catch (IllegalStateException e) {
                // Toolkit già avviato da un altro test: lo ignoriamo
                toolkitInitialized = true;
            }
        }
    }

    @Test
    void testRebuildShapeRectangle() throws Exception {
        Controller controller = new Controller();
        ShapeData data = new ShapeData("RectangleShape", 10, 20, 30, 40, Color.BLACK, Color.YELLOW);

        Method method = Controller.class.getDeclaredMethod("rebuildShape", ShapeData.class);
        method.setAccessible(true);
        Shape shape = (Shape) method.invoke(controller, data);

        assertNotNull(shape);
        assertTrue(shape.getNode() instanceof javafx.scene.shape.Rectangle);
    }

    @Test
    void testSaveAndLoadDrawingData() throws Exception {
        RectangleShape shape = new RectangleShape(10, 20, Color.BLACK);
        shape.onDrag(30, 40);
        ShapeData data = new ShapeAdapter(shape).getShapeData();
        DrawingData original = new DrawingData(List.of(data));

        File temp = File.createTempFile("drawing", ".bin");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(temp))) {
            out.writeObject(original);
        }

        DrawingData loaded;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(temp))) {
            loaded = (DrawingData) in.readObject();
        }

        assertNotNull(loaded);
        assertEquals(1, loaded.getShapes().size());
        assertEquals("RectangleShape", loaded.getShapes().get(0).getType());
        assertEquals(10, loaded.getShapes().get(0).getX());
        assertEquals(20, loaded.getShapes().get(0).getY());

        temp.delete();
    }

    @Test
    void testShapeAdapterProducesCorrectShapeData() {
        RectangleShape shape = new RectangleShape(15, 25, Color.BLUE);
        shape.onDrag(45, 65);
        ShapeData data = new ShapeAdapter(shape).getShapeData();

        assertEquals("RectangleShape", data.getType());
        assertEquals(15, data.getX());
        assertEquals(25, data.getY());
        assertEquals(30, data.getWidth());
        assertEquals(40, data.getHeight());
    }

    @Test
    void testDeleteShape() throws Exception {
        Controller controller = new Controller();

        // Inietta un Pane fittizio
        Field paneField = Controller.class.getDeclaredField("drawingPane");
        paneField.setAccessible(true);
        Pane fakePane = new Pane();
        paneField.set(controller, fakePane);

        // Crea e registra una shape
        RectangleShape shape = new RectangleShape(10, 10, Color.BLACK);
        shape.onDrag(20, 20);
        fakePane.getChildren().add(shape.getNode());

        // Inserisci nella lista
        Field shapesField = Controller.class.getDeclaredField("currentShapes");
        shapesField.setAccessible(true);
        List<AbstractShape> currentShapes = (List<AbstractShape>) shapesField.get(controller);
        currentShapes.add(shape);

        // Imposta come selezionata
        Field selectedField = Controller.class.getDeclaredField("selectedShapeInstance");
        selectedField.setAccessible(true);
        selectedField.set(controller, shape);

        // Invoca onDelete
        Method deleteMethod = Controller.class.getDeclaredMethod("onDelete");
        deleteMethod.setAccessible(true);
        deleteMethod.invoke(controller);

        // Verifica che sia stata rimossa
        assertTrue(fakePane.getChildren().isEmpty());
        assertTrue(currentShapes.isEmpty());
    }
}
