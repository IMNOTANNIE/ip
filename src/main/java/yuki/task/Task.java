package yuki.task;

/**
 * Represents a task in Yuki's task list, including its description and completion status.
 */
public class Task {
    /** The text describing the task. */
    private final String description;
    /** Whether this task has been marked as completed. */
    private boolean isDone;

    /**
     * Creates a new task that is initially not done.
     *
     * @param description The text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return The value {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return The value {@code "X"} if the task is done; otherwise, a space.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }
    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's completion status and description in list format.
     *
     * @return The formatted task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
