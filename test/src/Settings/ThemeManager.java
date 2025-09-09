package Settings;

import java.awt.*;
import java.awt.Container;

public class ThemeManager {

	public static void applyTheme(Component component) {
		if ("DEFAULT".equals(Setting.theme)) {
			applyDefaultTheme(component);
			return;
		}

		Color bgColor = Setting.theme.equals("DARK") ? Color.DARK_GRAY : Color.WHITE;
		Color fgColor = Setting.theme.equals("DARK") ? Color.WHITE : Color.BLACK;

		applyThemeRecursive(component, bgColor, fgColor);
	}

	private static void applyThemeRecursive(Component component, Color bg, Color fg) {
		component.setBackground(bg);
		component.setForeground(fg);

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
