package it.unisa.progettosadgruppo19.model.serialization;

import java.io.Serializable;
import java.util.List;

/**
 * DTO serializzabile che contiene la lista di ShapeData relative all'intero
 * disegno.
 */
public class DrawingData implements Serializable {

    private List<ShapeData> shapes;

    public DrawingData(List<ShapeData> shapes) {
        this.shapes = shapes;
    }

    /**
     * Restituisce la lista di ShapeData.
     *
     * @return lista di DTO delle shape
     */
    public List<ShapeData> getShapes() {
        return shapes;
    }
}
