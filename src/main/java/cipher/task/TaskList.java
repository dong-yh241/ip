package cipher.task;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cipher.CipherException;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> loaded) {
        this.tasks = new ArrayList<>(loaded == null ? List.of() : loaded);
    }

    public void add(Task t) {
        tasks.add(t);
    }

    public Task get(int oneBasedIndex) throws CipherException {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= tasks.size()) {
            throw new CipherException("Task number is out of range.");
        }
        return tasks.get(idx);
    }

    public Task remove(int oneBasedIndex) throws CipherException {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= tasks.size()) {
            throw new CipherException("Task number is out of range.");
        }
        return tasks.remove(idx);
    }

    public int size() {
        return tasks.size();
    }

    public List<Task> snapshot() {
        return Collections.unmodifiableList(tasks);
    }
}
