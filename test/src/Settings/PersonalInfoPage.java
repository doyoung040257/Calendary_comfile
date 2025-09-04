package Settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import frame.CalendarFrame;
import Settings.*;
import lg.*;

public class PersonalInfoPage extends JFrame {

//	private JPanel panel;
//	private Map<String, JTextField> textFields = new HashMap<>();
//	private JButton savebtn;
//	private JButton backButton;
//
//	public PersonalInfoPage() {
//		setTitle("개인 정보");
//
//		panel = new JPanel();
//		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//
//		// User user = UserSession.getUser(); // 로그인한 사용자 정보 가져오기
//
//		/*
//		 * if (user == null) { JOptionPane.showMessageDialog(this, "로그인 정보가 없습니다.");
//		 * FrameBase.getInstance(new FrameBegin()); dispose(); return; }
//		 */
//
//		// 이름
//		panel.add(new JLabel("이름:"));
//		// JTextField nameField = new JTextField(user.getName());
//		// textFields.put("name", nameField);
//		// panel.add(nameField);
//
//		// 이메일
//		panel.add(new JLabel("이메일:"));
//		// JTextField emailField = new JTextField(user.getEmail());
//		// textFields.put("email", emailField);
//		// panel.add(emailField);
//
//		// 버튼
//		savebtn = new JButton(LanguageManager.get("button.Save"));
//		backButton = new JButton(LanguageManager.get("button.back"));
//
//		savebtn.addActionListener(e -> {
//			String newName = textFields.get("name").getText();
//			String newEmail = textFields.get("email").getText();
//
//			if (newName.isEmpty() || newEmail.isEmpty()) {
//				JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
//				dispose();
//				return;
//			}
//			// 세션의 user 객체 정보 업데이트
//			// user.setName(newName);
//			// user.setEmail(newEmail);
//
//			JOptionPane.showMessageDialog(this, "정보가 성공적으로 저장되었습니다.");
//		});
//
//		// 뒤로가기 버튼
//		backButton = new JButton(LanguageManager.get("button.back"));
//		backButton.addActionListener(e -> {
//			new SettingsMenu().setVisible(true);
//			this.dispose(); // 현재 창 닫기
//		});
//
//		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // 가운데 정렬, 버튼 사이 간격 20px
//		buttonPanel.add(savebtn);
//		buttonPanel.add(backButton);
//
//		panel.add(buttonPanel);
//
//		add(panel);
//		applyTheme();
//
//		setSize(400, 300);
//		setVisible(true);
//
//	}


	private User user;
	private JPanel panel;
	private JButton savebtn;
	private JButton backButton;

	public PersonalInfoPage(User user) { // User 객체를 생성자로 받음
		if (user == null)
			throw new IllegalArgumentException("User cannot be null");
		this.user = user;
		setTitle(user.getName() + "님의 개인정보");

		panel = new JPanel();
		panel.setLayout(new GridLayout(6, 2, 10, 10));

		add(new JLabel("아이디:"));
		add(new JLabel(user.getId()));

		add(new JLabel("이름:"));
		add(new JLabel(user.getName()));

		add(new JLabel("생년월일:"));
		add(new JLabel(user.getBirth()));

		add(new JLabel("성별:"));
		add(new JLabel(user.getGender()));

		add(new JLabel("이메일:"));
		add(new JLabel(user.getEmail()));

		savebtn = new JButton("저장");
		savebtn.addActionListener(e -> {
			// 나중에 User 객체 수정 가능 (setter 추가 필요)
			JOptionPane.showMessageDialog(this, "정보가 성공적으로 저장되었습니다.");
		});

		backButton = new JButton("뒤로가기");
		backButton.addActionListener(e -> {
			new SettingsMenu().setVisible(true);
			this.dispose(); // 현재 창 닫기
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		buttonPanel.add(savebtn);
		buttonPanel.add(backButton);

		add(buttonPanel);
		add(panel);

		applyTheme();

		setSize(400, 300);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void applyTheme() {
		Color bgColor = Settings.theme.equals("DARK") ? Color.DARK_GRAY : Color.WHITE;
		Color fgColor = Settings.theme.equals("DARK") ? Color.WHITE : Color.BLACK;

		panel.setBackground(bgColor);

		for (Component comp : panel.getComponents()) {
			if (comp instanceof JButton) {
				JButton btn = (JButton) comp;
				btn.setBackground(bgColor);
				btn.setForeground(fgColor);
			} else if (comp instanceof JLabel) {
				comp.setForeground(fgColor);
			} else if (comp instanceof JRadioButton) {
				comp.setBackground(bgColor);
				comp.setForeground(fgColor);
			}
		}
	}

}
