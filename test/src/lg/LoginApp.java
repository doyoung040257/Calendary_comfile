package lg;

import javax.swing.*;

public class LoginApp {
    public static void main(String[] args) {
        UserDatabase.loadUsers(); // 회원정보 불러오기

        SwingUtilities.invokeLater(LoginFrame::new);

        Runtime.getRuntime().addShutdownHook(new Thread(UserDatabase::saveUsers));
    }
}
