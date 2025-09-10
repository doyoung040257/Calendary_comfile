package todo;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import GroupTest.MainFrame;
import Settings.SettingsMenu;
import Settings.ThemeManager;
import lg.User;
import frame.CalendarFrame01;  // 캘린더 화면 접근
import frame.DateParser;       // 날짜 파싱 유틸리티
import GroupTest.MainPanel;
import lg.SessionManager;

public class todoMain extends JFrame {

    // 할 일 체크박스 리스트 (삭제 선택용)
    private final java.util.List<JCheckBox> rowChecks = new java.util.ArrayList<>();

    // 현재 사용자의 할 일 리스트
    private final todoListMake userList;
    private User user; // 현재 로그인된 사용자

    private boolean showCheckboxes = false; // 체크박스 표시 여부

    public JFrame fr; // 프레임 객체
    public JButton addition; // 추가 버튼
    public JButton delete;   // 제거 버튼
    private JPanel list;     // 할 일 리스트 패널

    private MainPanel mainPanel; // MainPanel 참조 (연동용)

    // 생성자 (MainPanel이 있는 경우)
    public todoMain(User user, MainPanel mainPanel) {
        this.user = user;
        this.userList = user.getTodolist(); // 로그인한 사용자의 리스트 가져오기
        this.mainPanel = mainPanel;
        initComponents();
    }

    // 생성자 (MainPanel 없을 경우)
    public todoMain(User user) {
        this(user, null);
    }

    // UI 초기화
    private void initComponents() {
        fr = new JFrame();
        fr.setTitle("할 일");
        fr.setSize(480, 800);
        getContentPane().setBackground(Color.white);
        fr.setLayout(null);
        fr.setLocationRelativeTo(null);
        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Font titleFont = new Font("SansSerif", Font.BOLD, 22);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        // 상단 패널 (제목 + 설정 버튼)
        JPanel topPanel = createNavPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.setBounds(10, 10, 450, 50);
        ThemeManager.applyTheme(topPanel);
        fr.add(topPanel);

        JLabel todo = new JLabel("할 일 작성하기", JLabel.CENTER);
        todo.setFont(titleFont);
        topPanel.add(todo, BorderLayout.CENTER);

        // 설정 버튼
        JButton settingsViewButton = createNavButton("설정", buttonFont);
        topPanel.add(settingsViewButton, BorderLayout.EAST);
        settingsViewButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(this.user).setVisible(true);
            dispose();
        });

        // 할 일 리스트 스크롤 패널
        JScrollPane scrollPane = listScrollBox();
        scrollPane.setBounds(10, 70, 450, 570);
        fr.add(scrollPane);

        // 실제 리스트 패널
        list = createNavPanel();
        list.setLayout(null);
        list.setBackground(Color.LIGHT_GRAY);
        scrollPane.setViewportView(list);

        // 하단 추가/삭제 버튼
        JPanel bottomPanel1 = new JPanel(new FlowLayout());
        bottomPanel1.setBounds(10, 640, 450, 50);
        fr.add(bottomPanel1);

        JButton addition = createNavButton("추가", buttonFont);
        JButton delete = createNavButton("제거", buttonFont);
        bottomPanel1.add(addition);
        bottomPanel1.add(delete);

        // 리스트 렌더링
        renderList();

        // 추가 버튼 클릭 시
        addition.addActionListener(e -> {
            todoAddition addi = new todoAddition(userList, todoMain.this::renderList);
            addi.todo_addition_page(); // 추가 페이지 열기
        });

        // 삭제 버튼 클릭 시
        delete.addActionListener(e -> {
            if (!showCheckboxes) {
                showCheckboxes = true;
                renderList(); // 체크박스 보이기
                return;
            }
            boolean any = false;
            for (int i = rowChecks.size() - 1; i >= 0; i--) {
                if (rowChecks.get(i).isSelected()) {
                    // ★ 삭제 전 캘린더 데이터와 동기화
                    todoList itemToDelete = userList.getTodolist().get(i);
                    removeFromCalendarTasks(itemToDelete);

                    // 리스트에서 항목 삭제
                    userList.getTodolist().remove(i);
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

        // 하단 내비게이션 버튼 (홈, 할일, 그룹)
        JPanel bottomPanel = createNavPanel();
        bottomPanel.setLayout(new GridLayout(1, 3, 10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.setBounds(10, 690, 450, 60);
        ThemeManager.applyTheme(bottomPanel);
        fr.add(bottomPanel);

        JButton homeButton = createNavButton("홈", buttonFont);
        JButton todoButton = createNavButton("할일", buttonFont);
        JButton groupButton = createNavButton("그룹", buttonFont);
        bottomPanel.add(homeButton);
        bottomPanel.add(todoButton);
        bottomPanel.add(groupButton);

        homeButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(homeButton, "홈 화면으로 이동합니다.");
            new CalendarFrame01(user).setVisible(true);
            dispose();
        });

        todoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "이미 할 일 화면입니다.");
        });

        groupButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(groupButton, "그룹 관리 화면으로 이동합니다.");
            SwingUtilities.invokeLater(() -> new MainFrame("사용자"));
            dispose();
        });

        fr.setVisible(true);
    }

    // ★ 수정된 항목을 캘린더에도 반영하는 메서드
    private void updateCalendarForItem(todoList updatedItem) {
        if (updatedItem == null) return;

        LocalDate todoDate = DateParser.parseDate(updatedItem.getDay());
        if (todoDate != null && user != null) {

            // 1) 기존에 같은 ID를 가진 항목 삭제
            user.getDailyTasks().values().forEach(list -> list.removeIf(e -> e.id.equals(updatedItem.getId())));

            // 2) 수정된 항목을 새 날짜에 추가
            CalendarFrame01.TodoEntry newEntry = new CalendarFrame01.TodoEntry(
                    updatedItem.getId(),
                    updatedItem.getWork(),
                    false,
                    new java.awt.Color(255, 255, 204)
            );
            user.getDailyTasks().computeIfAbsent(todoDate, k -> new java.util.ArrayList<>()).add(newEntry);
        }
    }

    // 캘린더 데이터에서 삭제
    private void removeFromCalendarTasks(todoList itemToDelete) {
        if (itemToDelete == null) return;

        LocalDate todoDate = DateParser.parseDate(itemToDelete.getDay());
        if (todoDate != null && user != null) {
            List<CalendarFrame01.TodoEntry> tasksForDay = user.getDailyTasks().get(todoDate);
            if (tasksForDay != null) {
                tasksForDay.removeIf(entry -> entry.id.equals(itemToDelete.getId()));
            }
        }
    }

    // 리스트 렌더링
    private void renderList() {
        list.removeAll();
        for (JCheckBox c : rowChecks) fr.getContentPane().remove(c);
        rowChecks.clear();

        int y = 10;
        for (int i = 0; i < userList.getTodolist().size(); i++) {
            todoList t = userList.getTodolist().get(i);

            JButton b = new JButton(t.getWork());
            b.setBounds(10, y, 405, 40);

            JCheckBox cb = new JCheckBox();
            cb.setBounds(421, y + 5, 30, 30);
            cb.setOpaque(false);
            cb.setVisible(showCheckboxes);
            rowChecks.add(cb);

            list.add(cb);
            list.add(b);

            final int idx = i;

            // ★ 버튼 클릭 시 수정창 열기
            b.addActionListener(ev -> {
                new todoModify(userList, idx, () -> {
                    renderList(); // UI 갱신
                    todoList updatedItem = userList.getTodolist().get(idx);
                    updateCalendarForItem(updatedItem); // ★ 캘린더 반영
                }).open();
            });

            y += 45;
        }

        list.setPreferredSize(new java.awt.Dimension(280, y));
        list.validate();
        list.repaint();
        fr.getContentPane().revalidate();
        fr.getContentPane().repaint();
    }

    // 스크롤 설정
    private JScrollPane listScrollBox() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        return scrollPane;
    }

    // 버튼 디자인
    private JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setColor(getBackground().darker());
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };

        button.setFont(font);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);

        return button;
    }

    // 패널 디자인
    public JPanel createNavPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    public todoListMake getSharedList() {
        return userList;
    }
}
