package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    private final Task task1 = new Task("Task1", "Desc1", "2023-01-01-01-01", 1);
    private final Task task2 = new Task("Task2", "Desc2", "2023-01-01-02-01", 1);
    private final Epic epic1 = new Epic("Epic1", "DescEpic1");
    private final Epic epic2 = new Epic("Epic2", "DescEpic2");


    @Test
    void addTask() {
        Task localTask = manager.createTask(task1);

        assertNotNull(manager.getTaskById(localTask.getId()));
        assertEquals(task1, manager.getTaskById(localTask.getId()));
    }

    @Test
    void addNullTask() {
        assertThrows(NullPointerException.class, () -> {
            manager.createTask(null);
        });
    }

    @Test
    void addEpic() {
        Task localEpic = manager.createEpic(epic1);

        assertNotNull(manager.getEpicById(localEpic.getId()));
        assertEquals(epic1, manager.getEpicById(localEpic.getId()));
    }

    @Test
    void addNullEpic() {
        assertThrows(NullPointerException.class, () -> manager.createEpic(null));
    }

    @Test
    void addSubtask() {
        Epic localEpic = manager.createEpic(epic1);
        Subtask subtask = manager
                .createSubtask(new Subtask("Subtask1", "DescSubtask1", localEpic.getId(), "2023-01-01-03-01", 1));

        assertNotNull(manager.getSubtaskById(subtask.getId()));
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    void addNullSubtask() {
        assertThrows(NullPointerException.class, () -> manager.createSubtask(null));
    }

    @Test
    void clearAllTasks() {
        List<Task> expectation = List.of();

        manager.createTask(task1);
        manager.createTask(task2);
        manager.clearAllTasks();

        assertEquals(expectation, manager.getAllTasks());
    }

    @Test
    void clearAllEpics() {
        List<Task> expectation = List.of();

        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.clearAllEpics();

        assertEquals(expectation, manager.getAllEpics());
    }

    @Test
    void clearAllEpicsWithSubtascsInIt() {
        List<Task> expectation = List.of();

        Epic epic11 = manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask subtask = manager
                .createSubtask(new Subtask("Subtask1", "DescSubtask1", epic11.getId(), "2023-01-01-03-01", 1));

        manager.clearAllEpics();

        assertEquals(expectation, manager.getAllEpics());
        assertEquals(expectation, manager.getAllSubtasks());
    }

    @Test
    void clearAllSubtasks() {
        List<Task> expectation = List.of();

        Epic localEpic1 = manager.createEpic(epic1);
        Epic localEpic2 = manager.createEpic(epic2);
        manager.createSubtask(new Subtask("Subtask1", "DescSubtask1", localEpic1.getId(), "2023-01-01-03-01", 1));
        manager.createSubtask(new Subtask("Subtask1", "DescSubtask1", localEpic2.getId(), "2023-01-01-04-01", 1));

        manager.clearAllSubtasks();

        assertEquals(expectation, manager.getAllSubtasks());
    }

    @Test
    void getAllTasks() {
        int expectation = 2;

        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(expectation, manager.getAllTasks().size());
    }

    @Test
    void getAllTasksNull() {
        int expectation = 0;

        assertEquals(expectation, manager.getAllTasks().size());
    }

    @Test
    void getAllEpics() {
        int expectation = 2;

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        assertEquals(expectation, manager.getAllEpics().size());
    }

    @Test
    void getAllEpicsNull() {
        int expectation = 0;

        assertEquals(expectation, manager.getAllEpics().size());
    }

    @Test
    void getAllSubtasks() {
        int expectation = 2;

        Epic localEpic1 = manager.createEpic(epic1);
        Epic localEpic2 = manager.createEpic(epic2);
        manager.createSubtask(new Subtask("Subtask1", "DescSubtask1", localEpic1.getId(), "2023-01-01-03-01", 1));
        manager.createSubtask(new Subtask("Subtask1", "DescSubtask1", localEpic2.getId(), "2023-01-01-04-01", 1));

        assertEquals(expectation, manager.getAllSubtasks().size());
    }

    @Test
    void getAllSubtasksNull() {
        int expectation = 0;

        assertEquals(expectation, manager.getAllSubtasks().size());
    }

    @Test
    void getAllSubtasksByEpicId() {
        int expectation = 2;

        Epic localEpic1 = manager.createEpic(epic1);
        manager.createSubtask(new Subtask("Subtask1", "DescSubtask1", localEpic1.getId(), "2023-01-01-03-01", 1));
        manager.createSubtask(new Subtask("Subtask2", "DescSubtask2", localEpic1.getId(), "2023-01-01-04-01", 1));

        assertEquals(expectation, manager.getAllSubtasksByEpicId(localEpic1.getId()).size());
    }

    @Test
    void getAllSubtasksByEpicIdWithNoSubtasks() {
        int expectation = 0;

        Epic localEpic1 = manager.createEpic(epic1);

        assertEquals(expectation, manager.getAllSubtasksByEpicId(localEpic1.getId()).size());
    }

    @Test
    void getAllSubtasksByEpicIdWithWrongEpicId() {
        int expectation = 0;

        assertEquals(expectation, manager.getAllSubtasksByEpicId(-1).size());
    }

    @Test
    void getTaskById() {
        Task localTask = manager.createTask(task1);

        assertNotNull(manager.getTaskById(localTask.getId()));

        Task task = manager.getTaskById(localTask.getId());
        assertEquals(task1, manager.getTaskById(localTask.getId()));
        assertEquals(task1.getName(), task.getName());
    }

    @Test
    void getEpicById() {
        Epic epic = manager.createEpic(epic1);

        assertNotNull(manager.getEpicById(epic.getId()));

        Epic epicById = manager.getEpicById(epic.getId());
        assertEquals(epic1, epicById);
        assertEquals(epic1.getName(), epicById.getName());
    }

    @Test
    void getSubtaskById() {
        Epic epic = manager.createEpic(epic1);
        String subtackName = "Subtask1";

        Subtask expectationSubtask = manager
                .createSubtask(new Subtask(subtackName, "DescSubtask1", epic.getId(), "2023-01-01-03-01", 1));

        assertNotNull(manager.getSubtaskById(expectationSubtask.getId()));

        Subtask subtask = manager.getSubtaskById(expectationSubtask.getId());
        assertEquals(expectationSubtask, subtask);
        assertEquals(subtackName, subtask.getName());
    }

    @Test
    void removeTaskById() {
        int expectation = 0;

        Task taskCreate = manager.createTask(task1);

        manager.removeTaskById(taskCreate.getId());

        assertThrows(NullPointerException.class, () -> manager.getTaskById(taskCreate.getId()));
        assertEquals(expectation, manager.getAllTasks().size());
    }

    @Test
    void removeEpicById() {
        int expectation = 0;

        Epic epic = manager.createEpic(epic1);

        manager.removeEpicById(epic.getId());

        assertThrows(NullPointerException.class, () -> manager.getEpicById(epic.getId()));
        assertEquals(expectation, manager.getAllEpics().size());
    }

    @Test
    void removeEpicByIdWithSubtasks() {
        List<Epic> expectation = List.of();

        Epic epic = manager.createEpic(epic1);
        Subtask subtask = manager
                .createSubtask(new Subtask("Subtask1", "DescSubtask1", epic.getId(), "2023-01-01-03-01", 1));

        manager.removeEpicById(epic.getId());

        assertThrows(NullPointerException.class, () -> manager.getEpicById(epic.getId()));
        assertEquals(expectation, manager.getAllEpics());
    }

    @Test
    void removeSubtaskById() {
        int expectation = 0;
        Epic epic = manager.createEpic(epic1);
        Subtask subtask = manager
                .createSubtask(new Subtask("Subtask1", "DescSubtask1", epic.getId(), "2023-01-01-03-01", 1));

        manager.removeSubtaskById(subtask.getId());

        assertThrows(NullPointerException.class, () -> manager.getSubtaskById(subtask.getId()));
        assertEquals(expectation, manager.getAllSubtasks().size());
        assertEquals(expectation, manager.getAllSubtasksByEpicId(epic.getId()).size());
    }

    @Test
    void updateTask() {
        String newName = "newName";
        String newDesc = "newDesc";

        Task task = manager.createTask(task1);

        task1.setName(newName);
        task1.setDescription(newDesc);
        manager.updateTask(task1);

        assertEquals(newName, manager.getTaskById(task.getId()).getName());
        assertEquals(newDesc, manager.getTaskById(task.getId()).getDescription());
    }

    @Test
    void updateEpic() {
        String newName = "newName";
        String newDesc = "newDesc";

        Epic epic = manager.createEpic(epic1);

        epic1.setName(newName);
        epic1.setDescription(newDesc);
        manager.updateEpic(epic1);

        assertEquals(newName, manager.getEpicById(epic.getId()).getName());
        assertEquals(newDesc, manager.getEpicById(epic.getId()).getDescription());
    }

    @Test
    void updateSubtask() {
        String newName = "newName";
        String newDesc = "newDesc";

        Epic epic = manager.createEpic(epic1);
        Subtask subtask = manager
                .createSubtask(new Subtask("Subtask1", "DescSubtask1", epic.getId(), "2023-01-01-03-01", 1));

        subtask.setName(newName);
        subtask.setDescription(newDesc);
        manager.updateSubtask(subtask);

        assertEquals(newName, manager.getSubtaskById(subtask.getId()).getName());
        assertEquals(newDesc, manager.getSubtaskById(subtask.getId()).getDescription());
    }

    @Test
    void getEmptyHistory() {
        int expectation = 0;

        assertEquals(expectation, manager.getHistory().size());
    }

    @Test
    void getHistory() {
        int expectation = 2;

        Task task2Created = manager.createTask(task2);
        Task task1Created = manager.createTask(task1);
        manager.getTaskById(task1Created.getId());
        manager.getTaskById(task2Created.getId());

        assertEquals(expectation, manager.getHistory().size());

        manager.getTaskById(task1Created.getId());

        assertEquals(expectation, manager.getHistory().size());
    }

    @Test
    void getEmptyHistoryAfterDelete() {
        int expectation = 0;

        Task task2Created = manager.createTask(task2);
        Task task1Created = manager.createTask(task1);
        manager.getTaskById(task1Created.getId());
        manager.getTaskById(task2Created.getId());

        manager.clearAllTasks();

        assertEquals(expectation, manager.getHistory().size());
    }
}
