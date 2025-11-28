package manager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exceptions.TimeOverlapException;
import manager.http.BaseHttpHandler;
import model.Task;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, query);
                    break;

                case "POST":
                    handlePost(exchange);
                    break;

                case "DELETE":
                    handleDelete(exchange, query);
                    break;

                default:
                    sendText(exchange, 405, "Метод не поддерживается");
            }

        } catch (Exception e) {
            sendText(exchange, 500, "Ошибка сервера: " + e.getMessage());
        }
    }

    // ================= GET =================
    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            List<Task> tasks = manager.getAllTasks();
            sendJson(exchange, 200, tasks);
            return;
        }

        if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Task task = manager.getTaskById(id);

            if (task == null) {
                sendText(exchange, 404, "Задача не найдена");
                return;
            }

            sendJson(exchange, 200, task);
        }
    }

    // ================= POST =================
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Task task = gson.fromJson(body, Task.class);

        try {
            if (manager.getTaskById(task.getId()) == null) {
                manager.createTask(task);
                sendText(exchange, 201, "Задача создана");
            } else {
                manager.updateTask(task);
                sendText(exchange, 200, "Задача обновлена");
            }
        } catch (TimeOverlapException e) {
            sendText(exchange, 406, e.getMessage());
        }
    }

    // ================= DELETE =================
    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null || !query.startsWith("id=")) {
            sendText(exchange, 400, "Укажите id");
            return;
        }

        int id = Integer.parseInt(query.substring(3));
        manager.deleteTaskById(id);
        sendText(exchange, 200, "Задача удалена");
    }
}
