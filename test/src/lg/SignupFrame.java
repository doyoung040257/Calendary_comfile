package lg;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SignupFrame extends JFrame {
    private JTextField idField, nameField, birthField, emailIdField;
    private JPasswordField pwField, confirmPwField;
    private JRadioButton maleButton, femaleButton;
    private GlassButton checkIdButton;
    private JComboBox<String> emailDomainBox;
    private boolean isIdChecked = false;

    private Component lastField; // 마지막 field 저장용

    public SignupFrame() {
        setTitle("회원가입");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ✅ root 패널 (AliceBlue 배경, 중앙 정렬)
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(240, 248, 255));
        root.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 8, 12, 8);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        // 아이디
        rootAdd(root, gbc, row, "아이디", createIdPanel()); row++;

        // 비밀번호
        rootAdd(root, gbc, row, "비밀번호", createInputField(true));
        pwField = (JPasswordField) ((JPanel) lastField).getClientProperty("field");
        row++;

        // 비밀번호 확인
        rootAdd(root, gbc, row, "비밀번호 확인", createInputField(true));
        confirmPwField = (JPasswordField) ((JPanel) lastField).getClientProperty("field");
        row++;

        // 이름
        rootAdd(root, gbc, row, "이름", createInputField(false));
        nameField = (JTextField) ((JPanel) lastField).getClientProperty("field");
        row++;

        // 생년월일
        rootAdd(root, gbc, row, "생년월일", createInputField(false));
        birthField = (JTextField) ((JPanel) lastField).getClientProperty("field");
        row++;

        // 성별
        maleButton = new JRadioButton("남자");
        femaleButton = new JRadioButton("여자");
        maleButton.setOpaque(false);
        femaleButton.setOpaque(false);
        maleButton.setForeground(Color.BLACK);
        femaleButton.setForeground(Color.BLACK);
        ButtonGroup group = new ButtonGroup();
        group.add(maleButton);
        group.add(femaleButton);

        JPanel genderPanel = new JPanel();
        genderPanel.setOpaque(false);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        rootAdd(root, gbc, row++, "성별", genderPanel);

        // 이메일 (가로 고정)
        emailIdField = new JTextField();
        emailIdField.setPreferredSize(new Dimension(100, 30));

        String[] domains = {"naver.com", "gmail.com", "daum.net", "hanmail.net", "nate.com"};
        emailDomainBox = new JComboBox<>(domains);
        emailDomainBox.setPreferredSize(new Dimension(120, 30));
        emailDomainBox.setRenderer(new GlassComboRenderer());

        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.X_AXIS));
        emailPanel.setOpaque(false);

        emailPanel.add(emailIdField);
        emailPanel.add(Box.createHorizontalStrut(5));
        emailPanel.add(new JLabel("@"));
        emailPanel.add(Box.createHorizontalStrut(5));
        emailPanel.add(emailDomainBox);

        rootAdd(root, gbc, row++, "이메일", emailPanel);

        // 버튼
        GlassButton backButton = new GlassButton("뒤로가기");
        GlassButton signupButton = new GlassButton("회원가입");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(backButton);
        btnPanel.add(signupButton);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        root.add(btnPanel, gbc);

        setContentPane(root);

        // 이벤트
        checkIdButton.addActionListener(e -> checkId());
        signupButton.addActionListener(e -> signup());
        backButton.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }

    /** 라벨 + 입력창 한 줄 */
    private void rootAdd(JPanel root, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridy = row;

        JPanel line = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        line.setOpaque(false);

        JLabel jlabel = new JLabel(label, JLabel.RIGHT);
        jlabel.setPreferredSize(new Dimension(80, 25));
        jlabel.setForeground(Color.BLACK);

        comp.setPreferredSize(new Dimension(200, 30));

        line.add(jlabel);
        line.add(comp);

        root.add(line, gbc);

        lastField = comp;
    }

    private JPanel createIdPanel() {
        idField = new JTextField(12);
        checkIdButton = new GlassButton("중복확인");
        return createGlassPanel(new BorderLayout(5, 0), idField, checkIdButton);
    }

    private JPanel createInputField(boolean isPassword) {
        JTextField field = isPassword ? new JPasswordField() : new JTextField();
        return createGlassPanel(new BorderLayout(), field, null);
    }

    private JPanel createGlassPanel(LayoutManager layout, JComponent field, JComponent sideComp) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 120));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 255, 255, 180));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
        };
        panel.setOpaque(false);

        if (field instanceof JTextField tf) {
            tf.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tf.setForeground(Color.BLACK);
            tf.setCaretColor(Color.BLACK);
            panel.add(tf, BorderLayout.CENTER);
            panel.putClientProperty("field", tf);
        }
        if (sideComp != null) {
            panel.add(sideComp, BorderLayout.EAST);
        }
        return panel;
    }

    /** 아이디 중복확인 */
    private void checkId() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.");
            isIdChecked = false;
            return;
        }
        if (UserDatabase.userDatabase.containsKey(id)) {
            JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.");
            isIdChecked = false;
        } else {
            JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.");
            isIdChecked = true;
        }
    }

    /** 회원가입 처리 */
    private void signup() {
        if (!isIdChecked) {
            JOptionPane.showMessageDialog(this, "아이디 중복확인을 해주세요.");
            return;
        }

        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword());
        String confirmPw = new String(confirmPwField.getPassword());
        String name = nameField.getText().trim();
        String birth = birthField.getText().trim();
        String gender = maleButton.isSelected() ? "남자" : (femaleButton.isSelected() ? "여자" : "");
        String email = emailIdField.getText().trim() + "@" + emailDomainBox.getSelectedItem();

        if (id.isEmpty() || pw.isEmpty() || confirmPw.isEmpty() || name.isEmpty()
                || birth.isEmpty() || gender.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
            return;
        }
        if (!pw.equals(confirmPw)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
            return;
        }
        if (pw.length() < 8 ||
                !pw.matches(".*[A-Z].*") ||
                !pw.matches(".*[a-z].*") ||
                !pw.matches(".*[0-9].*") ||
                !pw.matches(".*[!@#$%^&*].*")) {
            JOptionPane.showMessageDialog(this, "비밀번호는 8자리 이상, 대문자/소문자/숫자/특수문자 각 1개 포함해야 합니다.");
            return;
        }
        if (!birth.matches("^\\d{4}\\.\\d{2}\\.\\d{2}$")) {
            JOptionPane.showMessageDialog(this, "생년월일은 0000.00.00 형식으로 입력하세요.");
            return;
        }
        try {
            LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "유효하지 않은 날짜입니다.");
            return;
        }
        if (!emailIdField.getText().matches("^[A-Za-z0-9+_.-]+$")) {
            JOptionPane.showMessageDialog(this, "이메일 아이디 부분이 올바르지 않습니다.");
            return;
        }

        User newUser = new User(id, pw, name, birth, gender, email);
        UserDatabase.userDatabase.put(id, newUser);
        UserDatabase.saveUsers();

        JOptionPane.showMessageDialog(this, "회원가입 성공! 로그인 화면으로 이동합니다.");
        new LoginFrame();
        dispose();
    }
}

/** GlassButton (색 입힌 버튼) */
class GlassButton extends JButton {
    private boolean hover = false;

    public GlassButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.BLACK);
        setFont(getFont().deriveFont(Font.BOLD, 14f));
        setOpaque(false);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hover = true;
                repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ✅ 기본 배경색 & Hover 색상
        Color baseColor = hover
                ? new Color(150, 180, 255, 220)  // hover 시 진한 파란색
                : new Color(200, 220, 255, 200); // 기본 연한 하늘색

        g2.setColor(baseColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        g2.setColor(new Color(120, 120, 120, 180));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

        super.paintComponent(g2);
        g2.dispose();
    }
}

/** GlassComboRenderer (콤보박스 유리 느낌) */
class GlassComboRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setOpaque(true);
        if (isSelected) {
            label.setBackground(new Color(200, 200, 255, 180));
        } else {
            label.setBackground(new Color(255, 255, 255, 120));
        }
        label.setForeground(Color.BLACK);
        return label;
    }
}
