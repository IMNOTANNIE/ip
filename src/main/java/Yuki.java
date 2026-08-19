import java.util.Scanner;

public class Yuki {
    public static void main(String[] args) {
        String separatorLine = "❄──────❄──────❄──────❄──────❄──────❄──────❄";
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
                    System.out.println((i + 1) + ". " + "[" + taskList[i].getStatusIcon() + "] " + taskList[i].description);
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

                System.out.println("  [" + task.getStatusIcon() + "] " + task.getDescription());
                System.out.println(separatorLine);
            } else {
                // Add and store the new task
                taskList[taskCount] = new Task(command);
                taskCount++;
                System.out.println(separatorLine);
                System.out.println("added: " + command);
                System.out.println(separatorLine);
            }
        }
    }
}
