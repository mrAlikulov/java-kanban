package manager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exceptions.TimeOverlapException;
import manager.http.BaseHttpHandler;
import model.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

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
    }

    // GET
    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            List<Subtask> subtasks = manager.getAllSubtasks();
            sendJson(exchange, 200, subtasks);
            return;
        }

        String[] parts = query.split("=");

        if (parts.length == 2 && parts[0].equals("id")) {
            int id = Integer.parseInt(parts[1]);
            Subtask sub = manager.getSubtaskById(id);

            if (sub == null) {
                sendText(exchange, 404, "Подзадача не найдена");
            } else {
                sendJson(exchange, 200, sub);
            }

        } else {
            sendText(exchange, 400, "Неверный запрос");
        }
    }

    // POST
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask == null) {
            sendText(exchange, 400, "Не удалось распарсить JSON");
            return;
        }

        try {
            if (manager.getSubtaskById(subtask.getId()) == null) {
                manager.createSubtask(subtask);
                sendText(exchange, 201, "Подзадача создана");
            } else {
                manager.updateSubtask(subtask);
                sendText(exchange, 200, "Подзадача обновлена");
            }
        } catch (TimeOverlapException e) {
            sendText(exchange, 409, e.getMessage());
        }
    }

    // DELETE
    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            sendText(exchange, 400, "Укажите id");
            return;
        }

        String[] parts = query.split("=");

        if (parts.length == 2 && parts[0].equals("id")) {
            manager.deleteSubtaskById(Integer.parseInt(parts[1]));
            sendText(exchange, 200, "Подзадача удалена");
        } else {
            sendText(exchange, 400, "Неверный запрос");
        }
    }
}
