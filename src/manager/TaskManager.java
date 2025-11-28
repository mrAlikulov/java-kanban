package manager;

import manager.exceptions.TimeOverlapException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    int generateId();

    void createTask(Task task)throws TimeOverlapException;

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask)throws TimeOverlapException;

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void updateTask(Task task)throws TimeOverlapException;

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask)throws TimeOverlapException;

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);
    List<Task> getPrioritizedTasks();

    List<Task> getHistory();
}
