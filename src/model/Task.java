package model;

import util.TaskUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static model.TaskType.TASK;

public class Task {
    private String name;
    private String description;
    private TaskStatus status;
    private long id;
    private TaskType type = TASK;

    private LocalDateTime startTime;
    private int duration;

    public Task() {
    }

    public Task(String name, String description, TaskStatus status, long id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, TaskStatus status, long id, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = TaskUtil.getDateTimeFromString(startTime);
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = TaskUtil.getDateTimeFromString(startTime);
        this.duration = duration;
    }

    public Task(String name, String description, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.startTime = TaskUtil.getDateTimeFromString(startTime);
        this.duration = duration;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status)
                && Objects.equals(type, task.type)
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, type, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status.toString() + '\'' +
                ", id=" + id +
                ", type=" + type + '\'' +
                ", startTime=" + startTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")) + '\'' +
                ", duration=" + duration + '\'' +
                ", endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")) + '\'' +
                '}' + "\n";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
