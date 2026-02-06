package cipher.command;
import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.task.Todo;
import cipher.ui.Ui;

public class TodoCommand extends Command {
    private final String desc;

    public TodoCommand(String args) {
        this.desc = args == null ? "" : args.trim();
    }

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
