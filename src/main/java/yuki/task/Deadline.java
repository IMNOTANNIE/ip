package yuki.task;

import yuki.time.DateTimeParser;
import yuki.time.TaskDateTime;

/** Represents a task that must be completed by a specific date and time. */
public class Deadline extends Task {
    /** The date and time by which this task should be completed. */
    private final TaskDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description the text describing the task.
     * @param by the date and time by which the task should be completed.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the date and time by which this task should be completed.
     *
     * @return this deadline's due date and time.
     */
    public TaskDateTime getBy() {
        return by;
    }

    /**
     * Returns this deadline task in the format used in the task list.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
