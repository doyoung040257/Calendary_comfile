package Settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ButtonGroup;

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
	private JRadioButton maleBtn;
	private JRadioButton femaleBtn;
	private ButtonGroup genderGroup;
	
	public PersonalInfoPage(User user) { // User 객체를 생성자로 받음
		if (user == null)
			throw new IllegalArgumentException("User cannot be null");
		this.user = user;
		setTitle(user.getName() + "님의 개인정보");

		Design design = new Design();
		Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
		
		setSize(280, 280);
		setLocationRelativeTo(null);
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(5, 2, 0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		// 아이디는 수정 불가
		panel.add(new JLabel("아이디:", SwingConstants.CENTER));
		panel.add(new JLabel(user.getId()));

		// 이름
		panel.add(new JLabel("이름:", SwingConstants.CENTER));
		nameField = new JTextField(user.getName());
		panel.add(nameField);

		// 생년월일
		panel.add(new JLabel("생년월일:", SwingConstants.CENTER));
		birthField = new JTextField(user.getBirth());
		panel.add(birthField);

		// 성별
		panel.add(new JLabel("성별:", SwingConstants.CENTER));
		maleBtn = new JRadioButton("남자");
		femaleBtn = new JRadioButton("여자");

		// 로그인된 User 객체에 따라 선택 초기화
		if ("남자".equals(user.getGender())) {
		    maleBtn.setSelected(true);
		} else {
		    femaleBtn.setSelected(true);
		}
		// 그룹 지정
		genderGroup = new ButtonGroup();
		genderGroup.add(maleBtn);
		genderGroup.add(femaleBtn);

		// 성별 버튼을 담은 패널 생성
		JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		genderPanel.add(maleBtn);
		genderPanel.add(femaleBtn);
		
		// 패널 색상 적용
		Color bgColor, fgColor;
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
		        bgColor = Color.decode("#f0f8ff");
		        fgColor = Color.BLACK;
		}

		// genderPanel 배경
		genderPanel.setBackground(bgColor);

		// JRadioButton 배경 적용
		maleBtn.setBackground(bgColor);
		maleBtn.setForeground(fgColor);
		femaleBtn.setBackground(bgColor);
		femaleBtn.setForeground(fgColor);

		panel.add(genderPanel);

		// 이메일
		panel.add(new JLabel("이메일:", SwingConstants.CENTER));
		emailField = new JTextField(user.getEmail());
		panel.add(emailField);

		savebtn = design.createNavButton("확인", buttonFont);
		savebtn.addActionListener(e -> {
			// User 객체에 값 반영 (User 클래스에 setter가 있어야 함)
			user.setName(nameField.getText());
			user.setBirth(birthField.getText());
			if (maleBtn.isSelected()) {
		        user.setGender("남자");
		    } else if (femaleBtn.isSelected()) {
		        user.setGender("여자");
		    }
			user.setEmail(emailField.getText());

			JOptionPane.showMessageDialog(this, "정보가 성공적으로 저장되었습니다.");

			// 현재 창 닫고 메인 캘린더로 이동
			this.dispose();
			new CalendarFrame01(user).setVisible(true);
		});

		backButton = design.createNavButton("뒤로가기", buttonFont);
		backButton.addActionListener(e -> {
			new SettingsMenu(user).setVisible(true); // 로그인된 User 객체 전달
			this.dispose();
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
		buttonPanel.add(savebtn);
		buttonPanel.add(backButton);

		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// 그룹 등록
        ThemeManager.register("background", this);
        ThemeManager.applyTheme();
		FontManager.applyFontRecursively(this);
		setVisible(true);
	}

}


