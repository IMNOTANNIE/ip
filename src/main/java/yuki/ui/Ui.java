package yuki.ui;

import java.util.List;
import java.util.Scanner;

import yuki.task.Task;

/**
 * Handles all command-line interactions between Yuki and the user.
 */
public class Ui {
    /** Separates individual responses so they are easier to read. */
    private static final String SEPARATOR_LINE =
            "--------------------------------------------------------------------------------------------------------------------";
    /** The banner displayed when Yuki starts. */
    private static final String BANNER = "__   __     _    _ \n"
            + "\\ \\ / /   _| | _(_)\n"
            + " \\ V / | | | |/ / |\n"
            + "  | || |_| |   <| |\n"
            + "  |_| \\__,_|_|\\_\\_|\n";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Returns whether another command is available to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays Yuki's greeting. */
    public void showWelcome() {
        System.out.println(SEPARATOR_LINE);
        System.out.println(BANNER);
        System.out.println("...Hello. This is Yuki.");
        System.out.println("What do you need?");
        System.out.println(SEPARATOR_LINE);
    }

    /** Displays Yuki's farewell. */
    public void showGoodbye() {
        showResponse("...Goodbye.");
    }

    /** Displays all tasks in their current order. */
    public void showTaskList(List<Task> tasks) {
        System.out.println(SEPARATOR_LINE);
        System.out.println("Here... These are the tasks you have:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(SEPARATOR_LINE);
    }

    /** Displays a task after its completion status has changed. */
    public void showTaskStatusChanged(String message, Task task) {
        showResponse(message, task.toString());
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        showResponse(
                "Alright... I've added it.",
                "  " + task,
                "There are " + taskCount + " tasks now.");
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskDeleted(Task task, int taskCount) {
        showResponse(
                "Alright... I've removed it.",
                "  " + task,
                "There are " + taskCount + " tasks now.");
    }

    /** Displays an error caused while processing a command. */
    public void showError(String message) {
        showResponse("I couldn't process that. " + message);
    }

    /** Displays an error caused while loading saved tasks. */
    public void showLoadingError(String message) {
        System.out.println("I couldn't load the saved tasks. " + message);
    }

    /** Displays one response surrounded by separator lines. */
    private void showResponse(String... lines) {
        System.out.println(SEPARATOR_LINE);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(SEPARATOR_LINE);
    }
}
