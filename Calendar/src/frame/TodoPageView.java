// TodoPageView.java
package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class TodoPageView extends JFrame {

    private JProgressBar progressBar;
    private JPanel todoListPanel;
    private LocalDate currentDate;
    private JLabel dateLabel;
    private JTextArea oneLineReviewArea;
    
    // CalendarFrame01 인스턴스에 대한 참조를 유지
    private CalendarFrame01 mainFrame;

    // CalendarFrame01에서 호출할 때 사용될 생성자
    public TodoPageView(LocalDate date, CalendarFrame01 mainFrame) {
        this.currentDate = date;
        this.mainFrame = mainFrame;

        // --- 프레임 기본 설정 ---
        setTitle("할 일 페이지");
        setSize(480, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- 전체 컨텐츠 패널 설정 ---
        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(contentPane);

        // --- 1. 상단 섹션 (날짜 이동, 진행률) ---
        JPanel topSectionPanel = new JPanel();
        topSectionPanel.setLayout(new BoxLayout(topSectionPanel, BoxLayout.Y_AXIS));
        topSectionPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // 1-1. 날짜 이동 패널
        JPanel dateNavigationPanel = new JPanel(new BorderLayout());

        // 왼쪽 버튼들을 담을 패널
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); 
        leftPanel.setOpaque(false);
        
        JButton calendarButton = new JButton("달력"); 
        calendarButton.setPreferredSize(new Dimension(80, 40));
        calendarButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        calendarButton.addActionListener(e -> {
            // CalendarFrame01이 아닌 MonthlyCalendarView를 호출하도록 변경
            new MonthlyCalendarView(mainFrame).setVisible(true);
            dispose();
        });

        JButton prevButton = new JButton("<");
        prevButton.setPreferredSize(new Dimension(50, 40));
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusDays(1);
            refreshDateDisplay();
            loadTodoList();
        });
        
        leftPanel.add(calendarButton);
        leftPanel.add(prevButton);
        
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        dateLabel.setOpaque(true);
        dateLabel.setBackground(Color.decode("#F5E6CC"));
        dateLabel.setBorder(new LineBorder(Color.BLACK, 2));

        // 오른쪽 버튼들을 담을 패널
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        
        JButton nextButton = new JButton(">");
        nextButton.setPreferredSize(new Dimension(50, 40));
        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusDays(1);
            refreshDateDisplay();
            loadTodoList();
        });
        
        JButton settingsButton = new JButton("설정");
        settingsButton.setPreferredSize(new Dimension(80, 40));
        settingsButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        settingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "아직 설정창이 연결안되었습니다");
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

        // 할 일 목록 패널
        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setOpaque(true);
        todoListPanel.setBackground(Color.WHITE);

        JScrollPane todoListScrollPane = new JScrollPane(todoListPanel);
        todoListScrollPane.setPreferredSize(new Dimension(400, 400));
        //todoListScrollPane.setBorder(new LineBorder(Color.BLACK, 2));
        todoListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(todoListScrollPane, BorderLayout.NORTH);
        
        // 추가, 삭제 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addButton = new JButton("추가");
        JButton deleteButton = new JButton("삭제");
        
        addButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        deleteButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        addButton.setPreferredSize(new Dimension(100, 40));
        deleteButton.setPreferredSize(new Dimension(100, 40));
        
        addButton.addActionListener(e -> {
            String newTitle = JOptionPane.showInputDialog(this, "새로운 할 일을 입력하세요:");
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                CalendarFrame01.TodoEntry newTodo = new CalendarFrame01.TodoEntry(newTitle, false, Color.WHITE);
                List<CalendarFrame01.TodoEntry> tasks = CalendarFrame01.dailyTasks.getOrDefault(currentDate, new ArrayList<>());
                tasks.add(newTodo);
                CalendarFrame01.dailyTasks.put(currentDate, tasks);
                loadTodoList();
                mainFrame.updateTodoPanel();
                mainFrame.updateProgressBar();
            }
        });
        
        deleteButton.addActionListener(e -> {
             if (JOptionPane.showConfirmDialog(this, "선택된 할 일을 모두 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                List<CalendarFrame01.TodoEntry> tasks = CalendarFrame01.dailyTasks.getOrDefault(currentDate, new ArrayList<>());
                tasks.removeIf(task -> task.completed); // 완료된 할 일 삭제
                loadTodoList();
                mainFrame.updateTodoPanel();
                mainFrame.updateProgressBar();
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // 하루 한줄평 패널
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
                CalendarFrame01.dailyReviews.put(currentDate, oneLineReviewArea.getText());
            }
        });
        
        oneLineReviewArea.setText(CalendarFrame01.dailyReviews.getOrDefault(currentDate, "예시: 오늘 하루도 멋지게 완수!"));
        
        reviewPanel.add(oneLineReviewArea, BorderLayout.CENTER);
        reviewPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        centerPanel.add(reviewPanel, BorderLayout.SOUTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // --- 3. 하단 섹션 (완료 버튼) ---
        JPanel bottomPanel = new JPanel();
        JButton completeButton = new JButton("완료");
        completeButton.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        completeButton.setPreferredSize(new Dimension(150, 60));
        completeButton.setBackground(Color.WHITE);
        completeButton.setForeground(Color.BLACK);
        completeButton.setOpaque(true);
        completeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        completeButton.addActionListener(e -> {
            mainFrame.setVisible(true);
            mainFrame.updateWeekView();
            dispose();
        });
        
        bottomPanel.add(completeButton);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드
        loadTodoList();
    }
    
    private void loadTodoList() {
        todoListPanel.removeAll();
        List<CalendarFrame01.TodoEntry> todoList = CalendarFrame01.dailyTasks.getOrDefault(currentDate, new ArrayList<>());

        for (CalendarFrame01.TodoEntry todo : todoList) {
            todoListPanel.add(createTodoItemPanel(todo));
            todoListPanel.add(Box.createVerticalStrut(5));
        }
        updateProgressBar();
        
        oneLineReviewArea.setText(CalendarFrame01.dailyReviews.getOrDefault(currentDate, "예시: 오늘 하루도 멋지게 완수!"));

        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    private JPanel createTodoItemPanel(CalendarFrame01.TodoEntry todo) {
        JPanel todoItemPanel = new JPanel(new BorderLayout(5, 0));
        todoItemPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        todoItemPanel.setMaximumSize(new Dimension(400, 50));
        todoItemPanel.setBorder(new LineBorder(Color.BLACK, 2));
        todoItemPanel.setBackground(Color.WHITE);
        
        JLabel todoLabel = new JLabel(" " + todo.title);
        todoLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
        
        JButton completeButton = new JButton("완료");
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
        
        if (todo.completed) {
            todoLabel.setText("√ " + todo.title);
        } else {
            todoLabel.setText("• " + todo.title);
        }
        
        todoItemPanel.add(todoLabel, BorderLayout.CENTER);
        todoItemPanel.add(completeButton, BorderLayout.EAST);

        return todoItemPanel;
    }

    private void updateProgressBar() {
        List<CalendarFrame01.TodoEntry> todoList = CalendarFrame01.dailyTasks.getOrDefault(currentDate, new ArrayList<>());

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

    private void refreshDateDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREA);
        dateLabel.setText(currentDate.format(formatter));
    }

}