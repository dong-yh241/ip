package cipher.command;

import java.time.LocalDateTime;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Deadline;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

/**
 * Adds a {@link Deadline} task to the task list.
 * <p>
 * Expected format:
 * {@code deadline <description> /by <yyyy-MM-dd> [HHmm]}
 * <p>
 * Examples:
 * {@code deadline return book /by 2019-12-02}
 * {@code deadline submit tutorial /by 2019-12-02 1800}
 */
public class DeadlineCommand extends Command {
    private final String args;

    /**
     * Creates a DeadlineCommand with the raw argument string.
     *
     * @param args Arguments after the keyword {@code deadline}
     */
    public DeadlineCommand(String args) {
        this.args = args == null ? "" : args.trim();
    }

    /**
     * Parses the deadline command arguments, adds a deadline task, saves to storage,
     * and shows a confirmation message via the UI.
     *
     * @param tasks   Task list to add the new deadline into
     * @param ui      UI used to show feedback to the user
     * @param storage Storage used to persist the updated task list
     * @return A {@link CommandResult} indicating the app should continue
     * @throws CipherException If the input format is invalid or saving fails
     */
    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        if (args.isEmpty()) {
            throw new CipherException("The description of a deadline cannot be empty.");
        }

        String[] parts = args.split("\\s+/by\\s+", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new CipherException("Use: deadline <description> /by <yyyy-MM-dd> [HHmm]");
        }

        String desc = parts[0].trim();
        LocalDateTime by = Storage.parseUserDateOrDateTime(parts[1].trim());

        Task t = new Deadline(desc, by);
        tasks.add(t);
        storage.save(tasks.snapshot());

        ui.showMessage("Got it. I've added this task:\n" + t.toDisplayString()
                + "\nNow you have " + tasks.size() + " tasks in the list.");
        return CommandResult.cont();
    }
}
