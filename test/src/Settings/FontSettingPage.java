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

    public FontSettingPage(SettingsMenu parentMenu) {
        this.parentMenu = parentMenu;

        setTitle("글꼴 설정");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Font defaultFont = new Font("맑은 고딕", Font.PLAIN, 14);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("RadioButton.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);

        String[] fonts = {"맑은 고딕", "궁서체", "새굴림", "돋움체", "휴먼모음T", "HY얕은 샘물M"};

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
        if (availableFonts.isEmpty()) availableFonts.add("맑은 고딕");

        fontCombo = new JComboBox<>(availableFonts.toArray(new String[0]));
        Integer[] sizes = new Integer[41];
        for (int i = 0; i < sizes.length; i++) sizes[i] = i + 10;
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
        applyBtn.setFont(defaultFont);
        applyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        FontManager.applyFontRecursively(this);

    }

    private void updateFontPreview() {
        String fontName = (String) fontCombo.getSelectedItem();
        int fontSize = (Integer) sizeCombo.getSelectedItem();
        int style = Font.PLAIN;
        if (boldBtn.isSelected()) style = Font.BOLD;
        else if (italicBtn.isSelected()) style = Font.ITALIC;

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
                if ("👤".equals(text) || "🔒".equals(text)) continue;
            }

            comp.setFont(font);

            if (comp instanceof Container) {
                applyFontRecursively((Container) comp, font);
            }
        }
        container.setFont(font);
    }
}
