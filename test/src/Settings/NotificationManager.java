package Settings;

import javax.swing.*;
import Settings.*;

public class NotificationManager {

    // 알림 출력 메서드
	 public static void showNotification(String message) {
	        if (Settings.notificationEnabled) {
	            JOptionPane.showMessageDialog(null, message);
	        } else {
	            // 알림 꺼짐 상태이므로 팝업 띄우지 않음
	            System.out.println("🔕 알림 꺼짐: " + message);
	        }
	    }
}
