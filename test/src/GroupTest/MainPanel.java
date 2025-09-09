package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import Settings.SettingsMenu;
import lg.User;
import frame.CalendarFrame01;

public class MainPanel extends JPanel {

    private MainFrame frame;
    private JPanel groupButtonContainer;
    private User currentUser;
    private boolean deleteMode = false; // 체크박스 표시 모드 여부

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
        / ★ 수정됨: MainPanel 인스턴스를 전달
        settingBtn.addActionListener(e -> {
            this.setVisible(false); // ★ MainPanel 숨기기
            new SettingsMenu(currentUser, this).setVisible(true); // ★ MainPanel 전달
        });
        addHoverClickEffect(settingBtn, new Color(100, 149, 237));
        topPanel.add(settingBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ----------------- 그룹 버튼 컨테이너 -----------------
        groupButtonContainer = new JPanel();
        groupButtonContainer.setLayout(new BoxLayout(groupButtonContainer, BoxLayout.Y_AXIS));
        groupButtonContainer.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(groupButtonContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        // ----------------- 하단 버튼 -----------------
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 14);
        Dimension mainButtonSize = new Dimension(140, 40);

        // 그룹 만들기/삭제 버튼
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

        // 동기화 버튼 (홈, 할 일, 그룹)
        JPanel syncButtonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        syncButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        syncButtonPanel.setBackground(Color.WHITE);
        syncButtonPanel.setPreferredSize(new Dimension(0, 60));

        JButton homeBtn = new JButton("홈");
        JButton todoBtn = new JButton("할 일");
        JButton groupBtn = new JButton("그룹");

        addHoverClickEffect(homeBtn, Color.WHITE);
        addHoverClickEffect(todoBtn, Color.WHITE);
        addHoverClickEffect(groupBtn, Color.WHITE);

        homeBtn.setFont(buttonFont);
        homeBtn.setBackground(Color.WHITE);
        homeBtn.setFocusPainted(false);
        homeBtn.setPreferredSize(mainButtonSize);
        homeBtn.addActionListener(e -> disposeAndOpenCalendar());

        todoBtn.setFont(buttonFont);
        todoBtn.setBackground(Color.WHITE);
        todoBtn.setFocusPainted(false);
        todoBtn.setPreferredSize(mainButtonSize);
        todoBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "할 일 기능은 준비 중입니다."));

        groupBtn.setFont(buttonFont);
        groupBtn.setBackground(Color.WHITE);
        groupBtn.setFocusPainted(false);
        groupBtn.setPreferredSize(mainButtonSize);
        groupBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "현재 화면이 그룹 메인입니다."));

        syncButtonPanel.add(homeBtn);
        syncButtonPanel.add(todoBtn);
        syncButtonPanel.add(groupBtn);
        bottomPanel.add(syncButtonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // ----------------- 이벤트 처리 -----------------
        createBtn.addActionListener(e -> createGroupAction());
        deleteBtn.addActionListener(e -> toggleDeleteMode());
    }

    // ----------------- 그룹 생성 -----------------
    private void createGroupAction() {
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
            addGroupButton(groupNameText);
            JOptionPane.showMessageDialog(this, "그룹이 생성되었습니다.");
            break;
        }
    }

    // ----------------- 그룹 삭제 모드 토글 -----------------
    private void toggleDeleteMode() {
        deleteMode = !deleteMode;
        for (Component comp : groupButtonContainer.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component c : panel.getComponents()) {
                    if (c instanceof JCheckBox) c.setVisible(deleteMode);
                }
            }
        }

        if (deleteMode) {
            JOptionPane.showMessageDialog(this, "삭제할 그룹 체크 후, 다시 '그룹 삭제' 버튼을 클릭하세요.");
        } else {
            List<String> toDelete = new ArrayList<>();
            for (Component comp : groupButtonContainer.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    JButton groupBtn = (JButton) panel.getComponent(0);
                    JCheckBox cb = (JCheckBox) panel.getComponent(1);
                    if (cb.isSelected()) toDelete.add(groupBtn.getText());
                }
            }

            if (!toDelete.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "선택한 그룹을 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                for (String g : toDelete) {
                    frame.deleteGroup(g);
                    removeGroupButton(g);
                }
                JOptionPane.showMessageDialog(this, "선택한 그룹이 삭제되었습니다.");
            }
        }
    }

    // ----------------- 그룹 버튼 생성 (수정: 리스트 형태로 전체 너비 사용) -----------------
    private void addGroupButton(String groupName) {
        // 패널을 BorderLayout으로 하여 버튼이 중앙에, 체크박스가 오른쪽에 위치하도록 함
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        // 세로 높이를 고정하고 가로는 가능한 만큼 채우도록 설정
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        // 원래 디자인과 동일한 연보라색 배경 (리스트처럼 보이게)
        panel.setBackground(new Color(200, 200, 255));

        JButton groupBtn = new JButton(groupName);
        groupBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        groupBtn.setBackground(new Color(200, 200, 255));
        groupBtn.setFocusPainted(false);
        addHoverClickEffect(groupBtn, new Color(200, 200, 255));
        groupBtn.addActionListener(e -> openMemberPanel(groupName));

        JCheckBox deleteBox = new JCheckBox();
        deleteBox.setVisible(deleteMode); // 삭제 모드일 때만 표시
        deleteBox.setBackground(new Color(200, 200, 255));

        panel.add(groupBtn, BorderLayout.CENTER);
        panel.add(deleteBox, BorderLayout.EAST);

        // 왼쪽 정렬(박스 간 간격)이 필요하면 glue/strut 추가 가능
        groupButtonContainer.add(panel);
        groupButtonContainer.revalidate();
        groupButtonContainer.repaint();
    }

    // ----------------- 그룹 버튼 제거 -----------------
    private void removeGroupButton(String groupName) {
        for (Component comp : groupButtonContainer.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                JButton btn = (JButton) panel.getComponent(0);
                if (btn.getText().equals(groupName)) {
                    groupButtonContainer.remove(panel);
                    groupButtonContainer.revalidate();
                    groupButtonContainer.repaint();
                    return;
                }
            }
        }
    }

    private void openMemberPanel(String groupName) {
        MemberPanel mp = new MemberPanel(frame, groupName);
        frame.switchPanel("Member_" + groupName, mp);
    }

    private void disposeAndOpenCalendar() {
        frame.dispose();
        new CalendarFrame01().setVisible(true);
    }

    private void addHoverClickEffect(JButton btn, Color baseColor) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(baseColor.darker()); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(baseColor); }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) { btn.setBackground(baseColor.darker().darker()); }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) { btn.setBackground(baseColor); }
        });
    }

    // ----------------- 외부에서 그룹 제거 가능 -----------------
    public void removeGroup(String groupName) {
        removeGroupButton(groupName);
    }
}


