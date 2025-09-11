package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import frame.CalendarFrame01;
import lg.User;

public class MainFrame extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;

    String currentUserName;
    String myGroup;
    Map<String, List<String>> groupMembers = new HashMap<>();
    Map<String, List<String>> schedules = new HashMap<>();
    Map<String, List<String>> groupSchedules = new HashMap<>();

    private MainPanel mainPage;
    private User currentUser; // ★ MODIFIED: User 객체 추가

    // ★ MODIFIED: 열린 MemberPanel 관리
    private Map<String, MemberPanel> memberPanels = new HashMap<>();

    public MainFrame(User currentUser) { // ★ MODIFIED: String -> User 객체
        this.currentUser = currentUser; // ★ MODIFIED
        this.currentUserName = currentUser.getId(); // ★ MODIFIED

        setTitle("그룹 캘린더");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
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

        setVisible(true);
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

        // ★ MODIFIED: MemberPanel 관리
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

        // ★ MODIFIED: User의 GroupList에도 반영
        if(currentUser.getGroupList() == null) currentUser.setGroupList(new GroupList());
        Group newGroup = new Group(groupName);
        for(String member : members) newGroup.addMember(member);
        currentUser.getGroupList().addGroup(newGroup);

        // ★ MODIFIED: 이미 열린 MemberPanel이 있다면 갱신
        MemberPanel mp = getCurrentMemberPanel(groupName);
        if(mp != null) mp.updateMemberList();
    }

    // ----------------- 그룹 삭제 -----------------
    public void deleteGroup(String groupName) {
        groupMembers.remove(groupName);
        groupSchedules.remove(groupName);
        myGroup = null;

        // ★ MODIFIED: User GroupList에서도 삭제
        if(currentUser.getGroupList() != null) {
            Group g = currentUser.getGroupList().getGroups().stream()
                    .filter(gr -> gr.getName().equals(groupName))
                    .findFirst().orElse(null);
            if(g != null) currentUser.getGroupList().getGroups().remove(g);
        }

        // ★ MODIFIED: MemberPanel이 열려있으면 제거
        removeMemberPanel(groupName);
    }

    public void leaveGroup(String groupName) {
        if (!groupMembers.containsKey(groupName)) return;
        groupMembers.get(groupName).remove(currentUserName);
        schedules.remove(currentUserName);
        if (groupMembers.get(groupName).isEmpty()) deleteGroup(groupName);
        if (groupName.equals(myGroup)) myGroup = null;

        // ★ MODIFIED: GroupList에서도 탈퇴
        if(currentUser.getGroupList() != null) {
            Group g = currentUser.getGroupList().getGroupByName(groupName);
            if(g != null) g.getMembers().remove(currentUserName);
        }

        // ★ MODIFIED: MemberPanel이 열려있으면 갱신
        MemberPanel mp = getCurrentMemberPanel(groupName);
        if(mp != null) mp.updateMemberList();
    }

    public MainPanel getMainPanel() { return mainPage; }

    public void showMainPanel() {
        if (mainPage != null) cardLayout.show(mainPanel, "Main");
    }

    public User getCurrentUser() { return currentUser; } // ★ MODIFIED

    // ================= MemberPanel 관리 메서드 =================
    public MemberPanel getCurrentMemberPanel(String groupName) { // ★ MODIFIED
        return memberPanels.get("Member_" + groupName);
    }

    public void removeMemberPanel(String groupName) { // ★ MODIFIED
        memberPanels.remove("Member_" + groupName);
    }
}
/*

package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import frame.CalendarFrame01;
import lg.User;

public class MainFrame extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;

    String currentUserName;
    String myGroup;
    Map<String, List<String>> groupMembers = new HashMap<>(); // 기존 유지 (보조 용)
    Map<String, List<String>> schedules = new HashMap<>();
    Map<String, List<String>> groupSchedules = new HashMap<>();

    private MainPanel mainPage;
    private User currentUser;

    private Map<String, MemberPanel> memberPanels = new HashMap<>();

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;
        this.currentUserName = currentUser.getId();

        setTitle("그룹 캘린더");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        mainPage = new MainPanel(this);
        mainPanel.add(mainPage, "Main");

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

        setVisible(true);
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

        if(panel instanceof MemberPanel) memberPanels.put(name, (MemberPanel) panel);
    }

    public void backTo(String name) {
        cardLayout.show(mainPanel, name);
    }

    public Map<String, List<String>> getSchedules() { return schedules; }
    public Map<String, List<String>> getGroupSchedules() { return groupSchedules; }

    // ----------------- 그룹 생성 -----------------
    public void createGroup(String groupName, List<String> members) {
        // ★ MODIFIED: User의 GroupList 중심으로 반영
        if(currentUser.getGroupList() == null) currentUser.setGroupList(new GroupList());
        Group newGroup = new Group(groupName);
        for(String member : members) newGroup.addMember(member);
        currentUser.getGroupList().addGroup(newGroup);

        // ★ MODIFIED: Map도 갱신 (보조용)
        groupMembers.put(groupName, new ArrayList<>(members));
        groupSchedules.put(groupName, new ArrayList<>());
        for (String member : members) schedules.putIfAbsent(member, new ArrayList<>());
        myGroup = groupName;

        // ★ MODIFIED: 이미 열린 MemberPanel 갱신
        MemberPanel mp = getCurrentMemberPanel(groupName);
        if(mp != null) mp.updateMemberList();
    }

    // ----------------- 그룹 삭제 -----------------
    public void deleteGroup(String groupName) {
        groupMembers.remove(groupName);
        groupSchedules.remove(groupName);
        myGroup = null;

        if(currentUser.getGroupList() != null) {
            Group g = currentUser.getGroupList().getGroupByName(groupName); // ★ MODIFIED
            if(g != null) currentUser.getGroupList().getGroups().remove(g);
        }

        removeMemberPanel(groupName);
    }

    public void leaveGroup(String groupName) {
        if (!groupMembers.containsKey(groupName)) return;
        groupMembers.get(groupName).remove(currentUserName);
        schedules.remove(currentUserName);
        if (groupMembers.get(groupName).isEmpty()) deleteGroup(groupName);
        if (groupName.equals(myGroup)) myGroup = null;

        if(currentUser.getGroupList() != null) {
            Group g = currentUser.getGroupList().getGroupByName(groupName); // ★ MODIFIED
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

    // ----------------- MemberPanel 관리 -----------------
    public MemberPanel getCurrentMemberPanel(String groupName) {
        return memberPanels.get("Member_" + groupName);
    }

    public void removeMemberPanel(String groupName) {
        memberPanels.remove("Member_" + groupName);
    }
}

*/
