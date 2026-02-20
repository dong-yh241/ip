package cipher.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import cipher.CipherException;
import cipher.storage.Storage;

public class Deadline extends Task {
    private static final DateTimeFormatter OUT_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter OUT_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    private LocalDateTime by; // <-- removed final

    public Deadline(String description, LocalDateTime by) throws CipherException {
        super(description);
        if (by == null) {
            throw new CipherException("Deadline date/time cannot be empty.");
        }
        this.by = by;
    }

    public LocalDateTime getBy() {
        return by;
    }

    /** Reschedules this deadline to a new date/time. */
    public void setBy(LocalDateTime newBy) throws CipherException {
        if (newBy == null) {
            throw new CipherException("Deadline date/time cannot be empty.");
        }
        this.by = newBy;
    }

    private String formatBy() {
        if (by.getHour() == 0 && by.getMinute() == 0) {
            LocalDate d = by.toLocalDate();
            return d.format(OUT_DATE);
        }
        return by.format(OUT_DATE_TIME);
    }

    @Override
    public String toDisplayString() {
        return "[D][" + statusIcon() + "] " + getDescription() + " (by: " + formatBy() + ")";
    }

    @Override
    public String toStorageString() {
        return Storage.serializeDeadline(this);
    }
}
