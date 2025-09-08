package GroupTest;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SchedulePanel extends JPanel {

    private List<String> events; 
    private JTable table;

    public SchedulePanel(MainFrame frame, String groupName, String name, boolean isGroup) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ----------------- 상단 -----------------
        JLabel title = new JLabel(groupName + " - " + name + " 일정", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(Color.WHITE);

        JTextField searchField = new JTextField(10);
        JButton searchBtn = new JButton("검색");
        applyButtonStyle(searchBtn);

        topPanel.add(new JLabel("검색: "));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        JButton addBtn = new JButton("일정 추가");
        JButton editBtn = new JButton("일정 수정/삭제");
        JButton rangeEditBtn = new JButton("선택 범위 수정/삭제");
        applyButtonStyle(addBtn);
        applyButtonStyle(editBtn);
        applyButtonStyle(rangeEditBtn);

        if (!isGroup) {
            topPanel.add(addBtn);
            topPanel.add(editBtn);
            topPanel.add(rangeEditBtn);
        }

        add(topPanel, BorderLayout.NORTH);

        // ----------------- 이벤트 초기화 -----------------
        if (isGroup) {
            events = frame.groupSchedules.getOrDefault(groupName, new ArrayList<>());
            frame.groupSchedules.putIfAbsent(groupName, events);
        } else {
            events = frame.schedules.getOrDefault(name, new ArrayList<>());
            frame.schedules.putIfAbsent(name, events);
        }

        // ----------------- JTable -----------------
        String[] columns = {"시간", "일정"};
        Object[][] data = new Object[24][2];
        Map<Integer, String> scheduleMap = new HashMap<>();
        for (String event : events) {
            String[] parts = event.split("\\(");
            if (parts.length > 1) {
                String timePart = parts[1].replaceAll("[^0-9]", "");
                try { scheduleMap.put(Integer.parseInt(timePart), parts[0]); }
                catch (NumberFormatException ignored) {}
            }
        }
        for (int h = 0; h < 24; h++) {
            data[h][0] = h + "시";
            data[h][1] = scheduleMap.getOrDefault(h, "");
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (column == 1) {
                    if (isSelected) c.setBackground(new Color(173, 216, 230));
                    else if (value != null && !value.toString().isEmpty()) c.setBackground(new Color(255, 255, 153));
                    else c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ----------------- 일정 버튼 이벤트 -----------------
        if (!isGroup) {
            addBtn.addActionListener(e -> handleAddEvent());
            editBtn.addActionListener(e -> handleEditEvent());
            rangeEditBtn.addActionListener(e -> handleRangeEditEvent());
        }

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText();
            for (int i = 0; i < 24; i++) {
                String cell = (String) table.getValueAt(i, 1);
                if (cell != null && !cell.contains(keyword)) table.setValueAt("", i, 1);
            }
        });

        // ----------------- 하단 이전 화면 버튼 -----------------
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton backBtn = new JButton("이전 화면");
        backBtn.setPreferredSize(new Dimension(140, 40));
        applyButtonStyle(backBtn);
        backBtn.addActionListener(e -> {
            if (frame != null) frame.backTo("Member_" + groupName);
            else JOptionPane.showMessageDialog(this, "이전 화면으로 이동할 수 없습니다.");
        });

        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ----------------- 버튼 스타일 적용 -----------------
    private void applyButtonStyle(JButton btn) {
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btn.setBackground(new Color(180, 150, 200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);

        Dimension size = new Dimension(140, 40);
        btn.setPreferredSize(size);

        // 호버 효과
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(160, 130, 180)); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(180, 150, 200)); }
            @Override
            public void mousePressed(MouseEvent e) { btn.setBackground(new Color(140, 110, 160)); }
            @Override
            public void mouseReleased(MouseEvent e) { btn.setBackground(new Color(160, 130, 180)); }
        });
    }

    // ----------------- 일정 처리 메서드 -----------------
    private void handleAddEvent() {} // 기존 addBtn 이벤트 내용  
    private void handleEditEvent() {} //  기존 editBtn 이벤트 내용  
    private void handleRangeEditEvent() {} // 기존 rangeEditBtn 이벤트 내용  
}

