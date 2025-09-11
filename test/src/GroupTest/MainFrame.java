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
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPage = new MainPanel(this, currentUser); // 여기
        mainPanel.add(mainPage, "Main");
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
