package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class MouseEventHandlerTest {

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

    private Pane pane;
    private ArrayList<AbstractShape> shapes;
    private MouseEventHandler handler;

    @BeforeEach
    public void setup() {
        pane = new Pane();
        shapes = new ArrayList<>();
        handler = new MouseEventHandler(pane, shapes);
    }

    @Test
    public void testInitialState() {
        assertNull(handler.getSelectedShapeInstance());
    }

    @Test
    public void testSetSelectedShape() {
        handler.setSelectedShape("Linea");
        handler.setToolActive(true);
        handler.setStrokeColor(Color.RED);
        handler.setFillColor(Color.BLUE);
        assertTrue(true); // solo verifica che non lancia eccezioni
    }
}
