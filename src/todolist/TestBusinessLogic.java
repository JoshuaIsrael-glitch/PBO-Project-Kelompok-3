package todolist;

import java.time.LocalDateTime;

public class TestBusinessLogic {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        manager.addTask(new Task("TSK-001", "Desain Diagram State", "Untuk PPT Minggu Depan", LocalDateTime.now().plusDays(2)));
        manager.addTask(new Task("TSK-002", "Coding Business Logic", "Revisi sesuai dosen", LocalDateTime.now().plusDays(3)));
        manager.editTask("TSK-002", "Coding Business Logic Final", "Sudah pakai timestamp", LocalDateTime.now().plusDays(3));
        manager.addTask(new Task("TSK-003", "Setup NetBeans", "Selesai awal", LocalDateTime.now().plusDays(1)));
        manager.completeTask("TSK-003");
        manager.addTask(new Task("TSK-004", "Laporan Bab 1", "Telat dikumpulkan", LocalDateTime.now().minusDays(1)));

        System.out.println("=== HASIL EVALUASI STATUS OTOMATIS ===");
        for (Task t : manager.getAllTasks()) {
            System.out.println(t.getIdTask() + " | " + t.getTitle() + " | Status: " + t.getStatus());
        }
    }
}