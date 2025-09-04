package Group3;

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
