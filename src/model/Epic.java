package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        setStatus(Status.NEW);
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime(); // будет пересчитано в менеджере
    }

    @Override
    public Duration getDuration() {
        return super.getDuration(); // тоже пересчитаем
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateTime(Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super.setDuration(duration);
        super.setStartTime(startTime);
        this.endTime = endTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }
}
