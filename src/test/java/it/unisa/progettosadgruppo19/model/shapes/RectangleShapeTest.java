package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;

import static org.junit.jupiter.api.Assertions.*;

public class RectangleShapeTest {

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
    void testInitialProperties() {
        RectangleShape shape = new RectangleShape(10, 20, Color.BLACK);
        Rectangle r = (Rectangle) shape.getNode();

        assertEquals(10, r.getX());
        assertEquals(20, r.getY());
        assertEquals(0, r.getWidth());
        assertEquals(0, r.getHeight());
        assertEquals(Color.BLACK, r.getStroke());
        assertEquals(Color.TRANSPARENT, r.getFill());
    }

    @Test
    void testOnDragUpdatesSizeCorrectly() {
        RectangleShape shape = new RectangleShape(10, 20, Color.BLACK);
        shape.onDrag(30, 50); // dragging to bottom-right

        assertEquals(10, shape.getX());
        assertEquals(20, shape.getY());
        assertEquals(20, shape.getWidth());
        assertEquals(30, shape.getHeight());
    }

    @Test
    void testContainsPoint() {
        RectangleShape shape = new RectangleShape(10, 20, Color.BLACK);
        shape.onDrag(30, 50); // creates rectangle from (10,20) to (30,50)

        assertTrue(shape.contains(20, 30));
        assertFalse(shape.contains(5, 15));
    }
}
