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

        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        fontCombo = new JComboBox<>(fonts);

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
        previewLabel.setFont(new Font("Dialog", Font.PLAIN, 20));

        JButton applyBtn = new JButton("적용");
        applyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        applyBtn.addActionListener(e -> {
            Font appliedFont = previewLabel.getFont();
            if (appliedFont != null && parentMenu != null) {
                ThemeManager.applyFontSettingPage(parentMenu, appliedFont);
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
}