package lg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import frame.CalendarFrame01;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField pwField;

    public LoginFrame() {
        setTitle("로그인");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ✅ root 패널에 직접 배경색 지정 (AliceBlue)
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(240, 248, 255));
        root.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 위쪽 스페이서 (아래로 내리기)
        gbc.gridy = 0;
        gbc.weighty = 0.8;
        root.add(Box.createVerticalStrut(1), gbc);

        // 폼
        gbc.gridy = 1;
        gbc.weighty = 0;
        root.add(buildForm(), gbc);

        // 아래쪽 스페이서
        gbc.gridy = 2;
        gbc.weighty = 0.2;
        root.add(Box.createVerticalStrut(1), gbc);

        setContentPane(root);
        setVisible(true);
    }

    /** 아이콘+입력필드 2개와 버튼 행 */
    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setOpaque(false); // 배경 투명
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        // 아이디/비밀번호 입력 박스
        JPanel idPanel = createInputField("👤", "아이디", false);
        idField = (JTextField) idPanel.getClientProperty("field");

        JPanel pwPanel = createInputField("🔒", "비밀번호", true);
        pwField = (JPasswordField) pwPanel.getClientProperty("field");

        form.add(idPanel);
        form.add(Box.createRigidArea(new Dimension(0, 12)));
        form.add(pwPanel);
        form.add(Box.createRigidArea(new Dimension(0, 18)));

        // 버튼 행
        GlassButton signupButton = new GlassButton("회원가입");
        GlassButton loginButton = new GlassButton("로그인");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btnRow.setOpaque(false);
        btnRow.add(signupButton);
        btnRow.add(loginButton);

        form.add(btnRow);

        // 이벤트
        signupButton.addActionListener(e -> {
            new SignupFrame();
            dispose();
        });
        loginButton.addActionListener(e -> login());

        return form;
    }

    /** 아이콘 + 텍스트필드 */
    private JPanel createInputField(String iconText, String placeholder, boolean isPassword) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 180)); // 반투명 흰색
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(320, 40));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        JLabel icon = new JLabel(iconText);
        icon.setFont(icon.getFont().deriveFont(16f));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 8));
        panel.add(icon, BorderLayout.WEST);

        JTextField field = isPassword ? new JPasswordField() : new JTextField();
        field.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
        field.setForeground(Color.BLACK);   // 입력 글씨 색
        field.setCaretColor(Color.BLACK);   // 커서 색
        panel.add(field, BorderLayout.CENTER);

        panel.putClientProperty("field", field);
        return panel;
    }

    private void login() {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword());

        if (UserDatabase.userDatabase.containsKey(id)) {
            User user = UserDatabase.userDatabase.get(id);
            if (user.getPassword().equals(pw)) {
                JOptionPane.showMessageDialog(this, user.getName() + "님 환영합니다!");
                new CalendarFrame01(null).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "존재하지 않는 아이디입니다.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

/** GlassButton 클래스 */
class GlassButton extends JButton {
    private boolean hover = false;

    public GlassButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE); // ✅ 버튼 글씨 흰색
        setFont(getFont().deriveFont(Font.BOLD, 14f));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color glassColor = hover
                ? new Color(200, 200, 255, 180)
                : new Color(255, 255, 255, 150);
        g2.setColor(glassColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        g2.setColor(new Color(180, 180, 180, 200));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

        super.paintComponent(g2);
        g2.dispose();
    }
}
