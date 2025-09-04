package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import GroupTest.MainFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarFrame01 extends JFrame {

    // Making this static allows TodoPageView and MonthlyCalendarView to access and modify the same data
    public static Map<LocalDate, List<TodoEntry>> dailyTasks = new HashMap<>();
    public static Map<LocalDate, String> dailyReviews = new HashMap<>(); // 하루 한줄평을 저장할 새로운 맵 추가

    public static class TodoEntry {
        String title;
        boolean completed;
        Color color;

        public TodoEntry(String title, boolean completed, Color color) {
            this.title = title;
            this.completed = completed;
            this.color = color;
        }
    }

    public CalendarFrame01() {
        initializeTasks();

        setTitle("9월 (주간 목록형)");
        setSize(500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(contentPane);

        JButton monthButton = new JButton("9월");
        monthButton.setFont(new Font("Malgun Gothic", Font.BOLD, 28));
        monthButton.setBorderPainted(false);
        monthButton.setContentAreaFilled(false);
        monthButton.setFocusPainted(false);
        monthButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        monthButton.addActionListener(e -> {
            MonthlyCalendarView monthlyCalendarView = new MonthlyCalendarView();
            monthlyCalendarView.setVisible(true);

        });
        contentPane.add(monthButton, BorderLayout.NORTH);

        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setBorder(null);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        LocalDate startDate = LocalDate.of(2025, 9, 15);
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            calendarPanel.add(createDayPanel(date));
        }

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomButtonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton todoButton = createStyledButton("할일\n페이지\n버튼", new Color(255, 255, 204));
        todoButton.addActionListener(e -> {
            System.out.println("할일 페이지 버튼");
            TodoPageView todoPageView = new TodoPageView(LocalDate.now());
            todoPageView.setVisible(true);
        });

        JButton groupButton = createStyledButton("그룹\n페이지\n버튼", new Color(204, 255, 204));
        groupButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String loginUser = JOptionPane.showInputDialog("사용자 이름을 입력하세요:");
		        if (loginUser == null || loginUser.isEmpty()) loginUser = "사용자1";

		        String finalLoginUser = loginUser;
		        SwingUtilities.invokeLater(() -> new MainFrame(finalLoginUser));
				
			}
		});

        JButton settingsButton = createStyledButton("설정\n페이지\n버튼", new Color(220, 220, 220));
        settingsButton.addActionListener(e -> {
        	JDialog settingsDialog = new JDialog(this, "설정", true);
            new SettingsMenu(); // 설정 메뉴 창 띄우기
			dispose();
        });

        bottomButtonPanel.add(todoButton);
        bottomButtonPanel.add(groupButton);
        bottomButtonPanel.add(settingsButton);
        contentPane.add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    private void initializeTasks() {
        if(dailyTasks.isEmpty()){
            // 더미 데이터 생성 시 LocalDate를 key로 사용
            dailyTasks.put(LocalDate.of(2025, 9, 15), new ArrayList<>(List.of(
                    new TodoEntry("보고서 작성", false, new Color(204, 230, 255)),
                    new TodoEntry("아이디어 회의", false, new Color(255, 204, 204)),
                    new TodoEntry("기획안 제출", false, new Color(230, 204, 255)),
                    new TodoEntry("참고용", false, new Color(220, 220, 220))
            )));
            dailyTasks.put(LocalDate.of(2025, 9, 16), new ArrayList<>(List.of(
                    new TodoEntry("개인 공부", false, new Color(204, 255, 204))
            )));
            dailyTasks.put(LocalDate.of(2025, 9, 17), new ArrayList<>(List.of(
                    new TodoEntry("새 기능 구상", false, new Color(255, 255, 204))
            )));
            dailyTasks.put(LocalDate.of(2025, 9, 18), new ArrayList<>(List.of(
                    new TodoEntry("최종 발표", false, new Color(255, 204, 204)),
                    new TodoEntry("리허설", false, new Color(230, 204, 255))
            )));
            dailyTasks.put(LocalDate.of(2025, 9, 19), new ArrayList<>(List.of(
                    new TodoEntry("자료 정리", false, new Color(255, 255, 204))
            )));
            dailyTasks.put(LocalDate.of(2025, 9, 20), new ArrayList<>());
            dailyTasks.put(LocalDate.of(2025, 9, 21), new ArrayList<>());

            // 하루 한줄평 더미 데이터 초기화
            dailyReviews.put(LocalDate.of(2025, 9, 15), "프로젝트 시작!");
            dailyReviews.put(LocalDate.of(2025, 9, 16), "오늘도 열심히 공부했다.");
        }
    }

    private JPanel createDayPanel(LocalDate date) {
        JPanel dayPanel = new JPanel();
        dayPanel.setLayout(new BorderLayout());
        dayPanel.setBackground(Color.WHITE);
        dayPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        dayPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        dayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println(date.getMonthValue() + "월 " + date.getDayOfMonth() + "일 (" + date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA) + ") 클릭됨!");
                TodoPageView todoPageView = new TodoPageView(date);
                todoPageView.setVisible(true);
            }
        });
        
        JPanel dateInfoPanel = new JPanel();
        dateInfoPanel.setOpaque(false);
        dateInfoPanel.setLayout(new BoxLayout(dateInfoPanel, BoxLayout.Y_AXIS));
        dateInfoPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA);
        
        JLabel dayLabel = new JLabel(dayOfWeek);
        dayLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel dateLabel = new JLabel(date.getDayOfMonth() + "일");
        dateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        dateInfoPanel.add(dayLabel);
        dateInfoPanel.add(Box.createVerticalStrut(2));
        dateInfoPanel.add(dateLabel);

        dayPanel.add(dateInfoPanel, BorderLayout.WEST);

        JPanel tasksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tasksPanel.setOpaque(false);
        tasksPanel.setBorder(new EmptyBorder(0, 0, 5, 10));
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(10, 8));
        progressBar.setStringPainted(true);
        
        // 날짜 객체(date)를 키로 사용하여 할 일 목록을 가져옵니다.
        List<TodoEntry> todos = dailyTasks.getOrDefault(date, new ArrayList<>());
        if (!todos.isEmpty()) {
            for (TodoEntry todo : todos) {
                JCheckBox checkBox = new JCheckBox(todo.title);
                checkBox.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
                checkBox.setOpaque(true);
                checkBox.setBackground(todo.color);
                checkBox.setBorder(BorderFactory.createLineBorder(todo.color.darker(), 1));
                checkBox.setSelected(todo.completed);
                checkBox.setForeground(Color.BLACK);
                checkBox.setMargin(new Insets(2, 5, 2, 5));

                checkBox.addActionListener(e -> {
                    todo.completed = checkBox.isSelected();
                    updateProgressBar(progressBar, date);
                });
                tasksPanel.add(checkBox);
            }
        } else {
            JLabel noTaskLabel = new JLabel("할 일 없음");
            noTaskLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
            noTaskLabel.setForeground(Color.GRAY);
            tasksPanel.add(noTaskLabel);
        }

        dayPanel.add(tasksPanel, BorderLayout.CENTER);
        dayPanel.add(progressBar, BorderLayout.SOUTH);

        updateProgressBar(progressBar, date);
        
        return dayPanel;
    }

    public void updateProgressBar(JProgressBar progressBar, LocalDate date) {
        List<TodoEntry> todos = dailyTasks.getOrDefault(date, new ArrayList<>());
        if (todos.isEmpty()) {
            progressBar.setValue(100);
            progressBar.setString("할 일 없음");
            progressBar.setForeground(Color.LIGHT_GRAY);
            return;
        }

        long completedCount = todos.stream().filter(t -> t.completed).count();
        double percentage = (double) completedCount / todos.size() * 100;
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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        button.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        button.setPreferredSize(new Dimension(80, 50));
        return button;
    }

}
