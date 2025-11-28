package test.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.FileBackedTaskManager;
import manager.TaskManager;
import manager.http.HttpTaskServer;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private static HttpTaskServer server;
    private static TaskManager manager;
    private static HttpClient client;
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    @BeforeAll
    static void beforeAll() throws IOException {
        manager = FileBackedTaskManager.loadFromFile(new java.io.File("test.csv"));
        server = new HttpTaskServer(manager);
        server.start();

        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void shouldCreateAndReturnTask() throws Exception {

        Task task = new Task(1, "Test", "Desc");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));

        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        // Проверяем GET
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task?id=1"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("\"name\":\"Test\""));
    }
}
