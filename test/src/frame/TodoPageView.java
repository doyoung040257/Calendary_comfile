// TodoPageView.java 2차수정 (MouseListener 오류 수정)
package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import Settings.ThemeManager;
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
    private JTextField oneLineReviewField;   // 오늘의 한 줄 리뷰

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
        this.currentDate = date;           
        this.mainFrame = mainFrame;

        setTitle("할 일 페이지");
        setSize(480,800);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);      
        setResizable(false);
        setBackground(Color.BLACK);

//        // --- 전체 레이아웃 패널 ---
//        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
//        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
//        add(contentPane);

        // --- 상단 섹션: 날짜 및 진행도 표시 ---
        JPanel topSectionPanel = createNavPanel();
        topSectionPanel.setLayout(new BoxLayout(topSectionPanel, BoxLayout.Y_AXIS));
        topSectionPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        topSectionPanel.setBounds(10, 10, 445, 50);
        add(topSectionPanel);

        // 날짜 이동 패널
        JPanel dateNavigationPanel = createNavPanel();
        dateNavigationPanel.setLayout(new FlowLayout());

        // 좌측 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);

        JButton calendarButton = createNavButton("달력", new Font("맑은 고딕", Font.BOLD, 16));
        calendarButton.setPreferredSize(new Dimension(80, 40));
        calendarButton.addActionListener(e -> {
            new MonthlyCalendarView(mainFrame).setVisible(true);
            dispose();
        });

        leftPanel.add(calendarButton);

        // 날짜 표시 라벨
        JPanel dateCenterPanel = new JPanel(new FlowLayout());
        dateCenterPanel.setOpaque(false);
        dateCenterPanel.setBorder(null);
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        dateLabel.setOpaque(false);
//        dateLabel.setBackground(Color.decode("#F5E6CC"));
//        dateLabel.setBorder(new LineBorder(Color.BLACK, 2));
		ThemeManager.applyTheme(dateLabel);
		
		JLabel prevLabel = new JLabel("◀", SwingConstants.CENTER);
		prevLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
		prevLabel.setPreferredSize(new Dimension(40, 40));
		prevLabel.setOpaque(false);  
		prevLabel.setBorder(null);   

		// 전날 이동
		prevLabel.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent e) {
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

        // 다음날 이동
        nextLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
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

        // 우측 패널
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);

        JButton settingsButton = createNavButton("설정", new Font("맑은 고딕", Font.BOLD, 16));
        settingsButton.setPreferredSize(new Dimension(80, 40));
        settingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(this.user).setVisible(true);
            dispose();
        });

        rightPanel.add(settingsButton);

        dateNavigationPanel.add(leftPanel);
        dateNavigationPanel.add(dateCenterPanel);
        dateNavigationPanel.add(rightPanel);
        topSectionPanel.add(dateNavigationPanel);
        topSectionPanel.add(Box.createVerticalStrut(10));

        // --- 진행도 표시 ---
        progressBar = createRoundProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(getWidth(), 30));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        progressBar.setBackground(Color.LIGHT_GRAY);
        progressBar.setForeground(Color.BLUE);

        JLabel progressLabel = new JLabel("진행율", SwingConstants.CENTER);
        progressLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JPanel progressPanel = createNavPanel();
        progressPanel.setBounds(10, 70, 445, 50);
        
        progressPanel.setLayout(new BorderLayout());
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.NORTH);
//        progressPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        add(progressPanel);

        refreshDateDisplay();

        // --- 중앙 섹션 ---
        JPanel centerPanel = createNavPanel();
        centerPanel.setBounds(10, 130, 445, 430);
        centerPanel.setLayout(new BorderLayout());
        add(centerPanel);

        todoListPanel = createNavPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setBackground(Color.WHITE);

        JScrollPane todoListScrollPane = listScrollBox();
        todoListScrollPane.setViewportView(todoListPanel);
        todoListScrollPane.setPreferredSize(new Dimension(400, 430));
        todoListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(todoListScrollPane);

        // --- 추가 삭제 버튼 패널 ---
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


        // --- 하루 한 줄 리뷰 ---
        JPanel reviewPanel = createNavPanel();
        reviewPanel.setBounds(10, 620, 445, 40);
        reviewPanel.setLayout(new BorderLayout());
        oneLineReviewField  = new JTextField(25); // 25글자 폭
        oneLineReviewField.setBorder(new EmptyBorder(5, 5, 5, 5));
        oneLineReviewField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        
        oneLineReviewField.setText(SessionManager.getCurrentUser().getDailyReviews().getOrDefault(currentDate, ""));

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

        // --- 완료 버튼 ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBounds(10, 670, 445, 70);
        JButton completeButton = createNavButton("완료", new Font("맑은 고딕", Font.BOLD, 24));
        completeButton.setPreferredSize(new Dimension(150, 60));
        completeButton.setBackground(Color.WHITE);
        completeButton.setForeground(Color.BLACK);
        completeButton.setOpaque(true);
        completeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        bottomPanel.add(completeButton);
        add(bottomPanel);

        completeButton.addActionListener(e -> {
            mainFrame.currentDate = this.currentDate;
            mainFrame.setVisible(true);
            mainFrame.updateWeekView();
            dispose();
        });

		FontManager.applyFontRecursively(this);
        loadTodoList();
    }

    // --- 한 줄 리뷰 저장 ---
    private void saveReview() {
        SessionManager.getCurrentUser().getDailyReviews()
            .put(currentDate,oneLineReviewField.getText());
        mainFrame.updateTodoPanel();
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
            final CalendarFrame01.TodoEntry todoFinal = todo; // ★ final로 수정
            todoListPanel.add(createTodoItemPanel(todoFinal));
            todoListPanel.add(Box.createVerticalStrut(5));
        }
        updateProgressBar();
        oneLineReviewArea.setText(SessionManager.getCurrentUser().getDailyReviews()
                .getOrDefault(currentDate, "예시: 오늘 하루도 멋지게 완수!"));
        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    // --- 개별 할 일 패널 생성 ---
    private JPanel createTodoItemPanel(final CalendarFrame01.TodoEntry todo) {
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

        if (todo.completed) {
            todoLabel.setText("<html><strike>" + todo.title + stars + "</strike></html>");
            todoLabel.setForeground(Color.GRAY);
        } else {
            todoLabel.setText(todo.title + stars);
            todoLabel.setForeground(Color.BLACK);
        }

        // ★ MouseListener 오류 수정: final todo 사용
        todoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isDeleteMode) openModifyWindow(todo.id);
            }
        });

        JButton completeButton = createNavButton(todo.completed ? "취소" : "완료", new Font("Malgun Gothic", Font.BOLD, 18));
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

        if (intPercentage < 30) progressBar.setForeground(new Color(255, 105, 97));
        else if (intPercentage < 70) progressBar.setForeground(new Color(255, 218, 128));
        else progressBar.setForeground(new Color(144, 238, 144));
    }
        // --- toDate 갱신 ---
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

                // 배경
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                // 진행률 채우기
                int progressWidth = (int) (width * getPercentComplete());
                g2.setColor(getForeground());
                g2.fillRoundRect(0, 0, progressWidth, height, arc, arc);

                // ✅ 텍스트 직접 출력
                String text = getString(); // "67%"
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.setColor(Color.BLUE);   // 원하는 글자색
                g2.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 - 2);

                g2.dispose();
            }
        };

        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(76, 175, 80)); // 초록
        progressBar.setBackground(Color.LIGHT_GRAY);
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        return progressBar;
    }
        
	private JScrollPane listScrollBox() {
	    JScrollPane scrollPane = new JScrollPane() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g); // ← 기존 배경을 지우고 시작
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	            // 둥근 배경 채우기
	            g2.setColor(getBackground());
	            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

	            // 테두리
	            g2.setColor(Color.GRAY);
	            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

	            g2.dispose();
	        }
	    };

	    scrollPane.setBorder(null);
	    scrollPane.setOpaque(false);
	    scrollPane.getViewport().setOpaque(false);

	    // 스크롤바를 완전히 숨김
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	    // 마우스 휠 스크롤만 가능
	    scrollPane.getVerticalScrollBar().setUnitIncrement(20);

	    return scrollPane;
	}

    // --- 커스텀 버튼/패널 ---
    private JButton createNavButton(String text, Font font) {
    	 JButton button = new JButton(text) {

             @Override
             protected void paintComponent(Graphics g) {
                 Graphics2D g2 = (Graphics2D) g.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 // 배경 색상 (눌렸을 때 어둡게)
                 if (getModel().isArmed()) {
                     g2.setColor(getBackground().darker());
                 } else {
                     g2.setColor(getBackground());
                 }
                 // 둥근 사각형 배경
                 g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                 g2.dispose();
                 super.paintComponent(g);
             }

             @Override
             protected void paintBorder(Graphics g) {
                 Graphics2D g2 = (Graphics2D) g.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2.setColor(Color.GRAY); // 테두리 색상 (원하시면 여기 색을 변경하세요)
                 g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                 g2.dispose();
             }
         };

         button.setFont(font);
         button.setBackground(Color.WHITE);
         button.setForeground(Color.BLACK);

         // 기본 버튼 효과 제거
         button.setContentAreaFilled(false);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setOpaque(false);

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

