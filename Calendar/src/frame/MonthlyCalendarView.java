// MonthlyCalendarView.java
package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
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


public class MonthlyCalendarView extends JFrame {

    private LocalDate currentDate = LocalDate.of(2025, 9, 1);
    private JLabel monthLabel;
    private JPanel dateGridPanel;
    private CalendarFrame01 mainFrame;

    public MonthlyCalendarView() {
        // 기본 생성자 유지 (다른 프레임에서 매개변수 없이 호출될 경우)
        this(new CalendarFrame01());
    }

    public MonthlyCalendarView(CalendarFrame01 mainFrame) {
        this.mainFrame = mainFrame;
        // currentDate를 mainFrame의 현재 날짜로 초기화
        this.currentDate = mainFrame.currentDate;

        // --- 프레임 기본 설정 ---
        setTitle("월간 달력");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 전체 레이아웃 설정 ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        ((JPanel) contentPane).setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 상단 (NORTH): 월 이동 및 표시 섹션 ---
        JPanel monthNavigationPanel = new JPanel(new BorderLayout());
        
        // 유니코드 화살표 문자를 사용하고 글꼴을 명시적으로 지정
        JButton prevMonthButton = new JButton("◀");
        JButton nextMonthButton = new JButton("▶");
        
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        
        // 버튼 스타일 설정
        Font buttonFont = new Font("Malgun Gothic", Font.BOLD, 20);
        prevMonthButton.setFont(buttonFont);
        nextMonthButton.setFont(buttonFont);
        
        prevMonthButton.setFocusPainted(false);
        nextMonthButton.setFocusPainted(false);

        // 월 이동 버튼 리스너
        prevMonthButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        nextMonthButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        monthNavigationPanel.add(prevMonthButton, BorderLayout.WEST);
        monthNavigationPanel.add(monthLabel, BorderLayout.CENTER);
        monthNavigationPanel.add(nextMonthButton, BorderLayout.EAST);
        contentPane.add(monthNavigationPanel, BorderLayout.NORTH);

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
            // 기존 CalendarFrame01 인스턴스를 재활용
            mainFrame.currentDate = currentDate;
            mainFrame.updateWeekView();
            mainFrame.setVisible(true);
            dispose();
        });

        JButton settingsButton = new JButton("설정 페이지");
        settingsButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        settingsButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        
        settingsButton.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "설정 화면은 아직 준비중입니다.");
        });
        
        buttonPanel.add(mainButton);
        buttonPanel.add(settingsButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        // 초기 달력 표시
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
                    // 기존 CalendarFrame01 인스턴스를 TodoPageView에 전달
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

        List<CalendarFrame01.TodoEntry> todos = CalendarFrame01.dailyTasks.getOrDefault(date, new ArrayList<>());
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
}