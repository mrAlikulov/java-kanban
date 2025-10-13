package model;

public class Subtask extends Task{
    private final int epicId;

    public Subtask(int id, String name, String description, int epicId) {
        super(id, name, description);
        if (epicId == this.getId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком.");
        }
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
