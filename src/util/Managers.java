package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.*;
import service.httpServer.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault(String fileName) {
        return new FileBackedTasksManager(fileName);
    }

    public static TaskManager getDefault(String host, int port) {
        return new HttpTaskManager(host, port);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGsonWithLocalDateTimeAdapter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());

        return gsonBuilder.create();
    }
}
