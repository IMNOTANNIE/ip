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
        this(new Ui(), new Storage());
    }

    /** Creates Yuki using the supplied UI and storage components. */
    Yuki(Ui ui, Storage storage) {
        this.ui = ui;
        this.storage = storage;

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
     * Creates a Yuki instance that returns responses without printing them.
     *
     * @return A Yuki instance suitable for a graphical interface.
     */
    public static Yuki createGuiInstance() {
        return new Yuki(Ui.createSilentUi(), new Storage());
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
     * Processes one command and returns Yuki's response.
     *
     * @param input Message entered by the user.
     * @return Yuki's response to the message.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
        } catch (YukiException e) {
            ui.showError(e.getMessage());
        }
        return ui.getLastResponse();
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
