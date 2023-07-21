package util;

import service.*;

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
}
