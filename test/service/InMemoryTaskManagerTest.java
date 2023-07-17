package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @BeforeEach
    void setUp() {
        super.manager = new InMemoryTaskManager();
    }

    @Override
    @Test
    void addTask() {
        super.addTask();
    }

    @Override
    @Test
    void addEpic() {
        super.addEpic();
    }

    @Override
    @Test
    void addSubtask() {
        super.addSubtask();
    }

    @Override
    @Test
    void clearAllTasks() {
        super.clearAllTasks();
    }

    @Override
    @Test
    void clearAllEpics() {
        super.clearAllEpics();
    }

    @Override
    @Test
    void clearAllSubtasks() {
        super.clearAllSubtasks();
    }

    @Override
    @Test
    void addNullTask() {
        super.addNullTask();
    }

    @Override
    @Test
    void addNullEpic() {
        super.addNullEpic();
    }

    @Override
    @Test
    void addNullSubtask() {
        super.addNullSubtask();
    }

    @Override
    @Test
    void getAllTasks() {
        super.getAllTasks();
    }

    @Override
    @Test
    void getAllTasksNull() {
        super.getAllTasksNull();
    }

    @Override
    @Test
    void getAllEpics() {
        super.getAllEpics();
    }

    @Override
    @Test
    void getAllEpicsNull() {
        super.getAllEpicsNull();
    }

    @Override
    @Test
    void getAllSubtasks() {
        super.getAllSubtasks();
    }

    @Override
    @Test
    void getAllSubtasksNull() {
        super.getAllSubtasksNull();
    }

    @Override
    @Test
    void getAllSubtasksByEpicId() {
        super.getAllSubtasksByEpicId();
    }

    @Override
    @Test
    void getAllSubtasksByEpicIdWithNoSubtasks() {
        super.getAllSubtasksByEpicIdWithNoSubtasks();
    }

    @Override
    @Test
    void getAllSubtasksByEpicIdWithWrongEpicId() {
        super.getAllSubtasksByEpicIdWithWrongEpicId();
    }

    @Override
    @Test
    void getTaskById() {
        super.getTaskById();
    }

    @Override
    @Test
    void getEpicById() {
        super.getEpicById();
    }

    @Override
    @Test
    void getSubtaskById() {
        super.getSubtaskById();
    }

    @Override
    @Test
    void removeTaskById() {
        super.removeTaskById();
    }

    @Override
    @Test
    void removeEpicById() {
        super.removeEpicById();
    }

    @Override
    @Test
    void removeSubtaskById() {
        super.removeSubtaskById();
    }

    @Override
    @Test
    void updateTask() {
        super.updateTask();
    }

    @Override
    @Test
    void updateEpic() {
        super.updateEpic();
    }

    @Override
    @Test
    void updateSubtask() {
        super.updateSubtask();
    }

    @Override
    @Test
    void getEmptyHistory() {
        super.getEmptyHistory();
    }

    @Override
    @Test
    void getHistory() {
        super.getHistory();
    }

    @Override
    @Test
    void getEmptyHistoryAfterDelete() {
        super.getEmptyHistoryAfterDelete();
    }

    @Override
    @Test
    void clearAllEpicsWithSubtascsInIt() {
        super.clearAllEpicsWithSubtascsInIt();
    }

    @Override
    @Test
    void removeEpicByIdWithSubtasks() {
        super.removeEpicByIdWithSubtasks();
    }

    @Test
    void getPrioritizedTasks() {
        Set<Task> expectedSet = Set.of();
        InMemoryTaskManager manager = new InMemoryTaskManager();

        assertEquals(expectedSet, manager.getPrioritizedTasks());

        final Task task1 = manager.createTask(new Task("Task1", "Desc1", "2023-01-01-01-01", 1));
        final Task task2 = manager.createTask(new Task("Task2", "Desc2", "2022-01-01-02-01", 1));

        assertEquals(task2, manager.getPrioritizedTasks().iterator().next());
    }

    @Test
    void isIntesection() {
        assertThrows(IllegalStateException.class, () -> {
            manager.createTask(new Task("Task1", "Desc1", "2023-01-01-01-01", 1));
            manager.createTask(new Task("Task2", "Desc2", "2023-01-01-01-01", 1));
        });

        assertThrows(IllegalStateException.class, () -> {
            manager.createTask(new Task("Task1", "Desc1", "2023-01-01-01-01", 1));
            manager.createTask(new Task("Task2", "Desc2", "2023-01-01-01-02", 1));
        });

        assertThrows(IllegalStateException.class, () -> {
            manager.createTask(new Task("Task1", "Desc1", "2023-01-01-01-01", 10));
            manager.createTask(new Task("Task2", "Desc2", "2023-01-01-01-03", 10));
        });
    }
}
