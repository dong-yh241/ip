package cipher.command;
import java.time.LocalDateTime;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Event;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

public class EventCommand extends Command {
    private final String args;

    public EventCommand(String args) {
        this.args = args == null ? "" : args.trim();
    }

    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        if (args.isEmpty()) {
            throw new CipherException("The description of an event cannot be empty.");
        }

        String[] p1 = args.split("\\s+/from\\s+", 2);
        if (p1.length < 2 || p1[0].trim().isEmpty() || p1[1].trim().isEmpty()) {
            throw new CipherException("Use: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
        }

        String desc = p1[0].trim();
        String[] p2 = p1[1].split("\\s+/to\\s+", 2);
        if (p2.length < 2 || p2[0].trim().isEmpty() || p2[1].trim().isEmpty()) {
            throw new CipherException("Use: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
        }

        LocalDateTime start = Storage.parseUserDateTimeOnly(p2[0].trim());
        LocalDateTime end = Storage.parseUserDateTimeOnly(p2[1].trim());

        Task t = new Event(desc, start, end);
        tasks.add(t);
        storage.save(tasks.snapshot());

        ui.showMessage("Got it. I've added this task:\n" + t.toDisplayString()
                + "\nNow you have " + tasks.size() + " tasks in the list.");
        return CommandResult.cont();
    }
}
