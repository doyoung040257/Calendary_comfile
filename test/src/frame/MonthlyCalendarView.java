// MonthlyCalendarView.java
package frame;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Settings.FontManager;
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        ((JPanel) contentPane).setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 상단 (NORTH): 월 이동 및 표시 섹션 (둥근 하늘색 패널) ---
        JPanel topPanel = createNavPanel(); // 둥근 패널
        topPanel.setBackground(Color.decode("#ADD8E6")); // 하늘색
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ThemeManager.applyTheme(topPanel);

        monthLabel = new JLabel();
        monthLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));

        JButton prevMonthButton = createNavButton2("◀", new Font("SansSerif", Font.BOLD, 16));
        prevMonthButton.setPreferredSize(new Dimension(40, 40));
        prevMonthButton.setMargin(new Insets(0, 0, 0, 0));
        prevMonthButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        JButton nextMonthButton = createNavButton2("▶", new Font("SansSerif", Font.BOLD, 16));
        nextMonthButton.setPreferredSize(new Dimension(40, 40));
        nextMonthButton.setMargin(new Insets(0, 0, 0, 0));
        nextMonthButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        JPanel monthNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        monthNavPanel.setOpaque(false); // 상단 배경이 보이도록 투명
        monthNavPanel.add(prevMonthButton);
        monthNavPanel.add(monthLabel);
        monthNavPanel.add(nextMonthButton);

        topPanel.add(monthNavPanel, BorderLayout.WEST);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // --- 중앙 (CENTER): 요일 헤더 + 날짜 그리드 ---
        JPanel calendarPanel = new JPanel(new BorderLayout());

        JPanel dayOfWeekPanel = new JPanel(new GridLayout(1, 7));
        String[] dayNames = { "일", "월", "화", "수", "목", "금", "토" };
        for (int i = 0; i < dayNames.length; i++) {
            JLabel dayLabel = new JLabel(dayNames[i], SwingConstants.CENTER);
            if (i == 0) dayLabel.setForeground(Color.RED);
            if (i == 6) dayLabel.setForeground(Color.BLUE);
            dayOfWeekPanel.add(dayLabel);
        }
        calendarPanel.add(dayOfWeekPanel, BorderLayout.NORTH);

        dateGridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.add(dateGridPanel, BorderLayout.CENTER);

        contentPane.add(calendarPanel, BorderLayout.CENTER);

        // --- 하단 (SOUTH): 페이지 버튼 ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton mainButton = createNavButton("메인 페이지", new Font("맑은 고딕", Font.BOLD, 16));
        mainButton.addActionListener(e -> {
            mainFrame.currentDate = currentDate;
            mainFrame.updateWeekView();
            mainFrame.setVisible(true);
            dispose();
        });

        JButton settingsButton = createNavButton("설정 페이지", new Font("맑은 고딕", Font.BOLD, 16));
        settingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(this.user).setVisible(true);
            dispose();
        });

        buttonPanel.add(mainButton);
        buttonPanel.add(settingsButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        FontManager.applyFontRecursively(this);
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
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER); // 수평 중앙
        dateLabel.setVerticalAlignment(SwingConstants.CENTER);   // 수직 중앙
        dateLabel.setBorder(new EmptyBorder(0, 0, 0, 0));

        if (!isCurrentMonth) {
            dateLabel.setForeground(Color.LIGHT_GRAY);
        } else {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SUNDAY) dateLabel.setForeground(Color.RED);
            if (dayOfWeek == DayOfWeek.SATURDAY) dateLabel.setForeground(Color.BLUE);
        }
        cell.add(dateLabel, BorderLayout.NORTH);

        JPanel eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        User currentUser = lg.SessionManager.getCurrentUser();
        List<CalendarFrame01.TodoEntry> todos = (currentUser != null)
                ? currentUser.getDailyTasks().getOrDefault(date, new ArrayList<>())
                : new ArrayList<>();
        for (CalendarFrame01.TodoEntry todo : todos) {
            JLabel todoLabel = new JLabel(todo.title);
            todoLabel.setOpaque(true);
            todoLabel.setBackground(todo.color);
            todoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 10));
            todoLabel.setBorder(new EmptyBorder(2, 4, 2, 4));
            if (todo.completed) todoLabel.setText("√ " + todo.title);
            eventsPanel.add(todoLabel);
            eventsPanel.add(Box.createVerticalStrut(2));
        }

        cell.add(eventsPanel, BorderLayout.CENTER);
        return cell;
    }

    // --- 상단 달 변경 버튼 생성 함수 ---
    private JButton createNavButton2(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        return button;
    }

    // --- 둥근 버튼 생성 함수 ---
    private JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) g2.setColor(getBackground().darker());
                else g2.setColor(getBackground());
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
        return button;
    }

    // --- 둥근 패널 생성 함수 ---
    private JPanel createNavPanel() {
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







