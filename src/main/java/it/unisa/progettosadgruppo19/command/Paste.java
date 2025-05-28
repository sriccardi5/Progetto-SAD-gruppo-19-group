package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.command.receiver.ClipboardReceiver;
import it.unisa.progettosadgruppo19.controller.MouseEventHandler;
import it.unisa.progettosadgruppo19.controller.ShapeManager;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando undoable per incollare una {@link Shape} salvata nel clipboard
 * in una posizione definita.
 */
public class Paste implements UndoableCommand {

    private final ClipboardReceiver clipboard;
    private final ShapeManagerReceiver shapeManager;
    private Shape pastedShape;
    private final double x, y;    

    /**
    * Costruisce un comando Paste che incolla la shape alle coordinate specificate.
    *
    * @param clipboard il receiver del clipboard da cui recuperare la shape; non può essere {@code null}
    * @param shapeManager il receiver responsabile della gestione delle shape; non può essere {@code null}
    * @param x coordinata X in cui incollare la shape
    * @param y coordinata Y in cui incollare la shape
    */
    public Paste(ClipboardReceiver clipboard, ShapeManagerReceiver shapeManager, double x, double y) {
        this.clipboard = clipboard;
        this.shapeManager = shapeManager;
        this.x = x;
        this.y = y;
    }
    
    /**
     * Esegue l'incollaggio: preleva la shape dal clipboard, ne imposta posizione e
     * la clona, la aggiunge al manager e memorizza il clone per l'undo.
     * Se il buffer è vuoto, stampa un messaggio.
     */
    @Override
    public void execute() {
        Shape shape = clipboard.getClipboard();
        if (shape != null) {
            System.out.println("[PASTE] Incollando figura: " + shape.getClass().getSimpleName() + " @ " + x + ", " + y);
            shape.setX(x);
            shape.setY(y);
            pastedShape = shape.clone();
            shapeManager.addShape(pastedShape);
            pastedShape.getNode().setUserData(pastedShape);
            System.out.println("[PASTE] Nodo incollato: " + pastedShape.getClass().getSimpleName()
                    + " @ (" + pastedShape.getX() + ", " + pastedShape.getY() + ") - UserData: "
                    + pastedShape.getNode().getUserData());

        } else {
            System.out.println("[PASTE] Buffer vuoto.");
        }
    }

    /**
     * Annulla l'incollaggio rimuovendo la shape precedentemente incollata, se presente.
     */
    @Override
    public void undo() {
        if (pastedShape != null) {
            shapeManager.removeShape(pastedShape);
        }
    }
}
