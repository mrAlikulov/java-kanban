package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
public class InMemoryTaskManager implements TaskManager{
    private int currentId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );
    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }


    public int generateId() {
        return currentId++;
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null && task.getEndTime() != null && hasIntersection(task)) {

            tasks.remove(task.getId());
            throw new IllegalArgumentException("Невозможно создать задачу — пересечение по времени с другой задачей.");
        }

        addToPrioritizedTasks(task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null && subtask.getEndTime() != null && hasIntersection(subtask)) {
            subtasks.remove(subtask.getId());
            throw new IllegalArgumentException("Невозможно создать подзадачу — пересечение по времени с другой задачей.");
        }
        addToPrioritizedTasks(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }
    void updateEpicTime(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            // Нет подзадач → сбрасываем время
            epic.updateTime(
                    Duration.ZERO,
                    null,
                    null
            );
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime start = null;
        LocalDateTime end = null;

        for (Integer id : subtaskIds) {
            Subtask sub = subtasks.get(id);
            if (sub == null) continue;

            // duration
            if (sub.getDuration() != null) {
                totalDuration = totalDuration.plus(sub.getDuration());
            }

            // startTime
            if (sub.getStartTime() != null) {
                if (start == null || sub.getStartTime().isBefore(start)) {
                    start = sub.getStartTime();
                }
            }

            // endTime
            if (sub.getEndTime() != null) {
                if (end == null || sub.getEndTime().isAfter(end)) {
                    end = sub.getEndTime();
                }
            }
        }

        epic.updateTime(totalDuration, start, end);
    }


    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        epics.values().forEach(epic -> {
            epic.clearSubtasks();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        });

        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void updateTask(Task task) {
        Task old = tasks.get(task.getId());

        removeFromPrioritizedTasks(old);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null && task.getEndTime() != null && hasIntersection(task)) {
            tasks.put(old.getId(), old);
            addToPrioritizedTasks(old);
            throw new IllegalArgumentException(
                    "Невозможно обновить задачу — пересечение по времени с другой задачей."
            );
        }

        addToPrioritizedTasks(task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask old = subtasks.get(subtask.getId());
        removeFromPrioritizedTasks(subtasks.get(subtask.getId()));

        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null && subtask.getEndTime() != null && hasIntersection(subtask)) {
            subtasks.put(old.getId(), old);
            addToPrioritizedTasks(old);
            throw new IllegalArgumentException("Невозможно обновить подзадачу — пересечение по времени с другой задачей.");
        }
        addToPrioritizedTasks(subtask);

        updateEpicStatus(epics.get(subtask.getEpicId()));
        updateEpicTime(epics.get(subtask.getEpicId()));

    }

    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            removeFromPrioritizedTasks(task);
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        removeFromPrioritizedTasks(subtask);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) id);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }
    private boolean isTimeOverlap(Task a, Task b) {
        if (a == null || b == null) return false;

        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd = a.getEndTime();
        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd = b.getEndTime();

        if (aStart == null || aEnd == null || bStart == null || bEnd == null) {
            return false; // задачи без времени не участвуют в проверке
        }

        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private boolean hasIntersection(Task task) {
        if (task == null || task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .anyMatch(other -> other.getId() != task.getId() && isTimeOverlap(task, other));
    }

    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return Collections.emptyList();

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private void updateEpicStatus(Epic epic) {
        List<Status> statuses = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getStatus)
                .toList();

        if (statuses.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = statuses.stream().allMatch(s -> s == Status.NEW);
        boolean allDone = statuses.stream().allMatch(s -> s == Status.DONE);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

}
