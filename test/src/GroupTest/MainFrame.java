package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;

    String currentUser;        // 로그인 사용자
    String myGroup;            // 현재 선택된 그룹
    Map<String, List<String>> groupMembers = new HashMap<>();
    Map<String, List<String>> schedules = new HashMap<>();
    Map<String, List<String>> groupSchedules = new HashMap<>();

    // 🔹 추가
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

        // 🔹 MainPanel 생성 후 저장
        mainPage = new MainPanel(this);
        mainPanel.add(mainPage, "Main");

        setVisible(true);
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
        for (String member : members) {
            schedules.put(member, new ArrayList<>());
        }
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

    // 🔹 여기 추가
    public MainPanel getMainPanel() {
        return mainPage;
    }
}
