package todolist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> listTask;

    public TaskManager() {
        this.listTask = new ArrayList<>();
    }

    // 1. Tambah Task baru
    public void addTask(Task task) {
        if (task != null) {
            evaluateTaskStatus(task);
            listTask.add(task);
        }
    }

    // 2. Ambil semua Task sekaligus evaluasi status terbarunya
    public List<Task> getAllTasks() {
        refreshAllStatuses();
        return listTask;
    }

    // 3. Filter Task berdasarkan Status (DRAFT, PROGRESS, CLOSE, EXPIRED)
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

    // 4. Ambil khusus daftar Task yang EXPIRED
    public List<Task> getExpiredTasks() {
        return filterByStatus(StatusTask.EXPIRED);
    }

    // 5. Evaluasi seluruh Task di dalam ArrayList
    public void refreshAllStatuses() {
        for (Task task : listTask) {
            evaluateTaskStatus(task);
        }
    }

    // 6. Logika Utama Sesuai Arahan Dosen (createdDateTime vs modifiedDateTime vs dueDate)
    public void evaluateTaskStatus(Task task) {
        LocalDateTime now = LocalDateTime.now();

        if (task.isCompleted()) {
            // Selesai sebelum atau tepat pada due date -> CLOSE
            if (!task.getModifiedDateTime().isAfter(task.getDueDate())) {
                task.setStatus(StatusTask.CLOSE);
            } else {
                task.setStatus(StatusTask.EXPIRED);
            }
        } else {
            // Belum selesai, cek apakah waktu sekarang sudah lewat due date
            if (now.isAfter(task.getDueDate())) {
                task.setStatus(StatusTask.EXPIRED);
            } else if (task.getCreatedDateTime().equals(task.getModifiedDateTime())) {
                // Belum pernah diedit sejak dibuat -> DRAFT
                task.setStatus(StatusTask.DRAFT);
            } else {
                // Sudah diedit (modified != created) & belum due date -> PROGRESS
                task.setStatus(StatusTask.PROGRESS);
            }
        }
    }

    // 7. Edit data Task (Memicu perubahan modifiedDateTime -> PROGRESS)
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

    // 8. Tandai Task selesai (Memicu pengecekan CLOSE / EXPIRED)
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
}