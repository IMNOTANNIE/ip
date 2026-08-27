package yuki;

import yuki.command.Command;
import yuki.exception.YukiException;
import yuki.parser.Parser;
import yuki.storage.Storage;
import yuki.task.TaskList;
import yuki.ui.Ui;

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

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (YukiException e) {
                // Show the error and keep the program ready for the next command.
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Starts Yuki.
     *
     * @param args Command-line arguments, which are not used by Yuki.
     */
    public static void main(String[] args) {
        new Yuki().run();
    }

}
