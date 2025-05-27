package it.unisa.progettosadgruppo19.command;

public interface UndoableCommand extends Command {

    void undo();
}
