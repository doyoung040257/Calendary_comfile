// MonthlyCalendarView.java
package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout; 
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Settings.SettingsMenu;
import Settings.ThemeManager;
import lg.User;


public class MonthlyCalendarView extends JFrame {

    private LocalDate currentDate = LocalDate.of(2025, 9, 1);
    private JLabel monthLabel;
    private JPanel dateGridPanel;
    private CalendarFrame01 mainFrame;
	private User user;
    
    public MonthlyCalendarView() {
        this(new CalendarFrame01());
    }

    public MonthlyCalendarView(CalendarFrame01 mainFrame) {
        this.mainFrame = mainFrame;
        this.currentDate = mainFrame.currentDate;

        // --- 프레임 기본 설정 ---
        setTitle("월간 달력");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 전체 컨텐츠 패널 설정 ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        ((JPanel) contentPane).setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 상단 (NORTH): 월 이동 및 표시 섹션 ---
        JPanel topPanel = new JPanel(new BorderLayout());
//        topPanel.setBackground(Color.decode("#D8BFD8"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel monthControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthControlPanel.setOpaque(false);
        topPanel.add(monthControlPanel, BorderLayout.WEST);
     // 테마 적용
        ThemeManager.applyTheme(topPanel);
        
        // 레이아웃을 왼쪽 정렬로 변경
        JPanel monthNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0)); // 가로 간격을 1로 수정
        monthNavPanel.setOpaque(false);

        JButton prevMonthButton = new JButton("◀");
        styleArrowButton(prevMonthButton);
        prevMonthButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        monthLabel = new JLabel();
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        JButton nextMonthButton = new JButton("▶");
        styleArrowButton(nextMonthButton);
        nextMonthButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        monthNavPanel.add(prevMonthButton);
        monthNavPanel.add(monthLabel);
        monthNavPanel.add(nextMonthButton);
        
        // monthNavPanel을 topPanel의 왼쪽에 배치
        topPanel.add(monthNavPanel, BorderLayout.WEST);
        
        // '주간' 버튼을 제거
        // topPanel.add(weeklyViewButton, BorderLayout.EAST);
        
        contentPane.add(topPanel, BorderLayout.NORTH);

        // --- 중앙 (CENTER): 요일 헤더 + 날짜 그리드 ---
        JPanel calendarPanel = new JPanel(new BorderLayout());

        // 중앙-상단: 요일 헤더 (일, 월, 화...)
        JPanel dayOfWeekPanel = new JPanel(new GridLayout(1, 7));
        String[] dayNames = { "일", "월", "화", "수", "목", "금", "토" };
        for (int i = 0; i < dayNames.length; i++) {
            JLabel dayLabel = new JLabel(dayNames[i], SwingConstants.CENTER);
            if (i == 0)
                dayLabel.setForeground(Color.RED);
            if (i == 6)
                dayLabel.setForeground(Color.BLUE);
            dayOfWeekPanel.add(dayLabel);
        }
        calendarPanel.add(dayOfWeekPanel, BorderLayout.NORTH);

        // 중앙-중앙: 실제 날짜 그리드
        dateGridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.add(dateGridPanel, BorderLayout.CENTER);

        contentPane.add(calendarPanel, BorderLayout.CENTER);

        // --- 하단 (SOUTH): 페이지 버튼 ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton mainButton = new JButton("메인 페이지");
        mainButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        mainButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));

        mainButton.addActionListener(e -> {
            mainFrame.currentDate = currentDate;
            mainFrame.updateWeekView();
            mainFrame.setVisible(true);
            dispose();
        });

        JButton settingsButton = new JButton("설정 페이지");
        settingsButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        settingsButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        
        settingsButton.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "설정 화면은 으로 이동합니다.");
             new SettingsMenu(this.user).setVisible(true);
             dispose();
        });
        
        buttonPanel.add(mainButton);
        buttonPanel.add(settingsButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        updateCalendar();
    }

    private void updateCalendar() {
        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREA)));
        dateGridPanel.removeAll();
        
        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        int daysInMonth = yearMonth.lengthOfMonth();
        
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < firstDayOfWeek; i++) {
            dateGridPanel.add(createDayCell(firstDayOfMonth.minusDays(firstDayOfWeek - i), false));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            dateGridPanel.add(createDayCell(LocalDate.of(currentDate.getYear(), currentDate.getMonth(), day), true));
        }

        int totalCells = 42;
        int filledCells = firstDayOfWeek + daysInMonth;
        for (int i = 1; i <= totalCells - filledCells; i++) {
            dateGridPanel.add(createDayCell(firstDayOfMonth.plusDays(daysInMonth + i - 1), false));
        }
        
        dateGridPanel.revalidate();
        dateGridPanel.repaint();
    }

    private JPanel createDayCell(LocalDate date, boolean isCurrentMonth) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(Color.WHITE);
        cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cell.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isCurrentMonth) {
                    TodoPageView todoPage = new TodoPageView(date, mainFrame);
                    todoPage.setVisible(true);
                    dispose();
                }
            }
        });

        JLabel dateLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dateLabel.setBorder(new EmptyBorder(5, 5, 0, 0));

        if (!isCurrentMonth) {
            dateLabel.setForeground(Color.GRAY);
        } else {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SUNDAY)
                dateLabel.setForeground(Color.RED);
            if (dayOfWeek == DayOfWeek.SATURDAY)
                dateLabel.setForeground(Color.BLUE);
        }
        cell.add(dateLabel, BorderLayout.NORTH);

        JPanel eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        lg.User currentUser = lg.SessionManager.getCurrentUser();
        List<CalendarFrame01.TodoEntry> todos =(currentUser != null)
        		? currentUser.getDailyTasks().getOrDefault(date, new ArrayList<>())
	    	    : new ArrayList<>();
        for (CalendarFrame01.TodoEntry todo : todos) {
            JLabel todoLabel = new JLabel(todo.title);
            todoLabel.setOpaque(true);
            todoLabel.setBackground(todo.color);
            todoLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 10));
            todoLabel.setBorder(new EmptyBorder(2, 4, 2, 4));
            if(todo.completed) {
                todoLabel.setText("√ " + todo.title);
            }

            eventsPanel.add(todoLabel);
            eventsPanel.add(Box.createVerticalStrut(2));
        }

        cell.add(eventsPanel, BorderLayout.CENTER);

        return cell;
    }
    
    private void styleArrowButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

