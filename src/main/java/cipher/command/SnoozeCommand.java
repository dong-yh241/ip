package cipher.command;

import java.time.LocalDateTime;

import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Deadline;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

/**
 * Snoozes (postpones) a deadline task to a new date/time.
 *
 * Format: snooze <taskNumber> /to <yyyy-MM-dd> [HHmm]
 * Example: snooze 3 /to 2026-02-25
 * Example: snooze 3 /to 2026-02-25 1800
 */
public class SnoozeCommand extends Command {
    private final String args;

    public SnoozeCommand(String args) {
        this.args = args == null ? "" : args.trim();
    }

    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        if (args.isEmpty()) {
            throw new CipherException("Use: snooze <taskNumber> /to <yyyy-MM-dd> [HHmm]");
        }

        String[] parts = args.split("\\s+/to\\s+", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new CipherException("Use: snooze <taskNumber> /to <yyyy-MM-dd> [HHmm]");
        }

        int idx = parseIndex(parts[0].trim());
        LocalDateTime newBy = Storage.parseUserDateOrDateTime(parts[1].trim());

        Task t = tasks.get(idx);
        if (!(t instanceof Deadline)) {
            throw new CipherException("Only deadline tasks can be snoozed.");
        }

        Deadline d = (Deadline) t;
        d.setBy(newBy);
        storage.save(tasks.snapshot());

        ui.showMessage("OK, I've snoozed this deadline:\n" + idx + "." + d.toDisplayString());
        return CommandResult.cont();
    }

    private int parseIndex(String raw) throws CipherException {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new CipherException("Task number must be an integer.");
        }
    }
}