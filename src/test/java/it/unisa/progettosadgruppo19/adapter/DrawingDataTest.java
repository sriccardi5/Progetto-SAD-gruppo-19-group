package it.unisa.progettosadgruppo19.adapter;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DrawingDataTest {

    @Test
    void testSerializationAndDeserialization() throws Exception {
        ShapeData s = new ShapeData("RectangleShape", 5, 5, 15, 15);
        DrawingData original = new DrawingData(List.of(s));

        // Serializza su byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(original);
        out.close();

        // Deserializza
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        DrawingData restored = (DrawingData) in.readObject();

        assertEquals(1, restored.getShapes().size());
        assertEquals("RectangleShape", restored.getShapes().get(0).getType());
    }

}
