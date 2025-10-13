package test;

import model.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubtaskTest {
    @Test
    void shouldReturnEpicId_WhenSubtaskCreated() {
        Subtask subtask = new Subtask(2, "Subtask1", "Small task", 1);

        assertEquals(1, subtask.getEpicId(), "Subtask должен ссылаться на Epic с id=1");
    }

    @Test
    void shouldThrowException_WhenEpicIdEqualsSubtaskId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Subtask(1, "Subtask1", "Invalid", 1));

        assertEquals("Подзадача не может быть своим же эпиком.", exception.getMessage());
    }
}
