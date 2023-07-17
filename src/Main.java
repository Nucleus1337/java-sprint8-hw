import model.*;
import service.TaskManager;
import util.Managers;

import java.util.Random;

import static model.TaskStatus.DONE;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.createTask(new Task("Task1", "TaskDesc1", "2022-01-01-10-24", 30)); /*1*/
        taskManager.createTask(new Task("Task2", "TaskDesc2", "2022-01-01-10-46", 15)); /*2*/

        taskManager.createEpic(new Epic("Epic1", "Epic1 desc")); /*3*/
        taskManager.createSubtask(new Subtask("Subtask1 Epic1", "Subtask1 Epic1 desc", 3,
                "2022-01-01-12-46", 15)); /*4*/

        taskManager.createEpic(new Epic("Epic2", "Epic2 desc")); /*5*/
        taskManager.createSubtask(new Subtask("Subtask1 Epic2", "Subtask1 Epic2 desc", 5,
                "2022-01-01-12-46", 15)); /*6*/
        taskManager.createSubtask(new Subtask("Subtask2 Epic2", "Subtask2 Epic2 desc", 5,
                "2022-01-01-12-46", 15)); /*7*/

        System.out.println("=====CREATED=====");
        System.out.println("TASKS\n" + taskManager.getAllTasks());
        System.out.println("EPICS\n" + taskManager.getAllEpics());
        System.out.println("SUBTASKS\n" + taskManager.getAllSubtasks());

        taskManager.getTaskById(1);
        taskManager.getTaskById(1);

        taskManager.getEpicById(3);
        taskManager.getEpicById(3);
        taskManager.getEpicById(3);
        taskManager.getEpicById(5);

        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(7);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        System.out.println("=====HISTORY=====");
        System.out.println(taskManager.getHistory());

        taskManager.updateTask(new Task("Task1", "TaskDesc1", DONE, 1, "2022-01-01-10-24", 30));
        taskManager.updateSubtask(new Subtask("Subtask1 Epic2", "Subtask1 Epic2 desc plus some more text",
                DONE, 4, "2023-05-24-12-46", 68));
        taskManager.updateSubtask(new Subtask("Subtask1 Epic2", "Subtask1 Epic2 desc",
                DONE, 6, "2023-04-12-12-00", 15));

        System.out.println("=====UPDATED=====");
        System.out.println("TASKS\n" + taskManager.getAllTasks());
        System.out.println("EPICS\n" + taskManager.getAllEpics());
        System.out.println("SUBTASKS\n" + taskManager.getAllSubtasks());

        taskManager.removeTaskById(1);
        taskManager.removeSubtaskById(7);

        System.out.println("=====REMOVE-1 (task+subtask)=====");
        System.out.println("TASKS\n" + taskManager.getAllTasks());
        System.out.println("EPICS\n" + taskManager.getAllEpics());
        System.out.println("SUBTASKS\n" + taskManager.getAllSubtasks());

        System.out.println("=====HISTORY REMOVE-1 (task+subtask)=====");
        System.out.println(taskManager.getHistory());

        taskManager.removeSubtaskById(6);

        System.out.println("=====REMOVE-2 (subtask)=====");
        System.out.println("TASKS\n" + taskManager.getAllTasks());
        System.out.println("EPICS\n" + taskManager.getAllEpics());
        System.out.println("SUBTASKS\n" + taskManager.getAllSubtasks());

        System.out.println("=====HISTORY REMOVE-2 (subtask)=====");
        System.out.println(taskManager.getHistory());

        taskManager.clearAllEpics();

        System.out.println("=====CLEAR (epics)=====");
        System.out.println("TASKS\n" + taskManager.getAllTasks());
        System.out.println("EPICS\n" + taskManager.getAllEpics());
        System.out.println("SUBTASKS\n" + taskManager.getAllSubtasks());

        System.out.println("=====HISTORY CLEAR (epics)=====");
        System.out.println(taskManager.getHistory());
    }
}