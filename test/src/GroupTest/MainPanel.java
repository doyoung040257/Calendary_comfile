package GroupTest;

import javax.swing.*;

import frame.CalendarFrame01;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel {

    private DefaultListModel<String> groupListModel;
    private JList<String> groupList;  // 필드로 선언

    public MainPanel(MainFrame frame) {
        setLayout(new BorderLayout(10, 10));

        // ----------------- 상단 패널 -----------------
        JPanel topPanel = new JPanel(new BorderLayout());

        // 🔹 제목만 중앙 정렬용 패널에 추가
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel title = new JLabel("그룹 관리");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titlePanel.add(title);
        topPanel.add(titlePanel, BorderLayout.PAGE_END);

        // 🔹 "메인화면으로 돌아가기" 버튼 추가 (우측 상단)
        JButton mainBackBtn = new JButton("메인으로 돌아가기");
        mainBackBtn.setBackground(new Color(255, 102, 102)); // 빨간색으로 구분
        mainBackBtn.setForeground(Color.WHITE);
        mainBackBtn.setFocusPainted(false);
        mainBackBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        mainBackBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "메인화면으로 돌아가시겠습니까?",
                    "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SwingUtilities.getWindowAncestor(this).dispose();
                new CalendarFrame01(null).setVisible(true);
            }
        });
        topPanel.add(mainBackBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ----------------- 그룹 리스트 -----------------
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        add(new JScrollPane(groupList), BorderLayout.CENTER);

        // ----------------- 더블클릭으로 그룹 입장 -----------------
        groupList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블클릭
                    String selectedGroup = groupList.getSelectedValue();
                    if (selectedGroup != null) {
                        frame.myGroup = selectedGroup;
                        frame.switchPanel("Member_" + selectedGroup,
                                new MemberPanel(frame, selectedGroup));
                    }
                }
            }
        });

        // ----------------- 버튼 패널 -----------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 그룹 만들기
        JButton createBtn = new JButton("그룹 만들기");
        createBtn.addActionListener(e -> {
            String groupName = JOptionPane.showInputDialog(this, "그룹 이름을 입력하세요:");
            if (groupName == null || groupName.isEmpty()) return;

            int confirm = JOptionPane.showConfirmDialog(this,
                    "그룹 이름을 " + groupName + "로 하시겠습니까?",
                    "확인", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String countStr = JOptionPane.showInputDialog(this, "그룹 멤버 수를 입력하세요 (자신 포함):");
            if (countStr == null || countStr.isEmpty()) return;
            int count;
            try { count = Integer.parseInt(countStr); }
            catch (NumberFormatException ex) { return; }

            List<String> members = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String name = JOptionPane.showInputDialog(this, "멤버 " + (i + 1) + " 이름 입력:");
                if (name == null || name.isEmpty()) name = "사용자" + (i + 1);
                members.add(name);
            }

            frame.createGroup(groupName, members);
            groupListModel.addElement(groupName); // 리스트에 추가
            JOptionPane.showMessageDialog(this, "그룹이 생성되었습니다.");
        });
        buttonPanel.add(createBtn);

        // 그룹 삭제
        JButton deleteBtn = new JButton("그룹 삭제");
        deleteBtn.addActionListener(e -> {
            String selectedGroup = groupList.getSelectedValue();
            if (selectedGroup == null) {
                JOptionPane.showMessageDialog(this, "삭제할 그룹을 선택해주세요.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "그룹을 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            frame.deleteGroup(selectedGroup);
            groupListModel.removeElement(selectedGroup);
            JOptionPane.showMessageDialog(this, "그룹이 삭제되었습니다.");
        });
        buttonPanel.add(deleteBtn);

        // 그룹 들어가기
        JButton enterBtn = new JButton("그룹 들어가기");
        enterBtn.addActionListener(e -> {
            if (groupListModel.isEmpty()) { // 그룹 자체가 없는 경우
                JOptionPane.showMessageDialog(this, "그룹이 없습니다.");
                return;
            }

            String selectedGroup = groupList.getSelectedValue();
            if (selectedGroup == null) { // 그룹은 있는데 선택 안 된 경우
                JOptionPane.showMessageDialog(this, "들어갈 그룹을 선택해주세요.");
                return;
            }

            frame.myGroup = selectedGroup;
            frame.switchPanel("Member_" + selectedGroup,
                    new MemberPanel(frame, selectedGroup));
        });
        buttonPanel.add(enterBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ✅ 외부에서 그룹 제거할 수 있도록 메서드 제공
    public void removeGroup(String groupName) {
        groupListModel.removeElement(groupName);
    }
}

