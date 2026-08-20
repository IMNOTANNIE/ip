import java.util.ArrayList;
import java.util.Scanner;

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
        // Use this line to separate different sections of the program's output.
        String separatorLine = "❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄";

        // Display this banner when the program starts.
        String banner = "__   __     _    _ \n"
                + "\\ \\ / /   _| | _(_)\n"
                + " \\ V / | | | |/ / |\n"
                + "  | || |_| |   <| |\n"
                + "  |_| \\__,_|_|\\_\\_|\n";

        // Read commands entered by the user.
        Scanner scanner = new Scanner(System.in);
        // Store task objects in the order in which they were added.
        ArrayList<Task> taskList = new ArrayList<>();

        // Greet the user before starting the command loop.
        System.out.println(separatorLine);
        System.out.println(banner);
        System.out.println("...Hello. This is Yuki.");
        System.out.println("What do you need?");
        System.out.println(separatorLine);

        // Continue reading commands until the input ends or the user enters "bye".
        while (scanner.hasNextLine()) {
            // Remove extra spaces so commands such as " list " are recognised.
            String command = scanner.nextLine().trim();

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
                        System.out.println(separatorLine);
                        System.out.println("...Goodbye.");
                        System.out.println(separatorLine);
                        return;
                    }
                    case LIST -> {
                        validateNoArguments(command, commandType);
                        System.out.println(separatorLine);
                        System.out.println("Here... These are the tasks you have:");
                        for (int i = 0; i < taskList.size(); i++) {
                            System.out.println((i + 1) + "." + taskList.get(i));
                        }
                        System.out.println(separatorLine);
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
                        System.out.println(separatorLine);

                        if (commandType == CommandType.MARK) {
                            // Mark the selected task as completed.
                            task.markAsDone();
                            System.out.println("It's done now... I think.");
                        } else {
                            // Mark the selected task as not completed.
                            task.markAsNotDone();
                            System.out.println("The task is no longer marked as done:");
                        }

                        System.out.println(task);
                        System.out.println(separatorLine);
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

                        System.out.println(separatorLine);
                        System.out.println("Alright... I've removed it.");
                        System.out.println("  " + removedTask);
                        System.out.println("There are " + taskList.size() + " tasks now.");
                        System.out.println(separatorLine);
                    }
                    case TODO, DEADLINE, EVENT -> {
                        // Add and store a new task.
                        Task newTask = createTask(command, commandType);
                        taskList.add(newTask);
                        System.out.println(separatorLine);
                        System.out.println("Alright... I've added it.");
                        System.out.println("  " + newTask);
                        System.out.println("There are " + taskList.size() + " tasks now.");
                        System.out.println(separatorLine);
                    }
                }
            } catch (YukiException e) {
                // Show the error and keep the program ready for the next command.
                System.out.println(separatorLine);
                System.out.println("I couldn't process that. " + e.getMessage());
                System.out.println(separatorLine);
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
                        "The deadline needs both a description and a date. For example: deadline individual project /by Friday.");
            }

            return new Deadline(parts[0].trim(), parts[1].trim());
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
                        "The event needs a description, a start time and an end time. For example: event meeting /from 4pm /to 5pm.");
            }

            String[] time = firstPart[1].split(" /to ", 2);
            String from = time[0];
            // Reject the command if the /to separator or either time is missing.
            if (time.length != 2
                    || from.isBlank()
                    || time[1].isBlank()) {
                throw new YukiException(
                        "The event needs an end time. For example: event meeting /from 4pm /to 5pm.");
            }

            // Read the end time only after confirming that the /to part exists.
            String to = time[1];

            return new Event(
                    description.trim(),
                    from.trim(),
                    to.trim());
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
