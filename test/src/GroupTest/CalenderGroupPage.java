//로그인 내역을 포함하지 않은 코드. 기존에 사용자 이름만 입력하면 MainPanel.java(그룹관리창)으로 이동하여 그룹 페이지를 확인할 수 있게 해둔 코드

package GroupTest;


import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class CalenderGroupPage {
    public static void main(String[] args) {
        String loginUser = JOptionPane.showInputDialog("비밀번호를 입력하세요:");
        if (loginUser == null || loginUser.isEmpty()) loginUser = "비밀번호";

        String finalLoginUser = loginUser;
        SwingUtilities.invokeLater(() -> new MainFrame(finalLoginUser));
    }

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}

	


	
}


/* -> 로그인 내역을 포함한 첫 번째 수정 코드
package Group3;

import javax.swing.*;
import javax.swing.SwingUtilities;
import lg.UserDatabase;
import lg.User;

public class CalenderGroupPage {
    public static void main(String[] args) {
        JTextField idField = new JTextField();
        JPasswordField pwField = new JPasswordField();
        Object[] message = {
                "아이디:", idField,
                "비밀번호:", pwField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "그룹 페이지 로그인", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            // 취소 시 기본 사용자
            SwingUtilities.invokeLater(() -> new MainFrame("사용자1"));
            return;
        }

        String idInput = idField.getText().trim();
        String pwInput = new String(pwField.getPassword());

        // UserDatabase에서 확인
        if (UserDatabase.userDatabase.containsKey(idInput)) {
            User user = UserDatabase.userDatabase.get(idInput);
            if (user.getPassword().equals(pwInput)) {
                JOptionPane.showMessageDialog(null, "그룹 페이지 로그인에 성공하셨습니다.");
                String finalLoginUser = idInput;
                SwingUtilities.invokeLater(() -> new MainFrame(finalLoginUser));
            } else {
                JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 틀립니다.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 틀립니다.");
        }
    }
}
*/
//로그인 내역을 포함한 두 번째 수정코드(여기선 사용자가 로그인에 실패 시 다시 로그인 창이 뜨도록 코드를 수정했다)
/*
package Group3;

import javax.swing.*;
import lg.User;
import lg.UserDatabase;

public class CalenderGroupPage {
    public static void main(String[] args) {
        // 사용자 입력 반복
        while (true) {
            JTextField idField = new JTextField();
            JPasswordField pwField = new JPasswordField();
            Object[] message = {
                    "아이디:", idField,
                    "비밀번호:", pwField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "그룹 페이지 로그인", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) {
                // 취소 시 종료
                JOptionPane.showMessageDialog(null, "로그인을 취소하였습니다.");
                return;
            }

            String idInput = idField.getText().trim();
            String pwInput = new String(pwField.getPassword());

            // UserDatabase 확인
            User user = UserDatabase.userDatabase.get(idInput);
            if (user != null && user.getPassword().equals(pwInput)) {
                JOptionPane.showMessageDialog(null, "그룹 페이지 로그인 성공! " + user.getName() + "님 환영합니다.");
                // 로그인 성공 → MainPanel 실행
                SwingUtilities.invokeLater(() -> new MainFrame(idInput));
                break; // 반복 종료
            } else {
                JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 틀립니다.\n다시 입력해주세요.");
            }
        }
    }
}
*/

// 위의 두번째 수정 코드 내용에서 간단한 수정만 진행하였다.
/*
package Group3;

import javax.swing.*;
import lg.User;

public class CalenderGroupPage {

    public CalenderGroupPage(JFrame parentFrame, User currentUser) {
        // 사용자 입력 반복
        while (true) {
            JTextField idField = new JTextField();
            JPasswordField pwField = new JPasswordField();
            Object[] message = {
                    "아이디:", idField,
                    "비밀번호:", pwField
            };

            int option = JOptionPane.showConfirmDialog(parentFrame, message, "그룹 페이지 로그인", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(parentFrame, "로그인을 취소하였습니다.");
                return;
            }

            String idInput = idField.getText().trim();
            String pwInput = new String(pwField.getPassword());

            if (idInput.equals(currentUser.getId()) && pwInput.equals(currentUser.getPassword())) {
                JOptionPane.showMessageDialog(parentFrame, "그룹 페이지 로그인 성공! " + currentUser.getName() + "님 환영합니다.");
                // ✅ 로그인 성공 → MainPanel 실행
                SwingUtilities.invokeLater(() -> new MainFrame(currentUser.getId()));
                break;
            } else {
                JOptionPane.showMessageDialog(parentFrame, "아이디 또는 비밀번호가 틀립니다.\n다시 입력해주세요.");
            }
        }
    }
}
*/