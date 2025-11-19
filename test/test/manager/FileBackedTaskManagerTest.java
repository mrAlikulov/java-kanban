package test.manager;
import manager.FileBackedTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    File file;
    FileBackedTaskManager manager;

    @BeforeEach
    void setup() throws IOException {
        file = File.createTempFile("test", ".csv");
        manager = FileBackedTaskManager.loadFromFile(file);
    }

    @AfterEach
    void cleanup() {
        file.delete();
    }

    // ---------- 1. сохранение и загрузка задачи ----------
    @Test
    void shouldSaveAndLoadTask() {
        Task task = new Task(1, "Test Task", "Desc");
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        task.setDuration(Duration.ofMinutes(30));
        manager.createTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        Task loadedTask = loaded.getTaskById(1);
        assertNotNull(loadedTask);
        assertEquals("Test Task", loadedTask.getName());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), loadedTask.getStartTime());
        assertEquals(Duration.ofMinutes(30), loadedTask.getDuration());
    }

    // ---------- 2. сохранение и загрузка эпика ----------
    @Test
    void shouldSaveAndLoadEpic() {
        Epic epic = new Epic(1, "Epic1", "Desc");
        manager.createEpic(epic);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        Epic loadedEpic = loaded.getEpicById(1);

        assertNotNull(loadedEpic);
        assertEquals("Epic1", loadedEpic.getName());
    }

    // ---------- 3. Epic + Subtask ----------
    @Test
    void shouldSaveAndLoadEpicWithSubtasks() {
        Epic epic = new Epic(1, "Epic", "Desc");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Sub", "Desc", 1);
        subtask.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        subtask.setDuration(Duration.ofMinutes(60));
        manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        Epic loadedEpic = loaded.getEpicById(1);
        Subtask loadedSub = loaded.getSubtaskById(2);

        assertNotNull(loadedEpic);
        assertNotNull(loadedSub);
        assertEquals(1, loadedEpic.getSubtaskIds().size());
    }

    // ---------- 4. Пересчёт времени эпика ----------
    @Test
    void shouldRecalculateEpicTimeAfterLoading() {
        Epic epic = new Epic(1, "Epic", "Desc");
        manager.createEpic(epic);

        Subtask s1 = new Subtask(2, "S1", "Desc", 1);
        s1.setStartTime(LocalDateTime.of(2024, 1, 1, 8, 0));
        s1.setDuration(Duration.ofMinutes(30));
        manager.createSubtask(s1);

        Subtask s2 = new Subtask(3, "S2", "Desc", 1);
        s2.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        s2.setDuration(Duration.ofMinutes(60));
        manager.createSubtask(s2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        Epic loadedEpic = loaded.getEpicById(1);

        assertEquals(Duration.ofMinutes(90), loadedEpic.getDuration());
        assertEquals(LocalDateTime.of(2024, 1, 1, 8, 0), loadedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2024, 1, 1, 11, 0), loadedEpic.getEndTime());
    }

    // ---------- 5. Проверка приоритета задач ----------
    @Test
    void shouldLoadPrioritizedTasksInOrder() {
        Task t1 = new Task(1, "T1", "Desc");
        t1.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        t1.setDuration(Duration.ofMinutes(30));
        manager.createTask(t1);

        Task t2 = new Task(2, "T2", "Desc");
        t2.setStartTime(LocalDateTime.of(2024, 1, 1, 8, 0));
        t2.setDuration(Duration.ofMinutes(30));
        manager.createTask(t2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        var list = loaded.getPrioritizedTasks();
        assertEquals(2, list.get(0).getId());
        assertEquals(1, list.get(1).getId());
    }

    // ---------- 6. Пересечение задач ----------
    @Test
    void shouldThrowIfTasksIntersect() {
        Task t1 = new Task(1, "T1", "Desc");
        t1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        t1.setDuration(Duration.ofMinutes(60));
        manager.createTask(t1);

        Task t2 = new Task(2, "T2", "Desc");
        t2.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 30));
        t2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> manager.createTask(t2));
    }

    // ---------- 7. Пустой файл ----------
    @Test
    void shouldLoadEmptyFile() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

}