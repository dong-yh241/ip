package cipher.command;
import cipher.CipherException;
import cipher.storage.Storage;
import cipher.task.Task;
import cipher.task.TaskList;
import cipher.ui.Ui;

public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String args) {
        this.keyword = args == null ? "" : args.trim();
    }

    @Override
    public CommandResult execute(TaskList tasks, Ui ui, Storage storage) throws CipherException {
        if (keyword.isEmpty()) {
            throw new CipherException("Use: find <keyword>");
        }

        String key = keyword.toLowerCase();
        StringBuilder sb = new StringBuilder();
        sb.append("Here are the matching tasks in your list:\n");

        int matches = 0;
        for (int i = 1; i <= tasks.size(); i++) {
            Task t = tasks.get(i);
            if (t.getDescription().toLowerCase().contains(key)) {
                sb.append(i).append(".").append(t.toDisplayString()).append("\n");
                matches++;
            }
        }

        if (matches == 0) {
            sb.append("(No matching tasks)");
        }

        ui.showMessage(sb.toString().trim());
        return CommandResult.cont();
    }
}
