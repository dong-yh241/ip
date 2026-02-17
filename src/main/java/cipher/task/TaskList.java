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
        this.tasks = new ArrayList<>(loaded == null ? List.of() : loaded);
        assert this.tasks != null : "tasks list should be initialized";
    }

    public void add(Task t) {
        assert t != null : "Task to add should not be null";
        tasks.add(t);
        assert tasks.get(tasks.size() - 1) == t : "Added task should be the last element";
    }

    public Task get(int oneBasedIndex) throws CipherException {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= tasks.size()) {
            throw new CipherException("Task number is out of range.");
        }
        assert idx >= 0 && idx < tasks.size() : "idx should be valid after range check";
        Task t = tasks.get(idx);
        assert t != null : "Stored task should not be null";
        return t;
    }

    public Task remove(int oneBasedIndex) throws CipherException {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= tasks.size()) {
            throw new CipherException("Task number is out of range.");
        }
        assert idx >= 0 && idx < tasks.size() : "idx should be valid after range check";
        Task removed = tasks.remove(idx);
        assert removed != null : "Removed task should not be null";
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
