package GroupTest;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;


public class SchedulePanel extends JPanel {

    private List<String> events; // frame의 schedules 또는 groupSchedules와 연결
    private JTable table;
    private String groupName;
    private String memberName;
    private boolean isGroup;

    public SchedulePanel(MainFrame frame, String groupName, String name, boolean isGroup) {
        this.groupName = groupName;
        this.memberName = name;
        this.isGroup = isGroup;

        setLayout(new BorderLayout(10, 10));
        Color highlightColor = new Color(180, 150, 200); // 강조 색상

        // ----------------- 상단 타이틀 -----------------
        JLabel title = new JLabel(groupName + " - " + name + " 일정", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // ----------------- 상단 패널 (검색 + 버튼) -----------------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JTextField searchField = new JTextField(10);

        RoundedButton searchBtn = new RoundedButton("검색", 20);
        styleButton(searchBtn, highlightColor);

        /*
        topPanel.add(new JLabel("검색: "));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
*/
        RoundedButton addBtn = new RoundedButton("일정 추가", 20);
        RoundedButton editBtn = new RoundedButton("일정 수정/삭제", 20);
        
        styleButton(addBtn, highlightColor);
        styleButton(editBtn, highlightColor);
       

        if (!isGroup) {
            topPanel.add(addBtn);
            topPanel.add(editBtn);
           
        }

        add(topPanel, BorderLayout.NORTH);

        // ----------------- events 초기화 -----------------
        if (isGroup) {
            events = frame.groupSchedules.get(groupName);
            if (events == null) {
                events = new ArrayList<>();
                frame.groupSchedules.put(groupName, events);
            }
        } else {
            events = frame.schedules.get(memberName);
            if (events == null) {
                events = new ArrayList<>();
                frame.schedules.put(memberName, events);
            }
        }

        // ----------------- JTable 생성 -----------------
        String[] columns = {"시간", "일정"};
        Object[][] data = new Object[24][2];
        for (int h = 0; h < 24; h++) {
            data[h][0] = h + "시";
            data[h][1] = "";
        }

        table = new JTable(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        });

        table.setRowHeight(30);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);

        // ----------------- 셀 색상 -----------------
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

        updateTableFromEvents(); // 초기 테이블 반영

        // ----------------- 버튼 기능 -----------------
        if (!isGroup) {
            addBtn.addActionListener(e -> addEventAction());
            editBtn.addActionListener(e -> editOrDeleteAction());
        }

        // ----------------- 검색 기능 -----------------
       /*
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            for (int i = 0; i < 24; i++) {
                String cell = "";
                for (String event : events) {
                    String eventName = event.split("\\(")[0];
                    int time = Integer.parseInt(event.split("\\(")[1].replaceAll("[^0-9]", ""));
                    if (time == i && eventName.toLowerCase().contains(keyword)) {
                        cell = eventName;
                        break;
                    }
                }
                table.setValueAt(cell, i, 1);
            }
        });
*/
        // ----------------- 하단 이전 화면 버튼 -----------------
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        RoundedButton backBtn = new RoundedButton("이전 화면", 20);
        backBtn.setPreferredSize(new Dimension(140, 40));
        styleButton(backBtn, highlightColor);
        backBtn.addActionListener(e -> {
            if (frame != null) frame.backTo("Member_" + groupName);
        });
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ----------------- 이벤트 처리 메소드 -----------------
    private void updateTableFromEvents() {
        for (int h = 0; h < 24; h++) table.setValueAt("", h, 1);
        for (String event : events) {
            String name = event.split("\\(")[0];
            int time = Integer.parseInt(event.split("\\(")[1].replaceAll("[^0-9]", ""));
            table.setValueAt(name, time, 1);
        }
    }

    private void addEventAction() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) { JOptionPane.showMessageDialog(this, "추가할 시간대를 선택해주세요."); return; }

        boolean hasNonEmpty = false;
        for (int row : selectedRows) {
            String cell = (String) table.getValueAt(row, 1);
            if (cell != null && !cell.isEmpty()) { hasNonEmpty = true; break; }
        }
        if (hasNonEmpty) { JOptionPane.showMessageDialog(this, "선택된 범위에 이미 일정이 존재합니다."); return; }

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

    private void editOrDeleteAction() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) { JOptionPane.showMessageDialog(this, "수정할 시간대를 선택해주세요."); return; }

        boolean allEmpty = true;
        for (int row : selectedRows) {
            String cell = (String) table.getValueAt(row, 1);
            if (cell != null && !cell.isEmpty()) { allEmpty = false; break; }
        }
        if (allEmpty) { JOptionPane.showMessageDialog(this, "일정이 없습니다. 일정을 추가해주세요."); return; }

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
                table.clearSelection();
                table.repaint();
                JOptionPane.showMessageDialog(this, "삭제되었습니다");
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
                table.clearSelection();
                table.repaint();
                JOptionPane.showMessageDialog(this, "수정되었습니다");
            }
        }
    }

    private void rangeEditOrDeleteAction() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) { JOptionPane.showMessageDialog(this, "범위를 선택해주세요."); return; }

        boolean hasEvent = false;
        for (int row : selectedRows) {
            String cell = (String) table.getValueAt(row, 1);
            if (cell != null && !cell.isEmpty()) { hasEvent = true; break; }
        }
        if (!hasEvent) { JOptionPane.showMessageDialog(this, "선택 범위에 일정이 없습니다."); return; }

        String input = JOptionPane.showInputDialog(this, "선택 범위 일정 수정/삭제 (빈칸 → 삭제):");
        if (input == null) return;

        if (input.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "선택 범위를 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (int row : selectedRows) {
                    String current = (String) table.getValueAt(row, 1);
                    table.setValueAt("", row, 1);
                    if (current != null && !current.isEmpty()) events.removeIf(ev -> ev.startsWith(current + "("));
                }
                table.clearSelection();
                table.repaint();
                JOptionPane.showMessageDialog(this, "삭제되었습니다");
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this, "선택 범위를 수정하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (int row : selectedRows) {
                    String current = (String) table.getValueAt(row, 1);
                    table.setValueAt(input, row, 1);
                    if (current != null && !current.isEmpty()) events.removeIf(ev -> ev.startsWith(current + "("));
                    events.add(input + "(" + row + "시)");
                }
                table.clearSelection();
                table.repaint();
                JOptionPane.showMessageDialog(this, "수정되었습니다");
            }
        }
    }

    private void addHoverEffect(JButton button, Color original) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(original.darker()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(original); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { button.setBackground(original.darker().darker()); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { button.setBackground(original.darker()); }
        });
    }

    // ----------------- 공통 스타일 적용 -----------------
    private void styleButton(RoundedButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        addHoverEffect(button, bgColor);
    }

    public String getGroupName() { return groupName; }
}
