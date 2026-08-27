package yuki.command;

import yuki.storage.Storage;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Deletes a task selected by its user-facing task number.
 */
public class DeleteCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a command that deletes one task.
     *
     * @param taskNumber One-based task number to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Deletes, saves, and displays the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task removedTask = tasks.deleteTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
