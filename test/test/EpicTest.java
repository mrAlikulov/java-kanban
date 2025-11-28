package test;

import model.Epic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    @Test
    void shouldAddSubtaskId_WhenValidIdGiven() {
        Epic epic = new Epic(1, "Epic1", "Big task");

        epic.addSubtaskId(2);

        assertEquals(1, epic.getSubtaskIds().size(), "У эпика должен быть 1 сабтаск");
        assertEquals(2, epic.getSubtaskIds().get(0), "Id сабтаска должен быть 2");
    }

    @Test
    void should_ThrowException_When_Adding_Itself_As_Subtask() {
        Epic epic = new Epic(1, "Epic1", "Big task");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> epic.addSubtaskId(1));

        assertEquals("Эпик не может быть подзадачей самого себя.", exception.getMessage());
    }
    @Test
    void should_Remove_SubtaskId_When_Subtask_Deleted() {
        Epic epic = new Epic(1, "Epic1", "Big task");

        epic.addSubtaskId(2);
        epic.addSubtaskId(3);

        // Эмулируем удаление сабтаска с id=2
        epic.getSubtaskIds().remove(Integer.valueOf(2));

        assertEquals(1, epic.getSubtaskIds().size(), "У эпика должен остаться 1 сабтаск после удаления");
        assertFalse(epic.getSubtaskIds().contains(2), "Сабтаска с id=2 быть не должно");
    }
}
