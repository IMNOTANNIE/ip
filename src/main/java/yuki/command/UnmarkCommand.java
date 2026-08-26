package yuki.command;

import yuki.storage.Storage;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Marks a selected task as not completed.
 */
public class UnmarkCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a command that unmarks one task.
     *
     * @param taskNumber one-based task number to unmark
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Unmarks, displays, and saves the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task = tasks.unmarkTask(taskNumber);
        ui.showTaskStatusChanged("The task is no longer marked as done:", task);
        storage.saveTasks(tasks.getTasks());
    }
}
