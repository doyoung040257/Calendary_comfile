package Settings;

import javax.swing.*;
import java.awt.*;
import frame.CalendarFrame01;
import lg.User;
import GroupTest.MainPanel; // ★ 추가됨: MainPanel 참조 

public class SettingsMenu extends JFrame {

    private User user;

    // ★ 추가됨: 기존 MainPanel 인스턴스 참조
    private MainPanel mainPanel;

    // ① MainPanel에서 열 때
    public SettingsMenu(User user, MainPanel mainPanel) {
        this.user = user;
        this.mainPanel = mainPanel; // ★ MainPanel 참조 저장
        initComponents();
    }

    // ② CalendarFrame 등에서 열 때
    public SettingsMenu(User user) {
        this(user, null); // ★ mainPanel null
    }

    // ③ User 없이 열리는 생성자 (기존 유지)
    public SettingsMenu() {
        this(null, null); // ★ mainPanel null
    }

    public void initComponents() {
        setTitle("설정 메뉴");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // 버튼들
        JButton themeBtn = new JButton("테마 설정");
        JButton infoBtn = new JButton("개인정보 설정");
        JButton notificationBtn = new JButton("알림 설정");
        JButton fontBtn = new JButton("글꼴 변경");

        // 테마 설정 페이지
        themeBtn.addActionListener(e -> {
            this.setVisible(false);
            LightMode lightMode = new LightMode();
            lightMode.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    setVisible(true); // 다시 설정 메뉴로
                }
            });
        });

        // 개인정보 설정 페이지
        infoBtn.addActionListener(e -> {
            if (user == null) {
                JOptionPane.showMessageDialog(this, "로그인된 사용자 정보가 없습니다.");
                return;
            }
            this.setVisible(false);
            PersonalInfoPage infoPage = new PersonalInfoPage(user);
            infoPage.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    setVisible(true);
                }
            });
        });

        // 알림 설정 페이지
        notificationBtn.addActionListener(e -> {
            this.setVisible(false);
            Notificationsetting notifyPage = new Notificationsetting();
            notifyPage.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    setVisible(true);
                }
            });
        });

        // 글꼴 변경 페이지
        fontBtn.addActionListener(e -> {
            this.setVisible(false);
            new FontSettingPage(this).setVisible(true);
        });

        // ---------------- 뒤로가기 버튼 ----------------
        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> {
            // ★ 변경됨: MainPanel이 존재하면 기존 인스턴스로 복귀
            if (mainPanel != null) {
                this.dispose();           // SettingsMenu 닫기
                mainPanel.setVisible(true); // 기존 MainPanel 보여주기
            } else {
                // ★ 기존 동작 유지: CalendarFrame01로 이동
                new CalendarFrame01().setVisible(true);
                this.dispose();
            }
        });

        // 버튼 추가
        panel.add(themeBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(infoBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(notificationBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(fontBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(backButton);

        ThemeManager.applyTheme(this);
        add(panel);
        setVisible(true);
    }
}
