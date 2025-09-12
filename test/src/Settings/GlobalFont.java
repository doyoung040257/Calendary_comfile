package Settings;

import java.awt.*;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;

public class GlobalFont {
    // 전역 글꼴 저장
    public static Font currentFont = null;

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
