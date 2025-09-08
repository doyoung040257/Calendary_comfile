package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import frame.CalendarFrame01;

public class MainPanel extends JPanel {

    private DefaultListModel<String> groupListModel;
    private JList<String> groupList;
    private MainFrame frame;

    public MainPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ----------------- 상단 -----------------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("그룹 관리", JLabel.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        topPanel.add(title, BorderLayout.PAGE_END);

        JButton settingBtn = new JButton("설정");
        settingBtn.setBackground(new Color(100, 149, 237));
        settingBtn.setForeground(Color.WHITE);
        settingBtn.setFocusPainted(false);
        settingBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        settingBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "설정 화면은 준비 중입니다."));
        addHoverClickEffect(settingBtn, new Color(100, 149, 237));
        topPanel.add(settingBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ----------------- 그룹 리스트 -----------------
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        JScrollPane listScroll = new JScrollPane(groupList);
        listScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(listScroll, BorderLayout.CENTER);

        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedGroup = groupList.getSelectedValue();
                    if (selectedGroup != null) {
                        frame.myGroup = selectedGroup;
                        frame.switchPanel("Member_" + selectedGroup,
                                new MemberPanel(frame, selectedGroup));
                    }
                }
            }
        });

        // ----------------- 하단 버튼 -----------------
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 14);
        Dimension mainButtonSize = new Dimension(140, 40);

        // 그룹 만들기 / 삭제
        JPanel groupButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        groupButtonPanel.setBackground(Color.WHITE);
        groupButtonPanel.setPreferredSize(new Dimension(0, 60));

        JButton createBtn = new JButton("그룹 만들기");
        createBtn.setFont(buttonFont);
        createBtn.setBackground(new Color(180, 150, 200));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        createBtn.setPreferredSize(mainButtonSize);
        addHoverClickEffect(createBtn, new Color(180, 150, 200));

        JButton deleteBtn = new JButton("그룹 삭제");
        deleteBtn.setFont(buttonFont);
        deleteBtn.setBackground(new Color(180, 150, 200));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(mainButtonSize);
        addHoverClickEffect(deleteBtn, new Color(180, 150, 200));

        groupButtonPanel.add(createBtn);
        groupButtonPanel.add(deleteBtn);
        bottomPanel.add(groupButtonPanel, BorderLayout.NORTH);

        // 동기화 버튼
        JPanel syncButtonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        syncButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        syncButtonPanel.setBackground(Color.WHITE);
        syncButtonPanel.setPreferredSize(new Dimension(0, 60));

        JButton homeBtn = new JButton("홈");
        homeBtn.setFont(buttonFont);
        homeBtn.setBackground(Color.WHITE);
        homeBtn.setFocusPainted(false);
        homeBtn.setPreferredSize(mainButtonSize);
        addHoverClickEffect(homeBtn, Color.WHITE);
        homeBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "메인화면으로 돌아가시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            SwingUtilities.getWindowAncestor(this).dispose();
            new CalendarFrame01().setVisible(true);
        });

        JButton todoBtn = new JButton("할 일");
        todoBtn.setFont(buttonFont);
        todoBtn.setBackground(Color.WHITE);
        todoBtn.setFocusPainted(false);
        todoBtn.setPreferredSize(mainButtonSize);
        addHoverClickEffect(todoBtn, Color.WHITE);
        todoBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "할 일 기능은 준비 중입니다."));

        JButton groupBtn = new JButton("그룹");
        groupBtn.setFont(buttonFont);
        groupBtn.setBackground(Color.WHITE);
        groupBtn.setFocusPainted(false);
        groupBtn.setPreferredSize(mainButtonSize);
        addHoverClickEffect(groupBtn, Color.WHITE);
        groupBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "현재 화면이 그룹 메인입니다."));

        syncButtonPanel.add(homeBtn);
        syncButtonPanel.add(todoBtn);
        syncButtonPanel.add(groupBtn);
        bottomPanel.add(syncButtonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // ----------------- 이벤트 -----------------
        createBtn.addActionListener(e -> {
            while (true) {
                JTextField groupNameField = new JTextField();
                JTextField membersField = new JTextField();
                Object[] message = {
                        "그룹 이름:", groupNameField,
                        "그룹 멤버 이름 (콤마로 구분):", membersField
                };
                int option = JOptionPane.showConfirmDialog(this, message, "그룹 생성", JOptionPane.OK_CANCEL_OPTION);
                if (option != JOptionPane.OK_OPTION) break;

                String groupNameText = groupNameField.getText().trim();
                String membersText = membersField.getText().trim();
                if (groupNameText.isEmpty() || membersText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "그룹 이름과 멤버를 모두 입력해주세요.");
                    continue;
                }

                String[] memberArray = membersText.split(",");
                List<String> members = new ArrayList<>();
                for (String m : memberArray) {
                    if (!m.trim().isEmpty()) members.add(m.trim());
                }

                frame.createGroup(groupNameText, members);
                groupListModel.addElement(groupNameText);
                JOptionPane.showMessageDialog(this, "그룹이 생성되었습니다.");
                break;
            }
        });

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
    }

    // ----------------- 버튼 호버 + 클릭 효과 -----------------
    private void addHoverClickEffect(JButton button, Color original) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(original.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(original); }
            @Override
            public void mousePressed(MouseEvent e) { button.setBackground(original.darker().darker()); }
            @Override
            public void mouseReleased(MouseEvent e) { button.setBackground(original.darker()); }
        });
    }

    public void removeGroup(String groupName) {
        groupListModel.removeElement(groupName);
    }
}
