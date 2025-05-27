package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.controller.ShapeFileManager;
import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Load implements Command {

    private final Stage stage;
    private final List<AbstractShape> currentShapes;
    private final Pane drawingPane;
    private final ShapeFileManager fileManager;

    public Load(Stage stage, List<AbstractShape> currentShapes, Pane drawingPane, ShapeFileManager fileManager) {
        this.stage = stage;
        this.currentShapes = currentShapes;
        this.drawingPane = drawingPane;
        this.fileManager = fileManager;
    }

    @Override
    public void execute() {
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
}
