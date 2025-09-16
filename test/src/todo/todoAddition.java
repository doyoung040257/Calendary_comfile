package todo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lg.SessionManager;
import javax.swing.*;
import javax.swing.border.LineBorder;

import frame.CalendarFrame01; // 캘린더 데이터 접근을 위해 import
import frame.DateParser;      // 날짜 파싱 유틸리티 import
import frame.TodoPageView;

public class todoAddition extends JFrame {

	private final todoListMake list;
	private final Runnable afterSave;
	private TodoPageView pageView;

    public todoAddition(todoListMake list, Runnable afterSave, TodoPageView pageView) {
    	this.list = list;
    	this.afterSave = afterSave;
    	this.pageView = pageView;
    }

    public void todo_addition_page() {
    	LocalDate targetDate;
        
    	Font titleFont = new Font("맑은 고딕", Font.BOLD, 25);
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
    	
        setTitle("My Todo");
        setSize(350, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        
        // 상단 제목
        JPanel title = createNavPanel();
        title.setBounds(10, 10, 313, 50);
        title.setBackground(Color.LIGHT_GRAY);
        
        JLabel todo = new JLabel("할 일 추가하기", JLabel.CENTER);
        todo.setFont(titleFont);
        title.add(todo);
        add(title);
        
        JPanel centerPanel = createNavPanel();
        centerPanel.setLayout(null);
        centerPanel.setBounds(10,70,313,420);
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel);
        
        // 할 일 - 그룹
        JPanel one = createNavPanel();
        one.setLayout(null);
        one.setBounds(10, 10, 290, 35);
        centerPanel.add(one);
        
        JLabel todoGroup = new JLabel("그룹", JLabel.CENTER);
        todoGroup.setBounds(5,5,50,25);
		todoGroup.setFont(buttonFont);
        one.add(todoGroup);
        
        String[] items = {"업무", "건강", "공부", "취미", "금융", "기타"};
        JComboBox<String> groupbox = new JComboBox<String>(items);
        groupbox.setBounds(60,5,220,25);
        one.add(groupbox);
        
        groupbox.addActionListener(e -> {
            String selected = (String) groupbox.getSelectedItem();
            System.out.println("선택된 항목: " + selected);
        });
        
        // 할 일 - 제목
        JPanel two = createNavPanel();
        two.setLayout(null);
        two.setBounds(10, 55, 290, 35);
        centerPanel.add(two);
        
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(5,5,50,25);
		todoTitle.setFont(buttonFont);
        two.add(todoTitle);

        JTextField txt = new JTextField("할 일 입력");
        txt.setBounds(60,5,220,25);
        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
        txt.setBorder(new LineBorder(Color.BLACK, 1));
        two.add(txt);
        
        txt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals("할 일 입력")) txt.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) txt.setText("할 일 입력");
            }
        });

        // 할 일 날짜
        JPanel three = createNavPanel();
        three.setLayout(null);
        three.setBounds(10, 100, 290, 35);
        centerPanel.add(three);
        
        JLabel daytitle = new JLabel("날짜", JLabel.CENTER);
        daytitle.setBounds(5,5,50,25);
		daytitle.setFont(buttonFont);
        three.add(daytitle);
        
        LocalDate today = LocalDate.now();
        
        JButton datebtn = new JButton();
        datebtn.setBounds(100,5,130,25);
        datebtn.setFocusPainted(false);
        if (pageView != null) {
        	 datebtn.setText(pageView.getToDate());
        }else {
            targetDate = LocalDate.now();
            String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dayOfWeek = targetDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String targetDateStr = formattedDate + "[" + dayOfWeek + "]";
            datebtn.setText(targetDateStr);
        }
        datebtn.addActionListener(e -> new todoCalendar((year, month, day, dayWeek) -> datebtn.setText(year + "-" + month + "-" + day + "[" + dayWeek + "]")));
        three.add(datebtn);

        // 할 일 - 시간
        JPanel four = createNavPanel();
        four.setLayout(null);
        four.setBounds(10, 145, 290, 35);
        centerPanel.add(four);
        
        JLabel timetitle = new JLabel("시간", JLabel.CENTER);
        timetitle.setBounds(5,5,50,25);
		timetitle.setFont(buttonFont);
        four.add(timetitle);

        LocalTime nowtime = LocalTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH시 mm분");
		
        JButton timebtn = new JButton();
        timebtn.setBounds(100,5,130,25);
        timebtn.setFocusPainted(false);
		timebtn.setText(nowtime.format(formatter2));
		timebtn.addActionListener(e -> new todoClock((hour, minute) -> timebtn.setText(hour + "시 " + minute + "분")));
        four.add(timebtn);
        
        // 할 일 - 중요도
        JPanel five = createNavPanel();
        five.setLayout(null);
        five.setBounds(10, 190, 290, 35);
        centerPanel.add(five);
        
        JLabel importancetitle = new JLabel("중요도", JLabel.CENTER);
        importancetitle.setBounds(5,5,50,25);
		importancetitle.setFont(buttonFont);
        five.add(importancetitle);

        BufferedImage img1 = todoStarMake.createStarImage(25,25); // 노란별
        BufferedImage img2 = todoStarMake2.createStarImage(25,25); // 회색별
        ImageIcon ystar = new ImageIcon(img1);
        ImageIcon gstar = new ImageIcon(img2);
		
		JLabel[] starLabels = new JLabel[3];
		int x = 113;
        for (int i = 0; i < starLabels.length; i++) {
            final int starIndex = i;
            starLabels[i] = new JLabel(gstar);
            starLabels[i].setBounds(x, 5, 25, 25);
            five.add(starLabels[i]);
            x += 40;
			
		    final int index = i;
		    starLabels[i].addMouseListener(new MouseAdapter() {
		        @Override
		        public void mouseClicked(MouseEvent e) {
		            boolean isYellow = starLabels[index].getIcon().equals(ystar);
		            if (isYellow) {
		                for (int j = 0; j < starLabels.length; j++) starLabels[j].setIcon(gstar);
		            } else {
		                for (int j = 0; j < starLabels.length; j++) starLabels[j].setIcon(j <= index ? ystar : gstar);
		            }
		        }
		    });
		}

        // 할 일 - 메모
        JPanel six = createNavPanel();
        six.setLayout(null);
        six.setBounds(10, 235, 290, 175);
        centerPanel.add(six);
        
        JLabel noteTitle = new JLabel("메모", JLabel.CENTER);
        noteTitle.setFont(buttonFont);
        noteTitle.setBounds(5,5,50,150);
        six.add(noteTitle);
        
        JTextArea note = new JTextArea("메모를 입력하세요");
        note.setBounds(60,12,220,150);
		todoGroup.setFont(buttonFont);
        note.setBorder(new LineBorder(Color.BLACK, 1));
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        six.add(note);
        
        note.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (note.getText().equals("메모를 입력하세요")) note.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (note.getText().isEmpty()) note.setText("메모를 입력하세요");
            }
        });
        
        // 하단 패널
        JPanel bottomPanel = createNavPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.setBounds(10, 500, 313, 50);
        bottomPanel.setBackground(Color.WHITE);
        add(bottomPanel);

        // 저장 버튼
        JButton addition = createNavButton("추가",buttonFont);
        addition.setPreferredSize(new Dimension(120, 40));
//        save.setFocusPainted(false);
        bottomPanel.add(addition);

        addition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String groupStr = (String) groupbox.getSelectedItem();
                String workStr = txt.getText().trim();
                String dayStr = datebtn.getText();
                String timeStr = timebtn.getText();
                String memoStr = note.getText().trim();
                
                int importance = 0;
                for (JLabel starLabel : starLabels) {
                    if (starLabel.getIcon() == ystar) importance++;
                }

                // 1. 기존 todoListMake에 추가
                list.addTodo(groupStr, workStr, dayStr, timeStr, memoStr, importance);
                String uuid = list.getTodolist().get(list.getTodolist().size()-1).getId();
                
                LocalDate todoDate = frame.DateParser.parseDate(dayStr);
                
                lg.User currentUser = lg.SessionManager.getCurrentUser();
                if (currentUser != null) {
                    List<CalendarFrame01.TodoEntry> tasksForDay =
                        currentUser.getDailyTasks().computeIfAbsent(todoDate, k -> new ArrayList<>());

                    CalendarFrame01.TodoEntry newEntry = new CalendarFrame01.TodoEntry(
                        uuid, workStr, false, new Color(255, 255, 204)
                    );
                    tasksForDay.add(newEntry);

                    // UserDatabase 갱신
                    currentUser.setTodolist(list);
                    lg.UserDatabase.userDatabase.put(currentUser.getId(), currentUser);
                    lg.UserDatabase.saveUsers();
                }

				// ✅ 추가
				 if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
                    JOptionPane.showMessageDialog(todoAddition.this, "할 일이 추가되었습니다!");
                }
				
                if (afterSave != null) afterSave.run();
                dispose();
            }
        });
        
        // 닫기 버튼
        JButton cancel = createNavButton("닫기",buttonFont);
        cancel.setPreferredSize(new Dimension(120, 40));
//        cancel.setFocusPainted(false);
        bottomPanel.add(cancel);
        cancel.addActionListener(e -> dispose());
        
        setVisible(true);
    }

    public JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text) {
        
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 🚩 상태별 색상 처리
                if (getModel().isPressed()) { // 클릭 상태
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) { // hover 상태
                    g2.setColor(new Color(220, 220, 255)); // 💡 연한 파랑 hover
                } else { // 기본
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

        // 🚩 hover 활성화
        button.setRolloverEnabled(true);

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


