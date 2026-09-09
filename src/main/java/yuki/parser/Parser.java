package yuki.parser;

import java.time.Clock;
import java.util.Arrays;
import java.util.Objects;

import yuki.command.AddCommand;
import yuki.command.Command;
import yuki.command.DeleteCommand;
import yuki.command.ExitCommand;
import yuki.command.FindCommand;
import yuki.command.ListCommand;
import yuki.command.MarkCommand;
import yuki.command.RemindersCommand;
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
     * @param command The complete command entered by the user.
     * @return The command object representing the user's instruction.
     * @throws YukiException If the command or its arguments are invalid.
     */
    public static Command parse(String command) {
        return parse(command, Clock.systemDefaultZone());
    }

    /**
     * Converts a complete user command using the supplied clock.
     *
     * @param command The complete command entered by the user.
     * @param clock Clock used by commands that depend on the current time.
     * @return The command object representing the user's instruction.
     * @throws YukiException If the command or its arguments are invalid.
     */
    public static Command parse(String command, Clock clock) {
        Objects.requireNonNull(clock, "clock must not be null");
        CommandType commandType = parseCommandType(command);

        return switch (commandType) {
            case TODO, DEADLINE, EVENT -> new AddCommand(createTask(command, commandType));
            case DELETE -> new DeleteCommand(parseTaskNumber(command, commandType));
            case MARK -> new MarkCommand(parseTaskNumber(command, commandType));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(command, commandType));
            case FIND -> new FindCommand(parseKeyword(command, commandType));
            case REMINDERS -> {
                validateRemindersCommand(command, commandType);
                yield new RemindersCommand(clock);
            }
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
     * @param command The complete command entered by the user.
     * @return The matching command type.
     * @throws YukiException If the command is empty or unsupported.
     */
    private static CommandType parseCommandType(String command) {
        String normalizedCommand = command.trim();
        if (normalizedCommand.isEmpty()) {
            throw new YukiException("No command was entered. Please enter a command.");
        }

        String[] commandParts = normalizedCommand.split("\\s+", 2);
        String commandWord = commandParts[0];

        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType.getKeyword().equals(commandWord))
                .findFirst()
                .orElseThrow(() -> new YukiException("That command isn't familiar to me."));
    }

    /**
     * Creates a task from a task-creation command.
     *
     * @param command The complete command entered by the user.
     * @param commandType The already identified command type.
     * @return The task described by the command.
     * @throws YukiException If the command does not contain the required arguments.
     */
    private static Task createTask(String command, CommandType commandType) {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "createTask requires a task-creation command type";

        String arguments = command.trim()
                .substring(commandType.getKeyword().length())
                .trim();

        return switch (commandType) {
            case TODO -> createTodo(arguments);
            case DEADLINE -> createDeadline(arguments);
            case EVENT -> createEvent(arguments);
            default -> throw new YukiException("That command isn't familiar to me.");
        };
    }

    /** Creates a to-do task from its command arguments. */
    private static ToDo createTodo(String description) {
        if (description.isBlank()) {
            throw new YukiException(
                    "The todo description is missing. Please add a task description after 'todo'.");
        }
        return new ToDo(description);
    }

    /** Creates a deadline task from its command arguments. */
    private static Deadline createDeadline(String arguments) {
        String[] descriptionAndDate = arguments.split(" /by ", 2);
        if (descriptionAndDate.length != 2
                || descriptionAndDate[0].isBlank()
                || descriptionAndDate[1].isBlank()) {
            throw new YukiException(
                    "The deadline needs a description, date and time. For example: "
                            + "deadline return book /by 26/8/2026 1800.");
        }

        String description = descriptionAndDate[0].trim();
        TaskDateTime deadline = DateTimeParser.parse(descriptionAndDate[1].trim());
        return new Deadline(description, deadline);
    }

    /** Creates an event task from its command arguments. */
    private static Event createEvent(String arguments) {
        String[] descriptionAndTimes = arguments.split(" /from ", 2);
        if (descriptionAndTimes.length != 2
                || descriptionAndTimes[0].isBlank()
                || descriptionAndTimes[1].isBlank()) {
            throw new YukiException(
                    "The event needs a description, a start time and an end time. For example: "
                            + "event meeting /from 26/8/2026 1800 /to 26/8/2026 2000.");
        }

        String[] times = descriptionAndTimes[1].split(" /to ", 2);
        if (times.length != 2 || times[0].isBlank() || times[1].isBlank()) {
            throw new YukiException("The event needs an end time.");
        }

        TaskDateTime from = DateTimeParser.parse(times[0].trim());
        TaskDateTime to = DateTimeParser.parse(times[1].trim());
        if (to.isBefore(from)) {
            throw new YukiException("The event's end time cannot be before its start time.");
        }

        return new Event(descriptionAndTimes[0].trim(), from, to);
    }

    /**
     * Parses the task number following a command keyword.
     *
     * @param command The complete command entered by the user.
     * @param commandType The already identified command type.
     * @return The parsed task number.
     * @throws YukiException If the task number is missing or is not an integer.
     */
    private static int parseTaskNumber(String command, CommandType commandType) {
        assert commandType == CommandType.DELETE
                || commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                : "parseTaskNumber requires a numbered command type";

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
     * Returns the non-blank keyword following a find command.
     *
     * @param command The complete command entered by the user.
     * @param commandType The already identified find command type.
     * @return The trimmed keyword or phrase to search for.
     * @throws YukiException If the keyword is missing.
     */
    private static String parseKeyword(String command, CommandType commandType) {
        String keyword = command.trim()
                .substring(commandType.getKeyword().length())
                .trim();
        if (keyword.isBlank()) {
            throw new YukiException("The keyword is missing. Please enter a keyword after 'find'.");
        }
        return keyword;
    }

    /**
     * Checks that a command which takes no arguments contains only its keyword.
     *
     * @param command The complete command entered by the user.
     * @param commandType The already identified command type.
     * @throws YukiException If additional text follows the command keyword.
     */
    private static void validateNoArguments(String command, CommandType commandType) {
        if (!command.trim().equals(commandType.getKeyword())) {
            throw new YukiException(
                    "..There’s no need to add anything else to the "
                            + commandType.getKeyword() + " command.");
        }
    }

    /** Checks that a reminders command contains only its keyword. */
    private static void validateRemindersCommand(String command, CommandType commandType) {
        if (!command.trim().equals(commandType.getKeyword())) {
            throw new YukiException(
                    "There’s no need to add anything else to the reminders command.");
        }
    }
}
