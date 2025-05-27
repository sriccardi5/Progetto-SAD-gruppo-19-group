package it.unisa.progettosadgruppo19.adapter;

import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.*;

public class ShapeAdapterTest {

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
    void testShapeAdapterConvertsCorrectly() {
        RectangleShape rect = new RectangleShape(10, 20, Color.BLACK);
        rect.onDrag(30, 60); // crea una forma con width 20, height 40

        ShapeAdapter adapter = new ShapeAdapter(rect);
        ShapeData data = adapter.getShapeData();

        assertEquals("RectangleShape", data.getType());
        assertEquals(10, data.getX());
        assertEquals(20, data.getY());
        assertEquals(20, data.getWidth());
        assertEquals(40, data.getHeight());
    }
}
