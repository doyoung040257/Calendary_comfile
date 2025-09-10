package GroupTest;
import GroupTest.RoundedButton;
import lg.User;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import todo.todoMain;

public class MemberPanel extends JPanel {

    private MainFrame frame;
    private String groupName;
    private MainPanel mainPanel;
    private JPanel memberPanel;
    private JLabel titleLabel;
    private User currentUser;

    private final Color BUTTON_COLOR = new Color(180, 150, 200);

    public MemberPanel(MainFrame frame, String groupName, MainPanel mainPanel, User currentUser) {
        this.frame = frame;
        this.groupName = groupName;
        this.mainPanel = mainPanel;
        this.currentUser = currentUser;
    }
    
    public MemberPanel(MainFrame frame, String groupName, MainPanel mainPanel) {
        this.frame = frame;
        this.groupName = groupName;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

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

        // 그룹 나가기 버튼
        JButton leaveBtn = new RoundedButton("그룹 나가기", 20);
        leaveBtn.setFont(buttonFont);
        leaveBtn.setBackground(BUTTON_COLOR);
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setFocusPainted(false);
        leaveBtn.setPreferredSize(buttonSize);
        addHoverClickEffect(leaveBtn, BUTTON_COLOR);
        leaveBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "그룹에서 탈퇴하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            frame.leaveGroup(groupName);
            if (frame.getMainPanel() != null) frame.getMainPanel().removeGroup(groupName);

            JOptionPane.showMessageDialog(this, "탈퇴하셨습니다");
            frame.backTo("Main");
        });
        buttonPanel.add(leaveBtn);

        // 이전 화면 버튼
        JButton backBtn = new RoundedButton("이전 화면", 20);
        backBtn.setFont(buttonFont);
        backBtn.setBackground(BUTTON_COLOR);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(buttonSize);
        addHoverClickEffect(backBtn, BUTTON_COLOR);
        backBtn.addActionListener(e -> frame.backTo("Main"));
        buttonPanel.add(backBtn);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        // ----------------- 동기화 버튼 -----------------
        JPanel syncButtonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        syncButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        syncButtonPanel.setBackground(Color.WHITE);
        syncButtonPanel.setPreferredSize(new Dimension(0, 60));

        JButton homeBtn = new RoundedButton("홈", 30);
      //검정색 테두리 추가
        ((RoundedButton) homeBtn).setRoundedBorder(Color.BLACK, 2);
        
        JButton todoBtn = new RoundedButton("할 일", 30);
        //검정색 테두리 추가
        ((RoundedButton) todoBtn).setRoundedBorder(Color.BLACK, 2);
        
        JButton groupBtn = new RoundedButton("그룹", 30);
        //검정색 테두리 추가
        ((RoundedButton) groupBtn).setRoundedBorder(Color.BLACK, 2);
        
    
        addHoverClickEffect(homeBtn, Color.WHITE);
        addHoverClickEffect(todoBtn, Color.WHITE);
        addHoverClickEffect(groupBtn, Color.WHITE);

        homeBtn.setFont(buttonFont);
        homeBtn.setBackground(Color.WHITE);
        homeBtn.setFocusPainted(false);
        homeBtn.setPreferredSize(buttonSize);
        homeBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new frame.CalendarFrame01().setVisible(true);
        });

        todoBtn.setFont(buttonFont);
        todoBtn.setBackground(Color.WHITE);
        todoBtn.setFocusPainted(false);
        todoBtn.setPreferredSize(buttonSize);
        todoBtn.addActionListener(e -> {
            this.setVisible(false);
            if (currentUser != null) {
                new todoMain(currentUser, mainPanel).setVisible(true); 
            }
        });

        groupBtn.setFont(buttonFont);
        groupBtn.setBackground(Color.WHITE);
        groupBtn.setFocusPainted(false);
        groupBtn.setPreferredSize(buttonSize);
        groupBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "현재 화면이 그룹 메인입니다."));

        syncButtonPanel.add(homeBtn);
        syncButtonPanel.add(todoBtn);
        syncButtonPanel.add(groupBtn);
        bottomPanel.add(syncButtonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateMemberList() {
        memberPanel.removeAll();
        List<String> members = frame.groupMembers.get(groupName);
        if (members != null) {
            for (String m : members) {
                JPanel row = new JPanel(new BorderLayout(10, 5));
                row.setBackground(Color.WHITE);
                row.setBorder(new LineBorder(Color.GRAY, 1, true));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                JLabel memberLabel = new JLabel(m);
                memberLabel.setPreferredSize(new Dimension(150, 25));

                JButton scheduleBtn = new RoundedButton("일정 보기", 20);
                scheduleBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                scheduleBtn.setBackground(BUTTON_COLOR);
                scheduleBtn.setForeground(Color.WHITE);
                scheduleBtn.setFocusPainted(false);
                scheduleBtn.setPreferredSize(new Dimension(120, 30));
                addHoverClickEffect(scheduleBtn, BUTTON_COLOR);
                scheduleBtn.addActionListener(e -> frame.switchPanel("Schedule_" + groupName + "_" + m,
                        new SchedulePanel(frame, groupName, m, false)));

                row.add(memberLabel, BorderLayout.WEST);
                row.add(scheduleBtn, BorderLayout.EAST);

                memberPanel.add(row);
                memberPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        memberPanel.revalidate();
        memberPanel.repaint();
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

    public String getGroupName() {
        return groupName;
    }
    
    // 둥근 버튼 스타일 메서드
    private void styleRoundedButton(JButton button, Font font, Dimension size, Color bgColor, Color borderColor) {
        button.setFont(font);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(size);
        button.setBorder(new javax.swing.border.LineBorder(borderColor, 1, true)); // ★ 둥근 테두리
        button.setContentAreaFilled(true);
    }
}



