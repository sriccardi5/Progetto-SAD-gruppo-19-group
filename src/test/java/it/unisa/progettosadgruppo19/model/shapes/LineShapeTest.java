package it.unisa.progettosadgruppo19.model.shapes;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class LineShapeTest {

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @Test
    void testInitialProperties() {
        LineShape shape = new LineShape(5, 10, Color.RED);
        Line l = (Line) shape.getNode();

        assertEquals(5, l.getStartX());
        assertEquals(10, l.getStartY());
        assertEquals(5, l.getEndX());
        assertEquals(10, l.getEndY());
        assertEquals(Color.RED, l.getStroke());
        assertEquals(Color.TRANSPARENT, l.getFill());
    }

    @Test
    void testOnDragUpdatesLine() {
        LineShape shape = new LineShape(5, 10, Color.RED);
        shape.onDrag(25, 40);

        Line l = (Line) shape.getNode();
        assertEquals(25, l.getEndX());
        assertEquals(40, l.getEndY());

        assertEquals(5, shape.getX());
        assertEquals(10, shape.getY());
        assertEquals(20, shape.getWidth());
        assertEquals(30, shape.getHeight());
    }

    @Test
    void testContainsAlwaysFalse() {
        LineShape shape = new LineShape(0, 0, Color.RED);
        shape.onDrag(100, 100);
        // contains() su linee sottili è inaffidabile, ma testiamo comunque
        assertFalse(shape.contains(50, 51)); // fuori dal bordo sottile
    }
}
