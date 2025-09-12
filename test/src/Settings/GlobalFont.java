package Settings;

import java.awt.*;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;

public class GlobalFont {
    // 전역 글꼴 저장
    public static Font currentFont=new Font("맑은 고딕", Font.PLAIN, 14);//null이면 오류나서 임시로 글꼴, 폰트,크기 넣어둠

    // 재귀적으로 컨테이너에 폰트 적용
    public static void applyFontRecursively(Container container, Font font) {
        container.setFont(font);
        for (Component comp : container.getComponents()) {
            comp.setFont(font);
            if (comp instanceof Container) {
                applyFontRecursively((Container) comp, font);
            }
        }
    }

    // 편의 메서드: JFrame에 적용
    public static void applyFont(JFrame frame) {
        if (currentFont != null) {
            applyFontRecursively(frame.getContentPane(), currentFont);
        }
    }
}
