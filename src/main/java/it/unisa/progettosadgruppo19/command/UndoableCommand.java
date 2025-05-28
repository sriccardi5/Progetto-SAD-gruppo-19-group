package it.unisa.progettosadgruppo19.command;

/**
 * Estende {@link Command} aggiungendo il supporto all'operazione di undo.
 */
public interface UndoableCommand extends Command {

    /**
     * Annulla l'azione precedentemente eseguita dal comando.
     */
    void undo();
}
