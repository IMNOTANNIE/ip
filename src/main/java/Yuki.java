import java.util.Scanner;

public class Yuki {
    public static void main(String[] args) {
        String separatorLine = "❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄";
        String banner = "__   __     _    _ \n"
                + "\\ \\ / /   _| | _(_)\n"
                + " \\ V / | | | |/ / |\n"
                + "  | || |_| |   <| |\n"
                + "  |_| \\__,_|_|\\_\\_|\n";

        Scanner scanner = new Scanner(System.in);
        // A list to store tasks
        Task[] taskList = new Task[100];
        // A counter to record the number of tasks
        int taskCount = 0;

        // Greet the user
        System.out.println(separatorLine);
        System.out.println(banner);
        System.out.println("...Hello. This is Yuki.");
        System.out.println("What do you need?");
        System.out.println(separatorLine);

        while (true) {
            // Let the user enter a command and record the command
            String command = scanner.nextLine();
            // Print the list when the user types "list"
            if (command.equals("list")) {
                System.out.println(separatorLine);
                System.out.println("Here... These are the tasks you have:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + taskList[i].toString());
                }
                System.out.println(separatorLine);
            } else if (command.equals("bye")) {
                // Exit when the user types "bye"
                System.out.println(separatorLine);
                System.out.println("...Goodbye.");
                System.out.println(separatorLine);
                break;
            } else if (command.startsWith("mark ") || command.startsWith("unmark ")) {
                String taskNumberText;
                if (command.startsWith("mark ")) {
                    taskNumberText = command.substring(5);
                } else {
                    taskNumberText = command.substring(7);
                }
                // Extract the task number from the input command
                int taskNumber = Integer.parseInt(taskNumberText);
                Task task = taskList[taskNumber - 1];

                System.out.println(separatorLine);

                if (command.startsWith("mark ")) {
                    // mark the task as done
                    task.markAsDone();
                    System.out.println("It's done now... I think.");
                } else {
                    // mark the test as not done
                    task.markAsNotDone();
                    System.out.println("The task is no longer marked as done:");
                }

                System.out.println(task.toString());
                System.out.println(separatorLine);
            } else {
                // Add and store the new task
                Task newTask = createTask(command);
                taskList[taskCount] = newTask;
                taskCount++;
                System.out.println(separatorLine);
                System.out.println("Alright... I've added it.");
                System.out.println("  " + newTask);
                System.out.println("There are " + taskCount + " tasks now.");
                System.out.println(separatorLine);
            }
        }
    }

    /**
     * Creates the appropriate task object from an add-task command.
     *
     * @param command the command entered by the user
     * @return the created task, or {@code null} if the command is not an add-task command
     */
    public static Task createTask(String command) {
        // If it is a ToDo task
        if (command.startsWith("todo ")) {
            String description = command.substring(5);
            return new ToDo(description);
        }

        // If it is a Deadline task
        if (command.startsWith("deadline ")) {
            String content = command.substring(9);
            String[] parts = content.split(" /by ", 2);
            String description = parts[0];
            String by = parts[1];
            return new Deadline(description, by);
        }

        // If it is an Event task
        if (command.startsWith("event ")) {
            String content = command.substring(6);
            String[] firstPart = content.split(" /from ", 2);
            String description = firstPart[0];

            String[] time = firstPart[1].split(" /to ", 2);
            String from = time[0];
            String to = time[1];

            return new Event(description, from, to);
        }

        return null;
    }
}
