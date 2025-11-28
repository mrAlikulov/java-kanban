package manager.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.http.BaseHttpHandler;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendText(exchange, 405, "Метод не поддерживается");
            return;
        }

        List<Task> tasks = manager.getPrioritizedTasks();
        sendJson(exchange, 200, tasks);
    }
}
