package Settings;

import javax.swing.*;
import java.awt.*;

public class Notificationsetting extends JFrame {

	private JPanel panel;
	private JLabel titleLabel;
	private JRadioButton onRadio;
	private JRadioButton offRadio;
	private JButton confirmButton;
	private JButton backButton;

	public Notificationsetting() {
		setTitle("알림 설정");
		setSize(400, 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
		if (Settings.notificationEnabled) {
			onRadio.setSelected(true);
		} else {
			offRadio.setSelected(true);
		}

		// 확인 버튼
		confirmButton = new JButton("확인");
		confirmButton.addActionListener(e -> {
		    if (onRadio.isSelected()) {
		        Settings.notificationEnabled = true;
		        JOptionPane.showMessageDialog(this, "알림이 켜졌습니다");
		    } else {
		        Settings.notificationEnabled = false;
		        JOptionPane.showMessageDialog(this, "알림이 꺼졌습니다");
		    }
		    dispose(); // 창 닫기
		});


	// 뒤로가기 버튼
	backButton=new JButton("뒤로가기");backButton.addActionListener(e->

	{
		new SettingsMenu();
		dispose();
	});

	// 버튼 패널
	JPanel buttonPanel = new JPanel(
			new FlowLayout(FlowLayout.CENTER, 20, 10));buttonPanel.add(confirmButton);buttonPanel.add(backButton);

	// 패널에 요소 추가
	panel.add(Box.createVerticalStrut(20));panel.add(titleLabel);panel.add(Box.createVerticalStrut(15));panel.add(onRadio);panel.add(offRadio);panel.add(Box.createVerticalStrut(20));panel.add(buttonPanel);

	add(panel);

		// 테마 적용
		ThemeManager.applyTheme(this);
		setVisible(true);
	}
}
