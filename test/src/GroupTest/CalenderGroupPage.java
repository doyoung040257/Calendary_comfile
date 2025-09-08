package GroupTest;
//사용하지 않는 코드
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import frame.CalendarFrame01;

public class CalenderGroupPage {

    private CalenderGroupPage() {
        // 의도적으로 비워둠
    }

    public static void main(String[] args) {
        String loginUser;

        while (true) {
            loginUser = JOptionPane.showInputDialog("비밀번호를 입력하세요:");

            // 취소 클릭 시 종료 -> 전체종료 되는거 때문에 수정
            if (loginUser == null) {
                // 특정 페이지로 이동 (예: OtherPage)
                SwingUtilities.invokeLater(() -> new CalendarFrame01());
                break; // while 루프 종료
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





