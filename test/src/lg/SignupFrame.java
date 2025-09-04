package lg;
package lg;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private JTextField idField, nameField, birthField, emailField;
    private JPasswordField pwField, confirmPwField;
    private JRadioButton maleButton, femaleButton;
    private JButton checkIdButton;
    private boolean isIdChecked = false;

    public SignupFrame() {
        setTitle("회원가입");
        setSize(450, 600); // 3:4 비율
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // 아이디 + 중복확인
        addLabel(formPanel, gbc, row, "아이디");
        idField = new JTextField(12);
        checkIdButton = new JButton("중복확인");
        JPanel idPanel = new JPanel(new BorderLayout(5, 0));
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(checkIdButton, BorderLayout.EAST);
        addField(formPanel, gbc, row++, idPanel);

        // 비밀번호
        addLabel(formPanel, gbc, row, "비밀번호");
        pwField = new JPasswordField(12);
        addField(formPanel, gbc, row++, pwField);

        // 비밀번호 확인
        addLabel(formPanel, gbc, row, "비밀번호 확인");
        confirmPwField = new JPasswordField(12);
        addField(formPanel, gbc, row++, confirmPwField);

        // 이름
        addLabel(formPanel, gbc, row, "이름");
        nameField = new JTextField(12);
        addField(formPanel, gbc, row++, nameField);

        // 생년월일
        addLabel(formPanel, gbc, row, "생년월일");
        birthField = new JTextField(12);
        addField(formPanel, gbc, row++, birthField);

        // 성별
        addLabel(formPanel, gbc, row, "성별");
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        maleButton = new JRadioButton("남자");
        femaleButton = new JRadioButton("여자");
        ButtonGroup group = new ButtonGroup();
        group.add(maleButton);
        group.add(femaleButton);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        addField(formPanel, gbc, row++, genderPanel);

        // 이메일
        addLabel(formPanel, gbc, row, "이메일");
        emailField = new JTextField(12);
        addField(formPanel, gbc, row++, emailField);

        // 버튼
        JButton backButton = new JButton("뒤로가기");
        JButton signupButton = new JButton("회원가입");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.add(backButton);
        btnPanel.add(signupButton);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 이벤트
        checkIdButton.addActionListener(e -> checkId());
        signupButton.addActionListener(e -> signup());
        backButton.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel label = new JLabel(text, JLabel.RIGHT);
        label.setPreferredSize(new Dimension(100, 25)); // 라벨 고정
        panel.add(label, gbc);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, Component comp) {
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 1;
        comp.setPreferredSize(new Dimension(200, 25)); // 입력칸 고정
        panel.add(comp, gbc);
    }

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
        String gender = maleButton.isSelected() ? "남자" : femaleButton.isSelected() ? "여자" : "";
        String email = emailField.getText().trim();

        if (id.isEmpty() || pw.isEmpty() || confirmPw.isEmpty() || name.isEmpty() ||
                birth.isEmpty() || gender.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
            return;
        }

        if (!pw.equals(confirmPw)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
            return;
        }

        if (pw.length() < 8 || !pw.matches(".*[A-Z].*") || !pw.matches(".*[!@#$%^&*].*")) {
            JOptionPane.showMessageDialog(this, "비밀번호는 8자리 이상, 대문자 1개, 특수문자 포함해야 합니다.");
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

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private JTextField idField, nameField, birthField, emailField;
    private JPasswordField pwField, confirmPwField;
    private JRadioButton maleButton, femaleButton;
    private JButton checkIdButton;
    private boolean isIdChecked = false;

    public SignupFrame() {
        setTitle("회원가입");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 2, 10, 10));

        // 아이디 + 중복확인
        add(new JLabel("아이디:"));
        JPanel idPanel = new JPanel(new BorderLayout());
        idField = new JTextField();
        checkIdButton = new JButton("중복확인");
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(checkIdButton, BorderLayout.EAST);
        add(idPanel);

        // 비밀번호
        add(new JLabel("비밀번호:"));
        pwField = new JPasswordField();
        add(pwField);

        // 비밀번호 확인
        add(new JLabel("비밀번호 확인:"));
        confirmPwField = new JPasswordField();
        add(confirmPwField);

        // 이름
        add(new JLabel("이름:"));
        nameField = new JTextField();
        add(nameField);

        // 생년월일
        add(new JLabel("생년월일:"));
        birthField = new JTextField();
        add(birthField);

        // 성별
        add(new JLabel("성별:"));
        JPanel genderPanel = new JPanel();
        maleButton = new JRadioButton("남자");
        femaleButton = new JRadioButton("여자");
        ButtonGroup group = new ButtonGroup();
        group.add(maleButton);
        group.add(femaleButton);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        add(genderPanel);

        // 이메일
        add(new JLabel("이메일:"));
        emailField = new JTextField();
        add(emailField);

        // 버튼
        JButton signupButton = new JButton("회원가입");
        JButton backButton = new JButton("뒤로가기");
        add(backButton);
        add(signupButton);

        // 이벤트
        checkIdButton.addActionListener(e -> checkId());
        signupButton.addActionListener(e -> signup());
        backButton.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        setVisible(true);
    }

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
        String gender = maleButton.isSelected() ? "남자" : femaleButton.isSelected() ? "여자" : "";
        String email = emailField.getText().trim();

        if (id.isEmpty() || pw.isEmpty() || confirmPw.isEmpty() || name.isEmpty() ||
                birth.isEmpty() || gender.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
            return;
        }

        if (!pw.equals(confirmPw)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
            return;
        }

        if (pw.length() < 8 || !pw.matches(".*[A-Z].*") || !pw.matches(".*[!@#$%^&*].*")) {
            JOptionPane.showMessageDialog(this, "비밀번호는 8자리 이상, 대문자 1개, 특수문자 포함해야 합니다.");
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

