package Settings;

import java.awt.*;
import javax.swing.*;

public class ThemeManager {

	public static void applyTheme(Component component) {
		Color bgColor;
		Color fgColor;

		switch (Setting.theme) {
		case "DARK":
			bgColor = Color.DARK_GRAY;
			fgColor = Color.WHITE;
			break;
		case "PASTEL":
			bgColor = new Color(255, 228, 225);
			fgColor = Color.BLACK;
			break;
		case "DEFAULT":
		default:
			bgColor = Color.decode("#f0f8ff");
			fgColor = Color.BLACK;
			break;
		}

		// 재귀적으로 테마 적용 (버튼 제외)
		applyThemeRecursive(component, bgColor, fgColor);
	}

	private static void applyThemeRecursive(Component component, Color bg, Color fg) {
	    if (component == null) return;

	    // 버튼과 제외 표시된 컴포넌트는 건너뛰기
	    if (!(component instanceof JButton) &&
	        !(component instanceof JComponent && Boolean.TRUE.equals(
	                ((JComponent) component).getClientProperty("excludeTheme")))) {
	        component.setBackground(bg);
	        component.setForeground(fg);
	    }

	    if (component instanceof Container) {
	        for (Component child : ((Container) component).getComponents()) {
	            applyThemeRecursive(child, bg, fg);
	        }
	    }
	}

	private static void applyDefaultTheme(Component component) {
		component.setBackground(null);
		component.setForeground(null);

		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				applyDefaultTheme(child);
			}
		}
	}

	public static void applyFontSettingPage(Container container, Font font) {
		applyFontRecursive(container, font);
		container.revalidate();
		container.repaint();
	}

	private static void applyFontRecursive(Component comp, Font font) {
		comp.setFont(font);
		if (comp instanceof Container) {
			for (Component child : ((Container) comp).getComponents()) {
				applyFontRecursive(child, font);
			}
		}
	}
}
