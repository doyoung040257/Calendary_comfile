// TodoPageView.java 2차수정 (MouseListener 오류 수정 + 하루 한 줄 리뷰 placeholder)
package frame;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import Settings.FontManager;
import Settings.GlobalFont;
import Settings.SettingsMenu;
import Settings.ThemeManager;
import lg.SessionManager;
import lg.User;
import todo.todoAddition;
import todo.todoList;
import todo.todoModify;

public class TodoPageView extends JFrame {

    private JProgressBar progressBar;
    private JPanel todoListPanel;
    private LocalDate currentDate;
    private JLabel dateLabel;
    private JTextField oneLineReviewField;
    private ThemeManager themeManager;

    private boolean isDeleteMode = false;
    private List<JCheckBox> deleteCheckboxes = new ArrayList<>();
    private CalendarFrame01 mainFrame;
    private User user;
    private JButton deleteButton;
    private String toDate;

    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }

    public TodoPageView(LocalDate date, CalendarFrame01 mainFrame) {
        this.currentDate = date;
        this.mainFrame = mainFrame;

        setTitle("할 일 페이지");
        setSize(480,800);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(Color.BLACK);

        JPanel topSectionPanel = createNavPanel();
        topSectionPanel.setLayout(new BoxLayout(topSectionPanel, BoxLayout.Y_AXIS));
        topSectionPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        topSectionPanel.setBounds(10, 10, 445, 50);
        add(topSectionPanel);

        JPanel dateNavigationPanel = createNavPanel();
        dateNavigationPanel.setLayout(new FlowLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        JButton calendarButton = createNavButton("달력", new Font("맑은 고딕", Font.BOLD, 16));
        calendarButton.setPreferredSize(new Dimension(80, 40));
        calendarButton.addActionListener(e -> {
            new MonthlyCalendarView(mainFrame).setVisible(true);
            dispose();
        });
        leftPanel.add(calendarButton);

        JPanel dateCenterPanel = new JPanel(new FlowLayout());
        dateCenterPanel.setOpaque(false);
        dateCenterPanel.setBorder(null);
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        dateLabel.setOpaque(false);
        ThemeManager.applyTheme(dateLabel);

        JLabel prevLabel = new JLabel("◀", SwingConstants.CENTER);
        prevLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        prevLabel.setPreferredSize(new Dimension(40, 40));
        prevLabel.setOpaque(false);
        prevLabel.setBorder(null);
        prevLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentDate = currentDate.minusDays(1);
                refreshDateDisplay();
                updateToDate();
                isDeleteMode = false;
                loadTodoList();
            }
        });

        JLabel nextLabel = new JLabel("▶", SwingConstants.CENTER);
        nextLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        nextLabel.setPreferredSize(new Dimension(40, 40));
        nextLabel.setOpaque(false);
        nextLabel.setBorder(null);
        nextLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentDate = currentDate.plusDays(1);
                refreshDateDisplay();
                updateToDate();
                isDeleteMode = false;
                loadTodoList();
            }
        });

        dateCenterPanel.add(prevLabel);
        dateCenterPanel.add(dateLabel);
        dateCenterPanel.add(nextLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        JButton settingsButton = createNavButton("설정", new Font("맑은 고딕", Font.BOLD, 16));
        settingsButton.setPreferredSize(new Dimension(80, 40));
        settingsButton.addActionListener(e -> {
            User currentUser = mainFrame.getUser(); // CalendarFrame01에서 가져오기
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "로그인 정보가 없습니다.");
                return;
            }
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(currentUser).setVisible(true);
        }); 
        rightPanel.add(settingsButton);

        dateNavigationPanel.add(leftPanel);
        dateNavigationPanel.add(dateCenterPanel);
        dateNavigationPanel.add(rightPanel);
        topSectionPanel.add(dateNavigationPanel);
        topSectionPanel.add(Box.createVerticalStrut(10));

        progressBar = createRoundProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(getWidth(), 30));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        progressBar.setBackground(Color.LIGHT_GRAY);

        JLabel progressLabel = new JLabel("진행율", SwingConstants.CENTER);
        progressLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JPanel progressPanel = createNavPanel();
        progressPanel.setBounds(10, 70, 445, 50);
        progressPanel.setLayout(new BorderLayout());
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        add(progressPanel);

        refreshDateDisplay();

        JPanel centerPanel = createNavPanel();
        centerPanel.setBounds(10, 130, 445, 430);
        centerPanel.setLayout(new BorderLayout());
        add(centerPanel);

        todoListPanel = createNavPanel();
        todoListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setBackground(Color.WHITE);

        JScrollPane todoListScrollPane = listScrollBox();
        todoListScrollPane.setViewportView(todoListPanel);
        todoListScrollPane.setPreferredSize(new Dimension(400, 430));
        todoListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(todoListScrollPane);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBounds(10, 560, 445, 50);
        buttonPanel.setOpaque(false);
        JButton addButton = createNavButton("추가", new Font("맑은 고딕", Font.BOLD, 16));
        deleteButton = createNavButton("삭제", new Font("맑은 고딕", Font.BOLD, 16));
        addButton.setPreferredSize(new Dimension(100, 40));
        deleteButton.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel);

        updateToDate();
        addButton.addActionListener(e -> {
            new todoAddition(SessionManager.getCurrentUser().getTodolist(), () -> {
                isDeleteMode = false;
                loadTodoList();
                mainFrame.updateTodoPanel();
                mainFrame.updateProgressBar();
                if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
                    JOptionPane.showMessageDialog(this, "할 일이 추가되었습니다!");
                }
            },this).todo_addition_page();
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
                        CalendarFrame01.TodoEntry todo = SessionManager.getCurrentUser()
                                .getDailyTasks().getOrDefault(currentDate, new ArrayList<>()).get(i);
                        idsToDelete.add(todo.id);
                    }
                }

                if (!idsToDelete.isEmpty()) {
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

        // --- 하루 한 줄 리뷰 (placeholder 적용) ---
        JPanel reviewPanel = createNavPanel();
        reviewPanel.setBounds(10, 620, 445, 40);
        reviewPanel.setLayout(new BorderLayout());

        oneLineReviewField  = new JTextField(25);
        oneLineReviewField.setBorder(new EmptyBorder(5, 5, 5, 5));
        oneLineReviewField.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        String placeholder = "예시: 오늘 하루도 멋지게 완수!";
        oneLineReviewField.setText(SessionManager.getCurrentUser().getDailyReviews()
                .getOrDefault(currentDate, placeholder));
        if (oneLineReviewField.getText().isEmpty()) {
            oneLineReviewField.setText(placeholder);
            oneLineReviewField.setForeground(Color.GRAY);
        } else {
            oneLineReviewField.setForeground(Color.BLACK);
        }

        oneLineReviewField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (oneLineReviewField.getText().equals(placeholder)) {
                    oneLineReviewField.setText("");
                    oneLineReviewField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (oneLineReviewField.getText().isEmpty()) {
                    oneLineReviewField.setText(placeholder);
                    oneLineReviewField.setForeground(Color.GRAY);
                }
            }
        });

        oneLineReviewField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { saveReview(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { saveReview(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { saveReview(); }
        });

        oneLineReviewField.addActionListener(e -> {
            saveReview();
            oneLineReviewField.transferFocus();
        });

        reviewPanel.add(oneLineReviewField, BorderLayout.CENTER);
        reviewPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(reviewPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBounds(10, 670, 445, 70);
        
        JButton completeButton = createNavButton("완료", new Font("맑은 고딕", Font.BOLD, 24));
        completeButton.setPreferredSize(new Dimension(150, 60));
        completeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        bottomPanel.add(completeButton);
        add(bottomPanel);

        completeButton.addActionListener(e -> {
            mainFrame.currentDate = this.currentDate;
            mainFrame.setVisible(true);
            mainFrame.updateWeekView();
            dispose();
        });
        
        loadTodoList();
        FontManager.applyFontRecursively(this, GlobalFont.currentFont);
        //Settings.FontManager.applyFontRecursively(this);
    }

    // 테마 적용 메서드
    private void applyTheme() {
        ThemeManager.applyTheme(this);
    }

    private void saveReview() {
        SessionManager.getCurrentUser().getDailyReviews()
            .put(currentDate,oneLineReviewField.getText());
        mainFrame.updateTodoPanel();
    }

    private void refreshDateDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREA);
        dateLabel.setText(currentDate.format(formatter));
    }

    public void loadTodoList() {
        todoListPanel.removeAll();
        deleteCheckboxes.clear();
        List<CalendarFrame01.TodoEntry> todoList = SessionManager.getCurrentUser()
                .getDailyTasks().getOrDefault(currentDate, new ArrayList<>());

        for (CalendarFrame01.TodoEntry todo : todoList) {
            final CalendarFrame01.TodoEntry todoFinal = todo;
            todoListPanel.add(createTodoItemPanel(todoFinal));
            todoListPanel.add(Box.createVerticalStrut(5));
        }
        updateProgressBar();
        oneLineReviewField.setText(SessionManager.getCurrentUser().getDailyReviews()
                .getOrDefault(currentDate, "예시: 오늘 하루도 멋지게 완수!"));
        todoListPanel.revalidate();
        todoListPanel.repaint();
        
        FontManager.applyFontRecursively(TodoPageView.this, GlobalFont.currentFont);
    }
        // --- 개별 할 일 패널 생성 ---
    private JPanel createTodoItemPanel(final CalendarFrame01.TodoEntry todo) {
        JPanel todoItemPanel = createNavPanel();
        todoItemPanel.setLayout(new BoxLayout(todoItemPanel, BoxLayout.X_AXIS));
        todoItemPanel.setMaximumSize(new Dimension(400, 50));
        todoItemPanel.setBackground(Color.GRAY);

        if (isDeleteMode) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            deleteCheckboxes.add(checkBox);
            todoItemPanel.add(checkBox, BorderLayout.WEST);
        }

        JLabel todoLabel = new JLabel();
        todoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        todoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int importance = 0;
        todoList foundTodo = null;
        for (todoList t : SessionManager.getCurrentUser().getTodolist().getTodolist()) {
            if (t.getId().equals(todo.id)) {
                foundTodo = t;
                break;
            }
        }
        if (foundTodo != null) importance = foundTodo.getImportance();

        String stars = "";
        for (int i = 0; i < importance; i++) stars += "★";
        if (!stars.isEmpty()) stars = " " + stars;

        todoLabel.setText(todo.title + stars); // 문자열 그대로 표시

        if (todo.completed) {
            todoLabel.setForeground(Color.GRAY); // 완료된 항목 색상 회색
            todoLabel.setFont(GlobalFont.currentFont.deriveFont(Font.BOLD | Font.ITALIC, 18f)); 
        } else {
            todoLabel.setForeground(Color.BLACK); // 일반 항목 검정색
            todoLabel.setFont(GlobalFont.currentFont.deriveFont(Font.BOLD, 18f));
        }


        // ★ MouseListener 오류 수정: final todo 사용
        todoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isDeleteMode) openModifyWindow(todo.id);
            }
        });

        JButton completeButton = createNavButton(todo.completed ? "취소" : "완료", new Font("맑은 고딕", Font.BOLD, 18));
        completeButton.setBackground(Color.WHITE);
        completeButton.setForeground(Color.BLACK);
        completeButton.setOpaque(false);
        completeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        completeButton.setPreferredSize(new Dimension(60, 40));
        completeButton.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        completeButton.setFocusPainted(false);
        completeButton.addActionListener(e -> {
            todo.completed = !todo.completed;
            loadTodoList();
            mainFrame.updateTodoPanel();
            mainFrame.updateProgressBar();
        });

        todoItemPanel.add(Box.createHorizontalStrut(10));
        todoItemPanel.add(todoLabel);
        todoItemPanel.add(Box.createHorizontalGlue());
        todoItemPanel.add(completeButton);
        todoItemPanel.add(Box.createHorizontalStrut(10));
        
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

        if (intPercentage < 30) progressBar.setForeground(new Color(255, 105, 97));
        else if (intPercentage < 70) progressBar.setForeground(new Color(255, 218, 128));
        else progressBar.setForeground(new Color(144, 238, 144));
    }

    private void updateToDate() {
        String formatted = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        this.toDate = formatted + "[" + dayOfWeek + "]";
    }

    private JProgressBar createRoundProgressBar(int min, int max) {
        JProgressBar progressBar = new JProgressBar(min, max) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int arc = 30;

                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                int progressWidth = (int) (width * getPercentComplete());
                g2.setColor(getForeground());
                g2.fillRoundRect(0, 0, progressWidth, height, arc, arc);

                String text = getString();
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.setColor(Color.BLACK);
                g2.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 - 2);

                g2.dispose();
            }
        };

        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(76, 175, 80));
        progressBar.setBackground(Color.LIGHT_GRAY);
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        return progressBar;
    }

    private JScrollPane listScrollBox() {
        JScrollPane scrollPane = new JScrollPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.setColor(Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2.dispose();
            }
        };

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        return scrollPane;
    }

    private JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // hover / 클릭 상태 확인
                if (getModel().isPressed()) { // 클릭 중
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) { // hover 상태
                    g2.setColor(new Color(200, 230, 255)); // 연한 회색 등 원하는 색
                } else { // 기본
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

        // 🚩 rollover 효과 활성화
        button.setRolloverEnabled(true);

        return button;
    }


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
}








