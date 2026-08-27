package yuki.task;

/** Represents a task without a specific date or time. */
public class ToDo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the text describing the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns this to-do task in the format used in the task list.
     *
     * @return the formatted to-do task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
