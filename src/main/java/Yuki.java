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
        TaskList taskList;
        try {
            taskList = new TaskList(storage.loadTasks());
        } catch (YukiException e) {
            // Keep the chatbot usable even if the saved file is damaged.
            taskList = new TaskList();
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
                        ui.showTaskList(taskList.getTasks());
                    }
                    // Handle commands that change a task's completion status.
                    case MARK, UNMARK -> {
                        int taskNumber = Parser.parseTaskNumber(command, commandType);
                        Task task;

                        if (commandType == CommandType.MARK) {
                            task = taskList.markTask(taskNumber);
                            ui.showTaskStatusChanged("It's done now... I think.", task);
                        } else {
                            task = taskList.unmarkTask(taskNumber);
                            ui.showTaskStatusChanged("The task is no longer marked as done:", task);
                        }

                        storage.saveTasks(taskList.getTasks());
                    }
                    case DELETE -> {
                        int taskNumber = Parser.parseTaskNumber(command, commandType);
                        Task removedTask = taskList.deleteTask(taskNumber);
                        storage.saveTasks(taskList.getTasks());

                        ui.showTaskDeleted(removedTask, taskList.size());
                    }
                    case TODO, DEADLINE, EVENT -> {
                        // Add and store a new task.
                        Task newTask = Parser.createTask(command, commandType);
                        taskList.addTask(newTask);
                        storage.saveTasks(taskList.getTasks());
                        ui.showTaskAdded(newTask, taskList.size());
                    }
                }
            } catch (YukiException e) {
                // Show the error and keep the program ready for the next command.
                ui.showError(e.getMessage());
            }
        }
    }

}
