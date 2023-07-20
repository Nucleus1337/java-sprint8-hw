package service;

import service.httpClient.KVTaskClient;

public class HttpTaskManager extends FileBackedTasksManager{
    KVTaskClient kvTaskClient = new KVTaskClient();

    public HttpTaskManager(String fileName) {
        super(null);
    }

    @Override
    protected void save() {

    }
}
