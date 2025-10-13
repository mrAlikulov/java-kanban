package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    @Test
    void add_AndGetHistory_Should_Work() {
        HistoryManager history = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1");
        Task t2 = new Task(2, "Task2", "Desc2");

        history.add(t1);
        history.add(t2);

        List<Task> result = history.getHistory();

        assertEquals(2, result.size(), "История должна содержать 2 задачи");
        assertEquals(t1, result.get(0), "Первая задача должна быть Task1");
        assertEquals(t2, result.get(1), "Вторая задача должна быть Task2");
    }
    @Test
    void remove_Should_Delete_Task_From_History() {
        HistoryManager history = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1");
        Task t2 = new Task(2, "Task2", "Desc2");

        history.add(t1);
        history.add(t2);

        // Удаляем первую задачу
        history.remove(1);

        List<Task> result = history.getHistory();

        assertEquals(1, result.size(), "История должна содержать 1 задачу после удаления");
        assertEquals(t2, result.get(0), "В истории должна остаться только Task2");
    }
    @Test
    void add_Should_Not_Create_Duplicates() {
        HistoryManager history = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1");

        history.add(t1);
        history.add(t1);
        history.add(t1);

        List<Task> result = history.getHistory();

        assertEquals(1, result.size(), "В истории не должно быть дубликатов");
        assertEquals(t1, result.get(0), "В истории должна быть только Task1");
    }
    @Test
    void re_Add_Task_Should_Move_It_To_End() {
        HistoryManager history = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1");
        Task t2 = new Task(2, "Task2", "Desc2");
        Task t3 = new Task(3, "Task3", "Desc3");

        history.add(t1);
        history.add(t2);
        history.add(t3);

        history.remove(2);  // удаляем t2
        history.add(t2);    // снова открываем t2

        List<Task> result = history.getHistory();

        assertEquals(3, result.size(), "Должно быть 3 задачи");
        assertEquals(t1, result.get(0), "Первая задача должна быть Task1");
        assertEquals(t3, result.get(1), "Вторая задача должна быть Task3");
        assertEquals(t2, result.get(2), "Task2 должна вернуться в конец истории");
    }
}
