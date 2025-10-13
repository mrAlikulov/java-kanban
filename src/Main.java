import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Task;

public class Main {
    public static void main(String[] args) {
        HistoryManager history = new InMemoryHistoryManager();

        Task t1 = new Task(1, "Сходить в магазин", "Купить молоко и хлеб");
        Task t2 = new Task(2, "Сделать ДЗ", "Подготовиться к спринту 6");
        Task t3 = new Task(3, "Пробежка", "Утренняя пробежка 5 км");

        // Добавляем задачи в историю
        history.add(t1);
        history.add(t2);
        history.add(t3);

        // Повторный просмотр t2 — старый должен исчезнуть
        history.add(t2);

        System.out.println("История:");
        for (Task t : history.getHistory()) {
            System.out.println(t);
        }

        // Удаляем задачу 1
        history.remove(1);

        System.out.println("\nПосле удаления:");
        for (Task t : history.getHistory()) {
            System.out.println(t);
        }
    }
}