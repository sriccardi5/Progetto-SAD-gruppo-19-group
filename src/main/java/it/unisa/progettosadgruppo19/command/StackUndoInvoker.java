package it.unisa.progettosadgruppo19.command;

import java.util.Stack;

/**
 * Invoker per comandi undoable che utilizza uno stack
 * per memorizzare i comandi eseguiti e supportare l'operazione di undo.
 */
public class StackUndoInvoker {

     /**
     * Stack dei comandi eseguiti che supportano l'undo.
     */
    private Stack<UndoableCommand> stack = new Stack<>();

    /**
     * Esegue il comando specificato: lo invoca e,
     * se implementa {@link UndoableCommand}, lo aggiunge allo stack.
     *
     * @param command il comando da eseguire; non può essere {@code null}
     */
    public void execute(Command command) {
        command.execute();
        if (command instanceof UndoableCommand uc) {
            stack.push(uc);
        }
    }

    /**
     * Annulla l'ultimo comando eseguito, se presente.
     */
    public void undo() {
        if (!stack.isEmpty()) {
            stack.pop().undo();
        }
    }
}
