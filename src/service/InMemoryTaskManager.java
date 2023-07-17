package service;

import model.*;
import util.Managers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static util.Sequence.getNextId;
import static model.TaskStatus.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Long, Task> taskIdToTask = new HashMap<>();
    private final Map<Long, Epic> epicIdToEpic = new HashMap<>();
    private final Map<Long, Subtask> subtaskIdToSubtask = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder()));
    protected final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    @Override
    public Task createTask(Task task) {
        task.setId(getNextId());
        task.setStatus(NEW);

        taskIdToTask.put(task.getId(), task);
        prioritizedTasks.add(task);

        checkIntersection(task);

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epic.setStatus(NEW);

        epicIdToEpic.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(getNextId());
        subtask.setStatus(NEW);

        subtaskIdToSubtask.put(subtask.getId(), subtask);
        epicIdToEpic.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        prioritizedTasks.add(subtask);

        checkIntersection(subtask);

        calcEpicTime(getEpicById(subtask.getEpicId()));

        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskIdToTask.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicIdToEpic.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskIdToSubtask.values());
    }

    @Override
    public void clearAllTasks() {
        taskIdToTask.values().forEach(task -> historyManager.remove(task.getId()));
        taskIdToTask.values().forEach(prioritizedTasks::remove);
        taskIdToTask.clear();
    }

    @Override
    public void clearAllEpics() {
        epicIdToEpic.values().forEach(epic -> historyManager.remove(epic.getId()));
        epicIdToEpic.clear();

        subtaskIdToSubtask.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtaskIdToSubtask.values().forEach(prioritizedTasks::remove);
        subtaskIdToSubtask.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtaskIdToSubtask.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtaskIdToSubtask.values().forEach(prioritizedTasks::remove);
        subtaskIdToSubtask.clear();

        epicIdToEpic.values().forEach(epic -> {
            epic.setStatus(NEW);
            epic.clearSubtaskId();
        });
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(long epicId) {
        return subtaskIdToSubtask.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    @Override
    public Task getTaskById(long taskId) {
        historyManager.add(taskIdToTask.get(taskId));

        return taskIdToTask.get(taskId);
    }

    @Override
    public Epic getEpicById(long epicId) {
        historyManager.add(epicIdToEpic.get(epicId));

        return epicIdToEpic.get(epicId);
    }

    @Override
    public Subtask getSubtaskById(long subtaskId) {
        historyManager.add(subtaskIdToSubtask.get(subtaskId));

        return subtaskIdToSubtask.get(subtaskId);
    }

    @Override
    public void removeTaskById(long taskId) {
        prioritizedTasks.remove(taskIdToTask.get(taskId));
        taskIdToTask.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeSubtaskById(long subtaskId) {
        Epic epic = epicIdToEpic.get(subtaskIdToSubtask.get(subtaskId).getEpicId());
        epic.getSubtaskId().remove(subtaskId);

        prioritizedTasks.remove(subtaskIdToSubtask.get(subtaskId));
        subtaskIdToSubtask.remove(subtaskId);
        historyManager.remove(subtaskId);

        calcEpicStatus(epic);
        calcEpicTime(epic);
    }

    @Override
    public void removeEpicById(long epicId) {
        epicIdToEpic.get(epicId).getSubtaskId().forEach(subtaskId -> {
            prioritizedTasks.remove(subtaskIdToSubtask.get(subtaskId));
            subtaskIdToSubtask.remove(subtaskId);
            historyManager.remove(subtaskId);
        });

        epicIdToEpic.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void updateTask(Task task) {
        checkIntersection(task);
        updateMainTaskInfo(task, taskIdToTask.get(task.getId()));

    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask subtaskForUpdate = subtaskIdToSubtask.get(subtask.getId());
        updateMainTaskInfo(subtask, subtaskForUpdate);

        calcEpicStatus(epicIdToEpic.get(subtaskForUpdate.getEpicId()));
        calcEpicTime(epicIdToEpic.get(subtaskForUpdate.getEpicId()));

        checkIntersection(subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        updateMainTaskInfo(epic, epicIdToEpic.get(epic.getId()));
    }

    private void updateMainTaskInfo(Task newTask, Task taskForUpdate) {
        prioritizedTasks.remove(taskForUpdate);

        taskForUpdate.setName(newTask.getName());
        taskForUpdate.setDescription(newTask.getDescription());
        taskForUpdate.setStatus(newTask.getStatus());

        if (!(newTask instanceof Epic)) {
            taskForUpdate.setStartTime(newTask.getStartTime());
            taskForUpdate.setDuration(newTask.getDuration());
        }

        prioritizedTasks.add(taskForUpdate);
    }

    private void calcEpicStatus(Epic epic) {
        long newStatusCounter = 0;
        long doneStatusCounter = 0;
        long allSubtaskCounter = 0;

        for (Subtask subtask : subtaskIdToSubtask.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                switch (subtask.getStatus()) {
                    case NEW:
                        newStatusCounter++;
                        break;
                    case IN_PROGRESS:
                        break;
                    case DONE:
                        doneStatusCounter++;
                        break;
                }

                allSubtaskCounter++;
            }
        }

        if (allSubtaskCounter == 0 || newStatusCounter == allSubtaskCounter) {
            epic.setStatus(NEW);
        } else if (doneStatusCounter == allSubtaskCounter) {
            epic.setStatus(DONE);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }

    private void calcEpicTime(Epic epic) {
        LocalDateTime minSubtaskStartTime;
        LocalDateTime maxSubtaskEndTime;

        List<Subtask> subtasks = getAllSubtasksByEpicId(epic.getId());

        int epicDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .mapToInt(Integer::intValue)
                .sum();

        try {
            minSubtaskStartTime = subtasks.stream()
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElseThrow();

            maxSubtaskEndTime = subtasks.stream()
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElseThrow();
        } catch (NoSuchElementException e) {
            System.out.println("Не установлено время выполнение для подзадачи");
            return;
        }

        epic.setStartTime(minSubtaskStartTime);
        epic.setDuration(epicDuration);
        epic.setEndTime(maxSubtaskEndTime);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new LinkedList<>(prioritizedTasks);
    }

    public void checkIntersection(Task task) {
        boolean isIntersection = getPrioritizedTasks().stream().anyMatch(t -> t.getStartTime() != null
                && t.getId() != task.getId()
                && (task.getStartTime().isAfter(t.getStartTime()) && task.getStartTime().isBefore(t.getEndTime())
                    || task.getEndTime().isAfter(t.getStartTime()) && task.getEndTime().isBefore(t.getEndTime())
                    || task.getStartTime().equals(t.getStartTime())
                    || task.getStartTime().equals(t.getEndTime())
                    || task.getEndTime().equals(t.getStartTime())
                    || task.getEndTime().equals(t.getEndTime())));

        if (isIntersection) {
//            System.out.println("У задачи есть пересечения");
            throw new IllegalStateException("У задачи есть пересечения");
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected Map<Long, Task> getTaskIdToTask() {
        return taskIdToTask;
    }

    protected Map<Long, Epic> getEpicIdToEpic() {
        return epicIdToEpic;
    }

    protected Map<Long, Subtask> getSubtaskIdToSubtask() {
        return subtaskIdToSubtask;
    }
}
