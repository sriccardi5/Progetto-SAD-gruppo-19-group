package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class ShapeBuilderTest {

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
    public void testCreateDecoratedShape() {
        ShapeBuilder builder = new ShapeBuilder();
        Shape shape = builder.createDecoratedShape("Rettangolo", 10, 10, Color.BLACK, Color.RED);
        assertNotNull(shape);
        assertNotNull(shape.getNode());
    }
}
