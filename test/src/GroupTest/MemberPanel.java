package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MemberPanel extends JPanel {

    public MemberPanel(MainFrame frame, String groupName) {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(groupName + " - 멤버 목록", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        List<String> members = frame.groupMembers.get(groupName);

        JPanel memberListPanel = new JPanel(new GridLayout(members.size(), 1, 10, 10));
        for (String member : members) {
            JPanel memberPanel = new JPanel(new BorderLayout(5, 5));
            memberPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel nameLabel = new JLabel(member);
            nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            memberPanel.add(nameLabel, BorderLayout.CENTER);

            JButton viewBtn = new JButton("일정 보기");
            viewBtn.addActionListener(e -> frame.switchPanel(member,
                    new SchedulePanel(frame, groupName, member, false)));
            memberPanel.add(viewBtn, BorderLayout.EAST);

            memberListPanel.add(memberPanel);
        }

        add(new JScrollPane(memberListPanel), BorderLayout.CENTER);

        // ----------------- 하단 버튼 패널 -----------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 1. 그룹 나가기 버튼 (왼쪽)
        JButton leaveBtn = new JButton("그룹 나가기");
        leaveBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "그룹에서 탈퇴하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            // ✅ MainFrame에서 그룹 탈퇴 처리
            frame.leaveGroup(groupName);

            // ✅ MainPanel 리스트에서도 제거
            if (frame.getMainPanel() != null) {
                frame.getMainPanel().removeGroup(groupName);
            }

            JOptionPane.showMessageDialog(this, "탈퇴하셨습니다");
            frame.backTo("Main");
        });
        buttonPanel.add(leaveBtn);

        // 2. 이전 화면 버튼 (오른쪽)
        JButton backBtn = new JButton("이전 화면");
        backBtn.addActionListener(e -> frame.backTo("Main"));
        buttonPanel.add(backBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
