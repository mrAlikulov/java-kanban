package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>(); // быстрый доступ
    private Node head; // первый элемент списка
    private Node tail; // последний элемент списка

    @Override
    public void add(Task task) {
        if (task == null) return;

        remove(task.getId());

        Node newNode = new Node(tail, task, null);

        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode; // список пустой → это первый элемент
        }

        tail = newNode;

        // 3. Записываем в HashMap
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next; // если удаляем первый элемент
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev; // если удаляем последний элемент
        }
    }
}
