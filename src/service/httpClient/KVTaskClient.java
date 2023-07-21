package service.httpClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final int port = 8087;
    private final String host;
    private String token;
    private URI uri;

    public KVTaskClient(String host) {
        this.host = host;
        getToken();
    }

    public void put(String key, String json) {
        System.out.println("Сохраняем значения для ключа: " + key);
        String uriString = String.format("%s:%s/save/%s?API_TOKEN=%s", host, port, key, token);
        URI uri = URI.create(uriString);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();

        HttpResponse<String> response = createClient(request);

        if (response != null) {
            System.out.println("==Вернулся ответ с кодом: " + response.statusCode());
        } else {
            System.out.println("==Вернулся пустой ответ.");
        }
    }

    public String load(String key) {
        System.out.println("Загружаем данные для ключа: " + key);
        String uriString = String.format("%s:%s/load/%s?API_TOKEN=%s", host, port, key, token);
        URI uri = URI.create(uriString);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = createClient(request);
        if (response == null) {
            return null;
        }

        return response.body();
    }

    private void getToken() {
        URI uri = URI.create(host + ':' + port + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = createClient(request);

        token = response == null ? null : response.body();
    }

    private HttpResponse<String> createClient(HttpRequest request) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

            return httpClient.send(request, handler);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
