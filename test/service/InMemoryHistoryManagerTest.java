package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager manager;
    private final Task task = new Task("task1", "description1", TaskStatus.NEW, 1L, "2023-01-01-01-01", 1);

    @BeforeEach
    void createManager() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void add() {
        manager.add(task);

        final List<Task> expectation = List.of(task);

        final List<Task> result = manager.getHistory();

        assertEquals(expectation, result);
    }

    @Test
    void getHistory() {
        assertEquals(0, manager.getHistory().size(), "Ожидалась 0 записей");

        final List<Task> expectation = List.of(task);

        manager.add(task);

        final List<Task> result = manager.getHistory();

        assertEquals(expectation, result, "Ожидалась 1 запись");

        manager.add(task);

        final List<Task> result2 = manager.getHistory();

        assertEquals(expectation, result2, "Ожидалась 1 запись");
    }

    @Test
    void remove() {
        manager.add(task);
        manager.remove(1L);

        assertEquals(0, manager.getHistory().size(), "Ожидалась 0 записей");
    }
}
