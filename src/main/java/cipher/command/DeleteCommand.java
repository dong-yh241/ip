package cipher.command;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

/**
 * Deletes a task from the task list by its (1-based) index.
 * <p>
 * Expected format: {@code delete <taskNumber>}
 * <p>
 * Example: {@code delete 3}
 */
public class DeleteCommand extends Command {
    private final String args;

    /**
     * Creates a DeleteCommand with the raw argument string.
     *
     * @param args Task number string after the keyword {@code delete}
     */
    public DeleteCommand(String args) {
        this.args = args;
    }

    /**
     * Removes the specified task from the task list, saves the updated list,
     * and shows a confirmation message.
     *
     * @param tasks   Task list to remove the task from
     * @param ui      UI used to show feedback to the user
     * @param storage Storage used to persist the updated task list
     * @return A {@link CommandResult} indicating the app should continue
     * @throws CipherException If the task number is missing/invalid, or saving fails
     */
    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        int idx = parseIndex(args);
        Task removed = tasks.remove(idx);
        storage.save(tasks.snapshot());

        ui.showMessage("Noted. I've removed this task:\n" + removed.toDisplayString()
                + "\nNow you have " + tasks.size() + " tasks in the list.");
        return CommandResult.cont();
    }

    /**
     * Parses the raw task number string into an integer index (1-based).
     *
     * @param raw Raw task number string
     * @return Parsed task number as an integer (1-based)
     * @throws CipherException If the value is missing or not an integer
     */
    private int parseIndex(String raw) throws CipherException {
        if (raw == null || raw.trim().isEmpty()) {
            throw new CipherException("Task number must be an integer.");
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new CipherException("Task number must be an integer.");
        }
    }
}
