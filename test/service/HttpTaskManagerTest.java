package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

    private final String task1Json = "{\n" +
                                        "  \"name\":\"Task1\",\n" +
                                        "  \"description\":\"Desc1\",\n" +
                                        "\t\"startTime\": \"2023-01-01-01-01\",\n" +
                                        "\t\"duration\": 1\n" +
                                        "}";
    private final String task2Json = "{\n" +
                                        "  \"name\":\"Task2\",\n" +
                                        "  \"description\":\"Desc2\",\n" +
                                        "\t\"startTime\": \"2023-02-01-01-01\",\n" +
                                        "\t\"duration\": 2\n" +
                                        "}";
    private final String epic1Json = "{\n" +
                                        "\"name\":\"Epic1\",\n" +
                                        "\"description\":\"DescEpic1\"\n" +
                                        "}";
    private final String epic2Json = "{\n" +
                                        "\"name\":\"Epic2\",\n" +
                                        "\"description\":\"DescEpic2\"\n" +
                                        "}";

    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private static HttpClient client;
    private static Gson gson;

    @BeforeAll
    static void beforeAll() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @BeforeEach
    void setUp() throws IOException {
        System.out.println("KVServer");
        kvServer = new KVServer();
        kvServer.start();

        System.out.println("HttpTaskServer");
        taskServer = new HttpTaskServer();
        taskServer.start();

        manager = new HttpTaskManager("http://localhost", 8087);
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
        final URI url = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task1Json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача создана успешно", response.body());
    }

    @Test
    void getTaskByIdFromServer() throws IOException, InterruptedException {
        final URI urlToCreate = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task1Json);
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
        final URI urlToCreate = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(task1Json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(task2Json);

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

    @Test
    void createEpic() throws IOException, InterruptedException {
        final URI url = URI.create("http://localhost:8080/tasks/epic/");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epic1Json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Эпик создан успешно", response.body());
    }
}
