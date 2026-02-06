package cipher.command;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.TaskList;
import cipher.ui.Ui;

public abstract class Command {
    public abstract CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException;

    public boolean isExit() {
        return false;
    }
}
