package frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.border.Border;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import lg.User; // User 클래스를 사용하기 위해 import 추가

public class CalendarFrame01 extends JFrame {

    private User currentUser; // User 객체를 저장할 필드 추가
    LocalDate currentDate;
    private JLabel monthLabel;
    private JButton[] dayButtons = new JButton[7];
    private JPanel todoPanel;
    private JProgressBar progressBar;

    // 할 일 데이터 및 한줄평 데이터를 모든 프레임에서 공유하기 위한 static 변수
    public static Map<LocalDate, List<TodoEntry>> dailyTasks = new HashMap<>();
    public static Map<LocalDate, String> dailyReviews = new HashMap<>();

    // 할 일 항목을 나타내는 내부 클래스
    public static class TodoEntry {
        public String title;
        public boolean completed;
        public Color color;

        public TodoEntry(String title, boolean completed, Color color) {
            this.title = title;
            this.completed = completed;
            this.color = color;
        }
    }

    // 기본 생성자 유지 (LocalDate 인자)
    public CalendarFrame01(LocalDate date) {
        this.currentDate = date;

        // 초기 샘플 데이터 생성
        createSampleTasks();

        // --- 프레임 기본 설정 ---
        setTitle("주간 플래너");
        setSize(480, 800); // 크기 고정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        Font titleFont = new Font("SansSerif", Font.BOLD, 22);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        // --- 상단 패널 (월 이동 및 설정) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#D8BFD8"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel monthControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
                new MonthlyCalendarView(CalendarFrame01.this).setVisible(true);
                dispose();
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

        // '월간 달력' 버튼을 '설정'으로 변경하고 기능 제거
        JButton settingsViewButton = new JButton("설정");
        settingsViewButton.setFont(buttonFont);
        topPanel.add(settingsViewButton, BorderLayout.EAST);
        settingsViewButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "설정 화면은 아직 준비중입니다.");
        });

        // --- 날짜 버튼 패널 ---
        JPanel weekPanel = new JPanel(new BorderLayout());
        JPanel dayButtonsPanel = new JPanel(new GridLayout(1, 7, 5, 5));
        dayButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        for (int i = 0; i < 7; i++) {
            dayButtons[i] = new JButton();
            dayButtons[i].setFont(titleFont);
            dayButtons[i].addActionListener(new DayButtonListener(i));
            dayButtonsPanel.add(dayButtons[i]);
        }
        weekPanel.add(dayButtonsPanel, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(weekPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // --- 할일 목록 패널 (중앙) ---
        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS)); // 세로 정렬을 위해 BoxLayout 사용
        todoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(new JScrollPane(todoPanel), BorderLayout.CENTER);

        // --- 진행률 바 (하단) ---
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.BOLD, 18));
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // --- 하단 네비게이션 패널 ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.decode("#D8BFD8"));

        JPanel navPanel = new JPanel(new GridLayout(1, 3));
        navPanel.setOpaque(false);
        navPanel.setPreferredSize(new Dimension(0, 60));

        JButton homeButton = createNavButton("홈", buttonFont);
        JButton todoButton = createNavButton("할일", buttonFont);
        JButton groupButton = createNavButton("그룹", buttonFont);

        homeButton.addActionListener(e -> {
            // 현재 화면이 이미 홈이므로 메시지를 표시
            JOptionPane.showMessageDialog(this, "이미 홈 화면입니다.");
        });

        todoButton.addActionListener(e -> {
            // 할일 버튼 클릭 시 TodoPageView로 이동
            new TodoPageView(currentDate, this).setVisible(true);
            dispose();
        });

        groupButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "그룹 관리 화면으로 이동합니다.");
        });
        
        navPanel.add(homeButton);
        navPanel.add(todoButton);
        navPanel.add(groupButton);

        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(navPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

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

    // 로그인한 User 객체를 받는 생성자 추가
    public CalendarFrame01(User user) {
        this(LocalDate.of(2025, 9, 1)); // 기존 생성자 호출
        this.currentUser = user; // User 객체 저장
        // 사용자 이름으로 프레임 타이틀을 설정하거나 다른 개인화 작업 수행
        setTitle(user.getName() + "님의 주간 플래너");
    }

    private JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(true);
        return button;
    }

    private void setupArrowButton(JButton button, Font font) {
        button.setFont(font);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    void updateWeekView() {
        monthLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("M월", Locale.KOREA)));

        // 현재 주의 첫 번째 날짜를 구합니다.
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() % 7);

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            dayButtons[i].setText(String.valueOf(day.getDayOfMonth()));
        }

        updateTodoPanel();
        updateProgressBar();
    }

    public void updateTodoPanel() {
        todoPanel.removeAll();

        List<TodoEntry> tasks = dailyTasks.getOrDefault(currentDate, new ArrayList<>());
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

                // 할 일 항목에 마우스 리스너 추가
                todoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                todoLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // 할 일 클릭 시 TodoPageView 열기
                        new TodoPageView(currentDate, CalendarFrame01.this).setVisible(true);
                        dispose();
                    }
                });

                todoPanel.add(todoLabel);
                todoPanel.add(Box.createVerticalStrut(5));
            }
            todoPanel.add(Box.createVerticalGlue());
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

    public void updateProgressBar() {
        List<TodoEntry> tasks = dailyTasks.getOrDefault(currentDate, new ArrayList<>());

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

        public DayButtonListener(int dayIndex) {
            this.dayIndex = dayIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // 선택된 날짜로 currentDate를 업데이트하고, todoPanel을 갱신
            LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() % 7);
            currentDate = startOfWeek.plusDays(dayIndex);
            updateTodoPanel(); // 선택된 날짜에 맞는 할 일 목록을 표시
            updateProgressBar(); // 선택된 날짜에 맞는 진행률을 표시
        }
    }

    private void createSampleTasks() {
        dailyTasks.put(LocalDate.of(2025, 9, 1),
            new ArrayList<>(List.of(
                new TodoEntry("자바 프로젝트 시작", false, new Color(255, 255, 204)),
                new TodoEntry("UI 레이아웃 구상", false, new Color(255, 255, 204)),
                new TodoEntry("깃허브 레포 생성", true, new Color(255, 255, 204))
            ))
        );
        dailyTasks.put(LocalDate.of(2025, 9, 2),
            new ArrayList<>(List.of(
                new TodoEntry("알고리즘 문제 풀기", false, new Color(255, 255, 204)),
                new TodoEntry("점심 약속 (홍대)", false, new Color(255, 255, 204))
            ))
        );
        dailyTasks.put(LocalDate.of(2025, 9, 4),
            new ArrayList<>(List.of(
                new TodoEntry("마트 장보기", false, new Color(255, 255, 204)),
                new TodoEntry("저녁 요리하기", false, new Color(255, 255, 204))
            ))
        );
        dailyTasks.put(LocalDate.of(2025, 9, 5),
            new ArrayList<>(List.of(
                new TodoEntry("주말 계획 세우기", true, new Color(255, 255, 204)),
                new TodoEntry("영화 보기: 코드 마스터", true, new Color(255, 255, 204))
            ))
        );
        dailyTasks.put(LocalDate.of(2025, 9, 7),
            new ArrayList<>(List.of(
                new TodoEntry("주간 회고 작성", false, new Color(255, 255, 204)),
                new TodoEntry("다음 주 계획", false, new Color(255, 255, 204))
            ))
        );
        dailyTasks.put(LocalDate.of(2025, 9, 8),
            new ArrayList<>(List.of(
                new TodoEntry("새 기능 개발 착수", false, new Color(255, 255, 204)),
                new TodoEntry("코드 리뷰", false, new Color(255, 255, 204)),
                new TodoEntry("운동하기", false, new Color(255, 255, 204))
            ))
        );
    }
}
