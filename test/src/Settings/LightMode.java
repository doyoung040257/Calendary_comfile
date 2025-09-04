package Settings;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import frame.CalendarFrame;


public class LightMode extends JFrame {

	private JPanel panel;
	private JLabel title;
	private JRadioButton lightBtn;
	private JRadioButton darkBtn;
	private JRadioButton defaultBtn;
	private JButton backButton;
	private JButton chbtn;

	public LightMode() {

		setTitle("배경 모드 선택");
		setSize(500, 400);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		title = new JLabel("배경 모드 선택", SwingConstants.CENTER);
		title.setFont(new Font("굴림", Font.BOLD, 26));
		title.setAlignmentX(CENTER_ALIGNMENT); // 가운데 정렬

		lightBtn = new JRadioButton("화이트 모드");
		darkBtn = new JRadioButton("블랙");
		defaultBtn = new JRadioButton("기본 모드");

		lightBtn.setFont(new Font("굴림", Font.PLAIN, 20));
		darkBtn.setFont(new Font("굴림", Font.PLAIN, 20));
		defaultBtn.setFont(new Font("굴림", Font.PLAIN, 20));

		lightBtn.setAlignmentX(CENTER_ALIGNMENT);
		darkBtn.setAlignmentX(CENTER_ALIGNMENT);
		defaultBtn.setAlignmentX(CENTER_ALIGNMENT);
		
		ButtonGroup group = new ButtonGroup();
		group.add(lightBtn);
		group.add(darkBtn);
		group.add(defaultBtn);

		// 확인 버튼
		chbtn = new JButton("확인");

		// 뒤로가기 버튼
		backButton = new JButton("뒤로가기");
		backButton.addActionListener(e -> {
			new SettingsMenu().setVisible(true);
			this.dispose(); // 현재 창 닫기
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // 가운데 정렬, 버튼 사이 간격 20px
		buttonPanel.add(chbtn);
		buttonPanel.add(backButton);

		panel.add(title);
		panel.add(lightBtn);
		panel.add(darkBtn);
		panel.add(defaultBtn);
		panel.add(buttonPanel);

		if ("DARK".equals(Settings.theme)) {
			darkBtn.setSelected(true);
			
		} else {
			lightBtn.setSelected(true);
			
		}
		ThemeManager.applyTheme(this);

		chbtn.addActionListener(e -> {
			if (lightBtn.isSelected()) {
				Settings.theme = "LIGHT";
			} else if (darkBtn.isSelected()) {
				Settings.theme = "DARK";
			}
			new SettingsMenu();
			this.dispose();
			
		});

		add(panel);

		ThemeManager.applyTheme(this);
		setSize(400, 300);
		setVisible(true);
	}

}
