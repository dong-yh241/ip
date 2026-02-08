package cipher.task;

import cipher.CipherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    void setUp() {
        taskList = new TaskList();
    }

    @Test
    void testAddTask() throws CipherException {
        Task task = new Todo("Buy groceries");
        taskList.add(task);

        assertEquals(1, taskList.size(), "The task list should contain one task.");
    }

    @Test
    void testGetTask() throws CipherException {
        Task task = new Todo("Buy groceries");
        taskList.add(task);

        Task retrievedTask = taskList.get(1);
        assertEquals("Buy groceries", retrievedTask.getDescription(), "The task description should match.");
    }

    @Test
    void testRemoveTask() throws CipherException {
        Task task = new Todo("Buy groceries");
        taskList.add(task);

        Task removedTask = taskList.remove(1);
        assertEquals("Buy groceries", removedTask.getDescription(), "The removed task description should match.");
        assertEquals(0, taskList.size(), "The task list should be empty after removal.");
    }
}
