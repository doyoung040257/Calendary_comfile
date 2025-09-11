// CalendarFrame01.java
package frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.swing.border.Border;
import GroupTest.CalenderGroupPage;
import GroupTest.MainFrame;
import Settings.SettingsMenu;
import Settings.ThemeManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import lg.User; // User 클래스 임포트 추가
import todo.todoMain;

public class CalendarFrame01 extends JPanel {

//    protected static Object dailyTasks;
	LocalDate currentDate;
    private JLabel monthLabel;
    private JButton[] dayButtons = new JButton[7];
    private JPanel todoPanel;
    private JProgressBar progressBar;
    private int selectedButtonIndex = -1; // 선택된 버튼 인덱스 추가

    // 현재 로그인된 사용자를 저장할 필드 추가
    private User currentUser;
    private User user;

    // 할 일 데이터 및 한줄평 데이터를 모든 프레임에서 공유하기 위한 static 변수
//    public Map<LocalDate, List<TodoEntry>> dailyTasks = new HashMap<>();
//    public Map<LocalDate, String> dailyReviews = new HashMap<>();

    // 할 일 항목을 나타내는 내부 클래스
    public static class TodoEntry implements java.io.Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	public String id;
        public String title;
        public boolean completed;
        public Color color;

        public TodoEntry(String id, String title, boolean completed, Color color) {
            this.id = id;
        	this.title = title;
            this.completed = completed;
            this.color = color;
        }
    }

    public CalendarFrame01() {
        this(LocalDate.of(2025, 9, 1));
    }

    
    // User 객체를 인자로 받는 생성자 추가
    public CalendarFrame01(User user) {
        this(LocalDate.of(2025, 9, 1));
        //this.currentUser = user; // 전달받은 user 객체 저장
        //System.out.println("로그인한 사용자: " + currentUser.getName()); // 확인용 출력
        this.user = user; // 전달받은 user 객체 저장
        if (user != null) {
            System.out.println("로그인한 사용자: " + user.getName());
        } else {
            System.out.println("로그인한 사용자 정보 없음");
        }
    }

    public CalendarFrame01(LocalDate date) {
    	this.currentDate = date;
        this.user = lg.SessionManager.getCurrentUser();
        
         // --- 프레임 기본 설정 ---
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        Font titleFont = new Font("SansSerif", Font.BOLD, 22);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        // --- 상단 패널 (월 이동 및 설정) ---
        JPanel topPanel = createNavPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(topPanel);

        JPanel monthControlPanel = createNavPanel();
        monthControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        monthControlPanel.setOpaque(false);

        // 테마 적용
        ThemeManager.applyTheme(topPanel);
        
        JButton prevWeekButton = new JButton("◀");
        monthLabel = new JLabel();
        monthLabel.setFont(titleFont);

        // '월' 라벨에 마우스 리스너 추가하여 MonthlyCalendarView로 이동
        monthLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        monthLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 기존 CalendarFrame01 인스턴스를 MonthlyCalendarView에 전달
                new MonthlyCalendarView().setVisible(true);
            }
        });

        JButton nextWeekButton = new JButton("▶");

        setupArrowButton(prevWeekButton, titleFont);
        setupArrowButton(nextWeekButton, titleFont);

        monthControlPanel.add(prevWeekButton);
        monthControlPanel.add(Box.createHorizontalStrut(10));
        monthControlPanel.add(monthLabel);
        monthControlPanel.add(Box.createHorizontalStrut(10));
        monthControlPanel.add(nextWeekButton);

        topPanel.add(monthControlPanel, BorderLayout.WEST);

        // 설정버튼
		JButton settingsViewButton = createNavButton("설정",buttonFont);
		topPanel.add(settingsViewButton, BorderLayout.EAST);
        settingsViewButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(this.user).setVisible(true);
        });

        // --- 진행률 바 ---
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.BOLD, 18));
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        progressBar.setForeground(Color.decode("#F5E6CC"));

//        // --- 날짜 버튼 패널 ---
//        JPanel dayButtonsPanel = createNavPanel();
//		dayButtonsPanel.setLayout(new GridLayout(1, 7, 5, 5));
//        dayButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
//
//        for (int i = 0; i < 7; i++) {
//            dayButtons[i] = createNavButton2();
//            dayButtons[i].setFont(titleFont);
//            dayButtons[i].addActionListener(new DayButtonListener(i));
//            dayButtonsPanel.add(dayButtons[i]);
//        }
         // --- 날짜 버튼 패널 ---
        JPanel dayButtonsPanel = new JPanel(new GridLayout(1, 7, 5, 5));
        dayButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        for (int i = 0; i < 7; i++) {
            dayButtons[i] = new JButton();
            dayButtons[i].setFont(titleFont);
            dayButtons[i].addActionListener(new DayButtonListener(i));
            dayButtonsPanel.add(dayButtons[i]);
        }

        JPanel headerPanel = createNavPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);

        // 새로운 패널에 진행률 바와 날짜 버튼 패널을 추가
        JPanel progressAndDayPanel = createNavPanel();
        progressAndDayPanel.setLayout(new BorderLayout());
        progressAndDayPanel.add(progressBar, BorderLayout.NORTH);
        progressAndDayPanel.add(dayButtonsPanel, BorderLayout.CENTER);

        headerPanel.add(progressAndDayPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- 할일 목록 패널 (중앙) ---
        todoPanel = createNavPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS)); // 세로 정렬을 위해 BoxLayout 사용
        todoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        todoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        todoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	//원래코드
            	//new TodoPageView(currentDate, CalendarFrame01.this).setVisible(true);
                new TodoPageView(currentDate, CalendarFrame01.this,SessionManager.getCurrentUser()).setVisible(true); //변경
            }
        });
        add(new JScrollPane(todoPanel), BorderLayout.CENTER);

        prevWeekButton.addActionListener(e -> {
            currentDate = currentDate.minusWeeks(1);
            updateWeekView();
        });

        nextWeekButton.addActionListener(e -> {
            currentDate = currentDate.plusWeeks(1);
            updateWeekView();
        });

        updateWeekView();
    }
    

    private void setupArrowButton(JButton button, Font font) {
        button.setFont(font);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    void updateWeekView() {
        // Calculate the week of the month
        LocalDate firstDayOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        int weekNumber = ((currentDate.getDayOfYear() - firstDayOfMonth.getDayOfYear()) / 7) + 1;

        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("M월", Locale.KOREA)) + " " + weekNumber + "주차");

        // 현재 주의 첫 번째 날짜를 구합니다.
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            dayButtons[i].setText(String.valueOf(day.getDayOfMonth()));
        }

        // 모든 버튼의 테두리를 초기화합니다.
        for (int i = 0; i < 7; i++) {
            dayButtons[i].setBorder(new JButton().getBorder());
        }

        // 현재 날짜에 해당하는 버튼을 강조합니다.
        int dayIndex = (currentDate.getDayOfWeek().getValue() - 1);
        dayButtons[dayIndex].setBorder(BorderFactory.createLineBorder(Color.decode("#FF5733"), 3));

        updateTodoPanel();
        updateProgressBar();
    }


    public void updateTodoPanel() {
        todoPanel.removeAll();

        List<TodoEntry> tasks =(user != null) 
        		? user.getDailyTasks().getOrDefault(currentDate, new ArrayList<>())
				: new ArrayList<>();
        Font todoFont = new Font("SansSerif", Font.PLAIN, 20);

        if (tasks.isEmpty()) {
            JLabel noTaskLabel = new JLabel("예정된 할일이 없습니다.");
            noTaskLabel.setFont(todoFont);
            noTaskLabel.setForeground(Color.GRAY);
            todoPanel.add(noTaskLabel);
            todoPanel.add(Box.createVerticalGlue());
        } else {
            for (TodoEntry task : tasks) {
                JLabel todoLabel = new JLabel();
                todoLabel.setFont(todoFont);
                // 체크박스 대신 •과 √를 사용하여 완료 상태를 표시
                if (task.completed) {
                    todoLabel.setText("√  " + task.title);
                } else {
                    todoLabel.setText("•  " + task.title);
                }
                todoPanel.add(todoLabel);
                todoPanel.add(Box.createVerticalStrut(5));
            }
            todoPanel.add(Box.createVerticalGlue());
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

    public void updateProgressBar() {
        List<TodoEntry> tasks = (user != null)
                ? user.getDailyTasks().getOrDefault(currentDate, new ArrayList<>())
                : new ArrayList<>();

        if (tasks.isEmpty()) {
            progressBar.setValue(100);
            progressBar.setString("할 일 없음");
            progressBar.setForeground(Color.LIGHT_GRAY);
            return;
        }

        long completedCount = tasks.stream().filter(t -> t.completed).count();
        int totalCount = tasks.size();

        int progress = (int) Math.round(((double) completedCount / totalCount) * 100);
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
        updateProgressBarColor();
    }

    private void updateProgressBarColor() {
        int progress = progressBar.getValue();
        if (progress < 30) {
            progressBar.setForeground(new Color(255, 105, 97));
        } else if (progress < 70) {
            progressBar.setForeground(new Color(255, 218, 128));
        } else {
            progressBar.setForeground(new Color(144, 238, 144));
        }
    }

    private class DayButtonListener implements ActionListener {
        private int dayIndex;
        private final Border originalBorder = new JButton().getBorder();

        public DayButtonListener(int dayIndex) {
            this.dayIndex = dayIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Update the current date
            LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            currentDate = startOfWeek.plusDays(dayIndex);
            
            // Update the UI
            updateWeekView();
        }
    }

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
            // 버튼 텍스트 그대로 출력
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY); // 테두리 색상
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
                // 안티앨리어싱 (부드럽게)
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 배경을 둥근 사각형으로 채우기
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); 
                // (x, y, w, h, arcW, arcH)
                g2.dispose();
            }
	      @Override
	      protected void paintBorder(Graphics g) {
	          Graphics2D g2 = (Graphics2D) g.create();
	          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	          g2.dispose();
	      }
	  };
	  	panel.setOpaque(false); // 네모난 기본 배경 칠하지 않도록
	  	return panel;
    }
}


//    private void createSampleTasks() {
//    	user.getDailyTasks().put(LocalDate.of(2025, 9, 1),
//            new ArrayList<>(List.of(
//                new TodoEntry(UUID.randomUUID().toString(), "자바 프로젝트 시작", false, new Color(255, 255, 204)),
//                new TodoEntry(UUID.randomUUID().toString(), "UI 레이아웃 구상", false, new Color(255, 255, 204)),
//                new TodoEntry(UUID.randomUUID().toString(), "깃허브 레포 생성", true, new Color(255, 255, 204))
//            ))
//        );
//        dailyTasks.put(LocalDate.of(2025, 9, 2),
//            new ArrayList<>(List.of(
//                new TodoEntry(UUID.randomUUID().toString(), "알고리즘 문제 풀기", false, new Color(255, 255, 204)),
//                new TodoEntry(UUID.randomUUID().toString(), "점심 약속 (홍대)", false, new Color(255, 255, 204))
//            ))
//        );
//        dailyTasks.put(LocalDate.of(2025, 9, 4),
//            new ArrayList<>(List.of(
//                new TodoEntry(UUID.randomUUID().toString(), "마트 장보기", false, new Color(255, 255, 204)),
//                new TodoEntry(UUID.randomUUID().toString(), "저녁 요리하기", false, new Color(255, 255, 204))
//            ))
//        );
//        dailyTasks.put(LocalDate.of(2025, 9, 5),
//            new ArrayList<>(List.of(
//                new TodoEntry(UUID.randomUUID().toString(), "주말 계획 세우기", true, new Color(255, 255, 204)),
//                new TodoEntry(UUID.randomUUID().toString(), "영화 보기: 코드 마스터", true, new Color(255, 255, 204))
//            ))
//        );
//        dailyTasks.put(LocalDate.of(2025, 9, 7),
//            new ArrayList<>(List.of(
//                new TodoEntry(UUID.randomUUID().toString(), "주간 회고 작성", false, new Color(255, 255, 204)),
//                new TodoEntry(UUID.randomUUID().toString(), "다음 주 계획", false, new Color(255, 255, 204))
//            ))
//        );
//        dailyTasks.put(LocalDate.of(2025, 9, 8),
//            new ArrayList<>(List.of(
//                new TodoEntry(UUID.randomUUID().toString(), "새 기능 개발 착수", false, new Color(255, 255, 204)),
//                new TodoEntry(UUID.randomUUID().toString(), "코드 리뷰", false, new Color(255, 255, 204)),
//                new TodoEntry(UUID.randomUUID().toString(), "운동하기", false, new Color(255, 255, 204))
//            ))
//        );
//    }


