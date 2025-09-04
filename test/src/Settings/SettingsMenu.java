package Settings;

import javax.swing.*;
import java.awt.*;
import Settings.*;
import frame.CalendarFrame;
import lg.User;

public class SettingsMenu extends JFrame {

		private User user;
		
		 // User를 받는 생성자 
	    public SettingsMenu(User user) {
	        this.user = user;  // 로그인 성공 시 전달된 user 객체 저장
	        initComponents();
	    }

	    // ② 기존 생성자 (User 없이도 열리게)
	    public SettingsMenu() {
	        this(null);
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

		// 글꼴 변경 버튼 클릭
		fontBtn.addActionListener(e -> {
		    this.setVisible(false);                     // SettingsMenu 숨기기
		    new FontSettingPage(this).setVisible(true); // 창 띄우기!
		});


        
        

        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> {
            new CalendarFrame(user).setVisible(true);
            this.dispose();
        });

		// 버튼 추가
		panel.add(themeBtn);
		panel.add(Box.createVerticalStrut(15));
		panel.add(infoBtn);
		panel.add(Box.createVerticalStrut(15));
		panel.add(notificationBtn);
		panel.add(Box.createVerticalStrut(15));
		panel.add(fontBtn); // 글꼴 변경 버튼 추가
		panel.add(Box.createVerticalStrut(15));
		panel.add(backButton);

		ThemeManager.applyTheme(this);
		add(panel);
		setVisible(true);
	}
}
