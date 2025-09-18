// CalendarFrame01.java
package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import Settings.GlobalFont;
import Settings.SettingsMenu;
import Settings.ThemeManager;
import lg.SessionManager;
import lg.User; // User 클래스 임포트 추가

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
    User user;

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
        
        setLayout(null);

        Font titleFont = new Font("맑은 고딕", Font.BOLD, 22);
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);

        // --- 상단 패널 (월 이동 및 설정) ---
        JPanel topPanel = createNavPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.setBounds(10, 10, 445, 50);
        add(topPanel);
        

        JPanel monthControlPanel = createNavPanel2();
        monthControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        monthControlPanel.setOpaque(false);
        
        
        
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
        	if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
                JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            }
            new SettingsMenu(this.user).setVisible(true);
        });

         // --- 날짜 버튼 패널 ---
        JPanel dayButtonsPanel = createNavPanel();
        dayButtonsPanel.setLayout(new GridLayout(1, 7, 5, 5));
        dayButtonsPanel.setBounds(10, 70, 445, 60);
        dayButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        add(dayButtonsPanel);
        
        
        for (int i = 0; i < 7; i++) {
        	dayButtons[i] = createNavButton2("", buttonFont);
            dayButtons[i].addActionListener(new DayButtonListener(i));
            dayButtonsPanel.add(dayButtons[i]);
        }
        
        // --- 진행률 바 ---
        progressBar = createRoundProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        progressBar.setPreferredSize(new Dimension(445, 25));
        progressBar.setForeground(Color.decode("#F5E6CC"));
        progressBar.setBackground(Color.LIGHT_GRAY);
        progressBar.putClientProperty("excludeTheme", Boolean.TRUE);
        
        JPanel progressAndDayPanel = createNavPanel();
        progressAndDayPanel.setBounds(10, 140, 445, 25);
        progressAndDayPanel.setLayout(new BorderLayout());
        progressAndDayPanel.setBackground(Color.white);
        progressAndDayPanel.add(progressBar, BorderLayout.NORTH);
        
        add(progressAndDayPanel);
        

        // --- 할일 목록 패널 (중앙) ---
        JScrollPane scrollPane = listScrollBox();
        scrollPane.setBounds(10, 175, 445, 505);
        add(scrollPane);
        
        todoPanel = createNavPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS)); // 세로 정렬을 위해 BoxLayout 사용
        todoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        todoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        todoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	//원래코드
            	//new TodoPageView(currentDate, CalendarFrame01.this).setVisible(true);
            	 new TodoPageView(currentDate, CalendarFrame01.this,new ThemeManager()).setVisible(true);
            }
        });
        scrollPane.setViewportView(todoPanel);

        prevWeekButton.addActionListener(e -> {
            currentDate = currentDate.minusWeeks(1);
            updateWeekView();
        });

        nextWeekButton.addActionListener(e -> {
            currentDate = currentDate.plusWeeks(1);
            updateWeekView();
        });

     // 그룹 등록
        ThemeManager.register("background", this);
        ThemeManager.applyTheme();  
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
        LocalDate firstDayOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        int weekNumber = ((currentDate.getDayOfYear() - firstDayOfMonth.getDayOfYear()) / 7) + 1;
        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("M월", Locale.KOREA)) + " " + weekNumber + "주차");

        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            dayButtons[i].setText(String.valueOf(day.getDayOfMonth()));
            dayButtons[i].putClientProperty("selected", false); // 초기화
        }

        int dayIndex = (currentDate.getDayOfWeek().getValue() - 1);
        dayButtons[dayIndex].putClientProperty("selected", true);

        for (JButton btn : dayButtons) {
            btn.repaint(); // 다시 그리기
            updateTodoPanel();
            updateProgressBar();
        }
    }


    public void updateTodoPanel() {
        todoPanel.removeAll();

        List<TodoEntry> tasks =(user != null) 
        		? user.getDailyTasks().getOrDefault(currentDate, new ArrayList<>())
				: new ArrayList<>();
        //Font todoFont = new Font("맑은 고딕", Font.BOLD, 20);
        Font todoFont = GlobalFont.currentFont; //현제로 써는 이렇게 해야 할일을 눌렸을때 바꾼 글꼴이 안바뀐다.
        
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
            progressBar.setValue(0);
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
            revalidate();
            repaint();
        }
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

    private JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text) {    
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getBackground();
                Object selected = getClientProperty("selected");

                if (selected != null && (boolean) selected) {
                    bg = new Color(200, 230, 255); 
                } else if (getModel().isRollover()) {
                    bg = new Color(200, 230, 255); // Hover 색상
                }

                if (getModel().isArmed()) {
                    bg = bg.darker();
                }

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
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
	    button.putClientProperty("excludeTheme", Boolean.TRUE);
	    
        
        return button;
    }
    
    // 날짜버튼(강조테두리 생성위해 만듬) //수정
    private JButton createNavButton2(String text, Font font) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 선택된 버튼이면 강조색, 아니면 기본 배경색
                Color bg = getBackground();
                Object selected = getClientProperty("selected");
                if (selected != null && (boolean) selected) {
                    bg = new Color(200, 230, 255); // 강조색
                } else if (getModel().isRollover()) {
                    bg = new Color(200, 230, 255); // Hover 색상
                }

                if (getModel().isArmed()) {
                    bg = bg.darker();
                }

                // 둥근 사각형 배경
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);;
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY); // 항상 회색 테두리
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        
        
        button.setFont(font);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(false);
        button.putClientProperty("excludeTheme", Boolean.TRUE);
        button.putClientProperty("selected", false);
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
	  // 네모난 기본 배경 칠하지 않도록
      panel.putClientProperty("excludeTheme", Boolean.FALSE);
      panel.putClientProperty("roundPanel", Boolean.TRUE);  
      ThemeManager.register("groupA", panel);
      panel.setOpaque(false);
        
        return panel;
    }
    
    public JPanel createNavPanel2() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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
         // 네모난 기본 배경 칠하지 않도록
        panel.putClientProperty("excludeTheme", Boolean.FALSE);
        panel.putClientProperty("roundPanel", Boolean.TRUE);  
        ThemeManager.register("groupB", panel);
        panel.setOpaque(false);
        return panel;
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
                g2.setColor(Color.BLACK);   // 원하는 글자색
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

	public User getUser() {
		return this.user; 
	}
}


