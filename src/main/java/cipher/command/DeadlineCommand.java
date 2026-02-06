package cipher.command;
import java.time.LocalDateTime;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Deadline;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

public class DeadlineCommand extends Command {
    private final String args;

    public DeadlineCommand(String args) {
        this.args = args == null ? "" : args.trim();
    }

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
