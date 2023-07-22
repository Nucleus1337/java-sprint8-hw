package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import model.Epic;
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
import static util.Sequence.setStartId;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
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
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @BeforeEach
    void setUp() throws IOException {
        manager = new HttpTaskManager("http://localhost", 8087);

        System.out.println("setUp begin");
        System.out.println("KVServer");
        kvServer = new KVServer();
        kvServer.start();

        System.out.println("HttpTaskServer");
        taskServer = new HttpTaskServer();
        taskServer.start();
        System.out.println("setUp end");

        setStartId(1);
    }

    @AfterEach
    void disassamble() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void loadFromServer() throws IOException, InterruptedException {
        int expected = 1;

        final URI url = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task1Json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача создана успешно", response.body());

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

    @Test
    void getEpicByIdFromServer() throws IOException, InterruptedException {
        final URI urlToCreate = URI.create("http://localhost:8080/tasks/epic/");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epic1Json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlToCreate)
                .POST(body)
                .build();
        HttpResponse<String> responseOnPost = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnPost.statusCode());
        assertEquals("Эпик создан успешно", responseOnPost.body());

        final URI urlToGet = URI.create("http://localhost:8080/tasks/epic/?id=1");

        final HttpRequest requestToGet = HttpRequest.newBuilder()
                .uri(urlToGet)
                .GET()
                .build();
        HttpResponse<String> responseOnGet = client.send(requestToGet, HttpResponse.BodyHandlers.ofString());

        Task task = gson.fromJson(responseOnGet.body(), Epic.class);

        assertEquals(200, responseOnGet.statusCode());
        assertEquals(1, task.getId());
    }

    @Test
    void getAllEpicsFromServer() throws IOException, InterruptedException {
        final URI urlToCreate = URI.create("http://localhost:8080/tasks/epic/");

        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(epic1Json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(epic2Json);

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
        assertEquals("Эпик создан успешно", responseOnPost1.body());
        assertEquals(200, responseOnPost2.statusCode());
        assertEquals("Эпик создан успешно", responseOnPost2.body());

        final URI urlToGet = URI.create("http://localhost:8080/tasks/epic/");

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
    void deleteAllTasks() throws IOException, InterruptedException {
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

        final HttpRequest requestToDelete = HttpRequest.newBuilder()
                .uri(urlToGet)
                .DELETE()
                .build();
        HttpResponse<String> responseOnDelete = client.send(requestToDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnDelete.statusCode());

        final HttpRequest requestToGet = HttpRequest.newBuilder()
                .uri(urlToGet)
                .GET()
                .build();
        HttpResponse<String> responseOnGet = client.send(requestToGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnGet.statusCode());

        String response = responseOnGet.body();
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();

        assertEquals(0, jsonArray.size());
    }

    @Test
    void deleteAllEpics() throws IOException, InterruptedException {
        final URI urlToCreate = URI.create("http://localhost:8080/tasks/epic/");

        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(epic1Json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(epic2Json);

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
        assertEquals("Эпик создан успешно", responseOnPost1.body());
        assertEquals(200, responseOnPost2.statusCode());
        assertEquals("Эпик создан успешно", responseOnPost2.body());

        final URI urlToGet = URI.create("http://localhost:8080/tasks/epic/");

        final HttpRequest requestToDelete = HttpRequest.newBuilder()
                .uri(urlToGet)
                .DELETE()
                .build();
        HttpResponse<String> responseOnDelete = client.send(requestToDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnDelete.statusCode());

        final HttpRequest requestToGet = HttpRequest.newBuilder()
                .uri(urlToGet)
                .GET()
                .build();
        HttpResponse<String> responseOnGet = client.send(requestToGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnGet.statusCode());

        String response = responseOnGet.body();
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();

        assertEquals(0, jsonArray.size());
    }

    @Test
    void getHistoryFromServer() throws IOException, InterruptedException {
        final URI urlToCreate = URI.create("http://localhost:8080/tasks/task/");

        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(task1Json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(task2Json);

        HttpRequest request1 = HttpRequest.newBuilder().uri(urlToCreate).POST(body1).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(urlToCreate).POST(body2).build();

        HttpResponse<String> responseOnPost1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseOnPost2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnPost1.statusCode());
        assertEquals("Задача создана успешно", responseOnPost1.body());
        assertEquals(200, responseOnPost2.statusCode());
        assertEquals("Задача создана успешно", responseOnPost2.body());

        final HttpRequest requestToGet1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .GET()
                .build();
        HttpResponse<String> response1 = client.send(requestToGet1, HttpResponse.BodyHandlers.ofString());

        final HttpRequest requestToGet2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=2"))
                .GET()
                .build();
        HttpResponse<String> response2 = client.send(requestToGet2, HttpResponse.BodyHandlers.ofString());


        final URI urlToGet = URI.create("http://localhost:8080/tasks/history/");

        final HttpRequest requestToGet = HttpRequest.newBuilder().uri(urlToGet).GET().build();
        HttpResponse<String> responseOnGet = client.send(requestToGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnGet.statusCode());

        String response = responseOnGet.body();
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();

        assertEquals(2, jsonArray.size());
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
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

        final URI urlToGet = URI.create("http://localhost:8080/tasks/task/?id=1");

        final HttpRequest requestToDelete = HttpRequest.newBuilder()
                .uri(urlToGet)
                .DELETE()
                .build();
        HttpResponse<String> responseOnDelete = client.send(requestToDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnDelete.statusCode());

        final HttpRequest requestToGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=2"))
                .GET()
                .build();
        HttpResponse<String> responseOnGet = client.send(requestToGet, HttpResponse.BodyHandlers.ofString());

        Task task = gson.fromJson(responseOnGet.body(), Task.class);
        assertEquals(200, responseOnGet.statusCode());
        assertEquals(2, task.getId());
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        final URI urlToCreate = URI.create("http://localhost:8080/tasks/epic/");

        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(epic1Json);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(epic2Json);

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
        assertEquals("Эпик создан успешно", responseOnPost1.body());
        assertEquals(200, responseOnPost2.statusCode());
        assertEquals("Эпик создан успешно", responseOnPost2.body());

        final URI urlToGet = URI.create("http://localhost:8080/tasks/epic/?id=1");

        final HttpRequest requestToDelete = HttpRequest.newBuilder()
                .uri(urlToGet)
                .DELETE()
                .build();
        HttpResponse<String> responseOnDelete = client.send(requestToDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseOnDelete.statusCode());

        final HttpRequest requestToGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=2"))
                .GET()
                .build();
        HttpResponse<String> responseOnGet = client.send(requestToGet, HttpResponse.BodyHandlers.ofString());

        Epic epic = gson.fromJson(responseOnGet.body(), Epic.class);

        assertEquals(200, responseOnGet.statusCode());
        assertEquals(2, epic.getId());
    }
}
