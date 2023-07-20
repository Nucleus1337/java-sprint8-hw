package service.httpServer;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final static int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(DEFAULT_PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler());

            httpServer.start();

            System.out.println("HTTP-сервер запущен на " + DEFAULT_PORT + " порту!");
//            new KVServer().start();

        } catch (IOException e) {
            System.out.println("Возникли проблемы в работе: " + e.getMessage());
        }
    }
}
