import java.util.ArrayList;

/**
 * Runs Yuki's command-line task manager.
 */
public class Yuki {
    /**
     * Starts Yuki and processes commands entered by the user.
     *
     * @param args command-line arguments, which are not used by Yuki
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage();
        // Store task objects in the order in which they were added.
        ArrayList<Task> taskList;
        try {
            taskList = storage.loadTasks();
        } catch (YukiException e) {
            // Keep the chatbot usable even if the saved file is damaged.
            taskList = new ArrayList<>();
            ui.showLoadingError(e.getMessage());
        }

        // Greet the user before starting the command loop.
        ui.showWelcome();

        // Continue reading commands until the input ends or the user enters "bye".
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            // Handle invalid user input without stopping the whole program.
            try {
                CommandType commandType = Parser.parseCommandType(command);

                switch (commandType) {
                    case BYE -> {
                        Parser.validateNoArguments(command, commandType);
                        // End the program when the user enters "bye".
                        ui.showGoodbye();
                        return;
                    }
                    case LIST -> {
                        Parser.validateNoArguments(command, commandType);
                        ui.showTaskList(taskList);
                    }
                    // Handle commands that change a task's completion status.
                    case MARK, UNMARK -> {
                        int taskNumber = Parser.parseTaskNumber(command, commandType);
                        validateTaskNumber(taskNumber, taskList.size());

                        Task task = taskList.get(taskNumber - 1);

                        if (commandType == CommandType.MARK) {
                            // Mark the selected task as completed.
                            task.markAsDone();
                            ui.showTaskStatusChanged("It's done now... I think.", task);
                        } else {
                            // Mark the selected task as not completed.
                            task.markAsNotDone();
                            ui.showTaskStatusChanged("The task is no longer marked as done:", task);
                        }

                        storage.saveTasks(taskList);
                    }
                    case DELETE -> {
                        int taskNumber = Parser.parseTaskNumber(command, commandType);
                        validateTaskNumber(taskNumber, taskList.size());
                        Task removedTask = taskList.remove(taskNumber - 1);
                        storage.saveTasks(taskList);

                        ui.showTaskDeleted(removedTask, taskList.size());
                    }
                    case TODO, DEADLINE, EVENT -> {
                        // Add and store a new task.
                        Task newTask = Parser.createTask(command, commandType);
                        taskList.add(newTask);
                        storage.saveTasks(taskList);
                        ui.showTaskAdded(newTask, taskList.size());
                    }
                }
            } catch (YukiException e) {
                // Show the error and keep the program ready for the next command.
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Checks that a task number refers to an existing task.
     *
     * @param taskNumber the task number entered by the user
     * @param taskCount the number of tasks currently stored
     * @throws YukiException if the task number is outside the task list
     */
    private static void validateTaskNumber(int taskNumber, int taskCount) {
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new YukiException(
                    "I couldn't find a task with that number. Please enter a number between 1 and "
                            + taskCount + ".");
        }
    }

}
