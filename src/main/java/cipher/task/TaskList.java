package cipher.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cipher.CipherException;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
        assert this.tasks != null : "tasks list should be initialized";
    }

    public TaskList(List<Task> loaded) {
        List<Task> safeLoaded = (loaded == null) ? List.of() : loaded;
        this.tasks = new ArrayList<>(safeLoaded);
        assert this.tasks != null : "tasks list should be initialized";
        assert this.tasks.stream().noneMatch(t -> t == null) : "loaded tasks must not contain null";
    }

    public void add(Task task) {
        assert task != null : "task to add must not be null";
        tasks.add(task);
        assert tasks.get(tasks.size() - 1) == task : "added task should be last element";
    }

    public Task get(int oneBasedIndex) throws CipherException {
        int index = oneBasedIndex - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new CipherException("Task number is out of range.");
        }

        Task task = tasks.get(index);
        assert task != null : "stored task must not be null";
        return task;
    }

    public Task remove(int oneBasedIndex) throws CipherException {
        int index = oneBasedIndex - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new CipherException("Task number is out of range.");
        }

        Task removed = tasks.remove(index);
        assert removed != null : "removed task must not be null";
        return removed;
    }

    public int size() {
        return tasks.size();
    }

    public List<Task> snapshot() {
        return Collections.unmodifiableList(tasks);
    }
}
