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
        setBackground(Color.BLACK);

        // ----------------- 상단 -----------------
        JPanel topPanel = createNavPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel,BorderLayout.NORTH);
        
        titleLabel = new JLabel(groupName + " - 멤버 목록", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        topPanel.add(titleLabel);

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
        backBtn.setBackground(BUTTON_COLOR);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(buttonSize);
        addHoverClickEffect(backBtn, BUTTON_COLOR);
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
            List<String> members = g.getMembers();
            for (String m : members) {
                JPanel row = new JPanel(new BorderLayout(10, 5));
                row.setBackground(Color.WHITE);
                row.setBorder(new LineBorder(Color.GRAY, 1, true));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                // 멤버 이름에 리더 표시
                String labelText = m;
                if (g.getLeader().equals(m)) labelText += " (리더)";
                JLabel memberLabel = new JLabel(labelText);
                memberLabel.setPreferredSize(new Dimension(150, 25));

                RoundedButton scheduleBtn = new RoundedButton("일정 보기", 20);
                styleButton(scheduleBtn, BUTTON_COLOR);
                scheduleBtn.setPreferredSize(new Dimension(120, 30));

                
//                scheduleBtn.addActionListener(e -> {
//                    User memberUser = getUserByName(m);
//                    if (memberUser == null) memberUser = currentUser;
//
//                    todoListMake memberTodo = memberUser.getTodolist();
//                    if (memberTodo == null) memberTodo = new todoListMake();
//
//                    frame.switchPanel(
//                        "Schedule_" + groupName + "_" + m,
//                        new SchedulePanel(frame, groupName, m, false, memberTodo)
//                    );
//                });
                
                scheduleBtn.addActionListener(e -> {
                    SchedulePanel sp = new SchedulePanel(frame, groupName, m, false, parentFrame);
                    parentFrame.showSchedulePanel(groupName, m, sp);
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

    // ----------------- 멤버 이름으로 User 가져오기 (실제 구현에 맞게 수정) -----------------
    private User getUserByName(String name) {
        if (currentUser.getName().equals(name)) return currentUser;
        // 다른 멤버 리스트에 따른 검색 필요 시 구현
        return null;
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
    
    public JPanel createNavPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // 안티앨리어싱 (부드럽게)
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 배경을 둥근 사각형으로 채우기
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); 
                // (x, y, w, h, arcW, arcH)
                g2.dispose();
            }
	      @Override
	      protected void paintBorder(Graphics g) {
	          Graphics2D g2 = (Graphics2D) g.create();
	          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	          g2.dispose();
	      }
	  };
	  	panel.setOpaque(false); // 네모난 기본 배경 칠하지 않도록
	  	return panel;
    }
}
