package todolist;

import java.time.LocalDateTime;

public class Task {
    private String idTask;
    private String title;
    private String description;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private LocalDateTime dueDate;
    private StatusTask status;
    private boolean isCompleted;

    public Task(String idTask, String title, String description, LocalDateTime dueDate) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        
        LocalDateTime now = LocalDateTime.now();
        this.createdDateTime = now;
        this.modifiedDateTime = now;
        this.isCompleted = false;
        this.status = StatusTask.DRAFT;
    }

    public void updateTaskData(String newTitle, String newDescription, LocalDateTime newDueDate) {
        this.title = newTitle;
        this.description = newDescription;
        this.dueDate = newDueDate;
        this.modifiedDateTime = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.isCompleted = true;
        this.modifiedDateTime = LocalDateTime.now();
    }

    // Getters & Setters
    public String getIdTask() { return idTask; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedDateTime() { return createdDateTime; }
    public LocalDateTime getModifiedDateTime() { return modifiedDateTime; }
    public LocalDateTime getDueDate() { return dueDate; }
    public StatusTask getStatus() { return status; }
    public void setStatus(StatusTask status) { this.status = status; }
    public boolean isCompleted() { return isCompleted; }
}