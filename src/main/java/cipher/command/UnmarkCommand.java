package cipher.command;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

/**
 * Unmarks a task (sets it to "not done") using its 1-based index in the task list.
 * <p>
 * Expected format: {@code unmark <taskNumber>}
 * <p>
 * Example: {@code unmark 3}
 */
public class UnmarkCommand extends Command {
    private final String args;

    /**
     * Creates an UnmarkCommand with the raw argument string.
     *
     * @param args Task number string after {@code unmark}
     */
    public UnmarkCommand(String args) {
        this.args = args;
    }

    /**
     * Unmarks the specified task as not done, saves the updated list, and shows a confirmation message.
     *
     * @param tasks   Task list containing the target task
     * @param ui      UI used to show messages to the user
     * @param storage Storage used to persist tasks after unmarking
     * @return A {@link CommandResult} indicating the app should continue
     * @throws CipherException If the task number is missing/invalid or out of range
     */
    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        int idx = parseIndex(args);
        Task t = tasks.get(idx);
        t.unmarkDone();
        storage.save(tasks.snapshot());

        ui.showMessage("OK, I've marked this task as not done yet:\n" + idx + "." + t.toDisplayString());
        return CommandResult.cont();
    }

    /**
     * Parses a 1-based task index from the user input.
     *
     * @param raw Raw string containing the task number
     * @return Parsed 1-based task index
     * @throws CipherException If the input is empty or not a valid integer
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
