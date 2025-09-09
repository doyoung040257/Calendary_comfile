package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import frame.CalendarFrame01;

public class MainFrame extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;

    String currentUser;
    String myGroup;
    Map<String, List<String>> groupMembers = new HashMap<>();
    Map<String, List<String>> schedules = new HashMap<>();
    Map<String, List<String>> groupSchedules = new HashMap<>();

    private MainPanel mainPage;

    public MainFrame(String currentUser) {
        this.currentUser = currentUser;
        setTitle("그룹 캘린더");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 직접 처리
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        mainPage = new MainPanel(this);
        mainPanel.add(mainPage, "Main");

        // ----------------- 창 닫기 버튼 처리 -----------------
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                Component visible = getVisiblePanel();
                if (visible instanceof MainPanel) {
                    // MainPanel -> CalenderFrame01
                    dispose();
                    new CalendarFrame01().setVisible(true);
                } else if (visible instanceof MemberPanel) {
                    // MemberPanel -> MainPanel
                    backTo("Main");
                } else if (visible instanceof SchedulePanel) {
                    // SchedulePanel -> MemberPanel
                    SchedulePanel sp = (SchedulePanel) visible;
                    backTo("Member_" + sp.getGroupName());
                } else {
                    backTo("Main");
                }
            }
        });

        setVisible(true);
    }

    // 현재 보이는 패널 반환
    private Component getVisiblePanel() {
        for (Component comp : mainPanel.getComponents()) {
            if (comp.isVisible()) return comp;
        }
        return null;
    }

    public void switchPanel(String name, JPanel panel) {
        mainPanel.add(panel, name);
        cardLayout.show(mainPanel, name);
    }

    public void backTo(String name) {
        cardLayout.show(mainPanel, name);
    }

    public Map<String, List<String>> getSchedules() { return schedules; }
    public Map<String, List<String>> getGroupSchedules() { return groupSchedules; }

    public void createGroup(String groupName, List<String> members) {
        groupMembers.put(groupName, members);
        groupSchedules.put(groupName, new ArrayList<>());
        for (String member : members) schedules.put(member, new ArrayList<>());
        myGroup = groupName;
    }

    public void deleteGroup(String groupName) {
        groupMembers.remove(groupName);
        groupSchedules.remove(groupName);
        myGroup = null;
    }

    public void leaveGroup(String groupName) {
        if (!groupMembers.containsKey(groupName)) return;
        groupMembers.get(groupName).remove(currentUser);
        schedules.remove(currentUser);
        if (groupMembers.get(groupName).isEmpty()) deleteGroup(groupName);
        if (groupName.equals(myGroup)) myGroup = null;
    }

    public MainPanel getMainPanel() { return mainPage; }

    public void showMainPanel() {
        if (mainPage != null) cardLayout.show(mainPanel, "Main");
    }
}
