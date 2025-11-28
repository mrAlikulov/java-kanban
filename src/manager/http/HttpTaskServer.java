package manager.http;

import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import manager.http.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {

        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // --- Сначала длинные пути ---
        server.createContext("/tasks/task", new TaskHandler(manager));
        server.createContext("/tasks/epic", new EpicHandler(manager));
        server.createContext("/tasks/subtask", new SubtaskHandler(manager));
        server.createContext("/tasks/history", new HistoryHandler(manager));


        // --- Потом общий список задач ---
        server.createContext("/tasks", new PrioritizedHandler(manager));

        System.out.println("Сервер настроен!");
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на порту " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("HTTP-сервер остановлен.");
        server.stop(1);
    }
}
