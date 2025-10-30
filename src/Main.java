

import manager.FileBackedTaskManager;
import manager.TaskManager;
import model.*;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");

        TaskManager manager = FileBackedTaskManager.loadFromFile(file);


        Task task1 = new Task(1, "Проверить почту", "Проверить входящие письма");
        task1.setStatus(Status.NEW);
        manager.createTask(task1);

        Epic epic1 = new Epic(2, "Учёба", "Подготовка к экзамену");
        manager.createEpic(epic1);

        Subtask sub1 = new Subtask(3, "Прочитать лекцию", "Лекция по финансам", epic1.getId());
        sub1.setStatus(Status.IN_PROGRESS);
        manager.createSubtask(sub1);

        Subtask sub2 = new Subtask(4, "Сделать конспект", "Краткий пересказ лекции", epic1.getId());
        sub2.setStatus(Status.DONE);
        manager.createSubtask(sub2);

        System.out.println("✅ Данные сохранены в файл: " + file.getAbsolutePath());

        // загрузка данные обратно
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        System.out.println("\n📂 Данные, загруженные из файла:");
        for (Task t : loaded.getAllTasks()) {
            System.out.println(t);
        }
        for (Epic e : loaded.getAllEpics()) {
            System.out.println(e);
        }
        for (Subtask s : loaded.getAllSubtasks()) {
            System.out.println(s);
        }
    }
}
