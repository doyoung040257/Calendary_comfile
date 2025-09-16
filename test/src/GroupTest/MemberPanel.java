package GroupTest;

import GroupTest.RoundedButton;
import lg.User;
import todo.SetFrame;
import todo.todoListMake;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MemberPanel extends JPanel {

    private MainFrame frame;
    private String groupName;
    private MainPanel mainPanel;
    private JPanel memberPanel;
    private JLabel titleLabel;
    private User currentUser;
    private SetFrame parentFrame;

    private final Color BUTTON_COLOR = new Color(180, 150, 200);

    public MemberPanel(MainFrame frame, String groupName, MainPanel mainPanel, User currentUser, SetFrame parentFrame) {
        this.frame = frame;
        this.groupName = groupName;
        this.mainPanel = mainPanel;
        this.currentUser = currentUser;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 248, 255)); // AliceBlue

        // ----------------- 상단 -----------------
        titleLabel = new JLabel(groupName + " - 멤버 목록", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // ----------------- 멤버 리스트 -----------------
        memberPanel = new JPanel();
        memberPanel.setLayout(new BoxLayout(memberPanel, BoxLayout.Y_AXIS));
        memberPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(memberPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        updateMemberList();

        // ----------------- 하단 버튼 -----------------
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(140, 40);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(0, 60));

        // 이전 화면 버튼
        JButton backBtn = new RoundedButton("이전 화면", 20);
        backBtn.setFont(buttonFont);
        Color buttonColor = new Color(173, 216, 230); // LightBlue
        backBtn.setBackground(buttonColor);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(buttonSize);
        addHoverClickEffect(backBtn, buttonColor);
        backBtn.addActionListener(e -> parentFrame.showGroupPanel());
        buttonPanel.add(backBtn);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // ----------------- 멤버 목록 갱신 -----------------
    public void updateMemberList() {
        memberPanel.removeAll();

        if (currentUser.getGroupList() == null) return;
        Group g = currentUser.getGroupList().getGroupByName(groupName);
        if (g != null) {
            List<String> members = g.getMembers(); // 멤버 ID 리스트
            for (String memberId : members) {
                final String id = memberId; // 람다 캡처용

                // ID → User 조회 후 이름 가져오기
                User memberUser = lg.UserDatabase.getUser(id);
                String displayName = (memberUser != null) ? memberUser.getName() : id;

                JPanel row = new JPanel(new BorderLayout(10, 5));
                row.setBackground(Color.WHITE);
                row.setBorder(new LineBorder(Color.GRAY, 1, true));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                // 이름 + 리더 표시
                String labelText = displayName;
                if (g.getLeader() != null && g.getLeader().equals(id)) labelText += " (리더)";
                JLabel memberLabel = new JLabel(labelText);
                memberLabel.setPreferredSize(new Dimension(150, 25));

                RoundedButton scheduleBtn = new RoundedButton("일정 보기", 20);
                Color buttonColor = new Color(173, 216, 230); // LightBlue
                scheduleBtn.setBackground(buttonColor);
                scheduleBtn.setPreferredSize(new Dimension(120, 30));
                addHoverClickEffect(scheduleBtn, buttonColor);

                // 버튼 액션: 내부적으로 ID 사용
                scheduleBtn.addActionListener(e -> {
                    SchedulePanel sp = new SchedulePanel(frame, groupName, id, false, parentFrame);
                    parentFrame.showSchedulePanel(groupName, id, sp);
                });

                row.add(memberLabel, BorderLayout.WEST);
                row.add(scheduleBtn, BorderLayout.EAST);

                memberPanel.add(row);
                memberPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        memberPanel.revalidate();
        memberPanel.repaint();
    }

    // ----------------- 버튼 스타일 -----------------
    private void styleButton(RoundedButton button, Color bgColor) {
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        addHoverClickEffect(button, bgColor);
    }

    private void addHoverClickEffect(JButton button, Color original) {
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(original.darker()); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(original); }
            @Override public void mousePressed(MouseEvent e) { button.setBackground(original.darker().darker()); }
            @Override public void mouseReleased(MouseEvent e) { button.setBackground(original.darker()); }
        });
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
        titleLabel.setText(groupName + " - 멤버 목록");
        updateMemberList();
    }

    public String getGroupName() { return groupName; }
}
