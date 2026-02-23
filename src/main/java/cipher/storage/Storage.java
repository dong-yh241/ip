package cipher.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import cipher.CipherException;
import cipher.task.Deadline;
import cipher.task.Event;
import cipher.task.Task;
import cipher.task.Todo;

/**
 * Handles loading tasks from and saving tasks to a local data file.
 * Storage format is line-based using " | " as a delimiter.
 */
public class Storage {
    private static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final Path filePath;

    public Storage(String filePath) {
        assert filePath != null : "filePath must not be null";
        assert !filePath.isBlank() : "filePath must not be blank";
        this.filePath = Paths.get(filePath);
    }

    public List<Task> load() throws CipherException {
        assert filePath != null : "filePath must not be null";

        if (!Files.exists(filePath)) {
            return List.of();
        }

        ArrayList<Task> loadedTasks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                Task task = parseLine(trimmed);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new CipherException("Could not read data file: " + e.getMessage());
        }

        assert loadedTasks.stream().noneMatch(t -> t == null)
                : "loadedTasks must not contain null tasks";
        return loadedTasks;
    }

    public void save(List<Task> tasks) throws CipherException {
        assert tasks != null : "tasks must not be null";
        assert tasks.stream().noneMatch(t -> t == null) : "tasks must not contain null elements";

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(
                    filePath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                for (Task task : tasks) {
                    writer.write(task.toStorageString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new CipherException("Could not save data file: " + e.getMessage());
        }
    }

    /**
     * Parses one line in the storage file. Returns null if the line is invalid/corrupted.
     */
    private Task parseLine(String line) {
        assert line != null : "line must not be null";
        assert !line.isBlank() : "line must not be blank";

        try {
            String[] parts = line.split("\\s*\\|\\s*");
            if (parts.length < 3) {
                return null;
            }

            String type = parts[0].trim();
            String doneFlag = parts[1].trim();
            String description = parts[2].trim();

            assert !type.isBlank() : "type must not be blank";
            assert !description.isBlank() : "description must not be blank";

            boolean isDone = "1".equals(doneFlag);

            Task task = buildTaskFromParts(type, description, parts);
            if (task == null) {
                return null;
            }

            if (isDone) {
                task.markDone();
            }

            assert task != null : "task must not be null after parsing";
            return task;

        } catch (Exception e) {
            return null; // ignore corrupted lines
        }
    }

    private Task buildTaskFromParts(String type, String description, String[] parts) {
        switch (type) {
        case "T":
            return new Todo(description);

        case "D":
            if (parts.length < 4) {
                return null;
            }
            LocalDateTime by = parseStoredDateOrDateTime(parts[3].trim());
            return (by == null) ? null : new Deadline(description, by);

        case "E":
            if (parts.length < 5) {
                return null;
            }
            LocalDateTime start = parseStoredDateTime(parts[3].trim());
            LocalDateTime end = parseStoredDateTime(parts[4].trim());
            if (start == null || end == null) {
                return null;
            }
            return new Event(description, start, end);

        default:
            return null;
        }
    }

    private static LocalDateTime parseStoredDateOrDateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            if (raw.contains(" ")) {
                return LocalDateTime.parse(raw, INPUT_DATE_TIME);
            }
            return LocalDate.parse(raw, INPUT_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalDateTime parseStoredDateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(raw, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDateTime parseUserDateOrDateTime(String raw) throws CipherException {
        assert raw != null : "user date input must not be null";

        try {
            if (raw.contains(" ")) {
                return LocalDateTime.parse(raw, INPUT_DATE_TIME);
            }
            return LocalDate.parse(raw, INPUT_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new CipherException("Invalid date format. Use yyyy-MM-dd or yyyy-MM-dd HHmm.");
        }
    }

    public static LocalDateTime parseUserDateTimeOnly(String raw) throws CipherException {
        assert raw != null : "user date-time input must not be null";

        try {
            return LocalDateTime.parse(raw, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new CipherException("Invalid date/time format. Use yyyy-MM-dd HHmm.");
        }
    }

    public static String serializeDeadline(Deadline deadline) {
        assert deadline != null : "deadline must not be null";
        assert deadline.getBy() != null : "'by' must not be null";

        LocalDateTime by = deadline.getBy();
        String stored = (by.getHour() == 0 && by.getMinute() == 0)
                ? by.toLocalDate().format(INPUT_DATE)
                : by.format(INPUT_DATE_TIME);

        return "D | " + (deadline.isDone() ? 1 : 0)
                + " | " + deadline.getDescription()
                + " | " + stored;
    }

    public static String serializeEvent(Event event) {
        assert event != null : "event must not be null";
        assert event.getStart() != null : "event start must not be null";
        assert event.getEnd() != null : "event end must not be null";

        return "E | " + (event.isDone() ? 1 : 0)
                + " | " + event.getDescription()
                + " | " + event.getStart().format(INPUT_DATE_TIME)
                + " | " + event.getEnd().format(INPUT_DATE_TIME);
    }
}
