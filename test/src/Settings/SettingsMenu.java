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

	// User를 받는 생성자
	public SettingsMenu(User currentUser) {
		this.user = currentUser;
		this.source = "calendar"; // 기본은 캘린더 출처
		initComponents();
	}


	// ✅ 그룹 페이지에서 열 때 호출하는 생성자
	public SettingsMenu(User currentUser, String source, JPanel parentFrame) {
	    this.user = currentUser;
	    this.source = source;
	    this.parentFrame = parentFrame; // ✅ 부모 저장
	    initComponents();
	}
	
	public SettingsMenu(User currentUser, todoListMake listMaker) {
	    this.user = currentUser;
	    this.listMaker = listMaker;
	    this.source = "calendar"; // 기본값
	    initComponents();
	}

	public void initComponents() {
		setTitle("설정 메뉴");
		setSize(450, 350);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

		// 버튼들
		JButton themeBtn = new JButton("테마 설정");
		JButton infoBtn = new JButton("개인정보 수정");
		JButton notificationBtn = new JButton("알림 설정");
		JButton fontBtn = new JButton("글꼴 변경");
		JButton logoutBtn = new JButton("로그아웃");
		

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
			fontPage.setVisible(true); // 창 띄우기!
		});
		
		// 로그아웃 버튼 페이지
		logoutBtn.addActionListener(e -> {
			int result = JOptionPane.showConfirmDialog(
		            this,
		            "로그아웃 하시겠습니까?",
		            "로그아웃 확인",
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE
		    );

		    if (result == JOptionPane.YES_OPTION) {
		        // 로그아웃 처리 (필요 시 user 초기화)
		    	if (user != null) {
		            // ✅ 현재 사용자의 todoList를 DB에 반영
		            UserDatabase.userDatabase.put(user.getId(), user);
		            UserDatabase.saveUsers();
		            
		            if (user.getTodolist() != null) {
		                user.getTodolist().clearAllTodos();
		            }
		        }

		        // ✅ 세션 해제
		        SessionManager.logout();

		        this.dispose();                 // 설정 메뉴 닫기
		        new LoginFrame().setVisible(true); // 로그인 페이지 열기
		    }
        });
		

		JButton backButton = new JButton("뒤로가기");
		backButton.addActionListener(e -> {
		    if ("group".equals(source)) {
		        if (parentFrame != null) {
		            parentFrame.setVisible(true);  // ✅ 기존 그룹 페이지 다시 보이기
		        }
		    } else {
		        new CalendarFrame01(user).setVisible(true);
		    }
		    dispose(); // 설정창 닫기
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
		panel.add(logoutBtn);
		panel.add(Box.createVerticalStrut(15));
		panel.add(backButton);
		panel.add(Box.createVerticalStrut(15));
		
		ThemeManager.applyTheme(this);
		add(panel);
		setVisible(true);
	}


}


