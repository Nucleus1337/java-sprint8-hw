package service;

import model.*;
import util.Sequence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static model.TaskStatus.DONE;
import static model.TaskType.EPIC;
import static model.TaskType.SUBTASK;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final static String PATH_TO_SAVE = "resources/";
    private final String fileName;

    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();

        return subtask;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(long taskId) {
        Task task = super.getTaskById(taskId);
        save();

        return task;
    }

    @Override
    public Epic getEpicById(long epicId) {
        Epic epic = super.getEpicById(epicId);
        save();

        return epic;
    }

    @Override
    public Subtask getSubtaskById(long subtaskId) {
        Subtask subtask = super.getSubtaskById(subtaskId);
        save();

        return subtask;
    }

    @Override
    public void removeTaskById(long taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeSubtaskById(long subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeEpicById(long epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_TO_SAVE + fileName))) {
            for (Task task : getTaskIdToTask().values()) {
                writeTaskToFile(writer, task);
            }

            for (Task task : getEpicIdToEpic().values()) {
                writeTaskToFile(writer, task);
            }

            for (Task task : getSubtaskIdToSubtask().values()) {
                writeTaskToFile(writer, task);
            }

            String historyInString = historyToString(getHistoryManager());

            writer.newLine();
            writer.write(historyInString);
        } catch (IOException e) {
            System.out.printf("Не удалось найти путь для сохранения: %s\n", PATH_TO_SAVE + fileName);
        }
    }

    private void writeTaskToFile(BufferedWriter writer, Task task) throws IOException {
        writer.write(taskToString(task));
        writer.newLine();
    }

    private String taskToString(Task task) {
        String row = String.format("%s,%s,%s,%s,%s", task.getId(),
                task.getType(),
                task.getName(),
                task.getDescription(),
                task.getStatus());

        if (task.getType() == SUBTASK) {
            long epicId = ((Subtask) task).getEpicId();

            row = row.concat(",").concat(Long.toString(epicId));
        }

        if (task.getStartTime() != null) {
            String startTime = task.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
            row = row.concat(",")
                    .concat(startTime)
                    .concat(",")
                    .concat(Integer.toString(task.getDuration()));
        }

        return row;
    }

    private static void addTask(FileBackedTasksManager tasksManager, Task task) {
        final long taskId = task.getId();
        switch (task.getType()) {
            case EPIC:
                tasksManager.getEpicIdToEpic().put(taskId, (Epic) task);
                break;
            case SUBTASK:
                tasksManager.getSubtaskIdToSubtask().put(taskId, (Subtask) task);
                tasksManager.prioritizedTasks.add(task);
                break;
            default:
                tasksManager.getTaskIdToTask().put(taskId, task);
                tasksManager.prioritizedTasks.add(task);
        }
    }

    private static void addHistory(FileBackedTasksManager tasksManager, List<Integer> history) {
        for (Integer taskId : history) {
            Task task = findTask(tasksManager, taskId);
            tasksManager.getHistoryManager().add(task);
        }
    }

    private static void addSubtaskToEpic(FileBackedTasksManager tasksManager) {
        for (Map.Entry<Long, Subtask> entry : tasksManager.getSubtaskIdToSubtask().entrySet()) {
            final Subtask subtask = entry.getValue();
            final Epic epic = tasksManager.getEpicIdToEpic().get(subtask.getEpicId());

            epic.addSubtaskId(subtask.getId());
        }
    }

    private static Task findTask(FileBackedTasksManager tasksManager, long taskId) {
        final Epic epic = tasksManager.getEpicIdToEpic().get(taskId);
        if (epic != null) return epic;

        final Subtask subtask = tasksManager.getSubtaskIdToSubtask().get(taskId);
        if (subtask != null) return subtask;

        return tasksManager.getTaskIdToTask().get(taskId);
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        long sequenceValue = 1;
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file.getName());

        try {
            List<String> rows = Files.readAllLines(Path.of(file.toURI()));

            for (String row : rows) {
                if (row.isEmpty()) break;

                Task task = taskFromString(row);
                addTask(tasksManager, task);

                long taskId = task.getId();
                sequenceValue = Math.max(sequenceValue, taskId);
            }

            addSubtaskToEpic(tasksManager);

            Sequence.setStartId(sequenceValue + 1);

            String historyString = rows.get(rows.size() - 1);
            List<Integer> history = historyFromString(historyString);
            addHistory(tasksManager, history);
        } catch (IOException e) {
            System.out.printf("Невозможно найти файл для загрузки: %s\n", file);
        }

        return tasksManager;
    }

    private static Task taskFromString(String value) {
        final String[] taskValues = value.split(",");

        final long taskId = Long.parseLong(taskValues[0]);
        final TaskType taskType = TaskType.valueOf(taskValues[1]);
        final String taskName = taskValues[2];
        final String taskDescription = taskValues[3];
        final TaskStatus taskStatus = TaskStatus.valueOf(taskValues[4]);
        final String startTime;
        final int duration;
        
        if (taskType == EPIC) {
            return new Epic(taskName, taskDescription, taskStatus, taskId);
        } else if (taskType == SUBTASK) {
            final long epicId = Long.parseLong(taskValues[5]);
            startTime = taskValues[6];
            duration = Integer.parseInt(taskValues[7]);

            return new Subtask(taskName, taskDescription, taskStatus, taskId, epicId, startTime, duration);
        } else {
            startTime = taskValues[5];
            duration = Integer.parseInt(taskValues[6]);

            return new Task(taskName, taskDescription, taskStatus, taskId, startTime, duration);
        }
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder();
        for (Task taskHistory : manager.getHistory()) {
            builder.append(taskHistory.getId()).append(",");
        }

        return builder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new LinkedList<>();
        String[] elements = value.split(",");

        for (String element : elements) {
            history.add(Integer.parseInt(element));
        }

        return history;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    /**
     * запускает выполнение
     * - рассматриваем работу в нормальных условиях
     * -- если файл еще не существует, то следует разкоментировать создание тасков (файл будет создан автоматом)
     * -- если файл создан следует закомментировать создание или создавать что-то иное
     *
     */
    public static void main(String[] args) {
        FileBackedTasksManager manager = loadFromFile(new File("resources/fileToSave.csv"));

        manager.createTask(new Task("Task1", "TaskDesc1", "2022-01-01-12-46", 15)); /*1*/
        manager.createEpic(new Epic("Epic1", "Epic1 desc")); /*2*/
        manager.createSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", 2, "2022-01-01-12-46", 15)); /*3*/

        manager.updateTask(new Task("Task1", "TaskDesc1", DONE, 1));

        manager.getEpicById(2);
        manager.getTaskById(1);

        manager.createEpic(new Epic("Epic4", "Epic4 desc")); /*4*/

        System.out.println("=====CREATED=====");
        System.out.println("TASKS\n" + manager.getAllTasks());
        System.out.println("EPICS\n" + manager.getAllEpics());
        System.out.println("SUBTASKS\n" + manager.getAllSubtasks());

        System.out.println("=====HISTORY=====");
        System.out.println(manager.getHistory());
    }
}
