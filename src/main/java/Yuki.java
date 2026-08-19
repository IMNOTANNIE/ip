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
        // Store up to 100 task objects in the order in which they were added.
        Task[] taskList = new Task[100];
        // Keep track of how many positions in taskList currently contain tasks.
        int taskCount = 0;

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

                // Print all stored tasks when the user enters "list".
                if (command.equals("list")) {
                    System.out.println(separatorLine);
                    System.out.println("Here... These are the tasks you have:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + taskList[i].toString());
                    }
                    System.out.println(separatorLine);
                } else if (command.equals("bye")) {
                    // End the program when the user enters "bye".
                    System.out.println(separatorLine);
                    System.out.println("...Goodbye.");
                    System.out.println(separatorLine);
                    break;
                // Handle commands that change a task's completion status.
                } else if (command.startsWith("mark ") || command.startsWith("unmark ") || command.equals("mark")  || command.equals("unmark")) {
                    if (command.equals("mark") || command.equals("unmark")) {
                        // Reject the command because it does not specify a task number.
                        throw new YukiException(
                                "The task number is missing.");
                    }

                    // Extract the user's task number after the mark/unmark keyword.
                    String taskNumberText = command.startsWith("mark ")
                            ? command.substring(5).trim()
                            : command.substring(7).trim();
                    int taskNumber;
                    try {
                        taskNumber = Integer.parseInt(taskNumberText);
                    } catch (NumberFormatException e) { // Reject the command if the task number is not an integer.
                    throw new YukiException("The task number must be a positive integer");
                    }

                    // Ensure the task number is in the valid range.
                    if (taskNumber < 1 || taskNumber > taskCount) {
                        throw new YukiException("I couldn't find a task with that number. " + "Please enter a number between 1 and " + taskCount + ".");
                    }

                    Task task = taskList[taskNumber - 1];
                    System.out.println(separatorLine);

                    if (command.startsWith("mark ")) {
                        // Mark the selected task as completed.
                        task.markAsDone();
                        System.out.println("It's done now... I think.");
                    } else {
                        // Mark the selected task as not completed.
                        task.markAsNotDone();
                        System.out.println("The task is no longer marked as done:");
                    }

                    System.out.println(task.toString());
                    System.out.println(separatorLine);
                } else {
                    // Add and store a new task.
                    Task newTask = createTask(command);
                    taskList[taskCount] = newTask;
                    taskCount++;
                    System.out.println(separatorLine);
                    System.out.println("Alright... I've added it.");
                    System.out.println("  " + newTask);
                    System.out.println("There are " + taskCount + " tasks now.");
                    System.out.println(separatorLine);
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
        if (command.startsWith("todo ") || command.equals("todo")) {
            String description = command.length() > 4
                    ? command.substring(4).trim()
                    : "";

            // Reject a todo command that does not include a description.
            if (description.isBlank()) {
                throw new YukiException("The todo description is missing. Please add a task description after 'todo'.");
            }

            return new ToDo(description);
        }

        if (command.startsWith("deadline ")  || command.equals("deadline")) {
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

        if (command.startsWith("event ") || command.equals("event")) {
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
}
