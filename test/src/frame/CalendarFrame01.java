// CalendarFrame01.java
package frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.border.Border;
import Settings.SettingsMenu;
import Settings.ThemeManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import lg.User; // User 클래스 임포트 추가
import Settings.GlobalFont;

public class CalendarFrame01 extends JPanel {

    LocalDate currentDate;
    private JLabel monthLabel;
    private JButton[] dayButtons = new JButton[7];
    private JPanel todoPanel;
    private JProgressBar progressBar;
    private int selectedButtonIndex = -1; // 선택된 버튼 인덱스 추가

    private User currentUser;
    User user;

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

    public CalendarFrame01(User user) {
        this(LocalDate.of(2025, 9, 1));
        this.user = user; 
        if (user != null) {
            System.out.println("로그인한 사용자: " + user.getName());
        } else {
            System.out.println("로그인한 사용자 정보 없음");
        }
    }

    public CalendarFrame01(LocalDate date) {
        this.currentDate = date;
        this.user = lg.SessionManager.getCurrentUser();

        setLayout(null);
        setBackground(Color.BLACK);

        Font titleFont = new Font("맑은 고딕", Font.BOLD, 22);
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);

        // 상단 패널
                JPanel topPanel = createNavPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.setBounds(10, 10, 445, 50);
        add(topPanel);

        JPanel monthControlPanel = new JPanel();
        monthControlPanel.setLayout(new BoxLayout(monthControlPanel, BoxLayout.X_AXIS));
        monthControlPanel.setOpaque(false);
        
        ThemeManager.applyTheme(topPanel);

        JButton prevWeekButton = new JButton("◀");
        monthLabel = new JLabel();
        monthLabel.setFont(titleFont);
        monthLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        monthLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MonthlyCalendarView().setVisible(true);
            }
        });

        JButton nextWeekButton = new JButton("▶");
        
        prevWeekButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        monthLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        nextWeekButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        setupArrowButton(prevWeekButton, titleFont);
        setupArrowButton(nextWeekButton, titleFont);

        monthControlPanel.add(prevWeekButton);
        monthControlPanel.add(Box.createHorizontalStrut(10));
        monthControlPanel.add(monthLabel);
        monthControlPanel.add(Box.createHorizontalStrut(10));
        monthControlPanel.add(nextWeekButton);

        topPanel.add(monthControlPanel, BorderLayout.WEST);

        // 설정 버튼
        JButton settingsViewButton = createNavButton("설정", buttonFont);
        topPanel.add(settingsViewButton, BorderLayout.EAST);
        settingsViewButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(this.user).setVisible(true);
        });

        // 날짜 버튼 패널
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

        // 진행률 바
        progressBar = createRoundProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        progressBar.setPreferredSize(new Dimension(445, 25));
        progressBar.setForeground(Color.decode("#F5E6CC"));
        progressBar.setBackground(Color.LIGHT_GRAY);

        JPanel progressAndDayPanel = createNavPanel();
        progressAndDayPanel.setBounds(10, 140, 445, 25);
        progressAndDayPanel.setLayout(new BorderLayout());
        progressAndDayPanel.setBackground(Color.white);
        progressAndDayPanel.add(progressBar, BorderLayout.NORTH);
        add(progressAndDayPanel);

        // 할일 목록 패널
        JScrollPane scrollPane = listScrollBox();
        scrollPane.setBounds(10, 175, 445, 505);
        add(scrollPane);

        todoPanel = createNavPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));
        todoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        todoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        todoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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

        updateWeekView();
    }

    // ◀ ▶ 버튼 hover
    private void setupArrowButton(JButton button, Font font) {
        button.setFont(font);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setRolloverEnabled(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground((new Color(173, 216, 230)));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.BLACK);
            }
        });
    }

    void updateWeekView() {
        LocalDate firstDayOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        int weekNumber = ((currentDate.getDayOfYear() - firstDayOfMonth.getDayOfYear()) / 7) + 1;
        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("M월", Locale.KOREA)) + " " + weekNumber + "주차");

        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            dayButtons[i].setText(String.valueOf(day.getDayOfMonth()));
            dayButtons[i].putClientProperty("selected", false);
        }

        int dayIndex = (currentDate.getDayOfWeek().getValue() - 1);
        dayButtons[dayIndex].putClientProperty("selected", true);

        for (JButton btn : dayButtons) {
            btn.repaint();
            updateTodoPanel();
            updateProgressBar();
        }
    }

    public void updateTodoPanel() {
        todoPanel.removeAll();
        List<TodoEntry> tasks = (user != null)
                ? user.getDailyTasks().getOrDefault(currentDate, new ArrayList<>())
                : new ArrayList<>();
        Font todoFont = GlobalFont.currentFont;

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
            LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            currentDate = startOfWeek.plusDays(dayIndex);
            updateWeekView();
            revalidate();
            repaint();
        }
    }

    private JScrollPane listScrollBox() {
        JScrollPane scrollPane = new JScrollPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        return scrollPane;
    }

    // 공통 버튼 hover
    private JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getBackground();
                if (getModel().isRollover()) {
                    bg = new Color(220, 235, 255); // Hover 색상
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
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setRolloverEnabled(true);

        return button;
    }

    // 날짜 버튼 hover
    private JButton createNavButton2(String text, Font font) {
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
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.putClientProperty("selected", false);
        button.setRolloverEnabled(true);

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

    private JProgressBar createRoundProgressBar(int min, int max) {
        JProgressBar progressBar = new JProgressBar(min, max) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int arc = 30;

                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                int progressWidth = (int) (width * getPercentComplete());
                g2.setColor(getForeground());
                g2.fillRoundRect(0, 0, progressWidth, height, arc, arc);

                String text = getString();
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.setColor(Color.BLACK);
                g2.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 - 2);

                g2.dispose();
            }
        };

        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(76, 175, 80));
        progressBar.setBackground(Color.LIGHT_GRAY);
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        return progressBar;
    }

    public User getUser() {
        return this.user;
    }
}



