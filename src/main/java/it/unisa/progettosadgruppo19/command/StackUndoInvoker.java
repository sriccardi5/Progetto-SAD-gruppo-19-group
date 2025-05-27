package it.unisa.progettosadgruppo19.command;

import java.util.Stack;

public class StackUndoInvoker {

    private Stack<UndoableCommand> stack = new Stack<>();

    public void execute(Command command) {
        command.execute();
        if (command instanceof UndoableCommand uc) {
            stack.push(uc);
        }
    }

    public void undo() {
        if (!stack.isEmpty()) {
            stack.pop().undo();
        }
    }
}
