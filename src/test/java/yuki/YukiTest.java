package yuki;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.exception.YukiException;
import yuki.storage.Storage;
import yuki.task.Deadline;
import yuki.task.Task;
import yuki.time.TaskDateTime;
import yuki.ui.Ui;

/** Tests the command-processing interface shared by Yuki's graphical UI. */
class YukiTest {

    @Test
    void getResponse_supportedCommands_actualResponsesReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new NoOpStorage());

        String addResponse = yuki.getResponse("todo read book");
        boolean isAddResponseError = yuki.isLastResponseError();
        String listResponse = yuki.getResponse("list");

        assertTrue(addResponse.contains("I've added it"));
        assertFalse(isAddResponseError);
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertFalse(yuki.isLastResponseError());
    }

    @Test
    void getResponse_unknownCommand_errorResponseReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new NoOpStorage());

        String response = yuki.getResponse("unknown command");

        assertTrue(response.contains("I couldn't process that"));
        assertTrue(yuki.isLastResponseError());
    }

    @Test
    void getResponse_saveFails_additionRolledBack() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new FailingSaveStorage());

        String response = yuki.getResponse("todo read book");
        String listResponse = yuki.getResponse("list");

        assertTrue(response.contains("couldn't save"));
        assertFalse(listResponse.contains("read book"));
    }

    @Test
    void getStartupReminderResponse_loadingFails_errorReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new FailingLoadStorage());

        String response = yuki.getStartupReminderResponse();

        assertTrue(response.contains("couldn't load"));
        assertTrue(yuki.isLastResponseError());
    }

    @Test
    void getStartupReminderResponse_upcomingDeadline_reminderReturned() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-09T01:00:00Z"), ZoneOffset.ofHours(8));
        List<Task> storedTasks = List.of(new Deadline("submit report",
                TaskDateTime.of(LocalDateTime.of(2026, 9, 10, 9, 0))));
        Yuki yuki = new Yuki(Ui.createSilentUi(), new FixedStorage(storedTasks), clock);

        String response = yuki.getStartupReminderResponse();

        assertTrue(response.contains("1.[D][ ] submit report"));
    }

    @Test
    void getStartupReminderResponse_noUpcomingTask_emptyResponseReturned() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-09T01:00:00Z"), ZoneOffset.ofHours(8));
        List<Task> storedTasks = List.of(new Deadline("later report",
                TaskDateTime.of(LocalDateTime.of(2026, 9, 11, 9, 1))));
        Yuki yuki = new Yuki(Ui.createSilentUi(), new FixedStorage(storedTasks), clock);

        assertTrue(yuki.getStartupReminderResponse().isEmpty());
    }

    /** Storage test double that avoids reading and writing the user's data file. */
    private static class NoOpStorage extends Storage {
        @Override
        public ArrayList<Task> loadTasks() {
            return new ArrayList<>();
        }

        @Override
        public void saveTasks(List<Task> tasks) {
            // Persistence behavior is covered by StorageTest.
        }
    }

    /** Storage test double that returns a fixed set of tasks. */
    private static class FixedStorage extends NoOpStorage {
        private final List<Task> storedTasks;

        FixedStorage(List<Task> storedTasks) {
            this.storedTasks = storedTasks;
        }

        @Override
        public ArrayList<Task> loadTasks() {
            return new ArrayList<>(storedTasks);
        }
    }

    /** Storage test double that simulates an unavailable data file during saving. */
    private static class FailingSaveStorage extends NoOpStorage {
        @Override
        public void saveTasks(List<Task> tasks) {
            throw new YukiException("I couldn't save the tasks.");
        }
    }

    /** Storage test double that simulates an unreadable data file during loading. */
    private static class FailingLoadStorage extends NoOpStorage {
        @Override
        public ArrayList<Task> loadTasks() {
            throw new YukiException("The saved data file is unreadable.");
        }
    }
}
