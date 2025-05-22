package it.unisa.progettosadgruppo19.model.shapes;

import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

public class EllipseShapeTest {

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
        EllipseShape shape = new EllipseShape(10, 20, Color.BLUE);
        Ellipse e = (Ellipse) shape.getNode();

        assertEquals(10, e.getCenterX());
        assertEquals(20, e.getCenterY());
        assertEquals(0, e.getRadiusX());
        assertEquals(0, e.getRadiusY());
        assertEquals(Color.BLUE, e.getStroke());
        assertEquals(Color.TRANSPARENT, e.getFill());
    }

    @Test
    void testOnDragUpdatesSizeCorrectly() {
        EllipseShape shape = new EllipseShape(10, 20, Color.BLUE);
        shape.onDrag(50, 80);

        assertEquals(10, shape.getX());
        assertEquals(20, shape.getY());
        assertEquals(40, shape.getWidth());
        assertEquals(60, shape.getHeight());
    }

    @Test
    void testContainsPoint() {
        EllipseShape shape = new EllipseShape(10, 20, Color.BLUE);
        shape.onDrag(30, 60);

        assertTrue(shape.contains(20, 40));
        assertFalse(shape.contains(5, 5));
    }
}
