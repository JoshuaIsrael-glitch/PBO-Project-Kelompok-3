package todolist;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TodoAppGUI extends JFrame {
    private TaskManager taskManager;
    private int taskCounter = 5;

    // Format tampilan tanggal & jam di tabel
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Komponen UI
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTitle, txtDesc, txtDueDate, txtSearch;
    private JComboBox<String> cmbFilter;
    private JLabel lblNotification;

    // Komponen Kartu Statistik
    private JLabel lblTotalCount, lblProgressCount, lblExpiredCount;

    // Palet Warna Modern
    private final Color PRIMARY_DARK = new Color(30, 41, 59);
    private final Color ACCENT_BLUE = new Color(37, 99, 235);
    private final Color BG_LIGHT = new Color(248, 250, 252);
    private final Color CARD_WHITE = Color.WHITE;
    private final Color DANGER_BG = new Color(254, 226, 226);
    private final Color DANGER_TEXT = new Color(185, 28, 28);
    private final Color SUCCESS_TEXT = new Color(21, 128, 61);
    private final Color WARNING_TEXT = new Color(180, 83, 9);

    public TodoAppGUI() {
        taskManager = new TaskManager();
        initSeedData();
        initUI();
        refreshTable(taskManager.getAllTasks());
    }

    private void initSeedData() {
        // 1. Baru dibuat (created == modified) -> Otomatis DRAFT
        taskManager.addTask(new Task("TSK-001", "Desain Diagram State", "Untuk PPT Minggu Depan", LocalDateTime.now().plusDays(2)));

        // 2. Dibuat lalu diedit -> modified berubah -> Otomatis PROGRESS
        taskManager.addTask(new Task("TSK-002", "Coding Business Logic", "Versi awal", LocalDateTime.now().plusDays(3)));
        taskManager.editTask("TSK-002", "Coding Business Logic", "Revisi pakai timestamp", LocalDateTime.now().plusDays(3));

        // 3. Dibuat lalu diselesaikan sebelum due date -> Otomatis CLOSE
        taskManager.addTask(new Task("TSK-003", "Setup NetBeans", "Install JDK & IDE", LocalDateTime.now().plusDays(1)));
        taskManager.completeTask("TSK-003");

        // 4. Lewat due date & belum selesai -> Otomatis EXPIRED
        taskManager.addTask(new Task("TSK-004", "Laporan Bab 1", "Kumpulkan dokumen", LocalDateTime.now().minusDays(1)));
    }

    private void initUI() {
        setTitle("Todo List Manager — Automated Timestamp Lifecycle");
        setSize(1220, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout(15, 15));

        // ================= 1. HEADER & SUMMARY CARDS =================
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(PRIMARY_DARK);
        topContainer.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        JLabel lblAppTitle = new JLabel("ToDo-LIST MANAGER");
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAppTitle.setForeground(Color.WHITE);
        JLabel lblSubtitle = new JLabel("Automated Status Transition (Created vs Modified vs Due Date)");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(148, 163, 184));
        titleBox.add(lblAppTitle);
        titleBox.add(lblSubtitle);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statsPanel.setOpaque(false);
        lblTotalCount = new JLabel("0", SwingConstants.CENTER);
        lblProgressCount = new JLabel("0", SwingConstants.CENTER);
        lblExpiredCount = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("TOTAL TASKS", lblTotalCount, new Color(51, 65, 85), Color.WHITE));
        statsPanel.add(createStatCard("IN PROGRESS", lblProgressCount, new Color(30, 58, 138), new Color(147, 197, 253)));
        statsPanel.add(createStatCard("EXPIRED", lblExpiredCount, new Color(127, 29, 29), new Color(252, 165, 165)));

        topContainer.add(titleBox, BorderLayout.WEST);
        topContainer.add(statsPanel, BorderLayout.EAST);
        add(topContainer, BorderLayout.NORTH);

        // ================= 2. FORM INPUT PANEL (KIRI) =================
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.setBorder(new EmptyBorder(0, 20, 0, 0));

        JPanel inputCard = new JPanel();
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));
        inputCard.setBackground(CARD_WHITE);
        inputCard.setPreferredSize(new Dimension(290, 0));
        inputCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblFormHeader = new JLabel("Create New Task (Auto DRAFT)");
        lblFormHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormHeader.setForeground(PRIMARY_DARK);
        lblFormHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(lblFormHeader);
        inputCard.add(Box.createVerticalStrut(15));

        txtId = createStyledTextField("TSK-005");
        txtTitle = createStyledTextField("");
        txtDesc = createStyledTextField("");
        txtDueDate = createStyledTextField(LocalDateTime.now().plusDays(1).format(dtf));

        addFormField(inputCard, "Task ID", txtId);
        addFormField(inputCard, "Task Title", txtTitle);
        addFormField(inputCard, "Description", txtDesc);
        addFormField(inputCard, "Due Date (yyyy-MM-dd HH:mm)", txtDueDate);

        JLabel lblInfo = new JLabel("<html><i>*Status awal otomatis <b>DRAFT</b>.<br>Edit task untuk mengubah ke <b>PROGRESS</b>.</i></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(100, 116, 139));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(lblInfo);
        inputCard.add(Box.createVerticalStrut(15));

        JButton btnAdd = new JButton("+ Add Task");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(ACCENT_BLUE);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputCard.add(btnAdd);

        leftWrapper.add(inputCard, BorderLayout.CENTER);
        add(leftWrapper, BorderLayout.WEST);

        // ================= 3. TABEL, FILTER, SEARCH & ACTION (TENGAH) =================
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 12));
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(0, 0, 0, 20));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(CARD_WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Panel Kiri Toolbar: Filter Status + Kotak Pencarian
        JPanel filterAndSearchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterAndSearchGroup.setOpaque(false);

        JLabel lblFilter = new JLabel("Status:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbFilter = new JComboBox<>(new String[]{"ALL", "DRAFT", "PROGRESS", "CLOSE", "EXPIRED"});
        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        txtSearch = new JTextField(14);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setToolTipText("Ketik judul task lalu tekan Cari...");

        JButton btnSearch = new JButton("Cari");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setBackground(PRIMARY_DARK);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        filterAndSearchGroup.add(lblFilter);
        filterAndSearchGroup.add(cmbFilter);
        filterAndSearchGroup.add(Box.createHorizontalStrut(6));
        filterAndSearchGroup.add(txtSearch);
        filterAndSearchGroup.add(btnSearch);

        // Panel Kanan Toolbar: Tombol Edit, Complete, dan Delete
        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionGroup.setOpaque(false);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEdit.setBackground(new Color(245, 158, 11));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnMarkClose = new JButton("Complete");
        btnMarkClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMarkClose.setBackground(new Color(22, 163, 74));
        btnMarkClose.setForeground(Color.WHITE);
        btnMarkClose.setFocusPainted(false);
        btnMarkClose.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDelete = new JButton("Delete");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(new Color(220, 38, 38));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        actionGroup.add(btnEdit);
        actionGroup.add(btnMarkClose);
        actionGroup.add(btnDelete);

        toolbar.add(filterAndSearchGroup, BorderLayout.WEST);
        toolbar.add(actionGroup, BorderLayout.EAST);

        // Kolom tabel menampilkan Created & Modified DateTime agar terlihat saat demo
        String[] columns = {"ID", "Title", "Description", "Created Time", "Modified Time", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        styleTable(taskTable);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(CARD_WHITE);

        centerWrapper.add(toolbar, BorderLayout.NORTH);
        centerWrapper.add(scrollPane, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // ================= 4. BANNER NOTIFIKASI (BAWAH) =================
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        bottomWrapper.setBorder(new EmptyBorder(0, 20, 15, 20));

        lblNotification = new JLabel("Status: All tasks are on track.");
        lblNotification.setOpaque(true);
        lblNotification.setBackground(DANGER_BG);
        lblNotification.setForeground(DANGER_TEXT);
        lblNotification.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNotification.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(252, 165, 165), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        bottomWrapper.add(lblNotification, BorderLayout.CENTER);
        add(bottomWrapper, BorderLayout.SOUTH);

        // Event Listeners
        btnAdd.addActionListener(e -> handleAddTask());
        cmbFilter.addActionListener(e -> handleFilter());
        btnSearch.addActionListener(e -> handleSearchTask());
        txtSearch.addActionListener(e -> handleSearchTask()); // Bisa tekan Enter langsung di kotak cari
        btnEdit.addActionListener(e -> handleEditTask());
        btnMarkClose.addActionListener(e -> handleMarkAsClose());
        btnDelete.addActionListener(e -> handleDeleteTask());
    }

    private JPanel createStatCard(String title, JLabel countLabel, Color bg, Color numColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setPreferredSize(new Dimension(115, 52));
        card.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(new Color(203, 213, 225));

        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        countLabel.setForeground(numColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        return card;
    }

    private JTextField createStyledTextField(String initialText) {
        JTextField field = new JTextField(initialText);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(PRIMARY_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(51, 65, 85));
        header.setPreferredSize(new Dimension(0, 36));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));

                String status = tbl.getValueAt(row, 6).toString();
                if (!isSelected) {
                    if ("EXPIRED".equals(status)) {
                        c.setBackground(new Color(255, 241, 242));
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 251, 252));
                    }
                }

                if (col == 6) {
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    switch (status) {
                        case "EXPIRED":
                            setForeground(DANGER_TEXT);
                            break;
                        case "CLOSE":
                            setForeground(SUCCESS_TEXT);
                            break;
                        case "PROGRESS":
                            setForeground(ACCENT_BLUE);
                            break;
                        default:
                            setForeground(WARNING_TEXT);
                            break;
                    }
                } else {
                    setForeground(PRIMARY_DARK);
                }
                return c;
            }
        });
    }

    private void handleAddTask() {
        try {
            String id = txtId.getText().trim();
            String title = txtTitle.getText().trim();
            String desc = txtDesc.getText().trim();
            LocalDateTime dueDate = LocalDateTime.parse(txtDueDate.getText().trim(), dtf);

            if (id.isEmpty() || title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Task ID and Title are required!", "Validation Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Task newTask = new Task(id, title, desc, dueDate);
            taskManager.addTask(newTask);

            cmbFilter.setSelectedItem("ALL");
            txtSearch.setText("");
            refreshTable(taskManager.getAllTasks());

            taskCounter++;
            txtId.setText(String.format("TSK-%03d", taskCounter));
            txtTitle.setText("");
            txtDesc.setText("");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use yyyy-MM-dd HH:mm (e.g., 2026-11-20 14:30)", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris tugas di tabel yang ingin diedit terlebih dahulu!", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idTask = (String) tableModel.getValueAt(selectedRow, 0);
        String oldTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String oldDesc = (String) tableModel.getValueAt(selectedRow, 2);
        String oldDue = (String) tableModel.getValueAt(selectedRow, 5);

        String newTitle = JOptionPane.showInputDialog(this, "Edit Judul Task:", oldTitle);
        if (newTitle == null || newTitle.trim().isEmpty()) return;

        String newDesc = JOptionPane.showInputDialog(this, "Edit Deskripsi Task:", oldDesc);
        if (newDesc == null) return;

        String newDueStr = JOptionPane.showInputDialog(this, "Edit Due Date (yyyy-MM-dd HH:mm):", oldDue);
        if (newDueStr == null) return;

        try {
            LocalDateTime newDue = LocalDateTime.parse(newDueStr.trim(), dtf);
            taskManager.editTask(idTask, newTitle.trim(), newDesc.trim(), newDue);
            handleFilter();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah! Gunakan yyyy-MM-dd HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleFilter() {
        String selected = (String) cmbFilter.getSelectedItem();
        if ("ALL".equals(selected)) {
            refreshTable(taskManager.getAllTasks());
        } else {
            refreshTable(taskManager.filterByStatus(StatusTask.valueOf(selected)));
        }
    }

    private void handleSearchTask() {
        String keyword = txtSearch.getText();
        List<Task> hasilCari = taskManager.searchTaskByTitle(keyword);
        refreshTable(hasilCari);
    }

    private void handleMarkAsClose() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris tugas di tabel terlebih dahulu!", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String idTask = (String) tableModel.getValueAt(selectedRow, 0);
        taskManager.completeTask(idTask);
        handleFilter();
    }

    private void handleDeleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris tugas yang ingin dihapus terlebih dahulu!", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idTask = (String) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus tugas \"" + title + "\" (" + idTask + ")?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            taskManager.deleteTask(idTask);
            handleFilter();
        }
    }

    private void refreshTable(List<Task> tasks) {
        tableModel.setRowCount(0);
        for (Task t : tasks) {
            tableModel.addRow(new Object[]{
                t.getIdTask(),
                t.getTitle(),
                t.getDescription(),
                t.getCreatedDateTime().format(dtf),
                t.getModifiedDateTime().format(dtf),
                t.getDueDate().format(dtf),
                t.getStatus()
            });
        }

        lblTotalCount.setText(String.valueOf(taskManager.getAllTasks().size()));
        lblProgressCount.setText(String.valueOf(taskManager.filterByStatus(StatusTask.PROGRESS).size()));
        int expiredCount = taskManager.getExpiredTasks().size();
        lblExpiredCount.setText(String.valueOf(expiredCount));

        if (expiredCount > 0) {
            lblNotification.setText("[WARNING] ATTENTION: " + expiredCount + " task(s) have passed their due date and are marked EXPIRED!");
            lblNotification.setVisible(true);
        } else {
            lblNotification.setVisible(false);
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new TodoAppGUI().setVisible(true));
    }
}