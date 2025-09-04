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

import frame.CalendarFrame01;
import Settings.*;
import lg.*;

public class PersonalInfoPage extends JFrame {

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

