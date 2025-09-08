package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
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

    private LocalDate currentDate;
    private JLabel monthLabel;
    private JPanel dateGridPanel;
    private CalendarFrame01 mainFrame;

    public MonthlyCalendarView(CalendarFrame01 mainFrame) {
        this.mainFrame = mainFrame;
        this.currentDate = mainFrame.currentDate;

        // --- 프레임 기본 설정 ---
        setTitle("월간 플래너");
        setSize(480, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // --- 상단 컨트롤 패널 ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));

        // 월 이동 버튼
        JButton prevButton = new JButton("◀");
        JButton nextButton = new JButton("▶");
        JButton weeklyViewButton = new JButton("Weekly");
        weeklyViewButton.setFocusPainted(false);
        weeklyViewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(prevButton);
        buttonPanel.add(monthLabel);
        buttonPanel.add(nextButton);
        buttonPanel.add(weeklyViewButton);

        headerPanel.add(buttonPanel, BorderLayout.CENTER);

        // 요일 레이블
        JPanel dayOfWeekPanel = new JPanel(new GridLayout(1, 7));
        String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < dayNames.length; i++) {
            JLabel dayLabel = new JLabel(dayNames[i], SwingConstants.CENTER);
            dayLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
            if (i == 0) dayLabel.setForeground(Color.RED);
            if (i == 6) dayLabel.setForeground(Color.BLUE);
            dayOfWeekPanel.add(dayLabel);
        }
        headerPanel.add(dayOfWeekPanel, BorderLayout.SOUTH);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // --- 날짜 그리드 패널 ---
        dateGridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        dateGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(dateGridPanel, BorderLayout.CENTER);

        // --- 액션 리스너 ---
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        weeklyViewButton.addActionListener(e -> {
            // mainFrame의 currentDate를 월간 뷰의 날짜로 동기화
            mainFrame.currentDate = this.currentDate;
            mainFrame.updateWeekView(); // 이 부분이 수정되었습니다.
            mainFrame.setVisible(true);
            dispose();
        });

        updateCalendar();
    }

    private void updateCalendar() {
        dateGridPanel.removeAll();
        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MMMM", Locale.KOREA);
        monthLabel.setText(yearMonth.format(formatter));

        LocalDate prevMonth = firstOfMonth.minusDays(dayOfWeekValue % 7);
        for (int i = 0; i < dayOfWeekValue % 7; i++) {
            addDateCell(prevMonth.plusDays(i), false);
        }

        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = firstOfMonth.plusDays(i);
            addDateCell(date, true);
        }

        int totalCells = dayOfWeekValue % 7 + daysInMonth;
        int remainingCells = 42 - totalCells;
        if (totalCells <= 35) {
            remainingCells = 35 - totalCells;
        }

        for (int i = 0; i < remainingCells; i++) {
            LocalDate nextMonth = firstOfMonth.plusDays(daysInMonth + i);
            addDateCell(nextMonth, false);
        }

        dateGridPanel.revalidate();
        dateGridPanel.repaint();
    }

    private void addDateCell(LocalDate date, boolean isCurrentMonth) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cell.setBackground(Color.WHITE);

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

        cell.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isCurrentMonth) {
                    mainFrame.currentDate = date;
                    mainFrame.updateWeekView(); // 이 부분이 수정되었습니다.
                    mainFrame.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "이전/다음 달의 날짜입니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        dateGridPanel.add(cell);
    }
}
