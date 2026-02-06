package cipher.command;
import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

public class DeleteCommand extends Command {
    private final String args;

    public DeleteCommand(String args) {
        this.args = args;
    }

    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        int idx = parseIndex(args);
        Task removed = tasks.remove(idx);
        storage.save(tasks.snapshot());

        ui.showMessage("Noted. I've removed this task:\n" + removed.toDisplayString()
                + "\nNow you have " + tasks.size() + " tasks in the list.");
        return CommandResult.cont();
    }

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
