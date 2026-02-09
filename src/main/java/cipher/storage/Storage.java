package cipher.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
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
 * <p>
 * The storage format is line-based, using " | " as a delimiter, e.g.
 * {@code T | 1 | read book}, {@code D | 0 | return book | 2019-12-02},
 * {@code E | 0 | meeting | 2019-12-02 1400 | 2019-12-02 1600}.
 */
public class Storage {
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final Path filePath;

    /**
     * Creates a Storage that reads/writes tasks to the given file path.
     *
     * @param filePath Path to the data file (e.g. {@code data/cipher.txt})
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the data file.
     * <p>
     * If the file does not exist (first run), an empty list is returned.
     * Corrupted lines are ignored.
     *
     * @return List of tasks loaded from disk
     * @throws CipherException If the file cannot be read
     */
    public List<Task> load() throws CipherException {
        if (!Files.exists(filePath)) {
            // first run: file not present is OK
            return List.of();
        }

        ArrayList<Task> loaded = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                Task t = parseLine(line);
                if (t != null) {
                    loaded.add(t);
                }
            }
        } catch (IOException e) {
            throw new CipherException("Could not read data file: " + e.getMessage());
        }
        return loaded;
    }

    /**
     * Saves the given tasks to the data file.
     * <p>
     * Creates parent directories if needed, and overwrites the existing file.
     *
     * @param tasks Tasks to be persisted
     * @throws CipherException If the file cannot be written
     */
    public void save(List<Task> tasks) throws CipherException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter bw = Files.newBufferedWriter(
                    filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Task t : tasks) {
                    bw.write(t.toStorageString());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new CipherException("Could not save data file: " + e.getMessage());
        }
    }

    /**
     * Parses a single storage line into a {@link Task}.
     * Returns {@code null} if the line is corrupted or unsupported.
     *
     * @param line One line from the data file
     * @return Parsed task, or {@code null} if the line is invalid
     */
    private Task parseLine(String line) {
        // expected formats:
        // T | 1 | read book
        // D | 0 | return book | 2019-12-02
        // D | 0 | submit tutorial | 2019-12-02 1800
        // E | 0 | meeting | 2019-12-02 1400 | 2019-12-02 1600
        try {
            String[] parts = line.split("\\s*\\|\\s*");
            if (parts.length < 3) {
                return null; // ignore corrupted line
            }

            String type = parts[0].trim();
            boolean done = "1".equals(parts[1].trim());
            String desc = parts[2].trim();

            Task t;
            switch (type) {
            case "T":
                t = new Todo(desc);
                break;
            case "D":
                if (parts.length < 4) return null;
                LocalDateTime by = parseStoredDateOrDateTime(parts[3].trim());
                t = new Deadline(desc, by);
                break;
            case "E":
                if (parts.length < 5) return null;
                LocalDateTime start = parseStoredDateTime(parts[3].trim());
                LocalDateTime end = parseStoredDateTime(parts[4].trim());
                t = new Event(desc, start, end);
                break;
            default:
                return null;
            }

            if (done) t.markDone();
            return t;
        } catch (Exception e) {
            // corrupted data -> ignore line rather than crash
            return null;
        }
    }

    /**
     * Parses a stored date or date-time string from the data file.
     * Returns {@code null} if parsing fails.
     *
     * @param raw Stored date string
     * @return Parsed {@link LocalDateTime} or {@code null}
     */
    private static LocalDateTime parseStoredDateOrDateTime(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        try {
            if (raw.contains(" ")) {
                return LocalDateTime.parse(raw, INPUT_DATE_TIME);
            }
            return LocalDate.parse(raw, INPUT_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a stored date-time string (yyyy-MM-dd HHmm) from the data file.
     * Returns {@code null} if parsing fails.
     *
     * @param raw Stored date-time string
     * @return Parsed {@link LocalDateTime} or {@code null}
     */
    private static LocalDateTime parseStoredDateTime(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        try {
            return LocalDateTime.parse(raw, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses user input that is either a date (yyyy-MM-dd) or a date-time (yyyy-MM-dd HHmm).
     *
     * @param raw User input string
     * @return Parsed {@link LocalDateTime}
     * @throws CipherException If the input format is invalid
     */
    public static LocalDateTime parseUserDateOrDateTime(String raw) throws CipherException {
        try {
            if (raw.contains(" ")) {
                return LocalDateTime.parse(raw, INPUT_DATE_TIME);
            }
            return LocalDate.parse(raw, INPUT_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new CipherException(
                    "Invalid date format. Use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2019-12-02 1800).");
        }
    }

    /**
     * Parses user input date-time only (yyyy-MM-dd HHmm).
     *
     * @param raw User input string
     * @return Parsed {@link LocalDateTime}
     * @throws CipherException If the input format is invalid
     */
    public static LocalDateTime parseUserDateTimeOnly(String raw) throws CipherException {
        try {
            return LocalDateTime.parse(raw, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new CipherException(
                    "Invalid date/time format. Use yyyy-MM-dd HHmm (e.g., 2019-12-02 1400).");
        }
    }

    /**
     * Converts a {@link Deadline} into the storage line format.
     *
     * @param d Deadline to serialize
     * @return Storage line string for the deadline
     */
    public static String serializeDeadline(Deadline d) {
        String stored;
        LocalDateTime by = d.getBy();
        if (by.getHour() == 0 && by.getMinute() == 0) {
            stored = by.toLocalDate().format(INPUT_DATE);
        } else {
            stored = by.format(INPUT_DATE_TIME);
        }
        return "D | " + (d.isDone() ? 1 : 0) + " | " + d.getDescription() + " | " + stored;
    }

    /**
     * Converts an {@link Event} into the storage line format.
     *
     * @param e Event to serialize
     * @return Storage line string for the event
     */
    public static String serializeEvent(Event e) {
        return "E | " + (e.isDone() ? 1 : 0) + " | " + e.getDescription()
                + " | " + e.getStart().format(INPUT_DATE_TIME)
                + " | " + e.getEnd().format(INPUT_DATE_TIME);
    }
}
