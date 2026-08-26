/**
 * Runs Yuki's command-line task manager.
 */
public class Yuki {
    /** Handles loading and saving tasks. */
    private final Storage storage;
    /** Contains the tasks managed during this Yuki session. */
    private final TaskList tasks;
    /** Handles all interaction with the user. */
    private final Ui ui;

    /**
     * Creates Yuki and loads saved tasks before the command loop begins.
     */
    public Yuki() {
        ui = new Ui();
        storage = new Storage();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (YukiException e) {
            // Keep the chatbot usable even if the saved file is damaged.
            loadedTasks = new TaskList();
            ui.showLoadingError(e.getMessage());
        }
        tasks = loadedTasks;
    }

    /**
     * Greets the user and processes commands until the user exits or input ends.
     */
    public void run() {
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
                        ui.showTaskList(tasks.getTasks());
                    }
                    // Handle commands that change a task's completion status.
                    case MARK, UNMARK -> {
                        int taskNumber = Parser.parseTaskNumber(command, commandType);
                        Task task;

                        if (commandType == CommandType.MARK) {
                            task = tasks.markTask(taskNumber);
                            ui.showTaskStatusChanged("It's done now... I think.", task);
                        } else {
                            task = tasks.unmarkTask(taskNumber);
                            ui.showTaskStatusChanged("The task is no longer marked as done:", task);
                        }

                        storage.saveTasks(tasks.getTasks());
                    }
                    case DELETE -> {
                        int taskNumber = Parser.parseTaskNumber(command, commandType);
                        Task removedTask = tasks.deleteTask(taskNumber);
                        storage.saveTasks(tasks.getTasks());

                        ui.showTaskDeleted(removedTask, tasks.size());
                    }
                    case TODO, DEADLINE, EVENT -> {
                        // Add and store a new task.
                        Task newTask = Parser.createTask(command, commandType);
                        tasks.addTask(newTask);
                        storage.saveTasks(tasks.getTasks());
                        ui.showTaskAdded(newTask, tasks.size());
                    }
                }
            } catch (YukiException e) {
                // Show the error and keep the program ready for the next command.
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Starts Yuki.
     *
     * @param args command-line arguments, which are not used by Yuki
     */
    public static void main(String[] args) {
        new Yuki().run();
    }

}
