import manager.FileBackedTaskManager;
import manager.TaskManager;
import manager.http.HttpTaskServer;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {

        File file = new File("tasks.csv");
        TaskManager manager = FileBackedTaskManager.loadFromFile(file);

        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();

        System.out.println("HTTP сервер запущен на http://localhost:8080/");
    }
}
