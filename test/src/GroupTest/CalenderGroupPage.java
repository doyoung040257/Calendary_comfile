package GroupTest;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class CalenderGroupPage {

    private CalenderGroupPage() {
        // 의도적으로 비워둠
    }

    public static void main(String[] args) {
        String loginUser;

        while (true) {
            loginUser = JOptionPane.showInputDialog("비밀번호를 입력하세요:");

            // 취소 클릭 시 종료
            if (loginUser == null) {
                System.exit(0);
            }

            // 입력이 없으면 경고창 띄우고 반복
            if (loginUser.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "비밀번호를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            // 올바른 입력이면 반복 종료
            break;
        }

        String finalLoginUser = loginUser;

        // MainFrame 실행
        SwingUtilities.invokeLater(() -> new MainFrame(finalLoginUser));
    }

    // 외부에서 setVisible 호출해도 동작 없음
    public void setVisible(boolean b) {}
}

/*
package Group3;

import javax.swing.*;
import java.awt.*;
import lg.User;
import lg.UserDatabase;
import frame.CalendarFrame;

public class CalenderGroupPage extends JDialog {

    public CalenderGroupPage(Frame parent, String userId) {
        super(parent, "그룹 페이지 로그인", true); // modal true
        setLayout(new BorderLayout());
        setSize(300, 150);
        setLocationRelativeTo(parent);

        JLabel label = new JLabel("비밀번호를 입력하세요:");
        JPasswordField pwField = new JPasswordField();

        JButton okBtn = new JButton("확인");
        JButton cancelBtn = new JButton("취소");

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(label);
        centerPanel.add(pwField);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(okBtn);
        bottomPanel.add(cancelBtn);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ✅ 확인 버튼 동작
        okBtn.addActionListener(e -> {
            String pwInput = new String(pwField.getPassword()).trim();

            if (pwInput.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "비밀번호를 입력해주세요.",
                        "경고",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = UserDatabase.userDatabase.get(userId);
            if (user != null && user.getPassword().equals(pwInput)) {
                // 로그인 성공 → MainFrame 실행
                dispose(); // 다이얼로그 닫기
                SwingUtilities.invokeLater(() -> new MainFrame(userId));
            } else {
                JOptionPane.showMessageDialog(this,
                        "비밀번호가 틀렸습니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                pwField.setText(""); // 입력 초기화
            }
        });

        // ✅ 취소 버튼 동작 → CalenderGroupPage만 닫고 CalendarFrame 복귀
        cancelBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new CalendarFrame().setVisible(true));
        });
    }

    // 실행 테스트용 main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 예시: "testUser" 라는 아이디를 가정
            new CalenderGroupPage(null, "testUser").setVisible(true);
        });
    }
}
*/
