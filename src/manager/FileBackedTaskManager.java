package manager;

import manager.exceptions.ManagerSaveException;
import manager.exceptions.TimeOverlapException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Менеджер, сохраняющий все данные о задачах в CSV-файл
 * и автоматически обновляющий его при каждом изменении.
 */
public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Сохранение

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,startTime,duration,epicId");
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
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file.getAbsolutePath(), e);
        }
    }

    private String toString(Task task, String type) {
        String startTime = task.getStartTime() == null ? "" : task.getStartTime().toString();

        String duration = (task instanceof Epic || task.getDuration() == null)
                ? ""
                : String.valueOf(task.getDuration().toMinutes());

        int epicId = task instanceof Subtask ? ((Subtask) task).getEpicId() : -1;

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName().replace(",", " "),
                task.getStatus().toString(),
                task.getDescription().replace(",", " "),
                startTime,
                duration,
                epicId == -1 ? "" : String.valueOf(epicId)
        );
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
            throw new ManagerSaveException("Ошибка загрузки данных из файла: " + file.getAbsolutePath(), e);

        }
        manager.getAllEpics().forEach(manager::updateEpicTime);
        return manager;
    }

    private void fromString(String line) {
        String[] fields = line.split(",", -1);

        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        LocalDateTime startTime = fields[5].isEmpty() ? null : LocalDateTime.parse(fields[5]);
        Duration duration = fields[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(fields[6]));
        String epicIdStr = fields[7];

        switch (type) {
            case "TASK":
                Task task = new Task(id, name, description);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
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
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                createSubtask(subtask);
                break;
        }
    }


    @Override
    public void createTask(Task task) throws TimeOverlapException {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) throws TimeOverlapException{
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) throws TimeOverlapException{
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TimeOverlapException{
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
