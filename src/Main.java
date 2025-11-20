import manager.FileBackedTaskManager;
import manager.TaskManager;
import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args)throws Exception {

        File file = new File("tasks.csv");
        TaskManager manager = FileBackedTaskManager.loadFromFile(file);

        // ---- Создаем задачи ----
        Task task1 = new Task(manager.generateId(), "Помыть машину", "Сходить на мойку");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        Epic epic1 = new Epic(manager.generateId(), "Учёба", "Подготовка к экзаменам");
        manager.createEpic(epic1);

        Subtask sub1 = new Subtask(manager.generateId(), "Прочитать главу 1", "Главы 1", epic1.getId());
        sub1.setStartTime(LocalDateTime.now().plusHours(1));
        sub1.setDuration(Duration.ofMinutes(45));
        manager.createSubtask(sub1);

        Subtask sub2 = new Subtask(manager.generateId(), "Решить задачи", "Тренировка", epic1.getId());
        sub2.setStartTime(LocalDateTime.now().plusHours(2));
        sub2.setDuration(Duration.ofMinutes(60));
        manager.createSubtask(sub2);

        // ---- Обновляем статус ----
        sub1.setStatus(Status.DONE);
        manager.updateSubtask(sub1);

        // ---- Выводим всё ----
        System.out.println("\n=== Все задачи ===");
        manager.getAllTasks().forEach(System.out::println);

        System.out.println("\n=== Все эпики ===");
        manager.getAllEpics().forEach(System.out::println);

        System.out.println("\n=== Все подзадачи ===");
        manager.getAllSubtasks().forEach(System.out::println);

        System.out.println("\n=== Подзадачи эпика '" + epic1.getName() + "' ===");
        manager.getSubtasksOfEpic(epic1.getId()).forEach(System.out::println);

        System.out.println("\n=== Приоритетные задачи ===");
        manager.getPrioritizedTasks().forEach(System.out::println);

        // ---- Проверяем загрузку ----
        TaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        System.out.println("\n=== Загружено из файла ===");
        loaded.getAllTasks().forEach(System.out::println);
        loaded.getAllEpics().forEach(System.out::println);
        loaded.getAllSubtasks().forEach(System.out::println);
    }
}
