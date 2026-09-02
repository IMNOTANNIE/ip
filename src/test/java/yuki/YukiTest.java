package yuki;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.storage.Storage;
import yuki.task.Task;
import yuki.ui.Ui;

/** Tests the command-processing interface shared by Yuki's graphical UI. */
class YukiTest {

    @Test
    void getResponse_supportedCommands_actualResponsesReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new NoOpStorage());

        String addResponse = yuki.getResponse("todo read book");
        String listResponse = yuki.getResponse("list");

        assertAll(() -> assertTrue(addResponse.contains("I've added it")), () ->
                assertTrue(listResponse.contains("1.[T][ ] read book")));
    }

    @Test
    void getResponse_unknownCommand_errorResponseReturned() {
        Yuki yuki = new Yuki(Ui.createSilentUi(), new NoOpStorage());

        String response = yuki.getResponse("unknown command");

        assertTrue(response.contains("I couldn't process that"));
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
}
