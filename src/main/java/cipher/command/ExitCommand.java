package cipher.command;
import cipher.storage.Storage;
import cipher.task.TaskList;
import cipher.ui.Ui;

public class ExitCommand extends Command {
    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) {
        return CommandResult.exit();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
