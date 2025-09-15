package Settings;

import javax.swing.*;

import GroupTest.MainFrame;
import GroupTest.MainPanel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import Settings.*;
import frame.CalendarFrame01;
import lg.LoginFrame;
import lg.SessionManager;
import lg.SignupFrame;
import lg.User;
import lg.UserDatabase;
import todo.todoList;
import todo.todoListMake;

public class SettingsMenu extends JFrame {

	private JPanel parentFrame;
	private User user;
	private todoListMake listMaker;
	private String source; // 📌 출처 구분 ("calendar" or "group")
	private ThemeManager themeManager; // ThemeManager 필드 추가

	// User를 받는 생성자
	public SettingsMenu(User currentUser) {
	    this.user = currentUser;
	    this.source = "calendar";
	    this.themeManager = new ThemeManager(); // 기본값
	    initComponents();
	}
	
	// ✅ 그룹 페이지에서 열 때 호출하는 생성자
	public SettingsMenu(User currentUser, String source, JPanel parentFrame) {
		this.user = currentUser;
		this.source = source;
		this.parentFrame = parentFrame;
		this.themeManager = new ThemeManager();
		initComponents();
	}

	public SettingsMenu(User currentUser, todoListMake listMaker) {
		this.user = currentUser;
		this.listMaker = listMaker;
		this.source = "calendar"; // 기본값
		this.themeManager = new ThemeManager();
		initComponents();
	}
	
    // ThemeManager를 받는 생성자 추가
    public SettingsMenu(User currentUser, ThemeManager themeManager) {
        this.user = currentUser;
        this.themeManager = themeManager;
        this.source = "calendar";
        initComponents();
    }

	public void initComponents() {
		Design design = new Design(); // 수정

		setTitle("설정");
		setSize(250, 310);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());
        applyTheme(); // 테마 적용

		Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

		// 버튼들
		JButton themeBtn = design.createNavButton("테마 설정", buttonFont);
		JButton infoBtn = design.createNavButton("개인정보 수정", buttonFont);
		JButton notificationBtn = design.createNavButton("알림 설정", buttonFont);
		;
		JButton fontBtn = design.createNavButton("글꼴 변경", buttonFont);
		JButton logoutBtn = design.createNavButton("로그아웃", buttonFont);
		;

		// 테마 설정 페이지
		themeBtn.addActionListener(e -> {
			this.dispose(); // 기존 메뉴 닫기
			new LightMode(user); // 새 창만 열기
		});

		// 개인정보 설정 페이지
		infoBtn.addActionListener(e -> {
			if (user == null) {
				JOptionPane.showMessageDialog(this, "로그인된 사용자 정보가 없습니다.");
				return;
			}
			this.dispose();
			new PersonalInfoPage(user); // 새 창만 열기
		});

		// 알림 설정 페이지
		notificationBtn.addActionListener(e -> {
			if (user == null) {
				JOptionPane.showMessageDialog(this, "로그인된 사용자 정보가 없습니다.");
				return; // user가 없으면 알림 설정 창 열지 않음
			}
			this.dispose();
			new Notificationsetting(user); // 안전하게 user 전달
		});

		// 글꼴 변경 버튼 클릭
		fontBtn.addActionListener(e -> {
			this.setVisible(false); // 메뉴 숨기기
		    FontSettingPage fontPage = new FontSettingPage(this); // parentMenu 전달
		    fontPage.setVisible(true);
		});

		// 로그아웃 버튼 페이지
		logoutBtn.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(this, "로그아웃 하시겠습니까?", "로그아웃 확인", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				// 로그아웃 처리 (필요 시 user 초기화)
				if (user != null) {
					// ✅ 현재 사용자의 todoList를 DB에 반영
					UserDatabase.userDatabase.put(user.getId(), user);
					UserDatabase.saveUsers();
				}

				// ✅ 세션 해제
				SessionManager.logout();
				
				  // ✅ 열려 있는 모든 창 닫기
		        for (Window window : Window.getWindows()) {
		            window.dispose();
		        }

				this.dispose(); // 설정 메뉴 닫기
				new LoginFrame().setVisible(true); // 로그인 페이지 열기
			}
		});

		JButton backButton = design.createNavButton("뒤로가기", buttonFont);
		;
		backButton.addActionListener(e -> {
			if ("group".equals(source)) {
				if (parentFrame != null) {
					parentFrame.setVisible(true); // ✅ 기존 그룹 페이지 다시 보이기
				}
			} else {
				new CalendarFrame01(user).setVisible(true);
			}
			dispose(); // 설정창 닫기
		});

		// 버튼 추가
		panel.add(themeBtn);
		panel.add(Box.createVerticalStrut(10));
		panel.add(infoBtn);
		panel.add(Box.createVerticalStrut(10));
		panel.add(notificationBtn);
		panel.add(Box.createVerticalStrut(10));
		panel.add(fontBtn); // 글꼴 변경 버튼 추가
		panel.add(Box.createVerticalStrut(10));
		panel.add(logoutBtn);
		panel.add(Box.createVerticalStrut(10));
		panel.add(backButton);
		panel.add(Box.createVerticalStrut(10));

		FontManager.applyFontRecursively(this);
		ThemeManager.applyTheme(this);
		add(panel);
		setVisible(true);
	}
	
    private void applyTheme() {
        if (themeManager != null) {
            themeManager.applyTheme(this);
        }
    }
}
