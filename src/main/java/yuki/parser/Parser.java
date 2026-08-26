package yuki.parser;

import yuki.command.AddCommand;
import yuki.command.Command;
import yuki.command.DeleteCommand;
import yuki.command.ExitCommand;
import yuki.command.ListCommand;
import yuki.command.MarkCommand;
import yuki.command.UnmarkCommand;
import yuki.exception.YukiException;
import yuki.task.Deadline;
import yuki.task.Event;
import yuki.task.Task;
import yuki.task.ToDo;
import yuki.time.DateTimeParser;
import yuki.time.TaskDateTime;

/**
 * Interprets user commands and converts their arguments into values used by Yuki.
 */
public final class Parser {
    /** Prevents creation of this stateless utility class. */
    private Parser() {
    }

    /**
     * Converts a complete user command into an executable command object.
     *
     * @param command the complete command entered by the user
     * @return the command object representing the user's instruction
     * @throws YukiException if the command or its arguments are invalid
     */
    public static Command parse(String command) {
        CommandType commandType = parseCommandType(command);

        return switch (commandType) {
            case TODO, DEADLINE, EVENT -> new AddCommand(createTask(command, commandType));
            case DELETE -> new DeleteCommand(parseTaskNumber(command, commandType));
            case MARK -> new MarkCommand(parseTaskNumber(command, commandType));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(command, commandType));
            case LIST -> {
                validateNoArguments(command, commandType);
                yield new ListCommand();
            }
            case BYE -> {
                validateNoArguments(command, commandType);
                yield new ExitCommand();
            }
        };
    }

    /**
     * Identifies the type of a command from its first word.
     *
     * @param command the complete command entered by the user
     * @return the matching command type
     * @throws YukiException if the command is empty or unsupported
     */
    private static CommandType parseCommandType(String command) {
        String normalizedCommand = command.trim();
        if (normalizedCommand.isEmpty()) {
            throw new YukiException("No command was entered. Please enter a command.");
        }

        String[] commandParts = normalizedCommand.split("\\s+", 2);
        String commandWord = commandParts[0];

        for (CommandType commandType : CommandType.values()) {
            if (commandType.getKeyword().equals(commandWord)) {
                return commandType;
            }
        }

        throw new YukiException("That command isn't familiar to me.");
    }

    /**
     * Creates a task from a task-creation command.
     *
     * @param command the complete command entered by the user
     * @param commandType the already identified command type
     * @return the task described by the command
     * @throws YukiException if the command does not contain the required arguments
     */
    private static Task createTask(String command, CommandType commandType) {
        String normalizedCommand = command.trim();
        if (commandType == CommandType.TODO) {
            String description = normalizedCommand.length() > 4
                    ? normalizedCommand.substring(4).trim()
                    : "";

            if (description.isBlank()) {
                throw new YukiException("The todo description is missing. Please add a task description after 'todo'.");
            }

            return new ToDo(description);
        }

        if (commandType == CommandType.DEADLINE) {
            String content = normalizedCommand.length() > 8
                    ? normalizedCommand.substring(8).trim()
                    : "";
            String[] parts = content.split(" /by ", 2);

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
            String content = normalizedCommand.length() > 5
                    ? normalizedCommand.substring(5).trim()
                    : "";

            String[] firstPart = content.split(" /from ", 2);
            String description = firstPart[0];
            if (firstPart.length != 2
                    || description.isBlank()
                    || firstPart[1].isBlank()) {
                throw new YukiException(
                        "The event needs a description, a start time and an end time. For example: "
                                + "event meeting /from 26/8/2026 1800 /to 26/8/2026 2000.");
            }

            String[] time = firstPart[1].split(" /to ", 2);
            if (time.length != 2
                    || time[0].isBlank()
                    || time[1].isBlank()) {
                throw new YukiException("The event needs an end time.");
            }

            TaskDateTime from = DateTimeParser.parse(time[0].trim());
            TaskDateTime to = DateTimeParser.parse(time[1].trim());
            if (to.isBefore(from)) {
                throw new YukiException("The event's end time cannot be before its start time.");
            }

            return new Event(description.trim(), from, to);
        }

        throw new YukiException("That command isn't familiar to me.");
    }

    /**
     * Parses the task number following a command keyword.
     *
     * @param command the complete command entered by the user
     * @param commandType the already identified command type
     * @return the parsed task number
     * @throws YukiException if the task number is missing or is not an integer
     */
    private static int parseTaskNumber(String command, CommandType commandType) {
        String normalizedCommand = command.trim();
        if (normalizedCommand.equals(commandType.getKeyword())) {
            throw new YukiException("The task number is missing.");
        }

        String taskNumberText = normalizedCommand.substring(commandType.getKeyword().length());
        try {
            return Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException e) {
            throw new YukiException("The task number must be a positive integer");
        }
    }

    /**
     * Checks that a command which takes no arguments contains only its keyword.
     *
     * @param command the complete command entered by the user
     * @param commandType the already identified command type
     * @throws YukiException if additional text follows the command keyword
     */
    private static void validateNoArguments(String command, CommandType commandType) {
        if (!command.trim().equals(commandType.getKeyword())) {
            throw new YukiException(
                    "..There’s no need to add anything else to the " + commandType.getKeyword() + " command.");
        }
    }
}
