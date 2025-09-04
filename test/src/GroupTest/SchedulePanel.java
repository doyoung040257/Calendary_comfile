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

    public SchedulePanel(MainFrame frame, String groupName, String name, boolean isGroup) {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(groupName + " - " + name + " 일정", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // 상단 패널 (검색, 추가, 수정/삭제, 범위 수정/삭제)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JTextField searchField = new JTextField(10);
        JButton searchBtn = new JButton("검색");
        topPanel.add(new JLabel("검색: "));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        JButton addBtn = new JButton("일정 추가");
        JButton editBtn = new JButton("일정 수정/삭제");
        JButton rangeEditBtn = new JButton("선택 범위 수정/삭제");

        if (!isGroup) {
            topPanel.add(addBtn);
            topPanel.add(editBtn);
            topPanel.add(rangeEditBtn);
        }

        add(topPanel, BorderLayout.NORTH);

        // events 초기화 (frame의 schedules 또는 groupSchedules와 연결)
        if (isGroup) {
            events = frame.groupSchedules.get(groupName);
            if (events == null) {
                events = new ArrayList<>();
                frame.groupSchedules.put(groupName, events);
            }
        } else {
            events = frame.schedules.get(name);
            if (events == null) {
                events = new ArrayList<>();
                frame.schedules.put(name, events);
            }
        }

        // JTable 생성
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

        // 선택 모드: 다중 범위 선택 가능
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);

        // 셀 색상 지정 (일정 노랑, 선택 파랑)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (column == 1) {
                    if (isSelected) c.setBackground(new Color(173, 216, 230)); // 연한 파랑
                    else if (value != null && !value.toString().isEmpty()) c.setBackground(new Color(255, 255, 153)); // 노랑
                    else c.setBackground(Color.WHITE); // 일정 없음
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        if (!isGroup) {
            // ------------------- 일정 추가 버튼 -------------------
            addBtn.addActionListener(e -> {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(this, "추가할 시간대를 선택해주세요.");
                    return;
                }

                boolean hasNonEmpty = false;
                for (int row : selectedRows) {
                    String cell = (String) table.getValueAt(row, 1);
                    if (cell != null && !cell.isEmpty()) {
                        hasNonEmpty = true;
                        break;
                    }
                }
                if (hasNonEmpty) {
                    JOptionPane.showMessageDialog(this, "선택된 범위에 이미 일정이 존재합니다.");
                    return;
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
            });

            // ------------------- 일정 수정/삭제 버튼 -------------------
            editBtn.addActionListener(e -> {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(this, "수정할 시간대를 선택해주세요.");
                    return;
                }

                boolean allEmpty = true;
                for (int row : selectedRows) {
                    String cell = (String) table.getValueAt(row, 1);
                    if (cell != null && !cell.isEmpty()) {
                        allEmpty = false;
                        break;
                    }
                }
                if (allEmpty) {
                    JOptionPane.showMessageDialog(this, "일정이 없습니다. 일정을 추가해주세요.");
                    return;
                }

                String newEvent = JOptionPane.showInputDialog(this, "선택 범위 일정 수정/삭제 (빈칸 → 삭제):");
                if (newEvent == null) return;

                if (newEvent.isEmpty()) { // 삭제
                    int confirm = JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        for (int row : selectedRows) {
                            String current = (String) table.getValueAt(row, 1);
                            table.setValueAt("", row, 1);
                            if (current != null && !current.isEmpty())
                                events.removeIf(ev -> ev.startsWith(current + "("));
                        }
                        table.clearSelection();
                        table.repaint();
                        JOptionPane.showMessageDialog(this, "삭제되었습니다");
                    }
                } else { // 수정
                    int confirm = JOptionPane.showConfirmDialog(this, "수정하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        for (int row : selectedRows) {
                            String current = (String) table.getValueAt(row, 1);
                            table.setValueAt(newEvent, row, 1);
                            if (current != null && !current.isEmpty())
                                events.removeIf(ev -> ev.startsWith(current + "("));
                            events.add(newEvent + "(" + row + "시)");
                        }
                        table.clearSelection();
                        table.repaint();
                        JOptionPane.showMessageDialog(this, "수정되었습니다");
                    }
                }
            });

            // ------------------- 선택 범위 일괄 수정/삭제 -------------------
            rangeEditBtn.addActionListener(e -> {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) return;

                int minRow = selectedRows[0];
                int maxRow = selectedRows[selectedRows.length - 1];

                String input = JOptionPane.showInputDialog(this,
                        "선택 범위(" + minRow + " ~ " + maxRow + "시) 일정 수정/삭제\n" +
                                "수정: 입력, 삭제: 빈칸");

                if (input == null) return;

                if (input.isEmpty()) { // 삭제
                    int confirm = JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        for (int row : selectedRows) {
                            String current = (String) table.getValueAt(row, 1);
                            table.setValueAt("", row, 1);
                            if (current != null && !current.isEmpty())
                                events.removeIf(ev -> ev.startsWith(current + "("));
                        }
                        table.clearSelection();
                        table.repaint();
                        JOptionPane.showMessageDialog(this, "삭제되었습니다");
                    }
                } else { // 수정
                    int confirm = JOptionPane.showConfirmDialog(this, "수정하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        for (int row : selectedRows) {
                            String current = (String) table.getValueAt(row, 1);
                            table.setValueAt(input, row, 1);
                            if (current != null && !current.isEmpty())
                                events.removeIf(ev -> ev.startsWith(current + "("));
                            events.add(input + "(" + row + "시)");
                        }
                        table.clearSelection();
                        table.repaint();
                        JOptionPane.showMessageDialog(this, "수정되었습니다");
                    }
                }
            });
        }

        // 검색 기능
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText();
            for (int i = 0; i < 24; i++) {
                String cell = (String) table.getValueAt(i, 1);
                if (cell != null && !cell.contains(keyword)) table.setValueAt("", i, 1);
            }
        });

     // 하단 이전 화면 버튼
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton backBtn = new JButton("이전 화면");
        backBtn.setPreferredSize(new Dimension(120, 40));
        backBtn.addActionListener(e -> {
            if (frame != null) {          
                // 이전 화면은 해당 그룹의 멤버 목록으로 이동
                frame.backTo("Member_" + groupName);
            } else {
                System.err.println("frame이 null입니다. 이전 화면으로 이동할 수 없습니다.");
                JOptionPane.showMessageDialog(this, "이전 화면으로 이동할 수 없습니다.");
            }
        });
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);


    }
}

