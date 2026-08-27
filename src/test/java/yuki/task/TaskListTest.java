package yuki.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.exception.YukiException;

/** Tests the ordered task collection and its user-facing numbering rules. */
class TaskListTest {
    @Test
    void addAndGetTask_validTaskNumber_taskStoredAtOneBasedNumber() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");

        tasks.addTask(task);

        assertAll(
                () -> assertEquals(1, tasks.size()),
                () -> assertSame(task, tasks.getTask(1)));
    }

    @Test
    void markAndUnmarkTask_existingTask_statusUpdated() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        Task markedTask = tasks.markTask(1);
        assertAll(
                () -> assertSame(task, markedTask),
                () -> assertTrue(task.isDone()));

        Task unmarkedTask = tasks.unmarkTask(1);
        assertAll(
                () -> assertSame(task, unmarkedTask),
                () -> assertFalse(task.isDone()));
    }

    @Test
    void deleteTask_existingTask_taskRemovedAndReturned() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task deleted = tasks.deleteTask(1);

        assertAll(
                () -> assertSame(first, deleted),
                () -> assertEquals(1, tasks.size()),
                () -> assertSame(second, tasks.getTask(1)));
    }

    @Test
    void getTask_taskNumberOutsideList_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new ToDo("only task")));

        assertAll(
                () -> assertThrows(YukiException.class, () -> tasks.getTask(0)),
                () -> assertThrows(YukiException.class, () -> tasks.getTask(2)));
    }

    @Test
    void findTasks_keywordWithDifferentCase_matchingDescriptionsReturnedInOrder() {
        Task first = new ToDo("read book");
        Task second = new ToDo("buy groceries");
        Task third = new ToDo("return Book");
        TaskList tasks = new TaskList(List.of(first, second, third));

        List<Task> matches = tasks.findTasks("BOOK");

        assertAll(
                () -> assertEquals(2, matches.size()),
                () -> assertSame(first, matches.get(0)),
                () -> assertSame(third, matches.get(1)));
    }

    @Test
    void findTasks_noMatchingDescription_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        List<Task> matches = tasks.findTasks("movie");

        assertTrue(matches.isEmpty());
    }

    @Test
    void getTasks_listLaterChanged_snapshotRemainsUnmodifiableAndUnchanged() {
        TaskList tasks = new TaskList(List.of(new ToDo("first")));
        List<Task> snapshot = tasks.getTasks();

        tasks.addTask(new ToDo("second"));

        assertAll(
                () -> assertEquals(1, snapshot.size()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> snapshot.add(new ToDo("third"))));
    }
}
