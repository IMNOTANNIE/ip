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
            // Remove extra spaces so commands such as " list " are recognised.
            String command = ui.readCommand().trim();

            // Handle invalid user input without stopping the whole program.
            try {
                if (command.isEmpty()) {
                    // An empty command cannot be interpreted as a task or command.
                    throw new YukiException("No command was entered. Please enter a command.");
                }

                CommandType commandType = CommandType.fromCommand(command);

                switch (commandType) {
                    case BYE -> {
                        validateNoArguments(command, commandType);
                        // End the program when the user enters "bye".
                        ui.showGoodbye();
                        return;
                    }
                    case LIST -> {
                        validateNoArguments(command, commandType);
                        ui.showTaskList(taskList);
                    }
                    // Handle commands that change a task's completion status.
                    case MARK, UNMARK -> {
                        if (command.equals(commandType.getKeyword())) {
                            // Reject the command because it does not specify a task number.
                            throw new YukiException("The task number is missing.");
                        }

                        String taskNumberText = command.substring(
                                commandType.getKeyword().length());
                        int taskNumber = parseTaskNumber(taskNumberText);
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
                        if (command.equals(commandType.getKeyword())) {
                            // Reject the command because it does not specify a task number.
                            throw new YukiException("The task number is missing.");
                        }

                        int taskNumber = parseTaskNumber(
                                command.substring(commandType.getKeyword().length()));
                        validateTaskNumber(taskNumber, taskList.size());
                        Task removedTask = taskList.remove(taskNumber - 1);
                        storage.saveTasks(taskList);

                        ui.showTaskDeleted(removedTask, taskList.size());
                    }
                    case TODO, DEADLINE, EVENT -> {
                        // Add and store a new task.
                        Task newTask = createTask(command, commandType);
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
     * Creates the appropriate task object from an add-task command.
     *
     * @param command the command entered by the user
     * @return the task created from the command
     * @throws YukiException if the command is unknown or has an invalid format
     */
    public static Task createTask(String command) {
        return createTask(command, CommandType.fromCommand(command));
    }

    /**
     * Creates a task after its command type has already been identified.
     *
     * @param command the command entered by the user
     * @param commandType the type of task-creation command
     * @return the task created from the command
     * @throws YukiException if the command type cannot create a task or has an invalid format
     */
    private static Task createTask(String command, CommandType commandType) {
        if (commandType == CommandType.TODO) {
            String description = command.length() > 4
                    ? command.substring(4).trim()
                    : "";

            // Reject a todo command that does not include a description.
            if (description.isBlank()) {
                throw new YukiException("The todo description is missing. Please add a task description after 'todo'.");
            }

            return new ToDo(description);
        }

        if (commandType == CommandType.DEADLINE) {
            String content = command.length() > 8
                    ? command.substring(8).trim()
                    : "";
            String[] parts = content.split(" /by ", 2);

            // Reject the command if the /by separator or either value is missing.
            if (parts.length != 2
                    || parts[0].isBlank()
                    || parts[1].isBlank()) {
                throw new YukiException(
                        "The deadline needs a description, date and time. For example: "
                                + "deadline return book /by 26/8/2026 1800.");
            }

            return new Deadline(parts[0].trim(), DateTimeParser.parse(parts[1].trim()));
        }

        if (commandType == CommandType.EVENT) {
            String content = command.length() > 5
                    ? command.substring(5).trim()
                    : "";

            String[] firstPart = content.split(" /from ", 2);
            String description = firstPart[0];
            // Reject the command if the /from separator or either value is missing.
            if (firstPart.length != 2
                    || description.isBlank()
                    || firstPart[1].isBlank()) {
                throw new YukiException(
                        "The event needs a description, a start time and an end time. For example: "
                                + "event meeting /from 26/8/2026 1800 /to 26/8/2026 2000.");
            }

            String[] time = firstPart[1].split(" /to ", 2);
            // Reject the command if the /to separator or either time is missing.
            if (time.length != 2
                    || time[0].isBlank()
                    || time[1].isBlank()) {
                throw new YukiException(
                        "The event needs an end time.");
            }

            TaskDateTime from = DateTimeParser.parse(time[0].trim());
            TaskDateTime to = DateTimeParser.parse(time[1].trim());
            if (to.isBefore(from)) {
                throw new YukiException("The event's end time cannot be before its start time.");
            }

            return new Event(description.trim(), from, to);
        }

        // Reject commands that do not match any supported task format.
        throw new YukiException("That command isn't familiar to me.");
    }

    /**
     * Parses a task number entered after a command keyword.
     *
     * @param taskNumberText text that should contain a positive integer
     * @return the parsed task number
     * @throws YukiException if the text is not an integer
     */
    private static int parseTaskNumber(String taskNumberText) {
        try {
            return Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException e) {
            throw new YukiException("The task number must be a positive integer");
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

    /**
     * Checks that a command which takes no arguments contains only its keyword.
     *
     * @param command the complete command entered by the user
     * @param commandType the identified command type
     * @throws YukiException if additional text follows the command keyword
     */
    private static void validateNoArguments(String command, CommandType commandType) {
        if (!command.equals(commandType.getKeyword())) {
            throw new YukiException(
                    "..There’s no need to add anything else to the " + commandType.getKeyword() + " command.");
        }
    }
}
