package manager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.http.BaseHttpHandler;
import model.Epic;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
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

    // ---------- GET ----------
    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            List<Epic> epics = manager.getAllEpics();
            sendJson(exchange, 200, epics);
            return;
        }

        String[] parts = query.split("=");

        if (parts.length == 2 && parts[0].equals("id")) {
            int id = Integer.parseInt(parts[1]);
            Epic epic = manager.getEpicById(id);

            if (epic == null) {
                sendText(exchange, 404, "Эпик не найден");
            } else {
                sendJson(exchange, 200, epic);
            }

        } else {
            sendText(exchange, 400, "Неверный запрос");
        }
    }

    // ---------- POST ----------
    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic == null) {
            sendText(exchange, 400, "Не удалось распарсить JSON");
            return;
        }

        if (manager.getEpicById(epic.getId()) == null) {
            manager.createEpic(epic);
            sendText(exchange, 201, "Эпик создан");
        } else {
            manager.updateEpic(epic);
            sendText(exchange, 200, "Эпик обновлен");
        }
    }

    // ---------- DELETE ----------
    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            sendText(exchange, 400, "Укажите id");
            return;
        }

        String[] parts = query.split("=");

        if (parts.length == 2 && parts[0].equals("id")) {
            manager.deleteEpicById(Integer.parseInt(parts[1]));
            sendText(exchange, 200, "Эпик удалён");
        } else {
            sendText(exchange, 400, "Неверный запрос");
        }
    }
}
