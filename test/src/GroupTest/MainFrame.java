
package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import frame.CalendarFrame01;
import lg.User;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private String currentUserName;
    private String myGroup;
    Map<String, List<String>> groupMembers = new HashMap<>(); // 깃허브에 올려져 있던 내용이라 일단 추가
    private Map<String, List<String>> schedules = new HashMap<>();
    private Map<String, List<String>> groupSchedules = new HashMap<>();

    private MainPanel mainPage;
    private User currentUser;

    // 열린 MemberPanel 관리
    private Map<String, MemberPanel> memberPanels = new HashMap<>();

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;
        this.currentUserName = currentUser.getId();

        setTitle("MainFrame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        setContentPane(mainPanel);

        // 로그인 후 바로 MainPanel만 추가
        mainPage = new MainPanel(this, currentUser);
        mainPanel.add(mainPage, "Main");

        // MainPanel 표시
        cardLayout.show(mainPanel, "Main");

        // 종료 시 동작
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                Component visible = getVisiblePanel();
                if (visible instanceof MainPanel) {
                    dispose();
                    new CalendarFrame01().setVisible(true);
                } else if (visible instanceof MemberPanel) {
                    backTo("Main");
                } else if (visible instanceof SchedulePanel) {
                    SchedulePanel sp = (SchedulePanel) visible;
                    backTo("Member_" + sp.getGroupName());
                } else {
                    backTo("Main");
                }
            }
        });

        
    }

    private Component getVisiblePanel() {
        for (Component comp : mainPanel.getComponents()) {
            if (comp.isVisible()) return comp;
        }
        return null;
    }

    // ----------------- 패널 전환 -----------------
    public void switchPanel(String name, JPanel panel) {
        mainPanel.add(panel, name);
        cardLayout.show(mainPanel, name);

        // MemberPanel 관리
        if(panel instanceof MemberPanel) memberPanels.put(name, (MemberPanel) panel);
    }

    public void backTo(String name) {
        cardLayout.show(mainPanel, name);
    }

    public Map<String, List<String>> getSchedules() { return schedules; }
    public Map<String, List<String>> getGroupSchedules() { return groupSchedules; }

    // ----------------- 그룹 생성 -----------------
    public void createGroup(String groupName, List<String> members) {
        groupMembers.put(groupName, members);
        groupSchedules.put(groupName, new ArrayList<>());
        for (String member : members) schedules.putIfAbsent(member, new ArrayList<>());
        myGroup = groupName;

        if(currentUser.getGroupList() == null) currentUser.setGroupList(new GroupList());
        Group newGroup = new Group(groupName, currentUser.getId());
        for(String member : members) newGroup.addMember(member);
        currentUser.getGroupList().addGroup(newGroup);

        // 이미 열린 MemberPanel이 있다면 갱신
        MemberPanel mp = getCurrentMemberPanel(groupName);
        if(mp != null) mp.updateMemberList();
    }

    // ----------------- 그룹 삭제 -----------------
    public void deleteGroup(String groupName) {
        groupMembers.remove(groupName);
        groupSchedules.remove(groupName);
        myGroup = null;

        if(currentUser.getGroupList() != null) {
            Group g = currentUser.getGroupList().getGroupByName(groupName);
            if(g != null) currentUser.getGroupList().getGroups().remove(g);
        }

        // MemberPanel 제거
        removeMemberPanel(groupName);
    }

    public void leaveGroup(String groupName) {
        if (!groupMembers.containsKey(groupName)) return;
        groupMembers.get(groupName).remove(currentUserName);
        schedules.remove(currentUserName);
        if (groupMembers.get(groupName).isEmpty()) deleteGroup(groupName);
        if (groupName.equals(myGroup)) myGroup = null;

        if(currentUser.getGroupList() != null) {
            Group g = currentUser.getGroupList().getGroupByName(groupName);
            if(g != null) g.getMembers().remove(currentUserName);
        }

        MemberPanel mp = getCurrentMemberPanel(groupName);
        if(mp != null) mp.updateMemberList();
    }

    public MainPanel getMainPanel() { return mainPage; }

    public void showMainPanel() {
        if (mainPage != null) cardLayout.show(mainPanel, "Main");
    }

    public User getCurrentUser() { return currentUser; }

    // ================= MemberPanel 관리 =================
    public MemberPanel getCurrentMemberPanel(String groupName) {
        return memberPanels.get("Member_" + groupName);
    }

    public void removeMemberPanel(String groupName) {
        memberPanels.remove("Member_" + groupName);
    }
}
