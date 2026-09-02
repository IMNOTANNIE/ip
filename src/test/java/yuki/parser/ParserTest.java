package yuki.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.command.DeleteCommand;
import yuki.command.ExitCommand;
import yuki.command.FindCommand;
import yuki.command.ListCommand;
import yuki.command.MarkCommand;
import yuki.command.UnmarkCommand;
import yuki.exception.YukiException;
import yuki.storage.Storage;
import yuki.task.Deadline;
import yuki.task.Event;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.task.ToDo;
import yuki.ui.Ui;

/** Tests Yuki's command parsing and validation rules. */
class ParserTest {
    @Test
    void parse_todoCommand_addsTodoWithDescription() {
        Task task = parseAndExecuteAddCommand("  todo   read a book  ");

        assertAll(() -> assertInstanceOf(ToDo.class, task), () ->
                assertEquals("read a book", task.getDescription()));
    }

    @Test
    void parse_deadlineCommand_addsDeadlineWithParsedDateTime() {
        Task task = parseAndExecuteAddCommand("deadline submit report /by 2/12/2026 1800");
        Deadline deadline = assertInstanceOf(Deadline.class, task);

        assertAll(() -> assertEquals("submit report", deadline.getDescription()), () ->
                assertEquals(LocalDateTime.of(2026, 12, 2, 18, 0),
                        deadline.getBy().getDateTime()));
    }

    @Test
    void parse_eventCommand_addsEventWithParsedDateRange() {
        Task task = parseAndExecuteAddCommand(
                "event project meeting /from 6/8/2026 /to 7/8/2026");
        Event event = assertInstanceOf(Event.class, task);

        assertAll(() -> assertEquals("project meeting", event.getDescription()), () ->
                assertEquals(LocalDate.of(2026, 8, 6), event.getFrom().getDate()), () ->
                assertEquals(LocalDate.of(2026, 8, 7), event.getTo().getDate()));
    }

    @Test
    void parse_blankOrUnknownCommand_exceptionThrown() {
        assertAll(() -> assertThrows(YukiException.class, () -> Parser.parse("   ")), () ->
                assertThrows(YukiException.class, () -> Parser.parse("dance")));
    }

    @Test
    void parse_taskCreationMissingRequiredParts_exceptionThrown() {
        assertAll(() -> assertThrows(YukiException.class, () -> Parser.parse("todo")), () ->
                assertThrows(YukiException.class, () -> Parser.parse("deadline submit report")), () ->
                assertThrows(YukiException.class, () ->
                        Parser.parse("event meeting /from 6/8/2026")));
    }

    @Test
    void parse_findCommandWithoutKeyword_exceptionThrown() {
        assertThrows(YukiException.class, () -> Parser.parse("find   "));
    }

    @Test
    void parse_eventEndBeforeStart_exceptionThrown() {
        YukiException exception = assertThrows(YukiException.class, () ->
                Parser.parse("event meeting /from 7/8/2026 /to 6/8/2026"));

        assertTrue(exception.getMessage().contains("before its start time"));
    }

    @Test
    void parse_numberedCommandWithoutValidNumber_exceptionThrown() {
        assertAll(() -> assertThrows(YukiException.class, () -> Parser.parse("mark")), () ->
                assertThrows(YukiException.class, () -> Parser.parse("delete first")));
    }

    @Test
    void parse_noArgumentCommandWithExtraText_exceptionThrown() {
        assertAll(() -> assertThrows(YukiException.class, () -> Parser.parse("list now")), () ->
                assertThrows(YukiException.class, () -> Parser.parse("bye now")));
    }

    @Test
    void parse_supportedNonAddCommands_correctCommandTypes() {
        assertAll(() -> assertInstanceOf(MarkCommand.class, Parser.parse("mark 1")), () ->
                assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1")), () ->
                assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1")), () ->
                assertInstanceOf(FindCommand.class, Parser.parse("find book")), () ->
                assertInstanceOf(ListCommand.class, Parser.parse("list")), () ->
                assertInstanceOf(ExitCommand.class, Parser.parse("bye")), () ->
                assertTrue(Parser.parse("bye").isExit()));
    }

    /** Parses and executes a task-creation command, then returns the added task. */
    private Task parseAndExecuteAddCommand(String input) {
        TaskList tasks = new TaskList();
        Parser.parse(input).execute(tasks, new SilentUi(), new NoOpStorage());
        return tasks.getTask(1);
    }

    /** UI test double that suppresses output from add commands. */
    private static class SilentUi extends Ui {
        @Override
        public void showTaskAdded(Task task, int taskCount) {
            // The parser test observes the resulting task list instead of console output.
        }
    }

    /** Storage test double that keeps parser tests independent of the file system. */
    private static class NoOpStorage extends Storage {
        @Override
        public void saveTasks(List<Task> tasks) {
            // Persistence is covered separately by StorageTest.
        }
    }
}
