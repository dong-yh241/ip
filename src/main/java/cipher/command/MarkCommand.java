package cipher.command;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

/**
 * Marks a task as done using its 1-based index in the task list.
 * <p>
 * Expected format: {@code mark <taskNumber>}
 * <p>
 * Example: {@code mark 2}
 */
public class MarkCommand extends Command {
    private final String args;

    /**
     * Creates a MarkCommand with the raw argument string.
     *
     * @param args Task number string after {@code mark}
     */
    public MarkCommand(String args) {
        this.args = args;
    }

    /**
     * Marks the specified task as done, saves the updated list, and shows a confirmation message.
     *
     * @param tasks   Task list containing the target task
     * @param ui      UI used to show messages to the user
     * @param storage Storage used to persist tasks after marking
     * @return A {@link CommandResult} indicating the app should continue
     * @throws CipherException If the task number is missing/invalid or out of range
     */
    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        int idx = parseIndex(args);
        Task t = tasks.get(idx);
        t.markDone();
        storage.save(tasks.snapshot());

        ui.showMessage("Nice! I've marked this task as done:\n" + idx + "." + t.toDisplayString());
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
