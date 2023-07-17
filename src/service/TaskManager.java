package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    /**
     * Создать задачу
     *
     * @param task объект класса Task
     */
    Task createTask(Task task);

    /**
     * Создать эпик-задачу
     *
     * @param epic объект класса Epic
     */
    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void clearAllTasks();

    void clearAllEpics();

    void clearAllSubtasks();

    Collection<Task> getAllTasks();

    Collection<Epic> getAllEpics();

    Collection<Subtask> getAllSubtasks();

    List<Subtask> getAllSubtasksByEpicId(long epicId);

    Task getTaskById(long taskId);

    Epic getEpicById(long epicId);

    Subtask getSubtaskById(long subtaskId);

    void removeTaskById(long taskId);

    void removeSubtaskById(long subtaskId);

    void removeEpicById(long epicId);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
