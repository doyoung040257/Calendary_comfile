package Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class FontSettingPage extends JFrame {

	private JComboBox<String> fontCombo;
	private JComboBox<Integer> sizeCombo;
	private JRadioButton plainBtn, boldBtn, italicBtn;
	private JLabel previewLabel;
	private Font selectedFont;
	private SettingsMenu parentMenu;

	public FontSettingPage(SettingsMenu parentMenu) {
		this.parentMenu = parentMenu;

		setTitle("글꼴 설정");
		setSize(280, 255);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setLocationRelativeTo(null);

		Font defaultFont = new Font("맑은 고딕", Font.PLAIN, 14);
		UIManager.put("Label.font", defaultFont);
		UIManager.put("Button.font", defaultFont);
		UIManager.put("RadioButton.font", defaultFont);
		UIManager.put("ComboBox.font", defaultFont);

		String[] fonts = { "맑은 고딕", "궁서체", "새굴림", "돋움체", "휴먼모음T", "HY얕은 샘물M" };

		// 설치된 글꼴만 필터링
		String[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		java.util.List<String> availableFonts = new java.util.ArrayList<>();
		for (String f : fonts) {
			for (String sysFont : systemFonts) {
				if (sysFont.equalsIgnoreCase(f)) {
					availableFonts.add(f);
					break;
				}
			}
		}
		if (availableFonts.isEmpty())
			availableFonts.add("맑은 고딕");

		fontCombo = new JComboBox<>(availableFonts.toArray(new String[0]));
		Integer[] sizes = new Integer[41];
		for (int i = 0; i < sizes.length; i++)
			sizes[i] = i + 10;
		sizeCombo = new JComboBox<>(sizes);

		plainBtn = new JRadioButton("보통", true);
		boldBtn = new JRadioButton("굵게");
		italicBtn = new JRadioButton("기울임");
		ButtonGroup group = new ButtonGroup();
		group.add(plainBtn);
		group.add(boldBtn);
		group.add(italicBtn);

		previewLabel = new JLabel("가나다 ABC 123 Preview", SwingConstants.CENTER);
		previewLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		previewLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // 상, 좌, 하, 우

		JPanel bottom = new JPanel();
		bottom.setOpaque(false);

		JButton applyBtn = createNavButton("적용");
		applyBtn.setPreferredSize(new Dimension(100, 40));
		applyBtn.setFont(defaultFont);
		applyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		bottom.add(applyBtn);

		applyBtn.addActionListener(e -> {
			Font appliedFont = previewLabel.getFont();
			if (appliedFont != null) {
				GlobalFont.currentFont = appliedFont;

				// 열린 모든 JFrame에 글꼴 적용
				for (Window w : Window.getWindows()) {
					if (w instanceof JFrame) {
						GlobalFont.applyFontRecursively((JFrame) w, appliedFont);
					}
				}
			}
			parentMenu.setVisible(true);
			this.dispose();
		});

		JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
		topPanel.setBorder(new EmptyBorder(0, 0, 0, 20)); // 상, 좌, 하, 우 -> 오른쪽 20px 여백
		topPanel.add(new JLabel("글꼴 이름:", SwingConstants.CENTER));
		topPanel.add(fontCombo);
		topPanel.add(new JLabel("글꼴 크기:", SwingConstants.CENTER));
		topPanel.add(sizeCombo);

		JPanel stylePanel = new JPanel();
		stylePanel.add(plainBtn);
		stylePanel.add(boldBtn);
		stylePanel.add(italicBtn);
		topPanel.add(new JLabel("스타일:", SwingConstants.CENTER));
		topPanel.add(stylePanel);

		add(topPanel, BorderLayout.NORTH);
		add(previewLabel, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		ActionListener updatePreview = e -> updateFontPreview();
		fontCombo.addActionListener(updatePreview);
		sizeCombo.addActionListener(updatePreview);
		plainBtn.addActionListener(updatePreview);
		boldBtn.addActionListener(updatePreview);
		italicBtn.addActionListener(updatePreview);

		FontManager.applyFontRecursively(this);

		// 그룹 등록
		ThemeManager.register("background", this);
		ThemeManager.applyTheme();

	}

	private void updateFontPreview() {
		String fontName = (String) fontCombo.getSelectedItem();
		int fontSize = (Integer) sizeCombo.getSelectedItem();
		int style = Font.PLAIN;
		if (boldBtn.isSelected())
			style = Font.BOLD;
		else if (italicBtn.isSelected())
			style = Font.ITALIC;

		selectedFont = new Font(fontName, style, fontSize);
		previewLabel.setFont(selectedFont);
	}

	// 🔹 재귀적으로 컨테이너 내부 글꼴 적용 (아이콘 제외)
	private void applyFontRecursively(Container container, Font font) {
		for (Component comp : container.getComponents()) {
			// JLabel 이모지 제외
			if (comp instanceof JLabel) {
				JLabel label = (JLabel) comp;
				String text = label.getText();
				if ("👤".equals(text) || "🔒".equals(text))
					continue;
			}

			comp.setFont(font);

			if (comp instanceof Container) {
				applyFontRecursively((Container) comp, font);
			}
		}
		container.setFont(font);
	}

	private JButton createNavButton(String text) {
		JButton button = new JButton(text) {

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// 배경 색상 (눌렸을 때 어둡게)
				if (getModel().isArmed()) {
					g2.setColor(getBackground().darker());
				} else {
					g2.setColor(getBackground());
				}
				// 둥근 사각형 배경
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				g2.dispose();
				// 버튼 텍스트 그대로 출력
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.GRAY); // 테두리 색상
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
				g2.dispose();
			}
		};
		button.setBackground(Color.WHITE);
		button.setForeground(Color.BLACK);

		// 기본 버튼 효과 제거
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setOpaque(false);

		return button;
	}
}
