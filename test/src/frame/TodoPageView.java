package frame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// DocumentListener를 사용하기 위해 import 합니다.
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Settings.SettingsMenu;

public class TodoPageView extends JFrame {

    private JProgressBar progressBar;
    private JPanel todoListPanel;
    private LocalDate currentDate;
    private JLabel dateLabel;
    private JTextField newTodoTextField;
    private JTextArea oneLineReviewArea; // 클래스 멤버 변수로 선언

    public TodoPageView(LocalDate date) {
        currentDate = date;

        // --- 프레임 기본 설정 ---
        setTitle("할 일 페이지");
        setSize(500, 800);
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
        JButton prevButton = new JButton("<<");
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusDays(1);
            refreshDateDisplay();
            loadTodoList();
        });

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        dateLabel.setBorder(new LineBorder(Color.BLACK, 2));
        dateLabel.setPreferredSize(new Dimension(getWidth(), 40));
        dateLabel.setOpaque(true);
        dateLabel.setBackground(Color.WHITE);

        JButton nextButton = new JButton(">>");
        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusDays(1);
            refreshDateDisplay();
            loadTodoList();
        });

        dateNavigationPanel.add(prevButton, BorderLayout.WEST);
        dateNavigationPanel.add(dateLabel, BorderLayout.CENTER);
        dateNavigationPanel.add(nextButton, BorderLayout.EAST);
        topSectionPanel.add(dateNavigationPanel);
        topSectionPanel.add(Box.createVerticalStrut(10));

        // 1-2. 진행률 바
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(getWidth(), 30));
        progressBar.setStringPainted(true);
        JPanel progressBarPanel = new JPanel(new BorderLayout());
        progressBarPanel.setBorder(new LineBorder(Color.RED, 2));
        progressBarPanel.add(progressBar, BorderLayout.CENTER);
        topSectionPanel.add(progressBarPanel);
        topSectionPanel.add(Box.createVerticalStrut(10));

        refreshDateDisplay();
        contentPane.add(topSectionPanel, BorderLayout.NORTH);

        // --- 2. 중앙 섹션 (할 일 목록, 하루 한줄평) ---
        JPanel centerSectionPanel = new JPanel();
        centerSectionPanel.setLayout(new BoxLayout(centerSectionPanel, BoxLayout.Y_AXIS));
        centerSectionPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setOpaque(false);
        JScrollPane todoListScrollPane = new JScrollPane(todoListPanel);
        todoListScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        todoListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 새로운 할 일 입력 필드와 추가 버튼 패널
        JPanel newTodoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        newTodoTextField = new JTextField(20);
        newTodoTextField.setBorder(new LineBorder(Color.GRAY, 1));
        
        JButton addNewTodoButton = new JButton("추가");
        addNewTodoButton.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        addNewTodoButton.setFocusPainted(false);
        addNewTodoButton.setBackground(new Color(255, 255, 204));
        addNewTodoButton.setBorder(new LineBorder(Color.BLACK, 2));
        addNewTodoButton.setPreferredSize(new Dimension(80, 40));

        addNewTodoButton.addActionListener(e -> {
            String newTitle = newTodoTextField.getText(); // 입력 필드에서 텍스트를 가져옵니다.
            if (newTitle.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "할 일을 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            CalendarFrame01.TodoEntry newTodo = new CalendarFrame01.TodoEntry(newTitle, false, new Color(255, 255, 204));
            
            List<CalendarFrame01.TodoEntry> tasks = CalendarFrame01.dailyTasks.getOrDefault(currentDate, new ArrayList<>());
            tasks.add(newTodo);
            CalendarFrame01.dailyTasks.put(currentDate, tasks);
            
            newTodoTextField.setText(""); // 입력 필드 초기화
            loadTodoList();
        });
        
        newTodoPanel.add(newTodoTextField);
        newTodoPanel.add(addNewTodoButton);
        centerSectionPanel.add(newTodoPanel);
        centerSectionPanel.add(Box.createVerticalStrut(10));
        
        centerSectionPanel.add(todoListScrollPane);

        // 하루 한줄평 컴포넌트를 먼저 생성하고, 그 다음에 loadTodoList()를 호출하도록 순서를 변경했습니다.
        String reviewText = CalendarFrame01.dailyReviews.getOrDefault(currentDate, "하루 한줄평");
        oneLineReviewArea = new JTextArea(reviewText, 5, 25);
        oneLineReviewArea.setBorder(new LineBorder(new Color(128, 0, 128), 2));
        oneLineReviewArea.setLineWrap(true);
        oneLineReviewArea.setWrapStyleWord(true);
        
        oneLineReviewArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saveReview();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                saveReview();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                saveReview();
            }
            private void saveReview() {
                CalendarFrame01.dailyReviews.put(currentDate, oneLineReviewArea.getText());
            }
        });
        
        loadTodoList(); // 이제 oneLineReviewArea가 생성되었으므로 안전하게 호출할 수 있습니다.

        JScrollPane scrollPane = new JScrollPane(oneLineReviewArea);
        scrollPane.setPreferredSize(new Dimension(400, 150));

        JPanel reviewPanelWrapper = new JPanel();
        reviewPanelWrapper.setLayout(new BoxLayout(reviewPanelWrapper, BoxLayout.X_AXIS));
        reviewPanelWrapper.add(Box.createHorizontalGlue());
        reviewPanelWrapper.add(scrollPane);
        reviewPanelWrapper.add(Box.createHorizontalGlue());

        centerSectionPanel.add(reviewPanelWrapper);
        contentPane.add(centerSectionPanel, BorderLayout.CENTER);

        // --- 3. 하단 섹션 (메인 페이지, 설정 페이지 버튼) ---
        JPanel bottomSectionPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomSectionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton mainPageButton = new JButton();
        setupMultiLineButton(mainPageButton, "메인<br>페이지<br>버튼", Color.BLACK, 100, 80);
        mainPageButton.addActionListener(e -> {
            new CalendarFrame01().setVisible(true);

        });

        JButton settingsPageButton = new JButton();
        setupMultiLineButton(settingsPageButton, "설정<br>페이지<br>버튼", Color.GRAY, 100, 80);
        settingsPageButton.addActionListener(e -> {
            JDialog settingsDialog = new JDialog(this, "설정", true);
            new SettingsMenu(); // 설정 메뉴 창 띄우기
			dispose();
        });

        bottomSectionPanel.add(mainPageButton);
        bottomSectionPanel.add(settingsPageButton);
        contentPane.add(bottomSectionPanel, BorderLayout.SOUTH);
    }
    
    private void loadTodoList() {
        todoListPanel.removeAll();
        // LocalDate 객체를 키로 사용해서 할일 목록을 가져옵니다.
        List<CalendarFrame01.TodoEntry> todoList = CalendarFrame01.dailyTasks.getOrDefault(currentDate, new ArrayList<>());

        for (CalendarFrame01.TodoEntry todo : todoList) {
            todoListPanel.add(createTodoItemPanel(todo, todoList));
            todoListPanel.add(Box.createVerticalStrut(10));
        }
        updateProgressBar();
        
        // 하루 한줄평 텍스트 로드
        oneLineReviewArea.setText(CalendarFrame01.dailyReviews.getOrDefault(currentDate, "하루 한줄평"));

        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    private JPanel createTodoItemPanel(CalendarFrame01.TodoEntry todo, List<CalendarFrame01.TodoEntry> todoList) {
        JPanel todoItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        todoItemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox checkBox = new JCheckBox();
        checkBox.setBorder(new EmptyBorder(0, 0, 0, 5));
        checkBox.setSelected(todo.completed);

        JTextField todoTextField = new JTextField(todo.title, 25);
        todoTextField.setBorder(new LineBorder(Color.BLUE, 2));
        todoTextField.setPreferredSize(new Dimension(todoTextField.getPreferredSize().width, 30));

        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> {
            todoList.remove(todo);
            loadTodoList();
            updateProgressBar();
        });

        checkBox.addActionListener(e -> {
            todo.completed = checkBox.isSelected();
            updateProgressBar();
        });
        
        todoItemPanel.add(checkBox);
        todoItemPanel.add(todoTextField);
        todoItemPanel.add(deleteButton);

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 dd일 E요일", Locale.KOREA);
        dateLabel.setText(currentDate.format(formatter));
    }

    private void setupMultiLineButton(JButton button, String text, Color borderColor, int width, int height) {
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(UIManager.getColor("Panel.background"));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 3));
        button.setText("<html><center>" + text + "</center></html>");
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
    }

}

