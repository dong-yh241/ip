package cipher.task;
import cipher.CipherException;

public class Todo extends Task {
    public Todo(String description) throws CipherException {
        super(description);
    }

    @Override
    public String toDisplayString() {
        return "[T][" + statusIcon() + "] " + getDescription();
    }

    @Override
    public String toStorageString() {
        return "T | " + (isDone() ? 1 : 0) + " | " + getDescription();
    }
}
