package calendarapp;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalendarAllButtons extends JFrame {

    public CalendarAllButtons() {
        // 프레임 기본 설정
        setTitle("9월 (모두 버튼 버전)");
        setSize(500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 전체 컨텐츠 패널
        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(contentPane);

        // 1. 상단 "9월" 버튼 (JLabel에서 JButton으로 변경)
        JButton monthButton = new JButton("9월");
        monthButton.setFont(new Font("Malgun Gothic", Font.BOLD, 28));
        
        // --- 스타일 변경 부분 ---
        // 레이블처럼 보이도록 스타일 제거
        monthButton.setBorderPainted(false);
        monthButton.setContentAreaFilled(false);
        monthButton.setFocusPainted(false);
        monthButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 오버 시 손가락 커서
        
        // 클릭 이벤트 리스너 추가
        monthButton.addActionListener(e -> {
            System.out.println("9월 버튼 클릭됨! (월 변경 기능 등을 여기에 추가할 수 있습니다)");
            // 예: JOptionPane.showMessageDialog(null, "월을 변경합니다.");
        });
        
        contentPane.add(monthButton, BorderLayout.NORTH);

        // 2. 중앙 캘린더 그리드
        JPanel calendarPanel = new JPanel(new GridLayout(7, 1, 0, 0));
        calendarPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));

        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        int[] dates = {15, 16, 17, 18, 19, 20, 21};

        for (int i = 0; i < days.length; i++) {
            boolean isLast = (i == days.length - 1);
            calendarPanel.add(createDayButton(days[i], dates[i], isLast));
        }
        
        contentPane.add(calendarPanel, BorderLayout.CENTER);

        // 3. 하단 버튼 패널
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomButtonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        bottomButtonPanel.add(createStyledButton("할일\n페이지\n버튼", Color.YELLOW));
        bottomButtonPanel.add(createStyledButton("그룹\n페이지\n버튼", Color.GREEN));
        bottomButtonPanel.add(createStyledButton("설정\n페이지\n버튼", Color.GRAY));

        contentPane.add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    // 각 날짜의 패널을 '버튼'으로 생성하는 메소드 (이전과 동일)
    private JButton createDayButton(String day, int date, boolean isLast) {
        JButton dayButton = new JButton();
        dayButton.setLayout(new BorderLayout(5, 0));
        dayButton.setBackground(Color.WHITE);
        dayButton.setBorderPainted(false);
        dayButton.setFocusPainted(false);
        dayButton.setContentAreaFilled(false);
        dayButton.setOpaque(true);
        dayButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 날짜 버튼에도 커서 추가

        if (!isLast) {
            dayButton.setBorder(new MatteBorder(0, 0, 2, 0, Color.GREEN));
        } else {
            dayButton.setBorder(new EmptyBorder(2, 0, 0, 0));
        }

        JPanel dateInfoPanel = new JPanel();
        dateInfoPanel.setOpaque(false);
        dateInfoPanel.setLayout(new BoxLayout(dateInfoPanel, BoxLayout.Y_AXIS));
        dateInfoPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JLabel dayLabel = new JLabel(day);
        dayLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel dateLabel = new JLabel(date + "일");
        dateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dateInfoPanel.add(dayLabel);
        dateInfoPanel.add(Box.createVerticalStrut(2));
        dateInfoPanel.add(dateLabel);
        dayButton.add(dateInfoPanel, BorderLayout.WEST);

        JPanel tasksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        tasksPanel.setOpaque(false);
        tasksPanel.setBorder(new EmptyBorder(5, 0, 5, 5));
        
        if (day.equals("월")) {
            tasksPanel.add(createTaskLabel("보고서 작성", new Color(173, 216, 230)));
            tasksPanel.add(createTaskLabel("아이디어 회의", new Color(255, 182, 193)));
            tasksPanel.add(createTaskLabel("기획안 제출", new Color(216, 191, 216)));
            tasksPanel.add(createTaskLabel("참고용", new Color(220, 220, 220)));
        } else if (day.equals("화")) {
            tasksPanel.add(createTaskLabel("개인 공부", new Color(144, 238, 144)));
        } else if (day.equals("수")) {
            tasksPanel.add(createTaskLabel("새 기능 구상", new Color(255, 255, 224)));
        } else if (day.equals("목")) {
            tasksPanel.add(createTaskLabel("최종 발표", new Color(255, 192, 203)));
        } else if (day.equals("금")) {
            tasksPanel.add(createTaskLabel("자료 정리", new Color(255, 250, 205)));
        }
        dayButton.add(tasksPanel, BorderLayout.CENTER);

        dayButton.addActionListener(e -> System.out.println("9월 " + date + "일 (" + day + ") 클릭됨!"));
        return dayButton;
    }
    
    // 할일 항목 레이블 생성 메소드 (이전과 동일)
    private JLabel createTaskLabel(String text, Color bgColor) {
        JLabel taskLabel = new JLabel("✔ " + text);
        taskLabel.setOpaque(true);
        taskLabel.setBackground(bgColor);
        taskLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
        taskLabel.setBorder(new EmptyBorder(4, 7, 4, 7));
        return taskLabel;
    }
    
    // 하단 버튼 생성 메소드 (이전과 동일)
    private JButton createStyledButton(String text, Color borderColor) {
        JButton button = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        button.setPreferredSize(new Dimension(100, 80));
        button.setBackground(UIManager.getColor("Panel.background"));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 3));
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalendarAllButtons app = new CalendarAllButtons();
            app.setVisible(true);
        });
    }
}