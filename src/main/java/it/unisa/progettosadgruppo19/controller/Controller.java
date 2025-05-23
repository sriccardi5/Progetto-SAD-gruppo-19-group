package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.ShapeDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private Pane drawingPane;
    @FXML
    private Button lineButton, rectButton, ellipseButton, saveButton, loadButton, deleteButton;
    @FXML
    private ColorPicker strokePicker, fillPicker;
    @FXML
    private Button copyButton;

    private final List<AbstractShape> currentShapes = new ArrayList<>();
    private MouseEventHandler mouseHandler;
    private ShapeFileManager fileManager = new ShapeFileManager();

    private String selectedShape = "Linea";

    @FXML
    public void initialize() {
mouseHandler = new MouseEventHandler(drawingPane, currentShapes);
System.out.println("[DEBUG] Gestionnaire de souris initialisé");
mouseHandler.setSelectedShape(selectedShape);
System.out.println("[DEBUG] Forme sélectionnée définie: " + selectedShape);
mouseHandler.setToolActive(true);
System.out.println("[DEBUG] Outil activé");

strokePicker.setValue(javafx.scene.paint.Color.BLACK);
System.out.println("[DEBUG] Couleur de contour définie: Noir");
fillPicker.setValue(javafx.scene.paint.Color.TRANSPARENT);
System.out.println("[DEBUG] Couleur de remplissage définie: Transparent");

saveButton.setOnAction(evt -> {
    System.out.println("[ACTION] Bouton Sauvegarder cliqué");
    onSave();
});
loadButton.setOnAction(evt -> {
    System.out.println("[ACTION] Bouton Charger cliqué"); 
    onLoad();
});
deleteButton.setOnAction(evt -> {
    System.out.println("[ACTION] Bouton Supprimer cliqué");
    onDelete();
});

lineButton.setOnAction(e -> {
    setTool("Linea");
    System.out.println("[OUTIL] Changé vers: Ligne");
});
rectButton.setOnAction(e -> {
    setTool("Rettangolo");
    System.out.println("[OUTIL] Changé vers: Rectangle"); 
});
ellipseButton.setOnAction(e -> {
    setTool("Ellisse");
    System.out.println("[OUTIL] Changé vers: Ellipse");
});

copyButton.setOnAction(evt -> {
    System.out.println("[ACTION] Bouton Copier cliqué");
    onCopy();
});

strokePicker.setOnAction(e -> {
    System.out.println("[EVENT] Changement de couleur de contour");
    Shape selected = mouseHandler.getSelectedShapeInstance();
    if (selected != null) {
        System.out.println("[DECORATOR] Application nouveau contour sur forme ID: " + selected.hashCode());
        Shape decorated = new StrokeDecorator(selected, strokePicker.getValue());
        int index = drawingPane.getChildren().indexOf(selected.getNode());
        drawingPane.getChildren().set(index, decorated.getNode());
        decorated.getNode().setUserData(decorated);
        mouseHandler.setSelectedShapeInstance(decorated);
    }
});

fillPicker.setOnAction(e -> {
    System.out.println("[EVENT] Changement de couleur de remplissage");
    Shape selected = mouseHandler.getSelectedShapeInstance();
    if (selected != null) {
        System.out.println("[DECORATOR] Application nouveau remplissage sur forme ID: " + selected.hashCode());
        Shape decorated = new FillDecorator(selected, fillPicker.getValue());
        int index = drawingPane.getChildren().indexOf(selected.getNode());
        drawingPane.getChildren().set(index, decorated.getNode());
        decorated.getNode().setUserData(decorated);
        mouseHandler.setSelectedShapeInstance(decorated);
    }
});

drawingPane.setOnMousePressed(e -> {
    System.out.println("[SOURIS] Pressé à X:" + e.getX() + " Y:" + e.getY());
    mouseHandler.onPressed(e);
});
drawingPane.setOnMouseDragged(e -> {
    System.out.println("[SOURIS] Drag à X:" + e.getX() + " Y:" + e.getY());
    mouseHandler.onDragged(e);
}); 
drawingPane.setOnMouseReleased(e -> {
    System.out.println("[SOURIS] Relâché à X:" + e.getX() + " Y:" + e.getY());
    mouseHandler.onReleased(e);
});
drawingPane.setOnMouseClicked(e -> {
    System.out.println("[SOURIS] Clic à X:" + e.getX() + " Y:" + e.getY());
    mouseHandler.onMouseClick(e);
});
    }

    private void setTool(String tipo) {
        selectedShape = tipo;
        mouseHandler.setSelectedShape(tipo);
        mouseHandler.setStrokeColor(strokePicker.getValue());
        mouseHandler.setFillColor(fillPicker.getValue());
        mouseHandler.setToolActive(true);

        // Deseleziona la figura attuale (se presente)
        if (mouseHandler.getSelectedShapeInstance() != null) {
            javafx.scene.shape.Shape fx = (javafx.scene.shape.Shape) mouseHandler.getSelectedShapeInstance().getNode();
            fx.setStrokeWidth(1);
            fx.setEffect(null);
            mouseHandler.setSelectedShapeInstance(null);
        }
    }

    private void onSave() {
        System.out.println("[DEBUG] onSave() called");

        Stage stage = (Stage) saveButton.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salva disegno");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin"));
        chooser.setInitialFileName("drawing.bin");

        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            System.out.println("[DEBUG] File selezionato per il salvataggio: " + file.getAbsolutePath());
            try {
                fileManager.saveToFile(currentShapes, file);
                System.out.println("[DEBUG] Salvataggio completato con successo.");
            } catch (IOException e) {
                System.out.println("[ERRORE] Errore durante il salvataggio: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("[DEBUG] Nessun file selezionato, salvataggio annullato.");
        }
    }

    private void onLoad() {
        Stage stage = (Stage) loadButton.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Carica disegno");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin"));

        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            try {
                DrawingData data = fileManager.loadFromFile(file);
                List<AbstractShape> shapes = fileManager.rebuildShapes(data);
                drawingPane.getChildren().clear();
                currentShapes.clear();
                for (AbstractShape s : shapes) {
                    currentShapes.add(s);
                    drawingPane.getChildren().add(s.getNode());
                }

                for (ShapeData s : data.getShapes()) {
                    System.out.println("  " + s.getType() + " @ " + s.getX() + "," + s.getY());
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void onDelete() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Node node = selected.getNode();
            drawingPane.getChildren().remove(node);
            currentShapes.removeIf(shape -> shape.getNode().equals(node));
            mouseHandler.setSelectedShapeInstance(null);
        }
    }

    private AbstractShape unwrapToAbstract(Shape shape) {
        while (shape instanceof ShapeDecorator) {
            shape = ((ShapeDecorator) shape).getWrapped();
        }
        if (shape instanceof AbstractShape) {
            return (AbstractShape) shape;
        } else {
            throw new IllegalStateException("Shape non è un AbstractShape dopo l'unwrapping");
        }
    }

    private void onCopy() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            // Clona la shape, incluso eventuali decorator
            Shape copied = selected.clone();

            // Sposta leggermente la copia per distinguere visivamente
            copied.moveBy(10, 10);

            // Aggiunge la nuova forma alla UI
            drawingPane.getChildren().add(copied.getNode());

            // Estrai l'AbstractShape base (senza decorator) per la logica
            AbstractShape baseShape = unwrapToAbstract(copied);
            currentShapes.add(baseShape);

            // Se vuoi selezionare subito la copia:
            mouseHandler.setSelectedShapeInstance(copied);

            // 🔍 Stampa di controllo
            System.out.println("Figura copiata: " + copied.getClass().getSimpleName()
                    + " | X: " + copied.getX()
                    + " | Y: " + copied.getY());
        } else {
            System.out.println("Nessuna figura selezionata da copiare.");
        }

    }
}
