package todolist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> listTask;
    public TaskManager() {
        this.listTask = new ArrayList<>();
    }

    //untuk menambah & mengambil data 
    public void addTask(Task task) {
        if (task != null) {
            evaluateTaskStatus(task);
            listTask.add(task);
        }
    }

    public List<Task> getAllTasks() {
        refreshAllStatuses();
        return listTask;
    }

    //untuk menyaring status & tugas kadaluwarsa
    public List<Task> filterByStatus(StatusTask targetStatus) {
        refreshAllStatuses();
        List<Task> filteredList = new ArrayList<>();
        
        for (Task task : listTask) {
            if (task.getStatus() == targetStatus) {
                filteredList.add(task);
            }
        }
        return filteredList;
    }

    public List<Task> getExpiredTasks() {
        return filterByStatus(StatusTask.EXPIRED);
    }

    public void refreshAllStatuses() {
        for (Task task : listTask) {
            evaluateTaskStatus(task);
        }
    }

    //inti utama
    public void evaluateTaskStatus(Task task) {
        LocalDateTime now = LocalDateTime.now();

        if (task.isCompleted()) {
            if (!task.getModifiedDateTime().isAfter(task.getDueDate())) {
                task.setStatus(StatusTask.CLOSE);
            } else {
                task.setStatus(StatusTask.EXPIRED);
            }
        } else {
            if (now.isAfter(task.getDueDate())) {
                task.setStatus(StatusTask.EXPIRED);
            } else if (task.getCreatedDateTime().equals(task.getModifiedDateTime())) {
                task.setStatus(StatusTask.DRAFT);
            } else {
                task.setStatus(StatusTask.PROGRESS);
            }
        }
    }

    // untuk mengedit & menyelesaikan tugas
    public boolean editTask(String idTask, String newTitle, String newDescription, LocalDateTime newDueDate) {
        for (Task task : listTask) {
            if (task.getIdTask().equalsIgnoreCase(idTask)) {
                task.updateTaskData(newTitle, newDescription, newDueDate);
                evaluateTaskStatus(task);
                return true;
            }
        }
        return false;
    }

    public boolean completeTask(String idTask) {
        for (Task task : listTask) {
            if (task.getIdTask().equalsIgnoreCase(idTask)) {
                task.markAsCompleted();
                evaluateTaskStatus(task);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteTask(String idTask){
        for (Task task : listTask) {
            if (task.getIdTask(). equalsIgnoreCase(idTask)){
                listTask.remove(task);
                return true;
            }
        }
        return false;
    }
    
   public List<Task> searchTaskByTitle(String keyword){
       List<Task> result = new ArrayList<>();
       for (Task task : listTask) {
           if (task.getTitle().toLowerCase().contains(keyword.toLowerCase())){
               result.add(task);
           
           }
       }
       return result;
   }
}