package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class ShapeManagerTest {

    private Pane pane;
    private ArrayList<AbstractShape> shapeList;
    private ShapeManager manager;

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

    @BeforeEach
    public void setup() {
        pane = new Pane();
        shapeList = new ArrayList<>();
        manager = new ShapeManager(shapeList, pane);
    }

    @Test
    public void testAddAndRemoveShape() {
        AbstractShape shape = new RectangleShape(10, 10, Color.BLACK);
        manager.addShape(shape);
        assertEquals(1, manager.getCurrentShapes().size());

        manager.removeShape(shape);
        assertEquals(0, manager.getCurrentShapes().size());
    }

    @Test
    public void testClearAll() {
        AbstractShape shape = new RectangleShape(10, 10, Color.BLACK);
        manager.addShape(shape);
        manager.clearAll();
        assertTrue(manager.getCurrentShapes().isEmpty());
        assertTrue(manager.getDrawingPane().getChildren().isEmpty());
    }
}
