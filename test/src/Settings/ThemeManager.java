package Settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ThemeManager {

    private static final Map<String, List<Component>> groups = new HashMap<>();

    // 그룹 등록
    public static void register(String groupName, Component component) {
        groups.computeIfAbsent(groupName, k -> new ArrayList<>()).add(component);
    }

    // 테마 적용
    public static void applyTheme() {
        // 그룹별 색 정의
        Map<String, Color[]> groupColors = new HashMap<>();
        switch (Setting.theme) {
        case "DARK":
            groupColors.put("groupA", new Color[]{Color.decode("#555555"), Color.WHITE}); //연한 검은색
            groupColors.put("groupB", new Color[]{Color.decode("#AAAAAA"), Color.WHITE}); // 연한 회색
            groupColors.put("background", new Color[]{Color.decode("#333333"), Color.WHITE}); // 다크 그레이
            break;
        case "PASTEL":
            groupColors.put("groupA", new Color[]{Color.decode("#FFF0F5"), Color.BLACK});
            groupColors.put("groupB", new Color[]{Color.decode("#FFF0F5"), Color.DARK_GRAY});
            groupColors.put("background", new Color[]{Color.decode("#FFE4E1"), Color.BLACK});
            break;
        default:
            groupColors.put("groupA", new Color[]{Color.decode("#ADD8E6"), Color.BLACK}); 
            groupColors.put("groupB", new Color[]{Color.decode("#ADD8E6"), Color.BLACK});	
            groupColors.put("background", new Color[]{Color.decode("#F0F8FF"), Color.WHITE}); 
            break;
    }
     // **배경 먼저 적용**
        applyGroup("background", groupColors.get("background")[0], groupColors.get("background")[1]);


     // 일반 그룹 적용 (배경 위에 덮기)
        for (String group : new String[]{"groupA", "groupB"}) {
            applyGroup(group, groupColors.get(group)[0], groupColors.get(group)[1]);
        }
        
    }

    private static void applyGroup(String groupName, Color bg, Color fg) {
        if (!groups.containsKey(groupName)) return;
        boolean isBackground = "background".equals(groupName);
        for (Component comp : groups.get(groupName)) {
            applyThemeRecursive(comp, bg, fg, isBackground);
        }
    }

    private static void applyThemeRecursive(Component component, Color bg, Color fg, boolean isBackground) {
        if (component == null) return;

        boolean exclude = component instanceof JComponent &&
                          Boolean.TRUE.equals(((JComponent) component).getClientProperty("excludeTheme"));

        if (!exclude) {
            if (isBackground) {
                // 강제로 배경색 적용
                component.setBackground(bg);
            } else {
                component.setBackground(bg);
                component.setForeground(fg);
            }

            if (component instanceof JPanel) {
                JComponent jc = (JComponent) component;
                if (!Boolean.TRUE.equals(jc.getClientProperty("roundPanel"))) {
                    ((JPanel) component).setOpaque(true);
                }
            }
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyThemeRecursive(child, bg, fg, isBackground);
            }
        }
    }
}
