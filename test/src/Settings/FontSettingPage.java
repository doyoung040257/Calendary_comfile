package Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FontSettingPage extends JFrame {

	private JComboBox<String> fontCombo;
	private JComboBox<Integer> sizeCombo;
	private JRadioButton plainBtn, boldBtn, italicBtn;
	private JLabel previewLabel;
	private Font selectedFont;
	private SettingsMenu parentMenu;

	private JPanel panel;

	public FontSettingPage(SettingsMenu parentMenu) {
		this.parentMenu = parentMenu;

		setTitle("글꼴 설정");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		// 전체 기본 폰트 한글 깨짐 방지
		Font defaultFont = new Font("맑은 고딕", Font.PLAIN, 14);
		UIManager.put("Label.font", defaultFont);
		UIManager.put("Button.font", defaultFont);
		UIManager.put("RadioButton.font", defaultFont);
		UIManager.put("ComboBox.font", defaultFont);

		String[] fonts = { "궁서체", "맑은 고딕", "새굴림", "돋움체", "휴먼모음T", "HY얕은 샘물M" };

		// 시스템에 설치된 글꼴 확인
		String[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		java.util.List<String> availableFonts = new java.util.ArrayList<>();

		for (String f : fonts) {
			boolean exists = false;
			for (String sysFont : systemFonts) {
				if (sysFont.equalsIgnoreCase(f)) {
					exists = true;
					break;
				}
			}
			// 설치되어 있으면 추가
			if (exists) {
				availableFonts.add(f);
			}
		}

		// 만약 리스트가 비어 있으면 기본 글꼴만 추가
		if (!availableFonts.contains(getName())) {
		    String fontName = "맑은 고딕";
		}

		// ✅ 최종적으로 JComboBox 생성
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

		JButton applyBtn = new JButton("적용");
		applyBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		applyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		applyBtn.addActionListener(e -> {
			Font appliedFont = previewLabel.getFont();
			if (appliedFont != null) {
		        // 1. 전역 글꼴 업데이트
		        GlobalFont.currentFont = appliedFont;

		        // 2. 현재 열려있는 모든 프레임에 글꼴 적용
		        for (Window w : Window.getWindows()) {
		            if (w instanceof JFrame frame) {
		                ThemeManager.applyFontSettingPage(frame.getContentPane(), appliedFont);
		            }
		        }
		    }
			parentMenu.setVisible(true);
			this.dispose();
		});

		JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
		topPanel.add(new JLabel("글꼴 이름:"));
		topPanel.add(fontCombo);
		topPanel.add(new JLabel("글꼴 크기:"));
		topPanel.add(sizeCombo);

		JPanel stylePanel = new JPanel();
		stylePanel.add(plainBtn);
		stylePanel.add(boldBtn);
		stylePanel.add(italicBtn);
		topPanel.add(new JLabel("스타일:"));
		topPanel.add(stylePanel);

		add(topPanel, BorderLayout.NORTH);
		add(previewLabel, BorderLayout.CENTER);
		add(applyBtn, BorderLayout.SOUTH);

		ActionListener updatePreview = e -> updateFontPreview();
		fontCombo.addActionListener(updatePreview);
		sizeCombo.addActionListener(updatePreview);
		plainBtn.addActionListener(updatePreview);
		boldBtn.addActionListener(updatePreview);
		italicBtn.addActionListener(updatePreview);

		applyTheme();
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

	private void applyTheme() {
		Color bgColor;
		Color fgColor;

		switch (Setting.theme) {
		case "DARK":
			bgColor = Color.DARK_GRAY;
			fgColor = Color.WHITE;
			break;
		case "PASTEL":
			bgColor = new Color(255, 228, 225);
			fgColor = Color.BLACK;
			break;
		case "DEFAULT":
		default:
			bgColor = Color.decode("#D8BFD8");
			fgColor = Color.BLACK;
			break;
		}

		// JFrame의 contentPane에 배경색 적용
		getContentPane().setBackground(bgColor);

		// 모든 컴포넌트 반복 적용
		for (Component comp : getContentPane().getComponents()) {
			applyThemeRecursive(comp, bgColor, fgColor);
		}
	}

	private void applyThemeRecursive(Component comp, Color bg, Color fg) {
		comp.setBackground(bg);
		comp.setForeground(fg);

		if (comp instanceof Container) {
			for (Component child : ((Container) comp).getComponents()) {
				applyThemeRecursive(child, bg, fg);
			}
		}
	}

}
