package yuki.time;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Holds either a parsed date, a parsed date-time, or the user's original text.
 */
public class TaskDateTime {
    private final LocalDate date;
    private final LocalDateTime dateTime;
    private final String text;

    private TaskDateTime(LocalDate date, LocalDateTime dateTime, String text) {
        this.date = date;
        this.dateTime = dateTime;
        this.text = text;
    }

    /** Creates a value containing both a date and a time. */
    public static TaskDateTime of(LocalDateTime dateTime) {
        return new TaskDateTime(null, dateTime, null);
    }

    /** Creates a value containing a date without a time. */
    public static TaskDateTime of(LocalDate date) {
        return new TaskDateTime(date, null, null);
    }

    /** Creates a value containing text that does not match a supported date format. */
    public static TaskDateTime of(String text) {
        return new TaskDateTime(null, null, text);
    }

    /** Returns whether this value contains both a date and a time. */
    public boolean hasDateTime() {
        return dateTime != null;
    }

    /** Returns whether this value contains a date without a time. */
    public boolean hasDateOnly() {
        return date != null;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getText() {
        return text;
    }

    /** Compares values of the same parsed type; text or mixed values are not ordered here. */
    public boolean isBefore(TaskDateTime other) {
        if (hasDateTime() && other.hasDateTime()) {
            return dateTime.isBefore(other.dateTime);
        }
        return hasDateOnly() && other.hasDateOnly() && date.isBefore(other.date);
    }
}
