package lg;

import javax.swing.*;

import frame.CalendarFrame01;

import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("로그인");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("아이디:"));
        idField = new JTextField();
        inputPanel.add(idField);
        inputPanel.add(new JLabel("비밀번호:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginButton = new JButton("로그인");
        JButton signupButton = new JButton("회원가입");
        buttonPanel.add(signupButton);
        buttonPanel.add(loginButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

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
                new CalendarFrame01(user).setVisible(true);
                this.dispose(); // 로그인 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "존재하지 않는 아이디입니다.");
        }
    }

}





