package cipher.task;
import cipher.CipherException;

public abstract class Task {
    private final String description;
    private boolean isDone;

    protected Task(String description) throws CipherException {
        if (description == null || description.trim().isEmpty()) {
            throw new CipherException("Task description cannot be empty.");
        }
        this.description = description.trim();
        this.isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void markDone() {
        isDone = true;
    }

    public void unmarkDone() {
        isDone = false;
    }

    protected String statusIcon() {
        return isDone ? "X" : " ";
    }

    public abstract String toDisplayString();

    public abstract String toStorageString();
}
