package yuki.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yuki.exception.YukiException;

/**
 * Owns and manages Yuki's ordered collection of tasks.
 */
public class TaskList {
    /** Tasks in the same order in which the user added them. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks loaded from storage.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a user-facing task number.
     *
     * @param taskNumber one-based task number entered by the user.
     * @return the removed task.
     * @throws YukiException if the number does not identify an existing task.
     */
    public Task deleteTask(int taskNumber) {
        return tasks.remove(toListIndex(taskNumber));
    }

    /**
     * Marks and returns the task at a user-facing task number.
     *
     * @param taskNumber one-based task number entered by the user.
     * @return the task that was marked as done.
     * @throws YukiException if the number does not identify an existing task.
     */
    public Task markTask(int taskNumber) {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at a user-facing task number.
     *
     * @param taskNumber one-based task number entered by the user.
     * @return the task that was marked as not done.
     * @throws YukiException if the number does not identify an existing task.
     */
    public Task unmarkTask(int taskNumber) {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to search for in task descriptions
     * @return matching tasks in their original order
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            String normalizedDescription = task.getDescription().toLowerCase(Locale.ROOT);
            if (normalizedDescription.contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return List.copyOf(matchingTasks);
    }

    /**
     * Returns the task at a user-facing task number.
     *
     * @param taskNumber one-based task number entered by the user.
     * @return the selected task.
     * @throws YukiException if the number does not identify an existing task.
     */
    public Task getTask(int taskNumber) {
        return tasks.get(toListIndex(taskNumber));
    }

    /** Returns the number of tasks currently in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot for displaying or saving the tasks.
     *
     * @return the tasks in their current order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /** Validates a user-facing number and converts it to a zero-based list index. */
    private int toListIndex(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new YukiException(
                    "I couldn't find a task with that number. Please enter a number between 1 and "
                            + tasks.size() + ".");
        }
        return taskNumber - 1;
    }
}
