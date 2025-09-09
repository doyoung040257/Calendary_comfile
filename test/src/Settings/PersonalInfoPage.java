package Settings;

import java.awt.BorderLayout;
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

import frame.CalendarFrame01;
import Settings.*;
import lg.*;

public class PersonalInfoPage extends JFrame {

	private User user;
	private JPanel panel;
	private JButton savebtn;
	private JButton backButton;

	// 수정 가능한 입력 필드
	private JTextField nameField;
	private JTextField birthField;
	private JTextField genderField;
	private JTextField emailField;

	public PersonalInfoPage(User user) { // User 객체를 생성자로 받음
		if (user == null)
			throw new IllegalArgumentException("User cannot be null");
		this.user = user;
		setTitle(user.getName() + "님의 개인정보");

		panel = new JPanel();
		panel.setLayout(new GridLayout(6, 2, 10, 10));

		// 아이디는 수정 불가
		panel.add(new JLabel("아이디:"));
		panel.add(new JLabel(user.getId()));

		// 이름
		panel.add(new JLabel("이름:"));
		nameField = new JTextField(user.getName());
		panel.add(nameField);

		// 생년월일
		panel.add(new JLabel("생년월일:"));
		birthField = new JTextField(user.getBirth());
		panel.add(birthField);

		// 성별
		panel.add(new JLabel("성별:"));
		genderField = new JTextField(user.getGender());
		panel.add(genderField);

		// 이메일
		panel.add(new JLabel("이메일:"));
		emailField = new JTextField(user.getEmail());
		panel.add(emailField);

		savebtn = new JButton("확인");
		savebtn.addActionListener(e -> {
			// User 객체에 값 반영 (User 클래스에 setter가 있어야 함)
			user.setName(nameField.getText());
			user.setBirth(birthField.getText());
			user.setGender(genderField.getText());
			user.setEmail(emailField.getText());

			JOptionPane.showMessageDialog(this, "정보가 성공적으로 저장되었습니다.");

			// 현재 창 닫고 메인 캘린더로 이동
			this.dispose();
			new CalendarFrame01(user).setVisible(true);
		});

		backButton = new JButton("뒤로가기");
		backButton.addActionListener(e -> {
			new SettingsMenu(user).setVisible(true); // 로그인된 User 객체 전달
			this.dispose();
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		buttonPanel.add(savebtn);
		buttonPanel.add(backButton);

		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		applyTheme();

		setSize(400, 300);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void applyTheme() {
		Color bgColor;
		Color fgColor;

		switch (Setting.theme) {
		case "DARK":
			bgColor = Color.BLACK;
			fgColor = Color.WHITE;
			break;
		case "PASTEL":
			bgColor = new Color(255, 228, 225);
			fgColor = Color.BLACK;
			break;
		default:
			bgColor = new Color(0xD8BFD8);
			fgColor = Color.BLACK;

		}

		panel.setBackground(bgColor);

		for (Component comp : panel.getComponents()) {
			comp.setBackground(bgColor);
			comp.setForeground(fgColor);
		}
	}

}
