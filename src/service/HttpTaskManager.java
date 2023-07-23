package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Epic;
import model.Subtask;
import model.Task;
import service.httpClient.KVTaskClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Managers.getGsonWithLocalDateTimeAdapter;

public class HttpTaskManager extends FileBackedTasksManager{
    private final static String[] kvServerTypes = {"task", "epic", "subtask", "history", "prioritized"};
    private static KVTaskClient kvTaskClient = null;
    private static Gson gson;

    public HttpTaskManager(String host, int port) {
        super(null);
        kvTaskClient = new KVTaskClient(host);

        gson = getGsonWithLocalDateTimeAdapter();
    }

    @Override
    protected void save() {
        Map<String, String> jTasks = new HashMap<>();
        jTasks.put(kvServerTypes[0], gson.toJson(getAllTasks()));
        jTasks.put(kvServerTypes[1], gson.toJson(getAllEpics()));
        jTasks.put(kvServerTypes[2], gson.toJson(getAllSubtasks()));
        jTasks.put(kvServerTypes[3], historyToString(getHistoryManager()));
        jTasks.put(kvServerTypes[4], gson.toJson(getPrioritizedTasks()));

        for (Map.Entry<String, String> entry : jTasks.entrySet()) {
            kvTaskClient.put(entry.getKey(), entry.getValue());
        }
    }

    public static HttpTaskManager loadFromServer(String host, int port) {
        HttpTaskManager manager = new HttpTaskManager(host, port);
        for (int i = 0; i < kvServerTypes.length; i++) {
            String data = kvTaskClient.load(kvServerTypes[i]);
            JsonArray jsonArray = new JsonArray();

            if (!kvServerTypes[i].equals("history")) {
                jsonArray = JsonParser.parseString(data).getAsJsonArray();
            }

            switch (i) {
                case 0:
                    addTasks(manager, jsonArray);
                    break;
                case 1:
                    addEpics(manager, jsonArray);
                    break;
                case 2:
                    addSubtasks(manager, jsonArray);
                    break;
                case 3:
                    addHistory(manager, data);
                    break;
                case 4:
                    addPrioritized(manager, jsonArray);
            }
        }

        return manager;
    }

    private static void addTasks(HttpTaskManager manager, JsonArray jsonArray) {
        for (JsonElement element : jsonArray) {
            Task task = gson.fromJson(element, Task.class);
            manager.getTaskIdToTask().put(task.getId(), task);
        }
    }

    private static void addEpics(HttpTaskManager manager, JsonArray jsonArray) {
        for (JsonElement element : jsonArray) {
            Epic epic = gson.fromJson(element, Epic.class);
            manager.getEpicIdToEpic().put(epic.getId(), epic);
        }
    }

    private static void addSubtasks(HttpTaskManager manager, JsonArray jsonArray) {
        for (JsonElement element : jsonArray) {
            Subtask subtask = gson.fromJson(element, Subtask.class);
            manager.getSubtaskIdToSubtask().put(subtask.getId(), subtask);
        }
    }

    private static void addHistory(HttpTaskManager manager, String history) {
        if (history.isBlank()) return;

        List<Integer> historyList = historyFromString(history);
        addHistory(manager, historyList);
    }

    private static void addPrioritized(HttpTaskManager manager, JsonArray jsonArray) {
        for (JsonElement element : jsonArray) {
            Task task = gson.fromJson(element, Task.class);
            manager.prioritizedTasks.add(task);
        }
    }
}
