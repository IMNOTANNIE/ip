package yuki.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yuki.exception.YukiException;
import yuki.task.Deadline;
import yuki.task.Event;
import yuki.task.Task;
import yuki.task.ToDo;
import yuki.time.TaskDateTime;

/** Tests task persistence using isolated temporary files. */
class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    void loadTasks_missingFile_emptyListReturned() {
        Storage storage = new Storage(tempDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void saveAndLoadTasks_allTaskTypesAndStatus_valuesPreserved() {
        Path dataFile = tempDirectory.resolve("nested").resolve("userdata.txt");
        Storage storage = new Storage(dataFile);
        ToDo todo = new ToDo("read | review 100% of notes");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report",
                TaskDateTime.of(LocalDateTime.of(2026, 12, 2, 18, 0)));
        Event event = new Event("project meeting",
                TaskDateTime.of(LocalDate.of(2026, 8, 6)),
                TaskDateTime.of(LocalDate.of(2026, 8, 7)));

        storage.saveTasks(List.of(todo, deadline, event));
        List<Task> restored = storage.loadTasks();

        ToDo restoredTodo = assertInstanceOf(ToDo.class, restored.get(0));
        Deadline restoredDeadline = assertInstanceOf(Deadline.class, restored.get(1));
        Event restoredEvent = assertInstanceOf(Event.class, restored.get(2));
        assertAll(
                () -> assertEquals(3, restored.size()),
                () -> assertEquals("read | review 100% of notes", restoredTodo.getDescription()),
                () -> assertTrue(restoredTodo.isDone()),
                () -> assertEquals(LocalDateTime.of(2026, 12, 2, 18, 0),
                        restoredDeadline.getBy().getDateTime()),
                () -> assertEquals(LocalDate.of(2026, 8, 6), restoredEvent.getFrom().getDate()),
                () -> assertEquals(LocalDate.of(2026, 8, 7), restoredEvent.getTo().getDate()));
    }

    @Test
    void loadTasks_invalidSavedStatus_exceptionThrown() throws IOException {
        Path dataFile = tempDirectory.resolve("userdata.txt");
        Files.writeString(dataFile, "T | 2 | broken task", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        YukiException exception = assertThrows(YukiException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("invalid"));
    }
}
