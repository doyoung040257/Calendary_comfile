package Settings;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

public class FontManager {

    public static void applyFontRecursively(Container container) {
        applyFontRecursively(container, GlobalFont.currentFont);
    }

    public static void applyFontRecursively(Container container, Font font) {
        container.setFont(font);
        for (Component comp : container.getComponents()) {
            comp.setFont(font);
            if (comp instanceof Container) {
                applyFontRecursively((Container) comp, font);
            }
        }
    }
}
