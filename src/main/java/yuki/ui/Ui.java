package yuki.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import yuki.task.Task;

/**
 * Formats Yuki's responses and handles command-line interaction with the user.
 */
public class Ui {
    /** Separates individual responses so they are easier to read. */
    private static final String SEPARATOR_LINE =
            "----------------------------------------------------------"
                    + "----------------------------------------------------------";
    /** The banner displayed when Yuki starts. */
    private static final String BANNER = "__   __     _    _ \n"
            + "\\ \\ / /   _| | _(_)\n"
            + " \\ V / | | | |/ / |\n"
            + "  | || |_| |   <| |\n"
            + "  |_| \\__,_|_|\\_\\_|\n";

    /** Reads commands entered through standard input. */
    private final Scanner scanner;
    /** Whether responses should also be printed to standard output. */
    private final boolean isOutputEnabled;
    /** Most recent response generated for the user. */
    private String lastResponse;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        this(true);
    }

    /** Creates a UI with the requested command-line output behavior. */
    private Ui(boolean isOutputEnabled) {
        scanner = new Scanner(System.in);
        this.isOutputEnabled = isOutputEnabled;
        lastResponse = "";
    }

    /**
     * Creates a UI that records responses without printing them.
     *
     * @return A UI suitable for a graphical interface.
     */
    public static Ui createSilentUi() {
        return new Ui(false);
    }

    /** Returns whether another command is available to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Returns the most recent response generated for the user. */
    public String getLastResponse() {
        return lastResponse;
    }

    /** Displays Yuki's greeting. */
    public void showWelcome() {
        showResponse(BANNER, "...Hello. This is Yuki.", "What do you need?");
    }

    /** Displays Yuki's farewell. */
    public void showGoodbye() {
        showResponse("...Goodbye.");
    }

    /** Displays all tasks in their current order. */
    public void showTaskList(List<Task> tasks) {
        showNumberedTaskList("Here... These are the tasks you have:", tasks);
    }

    /** Displays tasks whose descriptions matched a find command. */
    public void showMatchingTasks(List<Task> tasks) {
        showNumberedTaskList("Here... These are the matching tasks in your list:", tasks);
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
        lastResponse = "I couldn't load the saved tasks. " + message;
        if (isOutputEnabled) {
            System.out.println(lastResponse);
        }
    }

    /** Displays a heading and a one-based numbered list of tasks. */
    private void showNumberedTaskList(String heading, List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        lines.add(heading);
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + "." + tasks.get(i));
        }
        showResponse(lines.toArray(String[]::new));
    }

    /** Displays one response surrounded by separator lines. */
    private void showResponse(String... lines) {
        lastResponse = String.join(System.lineSeparator(), lines);
        if (isOutputEnabled) {
            System.out.println(SEPARATOR_LINE);
            System.out.println(lastResponse);
            System.out.println(SEPARATOR_LINE);
        }
    }
}
