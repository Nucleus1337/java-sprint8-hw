package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.httpServer.HttpTaskServer;
import service.httpServer.KVServer;
import service.httpServer.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private final Task task1 = new Task("Task1", "Desc1", "2023-01-01-01-01", 1);
    private final Task task2 = new Task("Task2", "Desc2", "2023-02-01-01-01", 1);
    private KVServer kvServer;
    private HttpTaskServer taskServer;

    @BeforeEach
    void setUp() throws IOException {
        System.out.println("KVServer");
        kvServer = new KVServer();
        kvServer.start();

        System.out.println("HttpTaskServer");
        taskServer = new HttpTaskServer();
        taskServer.start();

        super.manager = new HttpTaskManager("http://localhost", 8087);
    }

    @AfterEach
    void disassamble() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void loadFromServer() {
        int expected = 1;
        manager.createTask(task1);

        HttpTaskManager manager1 = HttpTaskManager.loadFromServer("http://localhost", 8087);
        assertEquals(expected, manager1.getAllTasks().size());
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        Gson gson = gsonBuilder.create();

        String json = gson.toJson(task1);
        URI url = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача создана успешно", response.body());
    }

    @Test
    void getTaskByIdFromServer() throws IOException, InterruptedException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        Gson gson = gsonBuilder.create();

        HttpClient client = HttpClient.newHttpClient();

        String json = gson.toJson(task1);
        final URI urlToCreate = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlToCreate)
                .POST(body)
                .build();
        HttpResponse<String> responseOnPost = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnPost.statusCode());
        assertEquals("Задача создана успешно", responseOnPost.body());

        final URI urlToGet = URI.create("http://localhost:8080/tasks/task/?id=1");

        final HttpRequest requestToGet = HttpRequest.newBuilder()
                .uri(urlToGet)
                .GET()
                .build();
        HttpResponse<String> responseOnGet = client.send(requestToGet, HttpResponse.BodyHandlers.ofString());

        Task task = gson.fromJson(responseOnGet.body(), Task.class);

        assertEquals(200, responseOnGet.statusCode());
        assertEquals(1, task.getId());
    }

    @Test
    void getAllTasksFromServer() throws IOException, InterruptedException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        Gson gson = gsonBuilder.create();

        HttpClient client = HttpClient.newHttpClient();

        final String json1 = gson.toJson(task1);
        final String json2 = gson.toJson(task2);

        final URI urlToCreate = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlToCreate)
                .POST(body1)
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(urlToCreate)
                .POST(body2)
                .build();

        HttpResponse<String> responseOnPost1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseOnPost2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnPost1.statusCode());
        assertEquals("Задача создана успешно", responseOnPost1.body());
        assertEquals(200, responseOnPost2.statusCode());
        assertEquals("Задача создана успешно", responseOnPost2.body());

        final URI urlToGet = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest requestToGet = HttpRequest.newBuilder()
                .uri(urlToGet)
                .GET()
                .build();
        HttpResponse<String> responseOnGet = client.send(requestToGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnGet.statusCode());

        String response = responseOnGet.body();
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();

        assertEquals(2, jsonArray.size());
    }
}
