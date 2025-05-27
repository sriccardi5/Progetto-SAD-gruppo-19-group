package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.controller.ShapeFileManager;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Save implements Command {

    private final Stage stage;
    private final List<AbstractShape> shapes;
    private final ShapeFileManager fileManager;

    public Save(Stage stage, List<AbstractShape> shapes, ShapeFileManager fileManager) {
        this.stage = stage;
        this.shapes = shapes;
        this.fileManager = fileManager;
    }

    @Override
    public void execute() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salva disegno");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin"));
        chooser.setInitialFileName("drawing.bin");

        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            try {
                fileManager.saveToFile(shapes, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
