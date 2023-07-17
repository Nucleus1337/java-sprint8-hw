package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static model.TaskType.EPIC;

public class Epic extends Task {
    private final List<Long> subtaskId = new ArrayList<>();
    private LocalDateTime endTime;

    /*Разве неверная парадигма того, что эпик является оболочкой для Сабтасков?
    * не добавил сюда поле для времени, потому мне оно кажется бессмысленным.
    * при добавлении сабтака нужные поля пересчитаются.
    * Если неправ, то добавлю*/
    public Epic(String name, String description, TaskStatus status, long id) {
        super(name, description, status, id);
        super.setType(EPIC);
    }

    public Epic(String name, String desc) {
        super.setName(name);
        super.setDescription(desc);
        super.setType(EPIC);
    }

    public List<Long> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtaskId(long subtaskId) {
        this.subtaskId.add(subtaskId);
    }

    public void clearSubtaskId() {
        subtaskId.clear();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status='" + this.getStatus().toString() + '\'' +
                ", id=" + this.getId() +
                ", type=" + super.getType() + '\'' +
                (this.getStartTime() == null
                        ? ""
                        : ", startTime=" + this.getStartTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")) + '\'' +
                          ", duration=" + this.getDuration() + '\'' +
                          ", endTime=" + this.getEndTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")) + '\'') +
                '}' + "\n";
    }
}
