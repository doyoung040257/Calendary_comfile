package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        mainPage = new MainPanel(this);
        mainPanel.add(mainPage, "Main");

        setVisible(true);
    }

    public JFrame getFrameWindow() {
        return this;  // WindowListener용
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

