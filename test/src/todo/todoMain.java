package todo;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate; // import 추가
import java.util.List;		// import 추가
import javax.swing.*;

import frame.CalendarFrame01; // 캘린더 데이터 접근을 위해 import
import frame.DateParser;	  // 날짜 파싱 유틸리티 import
import GroupTest.MainPanel;   // ★ MainPanel 참조

public class todoMain {

    private final static todoListMake sharedList = new todoListMake();
    private final java.util.List<JCheckBox> rowChecks = new java.util.ArrayList<>();

    public JFrame fr;
    public JButton addition;
    public JButton delete;
    private JPanel list;
    private boolean showCheckboxes = false;

    // ★ MainPanel 참조
    private MainPanel mainPanel;

    // ★ MainPanel에서 열 때
    public todoMain(MainPanel mainPanel) {
        this.mainPanel = mainPanel; // 기존 MainPanel 참조 저장
        initComponents();
    }

    // 기존 생성자 (MainPanel 없을 때)
    public todoMain() {
        this(null);
    }

    private void initComponents() {
        fr = new JFrame();
        fr.setTitle("할 일");
        fr.setSize(500,800);
        fr.setLayout(null);
        fr.setLocationRelativeTo(null);
        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ---------------- 타이틀 ----------------
        JPanel title = new JPanel();
        title.setBounds(100, 25, 300, 80);
        title.setBackground(Color.LIGHT_GRAY);
        Label todo = new Label("할 일", Label.CENTER);
        title.add(todo);
        fr.add(title);

        // ---------------- 할 일 리스트 ----------------
        list = new JPanel();
        list.setLayout(null);
        list.setBackground(Color.LIGHT_GRAY);
        
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBounds(87, 150, 325, 450);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        fr.add(scrollPane);

        // ---------------- 버튼 ----------------
        addition = new JButton("추가");
        addition.setFocusPainted(false);
        addition.setBounds(100, 650, 150, 80);
        delete = new JButton("삭제");
        delete.setFocusPainted(false);
        delete.setBounds(250, 650, 150, 80);
        fr.add(addition);
        fr.add(delete);

        renderList();

        // ---------------- 추가/삭제 이벤트 ----------------
        addition.addActionListener(e -> {
            todoAddition addi = new todoAddition(sharedList, this::renderList);
            addi.todo_addition_page();	
        });

        delete.addActionListener(e -> {
            if (!showCheckboxes) {
                showCheckboxes = true;
                renderList();
                return;
            }
            boolean any = false;
            for (int i = rowChecks.size() - 1; i >= 0; i--) {
                if (rowChecks.get(i).isSelected()) {
                    // =================================================================
                    // ============[수정된 부분] 캘린더 데이터 동기화=====================
                    // =================================================================
                    todoList itemToDelete = sharedList.getTodolist().get(i); // 삭제 전 항목 가져오기
                    removeFromCalendarTasks(itemToDelete); // 캘린더 데이터에서 삭제
                    
                    sharedList.getTodolist().remove(i); // 기존 목록에서 삭제
                    any = true;
                }
            }
            if (!any) {
                JOptionPane.showMessageDialog(fr, "삭제할 항목을 선택하세요.");
            } else {
                showCheckboxes = false;
                renderList();
            }
        });

        // ★ 창 닫기 이벤트 처리 (조건부)
        fr.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (mainPanel != null) { // MainPanel에서 열렸으면 기존 인스턴스로 복귀
                    mainPanel.setVisible(true);
                } else { // 기존 CalendarFrame01 로직 유지
                    new CalendarFrame01().setVisible(true);
                }
            }
        });

        fr.setVisible(true);
    }

    // ---------------- 캘린더 데이터 삭제 헬퍼 ----------------
    private void removeFromCalendarTasks(todoList itemToDelete) {
        if (itemToDelete == null) return;
        LocalDate todoDate = DateParser.parseDate(itemToDelete.getDay());
        if (todoDate != null) {
            List<CalendarFrame01.TodoEntry> tasksForDay = CalendarFrame01.dailyTasks.get(todoDate);
            if (tasksForDay != null) {
                // 할일 제목(work)이 같은 항목을 캘린더 목록에서 찾아 삭제
                tasksForDay.removeIf(entry -> entry.title.equals(itemToDelete.getWork()));
            }
        }
    }

    private void renderList() {
        list.removeAll();
        for (JCheckBox c : rowChecks) {
            fr.getContentPane().remove(c);
        }
        rowChecks.clear();

        int y = 10;
        for (int i = 0; i < sharedList.getTodolist().size(); i++) {
            todoList t = sharedList.getTodolist().get(i);

            JButton b = new JButton(t.getWork());
            b.setBounds(10, y, 280, 40);

            JCheckBox cb = new JCheckBox();
            cb.setBounds(295, y+5, 30, 30);
            cb.setOpaque(false);
            cb.setVisible(showCheckboxes);
            rowChecks.add(cb);

            list.add(cb);
            list.add(b);

            final int idx = i;

            b.addActionListener(ev -> new todoModify(sharedList, idx, this::renderList).open());

            y += 45;
        }

        list.setPreferredSize(new java.awt.Dimension(280, y));
        list.validate();
        list.repaint();
        fr.getContentPane().revalidate();
        fr.getContentPane().repaint();    
    }

    public static todoListMake getSharedList() {
        return sharedList;
    }
}
