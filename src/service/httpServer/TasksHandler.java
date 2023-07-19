package service.httpServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import model.TaskType;
import service.TaskManager;
import util.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static model.TaskType.EPIC;
import static model.TaskType.TASK;
import static service.httpServer.Endpoint.GET_EPICS;
import static service.httpServer.Endpoint.GET_PRIOR_TASKS;
import static service.httpServer.Endpoint.GET_SUBTASKS;
import static service.httpServer.Endpoint.GET_TASKS;
import static service.httpServer.Endpoint.POST_ADD_TASK;
import static service.httpServer.Endpoint.UNKNOWN;

public class TasksHandler implements HttpHandler {
    private static final Gson gson;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final TaskManager manager = Managers.getDefault("fileToSave.csv");

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();

        manager.createTask(new Task("Task1", "TaskDesc1", "2022-01-01-10-24", 3)); /*1*/
        manager.createTask(new Task("Task2", "TaskDesc2", "2022-01-01-10-46", 15)); /*2*/
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET_TASKS:
                getAllTasks(exchange);
                break;
            case POST_ADD_TASK:
                createTask(exchange);
                break;
            case UNKNOWN:
                writeResponse(exchange, "Эндпоинт " + path + " не существует", 404);
        }
    }

    private Endpoint getEndpoint(String path, String method) throws IOException {
        final String task = "task";
        final String epic = "epic";
        final String subtask = "subtask";
        String[] pathArray = path.split("/");
        String taskType = "";

        if (pathArray.length > 2) {
            taskType = pathArray[2];
        }

        switch (method) {
            case "GET":
                switch (taskType) {
                    case task:
                        return GET_TASKS;
                    case epic:
                        return GET_EPICS;
                    case subtask:
                        return GET_SUBTASKS;
                }

                return GET_PRIOR_TASKS;
            case "POST":
                if (taskType.equals("task")) {
                    return POST_ADD_TASK;
                }

                break;
            case "DELETE":

                break;
        }

        return UNKNOWN;
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(manager.getAllTasks());
        writeResponse(exchange, responseString, 200);
    }

    private void getAllEpicss(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(manager.getAllEpics());
        writeResponse(exchange, responseString, 200);
    }

    private void getAllSubtasks(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(manager.getAllSubtasks());
        writeResponse(exchange, responseString, 200);
    }

    private void createTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Task task = gson.fromJson(body, Task.class);

        manager.createTask(task);
        writeResponse(exchange, "Задача создана успешно", 200);
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isEmpty()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(bytes);
            }
        }

        exchange.close();
    }
}
