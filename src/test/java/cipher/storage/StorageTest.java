package cipher.storage;

import cipher.CipherException;
import cipher.task.Task;
import cipher.task.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class StorageTest {

    private Storage storage;
    private Path testFile;

    @BeforeEach
    void setUp() throws Exception {
        testFile = Path.of("testData", "testTasks.txt");
        storage = new Storage(testFile.toString());

        // Clear the file before each test
        Files.deleteIfExists(testFile);
    }

    @Test
    void testLoadEmptyFile() throws CipherException {
        List<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty(), "The task list should be empty when loading an empty file.");
    }

    @Test
    void testSaveAndLoadTasks() throws CipherException, IOException {
        Task todoTask = new Todo("Buy groceries");
        storage.save(List.of(todoTask));

        List<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size(), "The task list should contain one task.");
        assertEquals("Buy groceries", loadedTasks.get(0).getDescription(), "The task description should match.");
    }
}
