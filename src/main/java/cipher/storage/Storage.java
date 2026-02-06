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

public class Storage {
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

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
            // Stretch goal: corrupted data -> ignore line rather than crash
            return null;
        }
    }

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

    private static LocalDateTime parseStoredDateTime(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        try {
            return LocalDateTime.parse(raw, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

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

    public static LocalDateTime parseUserDateTimeOnly(String raw) throws CipherException {
        try {
            return LocalDateTime.parse(raw, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new CipherException(
                    "Invalid date/time format. Use yyyy-MM-dd HHmm (e.g., 2019-12-02 1400).");
        }
    }

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

    public static String serializeEvent(Event e) {
        return "E | " + (e.isDone() ? 1 : 0) + " | " + e.getDescription()
                + " | " + e.getStart().format(INPUT_DATE_TIME)
                + " | " + e.getEnd().format(INPUT_DATE_TIME);
    }
}
