package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class MonthlyCalendarView extends JFrame {

    private final int YEAR = 2025;
    private final int MONTH = 9;

    public MonthlyCalendarView() {
        // --- 프레임 기본 설정 ---
        setTitle("2025년 9월 달력");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 전체 레이아웃 설정 ---
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        ((JPanel) contentPane).setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 상단 (NORTH): "2025년 9월" ---
        JLabel monthLabel = new JLabel(YEAR + "년 " + MONTH + "월", SwingConstants.LEFT);
        monthLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        contentPane.add(monthLabel, BorderLayout.NORTH);

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
        JPanel dateGridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        populateDateGrid(dateGridPanel);
        calendarPanel.add(dateGridPanel, BorderLayout.CENTER);

        contentPane.add(calendarPanel, BorderLayout.CENTER);

        // --- 하단 (SOUTH): 페이지 버튼 ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton mainButton = new JButton("메인 페이지 버튼");
        mainButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        mainButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));

        mainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CalendarFrame01 calendarFrame = new CalendarFrame01();
                calendarFrame.setVisible(true);

            }
        });

        JButton settingsButton = new JButton("설정 페이지 버튼");
        settingsButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        settingsButton.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        
        settingsButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("설정 페이지 버튼");
                new SettingsMenu(); // 설정 메뉴 창 띄우기
			    dispose();
            }
        });
        
        buttonPanel.add(mainButton);
        buttonPanel.add(settingsButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateDateGrid(JPanel gridPanel) {
        YearMonth yearMonth = YearMonth.of(YEAR, MONTH);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        int daysInMonth = yearMonth.lengthOfMonth();

        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        // 1. 이전 달의 날짜 채우기
        LocalDate prevMonthLastDay = firstDayOfMonth.minusDays(1);
        for (int i = 0; i < firstDayOfWeek; i++) {
            gridPanel.add(createDayCell(prevMonthLastDay.minusDays(firstDayOfWeek - i - 1), false));
        }

        // 2. 이번 달의 날짜 채우기
        for (int day = 1; day <= daysInMonth; day++) {
            gridPanel.add(createDayCell(LocalDate.of(YEAR, MONTH, day), true));
        }

        // 3. 다음 달의 날짜 채우기
        int totalCells = 42;
        int filledCells = firstDayOfWeek + daysInMonth;
        for (int i = 1; i <= totalCells - filledCells; i++) {
            gridPanel.add(createDayCell(firstDayOfMonth.plusDays(daysInMonth + i -1), false));
        }
    }

    private JPanel createDayCell(LocalDate date, boolean isCurrentMonth) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(Color.WHITE);
        cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 셀 클릭 이벤트 리스너 추가
        cell.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isCurrentMonth) {
                    TodoPageView todoPage = new TodoPageView(date);
                    todoPage.setVisible(true);
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

        // 동기화된 할 일 목록 표시
        JPanel eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // LocalDate 객체를 키로 사용해서 할일 목록을 가져옵니다.
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
