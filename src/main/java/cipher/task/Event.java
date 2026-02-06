package cipher.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import cipher.CipherException;
import cipher.storage.Storage;

public class Event extends Task {
    private static final DateTimeFormatter OUT_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    private final LocalDateTime start;
    private final LocalDateTime end;

    public Event(String description, LocalDateTime start, LocalDateTime end) throws CipherException {
        super(description);
        if (start == null || end == null) {
            throw new CipherException("Event start/end cannot be empty.");
        }
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public String toDisplayString() {
        return "[E][" + statusIcon() + "] " + getDescription()
                + " (from: " + start.format(OUT_DATE_TIME)
                + " to: " + end.format(OUT_DATE_TIME) + ")";
    }

    @Override
    public String toStorageString() {
        return Storage.serializeEvent(this);
    }
}
