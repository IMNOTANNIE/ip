/**
 * Marks a selected task as completed.
 */
public class MarkCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a command that marks one task.
     *
     * @param taskNumber one-based task number to mark
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Marks, displays, and saves the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task = tasks.markTask(taskNumber);
        ui.showTaskStatusChanged("It's done now... I think.", task);
        storage.saveTasks(tasks.getTasks());
    }
}
