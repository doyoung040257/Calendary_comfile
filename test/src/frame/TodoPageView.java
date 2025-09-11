// TodoPageView.java
package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import Settings.SettingsMenu; // 설정 메뉴 화면
import lg.SessionManager;     // 현재 로그인한 사용자 정보 관리
import lg.User;               // 사용자 정보 객체
import todo.todoAddition;      // 할 일 추가 화면
import todo.todoModify;        // 할 일 수정 화면
import todo.todoList;          // 할 일 객체 리스트

public class TodoPageView extends JFrame {

    // --- UI 컴포넌트 선언 ---
    private JProgressBar progressBar;      // 할 일 진행도를 표시하는 바
    private JPanel todoListPanel;          // 할 일 목록 패널
    private LocalDate currentDate;         // 현재 선택한 날짜
    private JLabel dateLabel;              // 날짜를 표시하는 라벨
    private JTextArea oneLineReviewArea;   // 오늘의 한 줄 리뷰

    private boolean isDeleteMode = false;  // 삭제 모드 활성 여부
    private List<JCheckBox> deleteCheckboxes = new ArrayList<>(); // 삭제용 체크박스
    private CalendarFrame01 mainFrame;     // 메인 캘린더 프레임
    private User user;                     // 현재 사용자
    private JButton deleteButton;          // 삭제 버튼
    private String toDate;                 // 날짜 정보(0000-00-00[])
    
    public String getToDate() { return toDate; }

	public void setToDate(String toDate) { this.toDate = toDate; }

    // --- 생성자 ---
    public TodoPageView(LocalDate date, CalendarFrame01 mainFrame) {
        this.currentDate = date;           // 초기 날짜 설정
        this.mainFrame = mainFrame;        // 부모 프레임 참조

        setTitle("할 일 페이지");
        setSize(480, 800);                // 창 크기 고정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 닫을 때 해당 창만 종료
        setLocationRelativeTo(null);      // 화면 가운데 위치
        setResizable(false);              // 창 크기 조절 불가

        // --- 전체 레이아웃 패널 ---
        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(contentPane);

        // --- 상단 섹션: 날짜 및 진행도 표시 ---
        JPanel topSectionPanel = new JPanel();
        topSectionPanel.setLayout(new BoxLayout(topSectionPanel, BoxLayout.Y_AXIS));
        topSectionPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // 날짜 이동 패널 (좌/우 버튼 및 달력 버튼)
        JPanel dateNavigationPanel = new JPanel(new BorderLayout());

        // 좌측 패널 (달력 버튼 + 이전 날짜 버튼)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);

        JButton calendarButton = new JButton("달력");
        calendarButton.setPreferredSize(new Dimension(80, 40));
        calendarButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        calendarButton.addActionListener(e -> {
            // 달력 화면 열기
            new MonthlyCalendarView(mainFrame).setVisible(true);
            dispose(); // 현재 창 닫기
        });

        JButton prevButton = new JButton("<");
        prevButton.setPreferredSize(new Dimension(50, 40));
        prevButton.addActionListener(e -> {
            // 날짜를 하루 전으로 변경
            currentDate = currentDate.minusDays(1);
            refreshDateDisplay(); // 날짜 표시 갱신
            updateToDate();       // toDate 날짜 갱신
            isDeleteMode = false; // 삭제 모드 초기화
            loadTodoList();       // 할 일 목록 갱신
        });

        leftPanel.add(calendarButton);
        leftPanel.add(prevButton);

        // 날짜 표시 라벨
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        dateLabel.setOpaque(true);
        dateLabel.setBackground(Color.decode("#F5E6CC"));
        dateLabel.setBorder(new LineBorder(Color.BLACK, 2));

        // 우측 패널 (다음 날짜 버튼 + 설정 버튼)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);

        JButton nextButton = new JButton(">");
        nextButton.setPreferredSize(new Dimension(50, 40));
        nextButton.addActionListener(e -> {
            // 날짜를 하루 뒤로 변경
            currentDate = currentDate.plusDays(1);
            refreshDateDisplay();
            updateToDate(); 
            isDeleteMode = false;
            loadTodoList();
        });

        JButton settingsButton = new JButton("설정");
        settingsButton.setPreferredSize(new Dimension(80, 40));
        settingsButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        settingsButton.addActionListener(e -> {
            // 설정 화면 열기
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(this.user).setVisible(true);
            dispose();
        });

        rightPanel.add(nextButton);
        rightPanel.add(settingsButton);

        dateNavigationPanel.add(leftPanel, BorderLayout.WEST);
        dateNavigationPanel.add(dateLabel, BorderLayout.CENTER);
        dateNavigationPanel.add(rightPanel, BorderLayout.EAST);
        topSectionPanel.add(dateNavigationPanel);
        topSectionPanel.add(Box.createVerticalStrut(10)); // 위아래 여백

        // --- 진행도 표시 ---
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(getWidth(), 30));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        progressBar.setBorder(new LineBorder(Color.BLACK, 2));
        progressBar.setForeground(Color.BLUE);

        JLabel progressLabel = new JLabel("진행도", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.SOUTH);
        progressPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        topSectionPanel.add(progressPanel);

        contentPane.add(topSectionPanel, BorderLayout.NORTH);

        refreshDateDisplay(); // 초기 날짜 표시 갱신

        // --- 중앙 섹션: 할 일 목록 및 버튼 ---
        JPanel centerPanel = new JPanel(new BorderLayout());

        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setOpaque(true);
        todoListPanel.setBackground(Color.WHITE);

        JScrollPane todoListScrollPane = new JScrollPane(todoListPanel);
        todoListScrollPane.setPreferredSize(new Dimension(400, 400));
        todoListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(todoListScrollPane, BorderLayout.NORTH);

        // --- 할 일 추가 / 삭제 버튼 패널 ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addButton = new JButton("추가");
        deleteButton = new JButton("삭제");

        addButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        deleteButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        addButton.setPreferredSize(new Dimension(100, 40));
        deleteButton.setPreferredSize(new Dimension(100, 40));

        updateToDate(); // toDate 처음 실행 날짜
        // --- 할 일 추가 버튼 클릭 시 ---
        addButton.addActionListener(e -> {
            new todoAddition(SessionManager.getCurrentUser().getTodolist(), () -> {
                isDeleteMode = false;
                loadTodoList();         // 목록 갱신
                mainFrame.updateTodoPanel();  // 메인 캘린더 패널 갱신
                mainFrame.updateProgressBar(); // 메인 캘린더 진행도 갱신
                if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
                    JOptionPane.showMessageDialog(this, "할 일이 추가되었습니다!");
                }
            }).todo_addition_page(); // 추가 화면 표시
        });

        // --- 할 일 삭제 버튼 클릭 시 ---
        deleteButton.addActionListener(e -> {
            if (!isDeleteMode) {
                // 처음 클릭 시: 삭제 모드 활성화
                isDeleteMode = true;
                deleteButton.setText("선택 삭제");
                loadTodoList();
            } else {
                // 선택된 항목 삭제
                List<String> idsToDelete = new ArrayList<>();
                for (int i = 0; i < deleteCheckboxes.size(); i++) {
                    if (deleteCheckboxes.get(i).isSelected()) {
                        CalendarFrame01.TodoEntry todo = SessionManager.getCurrentUser()
                                .getDailyTasks().getOrDefault(currentDate, new ArrayList<>()).get(i);
                        idsToDelete.add(todo.id);
                    }
                }

                if (!idsToDelete.isEmpty()) {
                    // 실제 삭제 로직
                    SessionManager.getCurrentUser().getDailyTasks().get(currentDate)
                            .removeIf(todo -> idsToDelete.contains(todo.id));

                    SessionManager.getCurrentUser().getTodolist().getTodolist()
                            .removeIf(todo -> {
                                LocalDate todoDate = DateParser.parseDate(todo.getDay());
                                return idsToDelete.contains(todo.getId()) && currentDate.isEqual(todoDate);
                            });

                    if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
                        JOptionPane.showMessageDialog(this, "선택된 할 일이 삭제되었습니다.");
                    }
                } else {
                    if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
                        JOptionPane.showMessageDialog(this, "삭제할 할 일을 선택해주세요.");
                    }
                }

                isDeleteMode = false;
                deleteButton.setText("삭제");
                loadTodoList();
                mainFrame.updateTodoPanel();
                mainFrame.updateProgressBar();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // --- 오늘의 한 줄 리뷰 ---
        JPanel reviewPanel = new JPanel(new BorderLayout());
        oneLineReviewArea = new JTextArea(3, 25);
        oneLineReviewArea.setBorder(new LineBorder(Color.BLACK, 2));
        oneLineReviewArea.setLineWrap(true);
        oneLineReviewArea.setWrapStyleWord(true);
        oneLineReviewArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));

        // 리뷰 저장 리스너
        oneLineReviewArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { saveReview(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { saveReview(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { saveReview(); }
        });

        // Enter 입력 시 포커스 이동
        oneLineReviewArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    oneLineReviewArea.transferFocus();
                    e.consume();
                }
            }
        });

        // 기존 리뷰 불러오기
        oneLineReviewArea.setText(SessionManager.getCurrentUser().getDailyReviews().getOrDefault(currentDate, ""));
        reviewPanel.add(oneLineReviewArea, BorderLayout.CENTER);
        reviewPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        centerPanel.add(reviewPanel, BorderLayout.SOUTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // --- 하단 완료 버튼 ---
        JPanel bottomPanel = new JPanel();
        JButton completeButton = new JButton("완료");
        completeButton.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        completeButton.setPreferredSize(new Dimension(150, 60));
        completeButton.setBackground(Color.WHITE);
        completeButton.setForeground(Color.BLACK);
        completeButton.setOpaque(true);
        completeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        completeButton.addActionListener(e -> {
            mainFrame.currentDate = this.currentDate;
            mainFrame.setVisible(true);
            mainFrame.updateWeekView();
            dispose();
        });

        bottomPanel.add(completeButton);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        loadTodoList(); // 초기 할 일 목록 불러오기
    }

    // --- 한 줄 리뷰 저장 ---
    private void saveReview() {
        SessionManager.getCurrentUser().getDailyReviews().put(currentDate, oneLineReviewArea.getText());
        mainFrame.updateTodoPanel(); // 메인 캘린더 갱신
    }

    // --- 날짜 표시 갱신 ---
    private void refreshDateDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREA);
        dateLabel.setText(currentDate.format(formatter));
    }

    // --- 할 일 목록 불러오기 ---
    public void loadTodoList() {
        todoListPanel.removeAll();
        deleteCheckboxes.clear();
        List<CalendarFrame01.TodoEntry> todoList = SessionManager.getCurrentUser()
                .getDailyTasks().getOrDefault(currentDate, new ArrayList<>());

        for (CalendarFrame01.TodoEntry todo : todoList) {
            todoListPanel.add(createTodoItemPanel(todo));
            todoListPanel.add(Box.createVerticalStrut(5)); // 항목 간 간격
        }
        updateProgressBar(); // 진행도 갱신
        oneLineReviewArea.setText(SessionManager.getCurrentUser().getDailyReviews()
                .getOrDefault(currentDate, "예시: 오늘 하루도 멋지게 완수!"));
        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    // --- 개별 할 일 패널 생성 ---
    private JPanel createTodoItemPanel(CalendarFrame01.TodoEntry todo) {
        JPanel todoItemPanel = new JPanel(new BorderLayout(5, 0));
        todoItemPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        todoItemPanel.setMaximumSize(new Dimension(400, 50));
        todoItemPanel.setBorder(new LineBorder(Color.BLACK, 2));
        todoItemPanel.setBackground(Color.WHITE);

        // 삭제 모드 시 체크박스 추가
        if (isDeleteMode) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            deleteCheckboxes.add(checkBox);
            todoItemPanel.add(checkBox, BorderLayout.WEST);
        }

        JLabel todoLabel = new JLabel();
        todoLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
        todoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 중요도 표시
        int importance = 0;
        todoList foundTodo = null;
        for (todoList t : SessionManager.getCurrentUser().getTodolist().getTodolist()) {
            if (t.getId().equals(todo.id)) {
                foundTodo = t;
                break;
            }
        }
        if (foundTodo != null) {
            importance = foundTodo.getImportance();
        }

        String stars = "";
        for (int i = 0; i < importance; i++) stars += "★";
        if (!stars.isEmpty()) stars = " " + stars;

        if (todo.completed) {
            todoLabel.setText("<html><strike>" + todo.title + stars + "</strike></html>");
            todoLabel.setForeground(Color.GRAY);
        } else {
            todoLabel.setText(todo.title + stars);
            todoLabel.setForeground(Color.BLACK);
        }

        // 클릭 시 수정 화면 열기
        todoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isDeleteMode) openModifyWindow(todo.id);
            }
        });

        // 완료/취소 버튼
        JButton completeButton = new JButton(todo.completed ? "취소" : "완료");
        completeButton.setBackground(Color.WHITE);
        completeButton.setForeground(Color.BLACK);
        completeButton.setOpaque(true);
        completeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        completeButton.setPreferredSize(new Dimension(100, 40));
        completeButton.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        completeButton.setFocusPainted(false);
        completeButton.addActionListener(e -> {
            todo.completed = !todo.completed;
            loadTodoList();
            mainFrame.updateTodoPanel();
            mainFrame.updateProgressBar();
        });

        todoItemPanel.add(todoLabel, BorderLayout.CENTER);
        todoItemPanel.add(completeButton, BorderLayout.EAST);
        return todoItemPanel;
    }

    // --- 수정 화면 열기 ---
    private void openModifyWindow(String uuid) {
        List<todoList> allTodos = SessionManager.getCurrentUser().getTodolist().getTodolist();
        int foundIndex = -1;
        for (int i = 0; i < allTodos.size(); i++) {
            if (allTodos.get(i).getId().equals(uuid)) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex != -1) {
            new todoModify(SessionManager.getCurrentUser().getTodolist(), foundIndex, () -> {
                // 수정 후 기존 데이터 갱신
                todoList updatedItem = null;
                for (todoList item : SessionManager.getCurrentUser().getTodolist().getTodolist()) {
                    if (item.getId().equals(uuid)) {
                        updatedItem = item;
                        break;
                    }
                }

                if (updatedItem == null) {
                    loadTodoList();
                    mainFrame.updateTodoPanel();
                    mainFrame.updateProgressBar();
                    return;
                }

                java.util.Map<LocalDate, List<CalendarFrame01.TodoEntry>> dailyTasks =
                        SessionManager.getCurrentUser().getDailyTasks();

                LocalDate oldDateFound = null;
                CalendarFrame01.TodoEntry existingEntry = null;

                for (LocalDate dateKey : new ArrayList<>(dailyTasks.keySet())) {
                    List<CalendarFrame01.TodoEntry> listForDate = dailyTasks.get(dateKey);
                    if (listForDate == null) continue;
                    for (CalendarFrame01.TodoEntry entry : listForDate) {
                        if (entry.id.equals(uuid)) {
                            existingEntry = entry;
                            oldDateFound = dateKey;
                            break;
                        }
                    }
                    if (existingEntry != null) break;
                }

                if (existingEntry != null) {
                    existingEntry.title = updatedItem.getWork();
                }

                LocalDate newDate = DateParser.parseDate(updatedItem.getDay());
                if (newDate != null) {
                    if (existingEntry == null) {
                        CalendarFrame01.TodoEntry newEntry = new CalendarFrame01.TodoEntry(
                                updatedItem.getId(),
                                updatedItem.getWork(),
                                false,
                                new java.awt.Color(255, 255, 204)
                        );
                        dailyTasks.computeIfAbsent(newDate, k -> new ArrayList<>()).add(newEntry);
                    } else if (!newDate.equals(oldDateFound)) {
                        if (oldDateFound != null && dailyTasks.get(oldDateFound) != null) {
                            dailyTasks.get(oldDateFound).removeIf(e -> e.id.equals(uuid));
                            if (dailyTasks.get(oldDateFound).isEmpty()) {
                                dailyTasks.remove(oldDateFound);
                            }
                        }
                        dailyTasks.computeIfAbsent(newDate, k -> new ArrayList<>()).add(existingEntry);
                    }
                }

                loadTodoList();
                mainFrame.updateTodoPanel();
                mainFrame.updateProgressBar();
            }).open();
        }
    }

    // --- 진행도 바 갱신 ---
    private void updateProgressBar() {
        List<CalendarFrame01.TodoEntry> todoList = SessionManager.getCurrentUser()
                .getDailyTasks().getOrDefault(currentDate, new ArrayList<>());
        if (todoList.isEmpty()) {
            progressBar.setValue(100);
            progressBar.setString("할 일 없음");
            progressBar.setForeground(Color.LIGHT_GRAY);
            return;
        }

        long completedCount = todoList.stream().filter(t -> t.completed).count();
        double percentage = (double) completedCount / todoList.size() * 100;
        int intPercentage = (int) Math.round(percentage);

        progressBar.setValue(intPercentage);
        progressBar.setString(intPercentage + "%");

        if (intPercentage < 30) {
            progressBar.setForeground(new Color(255, 105, 97)); // 빨강
        } else if (intPercentage < 70) {
            progressBar.setForeground(new Color(255, 218, 128)); // 노랑
        } else {
            progressBar.setForeground(new Color(144, 238, 144)); // 초록
        }
    }
        // --- toDate 갱신 ---
        private void updateToDate() {
        String formatted = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        this.toDate = formatted + "[" + dayOfWeek + "]";
    }
}

