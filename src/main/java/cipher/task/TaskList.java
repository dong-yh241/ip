package cipher.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        assert tasks.size() >= 0 : "size should never be negative";
        return tasks.size();
    }

    public List<Task> snapshot() {
        List<Task> view = Collections.unmodifiableList(tasks);
        assert view != null : "snapshot view should not be null";
        return view;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     * Uses Java Streams (A-Streams).
     *
     * @param keyword keyword to search for (non-null, non-blank)
     * @return an unmodifiable list of matching tasks
     */
    public List<Task> findByKeyword(String keyword) {
        assert keyword != null : "keyword must not be null";

        String k = keyword.trim();
        assert !k.isEmpty() : "keyword must not be empty";

        return tasks.stream()
                .filter(t -> t != null)
                .filter(t -> t.getDescription() != null)
                .filter(t -> t.getDescription().contains(k))
                .collect(Collectors.toUnmodifiableList());
    }
}
