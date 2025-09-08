package lg;

import javax.swing.*;
import java.awt.*;

import frame.CalendarFrame01;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("로그인");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 전체 패널 (세로 BoxLayout)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // 상단 Glue (중앙 정렬용)
        mainPanel.add(Box.createVerticalGlue());

        // 아이디
        JLabel idLabel = new JLabel("아이디");
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(idLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        idField = new JTextField(15);
        idField.setMaximumSize(new Dimension(250, 30));
        idField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(idField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 비밀번호
        JLabel pwLabel = new JLabel("비밀번호");
        pwLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(pwLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(250, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passwordField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 버튼
        JButton loginButton = new JButton("로그인");
        JButton signupButton = new JButton("회원가입");

        loginButton.setPreferredSize(new Dimension(100, 30));
        signupButton.setPreferredSize(new Dimension(100, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(signupButton);
        buttonPanel.add(loginButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(buttonPanel);

        // 하단 Glue (중앙 정렬용)
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);

        // 이벤트
        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> {
            new SignupFrame();
            dispose();
        });

        setVisible(true);
    }

    private void login() {
        String id = idField.getText();
        String pw = new String(passwordField.getPassword());

        if (UserDatabase.userDatabase.containsKey(id)) {
            User user = UserDatabase.userDatabase.get(id);
            if (user.getPassword().equals(pw)) {
                JOptionPane.showMessageDialog(this, user.getName() + "님 환영합니다!");
                CalendarFrame01 calendarFrame01 = new CalendarFrame01();
                calendarFrame01.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "존재하지 않는 아이디입니다.");
        }
    }
}


