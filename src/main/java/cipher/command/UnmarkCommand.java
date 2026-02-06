package cipher.command;
import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

public class UnmarkCommand extends Command {
    private final String args;

    public UnmarkCommand(String args) {
        this.args = args;
    }

    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        int idx = parseIndex(args);
        Task t = tasks.get(idx);
        t.unmarkDone();
        storage.save(tasks.snapshot());

        ui.showMessage("OK, I've marked this task as not done yet:\n" + idx + "." + t.toDisplayString());
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
