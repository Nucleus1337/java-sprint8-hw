package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    void setUp() {
        super.manager = new FileBackedTasksManager("fileName.csv");
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
    void loadFromFile() {
        int taskExpected = 1;
        int epicExpected = 2;
        int subtaskExpected = 1;

        String fileToLoad = "resources/fileToLoad.csv";

        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(fileToLoad));

        assertEquals(taskExpected, manager.getAllTasks().size());
        assertEquals(epicExpected, manager.getAllEpics().size());
        assertEquals(subtaskExpected, manager.getAllSubtasks().size());

    }
}
