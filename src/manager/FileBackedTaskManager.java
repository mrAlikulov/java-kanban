package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Менеджер, сохраняющий все данные о задачах в CSV-файл
 * и автоматически обновляющий его при каждом изменении.
 */
public class FileBackedTaskManager extends TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Сохранение

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,description,status,epicId");
            writer.newLine();


            for (Task task : getAllTasks()) {
                writer.write(toString(task, "TASK"));
                writer.newLine();
            }


            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic, "EPIC"));
                writer.newLine();
            }


            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask, "SUBTASK"));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения данных в файл: " + file.getAbsolutePath(), e);
        }
    }

    private String toString(Task task, String type) {
        int epicId = -1;
        if (task instanceof Subtask) {
            epicId = ((Subtask) task).getEpicId();
        }
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName().replace(",", " "),
                task.getDescription().replace(",", " "),
                task.getStatus(),
                (epicId == -1 ? "" : epicId));
    }

    // Загрузка

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        if (!file.exists()) {
            return manager;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) {
                return manager;
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;
                manager.fromString(line);
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки данных из файла: " + file.getAbsolutePath(), e);
        }

        return manager;
    }

    private void fromString(String line) {
        String[] fields = line.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        String description = fields[3];
        Status status = Status.valueOf(fields[4]);
        String epicIdStr = fields.length > 5 ? fields[5] : "";

        switch (type) {
            case "TASK":
                Task task = new Task(id, name, description);
                task.setStatus(status);
                createTask(task);
                break;
            case "EPIC":
                Epic epic = new Epic(id, name, description);
                epic.setStatus(status);
                createEpic(epic);
                break;
            case "SUBTASK":
                int epicId = Integer.parseInt(epicIdStr);
                Subtask subtask = new Subtask(id, name, description, epicId);
                subtask.setStatus(status);
                createSubtask(subtask);
                break;
        }
    }



    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }
}
