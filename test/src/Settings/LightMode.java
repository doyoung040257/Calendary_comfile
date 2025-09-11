package Settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import frame.CalendarFrame01;
import lg.User;

public class LightMode extends JFrame {

	private JPanel panel;
	private JLabel title;
	private JRadioButton defaultBtn;
	private JRadioButton pastelBtn;
	private JRadioButton darkBtn;
	private JButton backButton;
	private JButton chbtn;
	private User user;

	public LightMode(User user) {
		this.user = user;
		initComponents();
	}

	private void initComponents() {
		Design design = new Design();
		Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
		
		setTitle("배경 선택");
		setSize(300, 255);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// 타이틀
		title = new JLabel("배경 선택", SwingConstants.CENTER);
//		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		title.setFont(new Font("맑은 고딕", Font.BOLD, 26));
		
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); 
		titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
		titlePanel.setOpaque(false);
		titlePanel.add(title);

		// 라디오 버튼
		defaultBtn = new JRadioButton("기본 배경");
		pastelBtn = new JRadioButton("파스텔 배경");
		darkBtn = new JRadioButton("블랙 배경");

		defaultBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		pastelBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		darkBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		radioPanel.setOpaque(false);

		defaultBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		pastelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		darkBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		radioPanel.add(defaultBtn);
		radioPanel.add(pastelBtn);
		radioPanel.add(darkBtn);

		// 버튼 그룹
		ButtonGroup group = new ButtonGroup();
		group.add(defaultBtn);
		group.add(pastelBtn);
		group.add(darkBtn);

		// 확인 버튼
		chbtn = design.createNavButton("확인", buttonFont);

		// 뒤로가기 버튼
		backButton = design.createNavButton("뒤로가기", buttonFont);

		// 하단 버튼 그룹(확인, 뒤로가기)
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 가운데 정렬, 버튼 사이 간격 20px
		buttonPanel.add(chbtn);
		buttonPanel.add(backButton);
		
		//패널 정렬
		titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		radioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		panel.add(titlePanel);
		panel.add(radioPanel);
		panel.add(buttonPanel);

		// 현재 테마 반영
		switch (Setting.theme) {
		case "DARK":
			darkBtn.setSelected(true);
			break;
		case "PASTEL":
			pastelBtn.setSelected(true);
			break;
		default:
			defaultBtn.setSelected(true);
			break;
		}

		add(panel);
		ThemeManager.applyTheme(this);

		// 확인 버튼 클릭 시
		chbtn.addActionListener(e -> {
			this.dispose();
			if (defaultBtn.isSelected())
				Setting.theme = "DEFAULT";
			else if (pastelBtn.isSelected())
				Setting.theme = "PASTEL";
			else if (darkBtn.isSelected())
				Setting.theme = "DARK";

			// 2. 열린 설정 창 모두 닫기
			closeAllSettingsWindows();

			for (Window w : Window.getWindows()) {
		        if (w instanceof JFrame) {
		            ThemeManager.applyTheme((JFrame) w);
		        }
		    }
			new CalendarFrame01(user).setVisible(true);
			this.dispose();

		});

		// 뒤로가기 버튼 클릭 시
		backButton.addActionListener(e -> {
			this.dispose();

			// SettingsMenu 창 열기
			new SettingsMenu(user).setVisible(true);
		});
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void closeAllSettingsWindows() {
		Window current = SwingUtilities.getWindowAncestor(this); // 현재 창 가져오기
		for (Window w : Window.getWindows()) {
			if (w instanceof JFrame && w != current) { // 현재 창 제외
				String title = ((JFrame) w).getTitle();
				if (title != null && (title.contains("설정") || title.equals("알림 설정") || title.equals("배경 선택"))) {
					w.dispose();
				}
			}
		}
	}

}
