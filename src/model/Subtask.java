package model;

import java.time.format.DateTimeFormatter;

import static model.TaskType.SUBTASK;

public class Subtask extends Task {
    private long epicId;

    public Subtask(String name, String description, TaskStatus status, long id, long epicId,
                   String startTime, int duration) {
        super(name, description, status, id, startTime, duration);
        super.setType(SUBTASK);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, long id, String startTime, int duration) {
        super(name, description, status, id, startTime, duration);
        super.setType(SUBTASK);
    }

    public Subtask(String name, String description, long epicId, String startTime, int duration) {
        super(name, description, startTime, duration);
        super.setType(SUBTASK);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status='" + this.getStatus().toString() + '\'' +
                ", id=" + this.getId() +
                ", epicId=" + epicId +
                ", type=" + super.getType() + '\'' +
                ", startTime=" + this.getStartTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")) + '\'' +
                ", duration=" + this.getDuration() + '\'' +
                ", endTime=" + this.getEndTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")) + '\'' +
                '}' + "\n";
    }
}
