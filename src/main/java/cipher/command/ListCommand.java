package cipher.command;

import cipher.storage.Storage;
import cipher.task.TaskList;
import cipher.ui.Ui;

/**
 * Lists all tasks currently in the task list.
 * <p>
 * Expected format: {@code list}
 */
public class ListCommand extends Command {

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks   Task list to display
     * @param ui      UI used to show the list to the user
     * @param storage Storage (not used in this command)
     * @return A {@link CommandResult} indicating the app should continue
     */
    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.size() == 0) {
            ui.showMessage("Here are the tasks in your list:\n(Empty)");
            return CommandResult.cont();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Here are the tasks in your list:\n");
        for (int i = 1; i <= tasks.size(); i++) {
            sb.append(i).append(".").append(tasks.snapshot().get(i - 1).toDisplayString()).append("\n");
        }

        ui.showMessage(sb.toString().trim());
        return CommandResult.cont();
    }
}
