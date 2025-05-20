package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.*;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.*;

public class ConcreteShapeCreatorTest {

    @BeforeAll
    static void initFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    //"Linea" -> LineShape
    @Test
    void testLineaCreatorReturnsLineShape() {
        ShapeCreator creator = ConcreteShapeCreator.getCreator("Linea");
        Shape shape = creator.create(0, 0, Color.BLACK);
        assertTrue(shape instanceof LineShape);
    }

    //"Rettangolo" -> RectangleShape
    @Test
    void testRettangoloCreatorReturnsRectangleShape() {
        ShapeCreator creator = ConcreteShapeCreator.getCreator("Rettangolo");
        Shape shape = creator.create(0, 0, Color.BLACK);
        assertTrue(shape instanceof RectangleShape);
    }

    //"Ellisse" -> EllipseShape
    @Test
    void testEllisseCreatorReturnsEllipseShape() {
        ShapeCreator creator = ConcreteShapeCreator.getCreator("Ellisse");
        Shape shape = creator.create(0, 0, Color.BLACK);
        assertTrue(shape instanceof EllipseShape);
    }

    //Tipo non valido -> eccezione
    @Test
    void testUnknownTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConcreteShapeCreator.getCreator("Triangolo");
        });
    }
}
