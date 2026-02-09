package cipher.command;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.task.Todo;
import cipher.ui.Ui;

/**
 * Adds a {@link Todo} task to the task list.
 * <p>
 * Expected format: {@code todo <description>}
 * <p>
 * Example: {@code todo buy groceries}
 */
public class TodoCommand extends Command {
    private final String desc;

    /**
     * Creates a TodoCommand with the raw description string.
     *
     * @param args Description after {@code todo}
     */
    public TodoCommand(String args) {
        this.desc = args == null ? "" : args.trim();
    }

    /**
     * Adds a new {@link Todo} task, saves the updated list, and shows a confirmation message.
     *
     * @param tasks   Task list to add the todo into
     * @param ui      UI used to show messages to the user
     * @param storage Storage used to persist tasks after adding
     * @return A {@link CommandResult} indicating the app should continue
     * @throws CipherException If the description is empty
     */
    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        if (desc.isEmpty()) {
            throw new CipherException("The description of a todo cannot be empty.");
        }

        Task t = new Todo(desc);
        tasks.add(t);
        storage.save(tasks.snapshot());

        ui.showMessage("Got it. I've added this task:\n" + t.toDisplayString()
                + "\nNow you have " + tasks.size() + " tasks in the list.");
        return CommandResult.cont();
    }
}
