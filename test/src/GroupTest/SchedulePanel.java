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
import lg.UserDatabase;
import lg.User;

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

        // ---------------------------
        // 1. users.dat 불러오기
        UserDatabase.loadUsers();

        // 2. 현재 사용자(User) 가져오기
        User currentUser = UserDatabase.getUser(memberName); // memberName == User ID
        if (currentUser != null) {
            System.out.println("User 데이터 로드 성공:");
            System.out.println("ID: " + currentUser.getId());
            System.out.println("Name: " + currentUser.getName());
            System.out.println("Birth: " + currentUser.getBirth());
            System.out.println("TodoList: " + currentUser.getTodolist());
        } else {
            System.out.println("User 데이터가 존재하지 않습니다: " + memberName);
        }

        // 3. todoData가 없는 경우 User의 todolist 사용
        if (todoData == null && currentUser != null) {
            todoData = currentUser.getTodolist();
        }

        // events 초기화
        events = new ArrayList<>(Collections.nCopies(24, ""));

        if (todoData != null) {
            for (todoList item : todoData.getTodolist()) {
                String timeStr = item.getTime(); // "14시 30분"
                int hour = 0;
                try {
                    hour = Integer.parseInt(timeStr.replaceAll("시.*", "")); // 시만 추출
                } catch (Exception e) { e.printStackTrace(); }

                // 기존 이벤트가 있으면 이어 붙이기
                String current = events.get(hour);
                if (current == null || current.isEmpty()) {
                    events.set(hour, item.getWork());
                } else {
                    events.set(hour, current + "\n" + item.getWork()); // 이어 붙이기
                }
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
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                JTextArea textArea = new JTextArea();
                textArea.setText(value != null ? value.toString() : "");
                textArea.setLineWrap(true);          // 줄바꿈 활성화
                textArea.setWrapStyleWord(true);     // 단어 단위로 줄바꿈
                textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
                textArea.setOpaque(true);

             // 색상 지정
                if (column == 0) { // 시간 컬럼
                    textArea.setBackground(new Color(220, 220, 220)); // 연한 회색
                } else { // 일정 컬럼
                    if (isSelected) textArea.setBackground(new Color(173, 216, 230));
                    else if (value != null && !value.toString().isEmpty()) textArea.setBackground(new Color(255, 255, 153));
                    else textArea.setBackground(Color.WHITE);
                }
                
//                if (isSelected) textArea.setBackground(new Color(173, 216, 230));
//                //else if (value != null && !value.toString().isEmpty()) textArea.setBackground(new Color(255, 255, 153));
//                else textArea.setBackground(Color.WHITE);

                return textArea;
            }
        });


        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        updateTableFromEvents();
        
     // JTable 행 높이 자동 조정 (줄바꿈 대응)
        for (int row = 0; row < table.getRowCount(); row++) {
            int maxHeight = 30; // 기본 높이
            Object value = table.getValueAt(row, 1);
            if (value != null) {
                int lines = value.toString().split("\n").length; // 줄 수 계산
                maxHeight = Math.max(maxHeight, lines * 20);    // 1줄당 20픽셀
            }
            table.setRowHeight(row, maxHeight);
        }


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
    	for (int h = 0; h < 24; h++) {
    	    table.setValueAt(events.get(h), h, 1); // 이미 이어붙인 문자열 그대로 JTable에 표시
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
