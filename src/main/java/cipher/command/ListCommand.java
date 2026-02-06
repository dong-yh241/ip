package cipher.command;
import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.TaskList;
import cipher.ui.Ui;

public class ListCommand extends Command {
    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.size() == 0) {
            ui.showMessage("Here are the tasks in your list:\n(Empty)");
            return CommandResult.cont();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Here are the tasks in your list:\n");
        for (int i = 1; i <= tasks.size(); i++) {
            try {
                sb.append(i).append(".").append(tasks.get(i).toDisplayString()).append("\n");
            } catch (CipherException e) {
                // should not happen
            }
        }
        ui.showMessage(sb.toString().trim());
        return CommandResult.cont();
    }
}
