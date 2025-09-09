package Settings;

import javax.swing.*;

import frame.CalendarFrame01;
import lg.User;

import java.awt.*;

public class Notificationsetting extends JFrame {

	private JPanel panel;
	private JLabel titleLabel;
	private JRadioButton onRadio;
	private JRadioButton offRadio;
	private JButton confirmButton;
	private JButton backButton;
	private User user;

	public Notificationsetting(User user) {
		this.user = user;
		setTitle("알림 설정");
		setSize(400, 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// 전체 기본 폰트 설정 (한글 깨짐 방지)
		Font defaultFont = new Font("맑은 고딕", Font.PLAIN, 14);
		UIManager.put("Label.font", defaultFont);
		UIManager.put("Button.font", defaultFont);
		UIManager.put("RadioButton.font", defaultFont);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// 제목 라벨
		titleLabel = new JLabel("알림 설정", SwingConstants.CENTER);
		titleLabel.setFont(new Font("굴림", Font.BOLD, 24));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 라디오 버튼
		onRadio = new JRadioButton("알림 켜기");
		offRadio = new JRadioButton("알림 끄기");
		onRadio.setFont(new Font("굴림", Font.PLAIN, 18));
		offRadio.setFont(new Font("굴림", Font.PLAIN, 18));
		onRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
		offRadio.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 그룹 지정
		ButtonGroup group = new ButtonGroup();
		group.add(onRadio);
		group.add(offRadio);

		// 현재 설정 반영
		if (Setting.notificationsEnabled) {
			onRadio.setSelected(true);
		} else {
			offRadio.setSelected(true);
		}

		// 확인 버튼
		confirmButton = new JButton("확인");

		confirmButton.addActionListener(e -> {
			if (onRadio.isSelected()) {
				Setting.notificationsEnabled = true;
				JOptionPane.showMessageDialog(this, "알림이 켜졌습니다");
			} else {
				Setting.notificationsEnabled = false;
				JOptionPane.showMessageDialog(this, "알림이 꺼졌습니다");
			}
			ThemeManager.applyTheme(this.getOwner());

			
			// 테마 적용
			ThemeManager.applyTheme(this);

			// 모든 창 순회
			for (Window w : Window.getWindows()) {
				if (w instanceof CalendarFrame01) {
					// 캘린더 창은 그대로
					continue;
				}
				// 현재 Notificationsetting 포함 나머지 창 닫기
				w.dispose();
			}
			// 현재 Notificationsetting 창 닫기
			this.dispose();
		});

		// 뒤로가기 버튼
		backButton = new JButton("뒤로가기");
		backButton.addActionListener(e -> {
			Window window = SwingUtilities.getWindowAncestor(backButton);
			if (window != null) {
				window.dispose(); // 현재 창 닫기
			}
			new SettingsMenu(user).setVisible(true); // 새 메뉴 열기
		});

		// 버튼 패널
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		buttonPanel.add(confirmButton);
		buttonPanel.add(backButton);

		// 패널에 요소 추가
		panel.add(Box.createVerticalStrut(20));
		panel.add(titleLabel);
		panel.add(Box.createVerticalStrut(15));
		panel.add(onRadio);
		panel.add(offRadio);
		panel.add(Box.createVerticalStrut(20));
		panel.add(buttonPanel);

		add(panel);

		// 테마 적용
		ThemeManager.applyTheme(this);
		setVisible(true);
	}
}
