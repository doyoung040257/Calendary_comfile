
package GroupTest;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import todo.todoListMake;
import todo.SetFrame;
import todo.todoList;

public class SchedulePanel extends JPanel {

    private List<String> events; // 일정 데이터
    private JTable table;
    private String groupName;
    private String memberName;
    private boolean isGroup;
    private SetFrame parentFrame; // 이전 화면 참조

    // ----------------- SetFrame 기반 생성자 -----------------
    public SchedulePanel(MainFrame frame, String groupName, String name, boolean isGroup, SetFrame parentFrame) {
        this.groupName = groupName;
        this.memberName = name;
        this.isGroup = isGroup;
        this.parentFrame = parentFrame;

        initUI(frame, null);
    }

    // ----------------- todoListMake 기반 생성자 -----------------
    public SchedulePanel(MainFrame frame, String groupName, String name, boolean isGroup, todoListMake todoData) {
        this.groupName = groupName;
        this.memberName = name;
        this.isGroup = isGroup;
        this.parentFrame = null; // todoListMake 생성자에서는 parentFrame 없음

        initUI(frame, todoData);
    }

    // ----------------- 공통 UI 초기화 -----------------
    private void initUI(MainFrame frame, todoListMake todoData) {
        setLayout(new BorderLayout(10, 10));
        Color highlightColor = new Color(180, 150, 200);

        // 상단 타이틀
        JLabel title = new JLabel(groupName + " - " + memberName + " 일정", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // 상단 버튼
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        RoundedButton addBtn = new RoundedButton("일정 추가", 20);
        RoundedButton editBtn = new RoundedButton("일정 수정/삭제", 20);
        styleButton(addBtn, highlightColor);
        styleButton(editBtn, highlightColor);

        if (!isGroup) {
            topPanel.add(addBtn);
            topPanel.add(editBtn);
        }
        add(topPanel, BorderLayout.NORTH);

        // events 초기화
        events = new ArrayList<>();
        if (todoData != null) {
            for (todoList item : todoData.getTodolist()) {
                String timeStr = item.getTime(); // "14:30"
                int hour = 0;
                try {
                    hour = Integer.parseInt(timeStr.split(":")[0]);
                } catch (Exception e) { e.printStackTrace(); }
                events.add(item.getWork() + "(" + hour + "시)");
            }
        } else {
            if (isGroup) {
                events = frame.getGroupSchedules().getOrDefault(groupName, new ArrayList<>());
            } else {
                events = frame.getSchedules().getOrDefault(memberName, new ArrayList<>());
            }
        }

        // JTable 생성
        String[] columns = {"시간", "일정"};
        Object[][] data = new Object[24][2];
        for (int h = 0; h < 24; h++) {
            data[h][0] = h + "시";
            data[h][1] = "";
        }

        table = new JTable(new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
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
                } else c.setBackground(Color.WHITE);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        updateTableFromEvents();

        // 버튼 기능
        if (!isGroup) {
            addBtn.addActionListener(e -> addEventAction());
            editBtn.addActionListener(e -> editOrDeleteAction());
        }

        // 하단 이전 화면 버튼
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        RoundedButton backBtn = new RoundedButton("이전 화면", 20);
        backBtn.setPreferredSize(new Dimension(140, 40));
        styleButton(backBtn, highlightColor);

        backBtn.addActionListener(e -> {
            if (parentFrame != null) {
                parentFrame.showMemberPanel(groupName); // MemberPanel로 돌아감
            }
        });

        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // JTable 업데이트
    private void updateTableFromEvents() {
        for (int h = 0; h < 24; h++) table.setValueAt("", h, 1);
        for (String event : events) {
            String name = event.split("\\(")[0];
            int time = Integer.parseInt(event.split("\\(")[1].replaceAll("[^0-9]", ""));
            table.setValueAt(name, time, 1);
        }
    }

    // 일정 추가
    private void addEventAction() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) { JOptionPane.showMessageDialog(this, "추가할 시간대를 선택해주세요."); return; }

        for (int row : selectedRows) {
            String cell = (String) table.getValueAt(row, 1);
            if (cell != null && !cell.isEmpty()) { JOptionPane.showMessageDialog(this, "선택된 범위에 이미 일정이 존재합니다."); return; }
        }

        String newEvent = JOptionPane.showInputDialog(this, "추가할 일정 입력:");
        if (newEvent == null || newEvent.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "일정을 추가하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        for (int row : selectedRows) {
            table.setValueAt(newEvent, row, 1);
            events.add(newEvent + "(" + row + "시)");
        }
        table.clearSelection();
        table.repaint();
        JOptionPane.showMessageDialog(this, "일정이 추가되었습니다");
    }

    // 일정 수정/삭제
    private void editOrDeleteAction() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) { JOptionPane.showMessageDialog(this, "수정할 시간대를 선택해주세요."); return; }

        boolean allEmpty = true;
        for (int row : selectedRows) {
            String cell = (String) table.getValueAt(row, 1);
            if (cell != null && !cell.isEmpty()) { allEmpty = false; break; }
        }
        if (allEmpty) { JOptionPane.showMessageDialog(this, "일정이 없습니다."); return; }

        String newEvent = JOptionPane.showInputDialog(this, "선택 범위 일정 수정/삭제 (빈칸 → 삭제):");
        if (newEvent == null) return;

        if (newEvent.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (int row : selectedRows) {
                    String current = (String) table.getValueAt(row, 1);
                    table.setValueAt("", row, 1);
                    if (current != null && !current.isEmpty()) events.removeIf(ev -> ev.startsWith(current + "("));
                }
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this, "수정하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (int row : selectedRows) {
                    String current = (String) table.getValueAt(row, 1);
                    table.setValueAt(newEvent, row, 1);
                    if (current != null && !current.isEmpty()) events.removeIf(ev -> ev.startsWith(current + "("));
                    events.add(newEvent + "(" + row + "시)");
                }
            }
        }
        table.clearSelection();
        table.repaint();
    }

    // 버튼 스타일
    private void styleButton(RoundedButton button, Color bgColor) {
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(bgColor.darker()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(bgColor); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { button.setBackground(bgColor.darker().darker()); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { button.setBackground(bgColor.darker()); }
        });
    }

    public String getGroupName() { return groupName; }
}




