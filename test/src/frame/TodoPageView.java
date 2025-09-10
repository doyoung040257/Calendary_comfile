// TodoPageView.java
package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import Settings.SettingsMenu;
import lg.SessionManager;
import lg.User;
import todo.todoMain;
import todo.todoAddition;
import todo.todoModify;
import todo.todoList;
import frame.DateParser;

public class TodoPageView extends JFrame {

    private JProgressBar progressBar;
    private JPanel todoListPanel;
    private LocalDate currentDate;
    private JLabel dateLabel;
    private JTextArea oneLineReviewArea;

    private boolean isDeleteMode = false;
    private List<JCheckBox> deleteCheckboxes = new ArrayList<>();
    private CalendarFrame01 mainFrame;
    private User user;
    private JButton deleteButton;

    public TodoPageView(LocalDate date, CalendarFrame01 mainFrame) {
        this.currentDate = date;
        this.mainFrame = mainFrame;

        setTitle("할 일 페이지");
        setSize(480, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(contentPane);

        // --- 1. 상단 섹션 (날짜 이동, 진행률) ---
        JPanel topSectionPanel = new JPanel();
        topSectionPanel.setLayout(new BoxLayout(topSectionPanel, BoxLayout.Y_AXIS));
        topSectionPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // 1-1. 날짜 이동 패널
        JPanel dateNavigationPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        JButton calendarButton = new JButton("달력");
        calendarButton.setPreferredSize(new Dimension(80, 40));
        calendarButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        calendarButton.addActionListener(e -> {
            new MonthlyCalendarView(mainFrame).setVisible(true);
            dispose();
        });

        JButton prevButton = new JButton("<");
        prevButton.setPreferredSize(new Dimension(50, 40));
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusDays(1);
            refreshDateDisplay();
            isDeleteMode = false;
            loadTodoList();
        });

        leftPanel.add(calendarButton);
        leftPanel.add(prevButton);
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        dateLabel.setOpaque(true);
        dateLabel.setBackground(Color.decode("#F5E6CC"));
        dateLabel.setBorder(new LineBorder(Color.BLACK, 2));
        ThemeManager.applyTheme(dateLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        JButton nextButton = new JButton(">");
        nextButton.setPreferredSize(new Dimension(50, 40));
        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusDays(1);
            refreshDateDisplay();
            isDeleteMode = false;
            loadTodoList();
        });

        JButton settingsButton = new JButton("설정");
        settingsButton.setPreferredSize(new Dimension(80, 40));
        settingsButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        settingsButton.addActionListener(e -> {
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
        topSectionPanel.add(Box.createVerticalStrut(10));

        // 1-2. 진행률 바
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
        refreshDateDisplay();

        // --- 2. 중앙 섹션 (할 일 목록, 버튼, 한줄평) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setOpaque(true);
        todoListPanel.setBackground(Color.WHITE);
        JScrollPane todoListScrollPane = new JScrollPane(todoListPanel);
        todoListScrollPane.setPreferredSize(new Dimension(400, 400));
        todoListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(todoListScrollPane, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addButton = new JButton("추가");
        deleteButton = new JButton("삭제");

        addButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        deleteButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        addButton.setPreferredSize(new Dimension(100, 40));
        deleteButton.setPreferredSize(new Dimension(100, 40));

        addButton.addActionListener(e -> {
            new todoAddition(SessionManager.getCurrentUser().getTodolist(), () -> {
                isDeleteMode = false;
                loadTodoList();
                mainFrame.updateTodoPanel();
                mainFrame.updateProgressBar();
                if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
                    JOptionPane.showMessageDialog(this, "할 일이 추가되었습니다!");
                }
                
            }).todo_addition_page();
        });

        deleteButton.addActionListener(e -> {
            if (!isDeleteMode) {
                isDeleteMode = true;
                deleteButton.setText("선택 삭제");
                loadTodoList();
            } else {
                List<String> idsToDelete = new ArrayList<>();
                for (int i = 0; i < deleteCheckboxes.size(); i++) {
                    if (deleteCheckboxes.get(i).isSelected()) {
                        CalendarFrame01.TodoEntry todo = SessionManager.getCurrentUser().getDailyTasks().getOrDefault(currentDate, new ArrayList<>()).get(i);
                        idsToDelete.add(todo.id);
                    }
                }

                if (!idsToDelete.isEmpty()) {
                	SessionManager.getCurrentUser().getDailyTasks().get(currentDate).removeIf(todo -> idsToDelete.contains(todo.id));
                    
                    SessionManager.getCurrentUser().getTodolist().getTodolist().removeIf(todo -> {
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

        JPanel reviewPanel = new JPanel(new BorderLayout());
        oneLineReviewArea = new JTextArea(3, 25);
        oneLineReviewArea.setBorder(new LineBorder(Color.BLACK, 2));
        oneLineReviewArea.setLineWrap(true);
        oneLineReviewArea.setWrapStyleWord(true);
        oneLineReviewArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));

        oneLineReviewArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { saveReview(); }
            @Override
            public void removeUpdate(DocumentEvent e) { saveReview(); }
            @Override
            public void changedUpdate(DocumentEvent e) { saveReview(); }
            private void saveReview() {
            	SessionManager.getCurrentUser().getDailyReviews().put(currentDate, oneLineReviewArea.getText());
            }
        });

        // 엔터키를 눌렀을 때 포커스 해제
        oneLineReviewArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    oneLineReviewArea.transferFocus();
                    e.consume(); // 이벤트 소비하여 줄바꿈 방지
                }
            }
        });

        oneLineReviewArea.setText(SessionManager.getCurrentUser().getDailyReviews().getOrDefault(currentDate, ""));
        reviewPanel.add(oneLineReviewArea, BorderLayout.CENTER);
        reviewPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        centerPanel.add(reviewPanel, BorderLayout.SOUTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);

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
        loadTodoList();
    }

    private void saveReview() {
    	SessionManager.getCurrentUser().getDailyReviews().put(currentDate, oneLineReviewArea.getText());
        mainFrame.updateTodoPanel();
    }

    private void refreshDateDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREA);
        dateLabel.setText(currentDate.format(formatter));
    }

    public void loadTodoList() {
        todoListPanel.removeAll();
        deleteCheckboxes.clear();
        List<CalendarFrame01.TodoEntry> todoList = SessionManager.getCurrentUser().getDailyTasks().getOrDefault(currentDate, new ArrayList<>());

        for (CalendarFrame01.TodoEntry todo : todoList) {
            todoListPanel.add(createTodoItemPanel(todo));
            todoListPanel.add(Box.createVerticalStrut(5));
        }
        updateProgressBar();
        oneLineReviewArea.setText(SessionManager.getCurrentUser().getDailyReviews().getOrDefault(currentDate, "예시: 오늘 하루도 멋지게 완수!"));
        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    private JPanel createTodoItemPanel(CalendarFrame01.TodoEntry todo) {
        JPanel todoItemPanel = new JPanel(new BorderLayout(5, 0));
        todoItemPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        todoItemPanel.setMaximumSize(new Dimension(400, 50));
        todoItemPanel.setBorder(new LineBorder(Color.BLACK, 2));
        todoItemPanel.setBackground(Color.WHITE);

        if (isDeleteMode) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            deleteCheckboxes.add(checkBox);
            todoItemPanel.add(checkBox, BorderLayout.WEST);
        }

        JLabel todoLabel = new JLabel();
        todoLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
        todoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 중요도에 따라 별표를 표시하는 로직
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
        for (int i = 0; i < importance; i++) {
            stars += "★";
        }
        if (!stars.isEmpty()) {
            stars = " " + stars;
        }

        // 완료 여부에 따라 텍스트 스타일 변경
        if (todo.completed) {
            todoLabel.setText("<html><strike>" + todo.title + stars + "</strike></html>");
            todoLabel.setForeground(Color.GRAY);
        } else {
            todoLabel.setText(todo.title + stars);
            todoLabel.setForeground(Color.BLACK);
        }

        todoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isDeleteMode) {
                    openModifyWindow(todo.id);
                }
            }
        });

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
                todoList updatedItem = null;
                for (todoList item : SessionManager.getCurrentUser().getTodolist().getTodolist()) {
                    if (item.getId().equals(uuid)) {
                        updatedItem = item;
                        break;
                    }
                }
                
                if (updatedItem != null) {
                    LocalDate todoDate = DateParser.parseDate(updatedItem.getDay());
                    if (todoDate != null) {
                        List<CalendarFrame01.TodoEntry> tasksForDay = SessionManager.getCurrentUser().getDailyTasks().get(todoDate);
                        if (tasksForDay != null) {
                            for (CalendarFrame01.TodoEntry entry : tasksForDay) {
                                if (entry.id.equals(uuid)) {
                                    entry.title = updatedItem.getWork();
                                    break;
                                }
                            }
                        }
                    }
                }
                
                loadTodoList();
                mainFrame.updateTodoPanel();
                mainFrame.updateProgressBar();
            }).open();
        }
    }

    private void updateProgressBar() {
        List<CalendarFrame01.TodoEntry> todoList = SessionManager.getCurrentUser().getDailyTasks().getOrDefault(currentDate, new ArrayList<>());
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
            progressBar.setForeground(new Color(255, 105, 97));
        } else if (intPercentage < 70) {
            progressBar.setForeground(new Color(255, 218, 128));
        } else {
            progressBar.setForeground(new Color(144, 238, 144));
        }
    }
}

