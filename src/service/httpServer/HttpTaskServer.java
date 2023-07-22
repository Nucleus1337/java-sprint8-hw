package service.httpServer;

import com.sun.net.httpserver.HttpServer;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

//import static service.HttpTaskManager.loadFromServer;

public class HttpTaskServer {
    private HttpServer httpServer;

    public void start() throws IOException {
        int DEFAULT_PORT = 8080;

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(DEFAULT_PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());

        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + DEFAULT_PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }
}
