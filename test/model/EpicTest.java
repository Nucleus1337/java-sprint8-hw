package model;

import org.junit.jupiter.api.Test;
import service.TaskManager;
import util.Managers;

import static model.TaskStatus.DONE;
import static model.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    @Test
    public void shouldHasNewStatusWithEmptySubtasks() {
        TaskManager taskManager = Managers.getDefault();

        Task epic = taskManager.createEpic(new Epic("Epic1", "Epic1 desc"));
        TaskStatus status = taskManager.getEpicById(epic.getId()).getStatus();

        assertEquals(TaskStatus.NEW, status, "Different statuses");
    }

    @Test
    public void shouldHasNewStatusWithAllNewStatusForSubtasks() {
        TaskManager taskManager = Managers.getDefault();

        Task epic = taskManager.createEpic(new Epic("Epic1", "Epic1 desc"));
        taskManager.createSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", epic.getId(),
                "2022-01-01-12-46", 15));
        taskManager.createSubtask(new Subtask("Subtask2 Epic1", "Subtask1 Epic1 desc", epic.getId(),
                "2022-01-01-13-46", 15));

        TaskStatus status = taskManager.getEpicById(epic.getId()).getStatus();

        assertEquals(TaskStatus.NEW, status, "Different statuses");
    }

    @Test
    public void shouldHasDoneStatusWithAllDoneStatusForSubtasks() {
        TaskManager taskManager = Managers.getDefault();

        Task epic = taskManager.createEpic(new Epic("Epic1", "Epic1 desc"));
        Task subtask1 = taskManager.createSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", epic.getId(),
                "2022-01-01-12-46", 15));
        Task subtask2 = taskManager.createSubtask(new Subtask("Subtask2 Epic1", "Subtask1 Epic1 desc", epic.getId(),
                "2022-01-01-13-46", 15));

        taskManager.updateSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", DONE, subtask1.getId(),
                "2022-01-01-12-46", 15));
        taskManager.updateSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", DONE, subtask2.getId(),
                "2022-01-01-13-46", 15));

        TaskStatus status = taskManager.getEpicById(epic.getId()).getStatus();

        assertEquals(TaskStatus.DONE, status, "Different statuses");
    }

    @Test
    public void shouldHasInProgressStatusWithNewAndDoneStatusesForSubtasks() {
        TaskManager taskManager = Managers.getDefault();

        Task epic = taskManager.createEpic(new Epic("Epic1", "Epic1 desc"));
        Task subtask = taskManager.createSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", epic.getId(),
                "2022-01-01-12-46", 15));
        taskManager.createSubtask(new Subtask("Subtask2 Epic1", "Subtask1 Epic1 desc", epic.getId(),
                "2022-01-01-13-46", 15));

        taskManager.updateSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", DONE, subtask.getId(),
                "2022-01-01-12-46", 15));

        TaskStatus status = taskManager.getEpicById(epic.getId()).getStatus();

        assertEquals(IN_PROGRESS, status, "Different statuses");
    }

    @Test
    public void shouldHasInProgressStatusWithAllInProgressStatusesForSubtasks() {
        TaskManager taskManager = Managers.getDefault();

        Task epic = taskManager.createEpic(new Epic("Epic1", "Epic1 desc"));
        Task subtask1 = taskManager.createSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", epic.getId(),
                "2022-01-01-12-46", 15));
        Task subtask2 = taskManager.createSubtask(new Subtask("Subtask2 Epic1", "Subtask1 Epic1 desc", epic.getId(),
                "2022-01-01-13-46", 15));

        taskManager.updateSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", IN_PROGRESS, subtask1.getId(),
                "2022-01-01-12-46", 15));
        taskManager.updateSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", IN_PROGRESS, subtask2.getId(),
                "2022-01-01-13-46", 15));

        TaskStatus status = taskManager.getEpicById(epic.getId()).getStatus();

        assertEquals(IN_PROGRESS, status, "Different statuses");
    }
}
