
package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import Settings.SettingsMenu;
import lg.User;
import todo.SetFrame;
import todo.todoMain;
import frame.CalendarFrame01;

public class MainPanel extends JPanel {

    private MainFrame frame;
    private JPanel groupButtonContainer;
    private User currentUser;
    private SetFrame parentFrame;
    private boolean deleteMode = false;

    
    public MainPanel(MainFrame frame, SetFrame parentFrame, User currentUser) {
        this.frame = frame;
        this.parentFrame = parentFrame;
        this.currentUser = currentUser;
        initUI();
    }

    public MainPanel(MainFrame frame, User currentUser) {
        this.frame = frame;
        this.parentFrame = null;
        this.currentUser = currentUser;
        initUI();
    }
    
    public void initUI()  { //여기
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

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
        settingBtn.addActionListener(e -> {
            this.setVisible(false);
            new SettingsMenu(currentUser, "group", this).setVisible(true); // ★ MainPanel 전달
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

        JPanel groupButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        groupButtonPanel.setBackground(Color.WHITE);
        groupButtonPanel.setPreferredSize(new Dimension(0, 60));

        RoundedButton createBtn = new RoundedButton("그룹 만들기", 20);
        createBtn.setFont(buttonFont);
        createBtn.setBackground(new Color(180, 150, 200));
        createBtn.setForeground(Color.WHITE);
        createBtn.setPreferredSize(mainButtonSize);
        addHoverClickEffect(createBtn, new Color(180, 150, 200));

        RoundedButton deleteBtn = new RoundedButton("그룹 삭제", 20);
        deleteBtn.setFont(buttonFont);
        deleteBtn.setBackground(new Color(180, 150, 200));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setPreferredSize(mainButtonSize);
        addHoverClickEffect(deleteBtn, new Color(180, 150, 200));

        groupButtonPanel.add(createBtn);
        groupButtonPanel.add(deleteBtn);
        bottomPanel.add(groupButtonPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // ----------------- 이벤트 처리 -----------------
        createBtn.addActionListener(e -> createGroupAction());
        deleteBtn.addActionListener(e -> toggleDeleteMode());

        loadExistingGroups(); // ★ MODIFIED: 초기 로드
        setVisible(true);
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
            for (String m : memberArray) if (!m.trim().isEmpty()) members.add(m.trim());

            frame.createGroup(groupNameText, members);
            loadExistingGroups(); // ★ MODIFIED: 그룹 버튼 갱신

            // ★ MODIFIED: 멤버 패널이 이미 열려있으면 업데이트
            MemberPanel mp = frame.getCurrentMemberPanel(groupNameText);
            if(mp != null) mp.updateMemberList();

            JOptionPane.showMessageDialog(this, "그룹이 생성되었습니다.");
            break;
        }
    }

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
                }
                loadExistingGroups(); // ★ MODIFIED: 삭제 후 갱신
                JOptionPane.showMessageDialog(this, "선택한 그룹이 삭제되었습니다.");
            }
        }
    }

    private void addGroupButton(String groupName) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setBackground(new Color(200, 200, 255));

        JButton groupBtn = new JButton(groupName);
        groupBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        groupBtn.setBackground(new Color(200, 200, 255));
        groupBtn.setFocusPainted(false);
        addHoverClickEffect(groupBtn, new Color(200, 200, 255));
        groupBtn.addActionListener(e -> openMemberPanel(groupName));

        JCheckBox deleteBox = new JCheckBox();
        deleteBox.setVisible(deleteMode);
        deleteBox.setBackground(new Color(200, 200, 255));

        panel.add(groupBtn, BorderLayout.CENTER);
        panel.add(deleteBox, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        groupButtonContainer.add(panel);
        groupButtonContainer.revalidate();
        groupButtonContainer.repaint();
    }

    private void openMemberPanel(String groupName) {
        if (parentFrame != null) {
            parentFrame.showMemberPanel(groupName);  // ★ SetFrame에 직접 위임
        }
    }

    private void disposeAndOpenCalendar() {
        frame.dispose();
        new CalendarFrame01().setVisible(true);
    }

    private void addHoverClickEffect(JButton btn, Color baseColor) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(baseColor.darker()); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(baseColor); }
            @Override public void mousePressed(MouseEvent e) { btn.setBackground(baseColor.darker().darker()); }
            @Override public void mouseReleased(MouseEvent e) { btn.setBackground(baseColor); }
        });
    }

    public void loadExistingGroups() {
        groupButtonContainer.removeAll(); // ★ MODIFIED: 기존 버튼 제거

        if (currentUser == null || currentUser.getGroupList() == null) return;
        for(Group g : currentUser.getGroupList().getGroups()) addGroupButton(g.getName());

        groupButtonContainer.revalidate();
        groupButtonContainer.repaint(); // ★ MODIFIED: UI 갱신
    }

    public void removeGroup(String groupName) {
        loadExistingGroups(); // ★ MODIFIED: 버튼 갱신
    }
}
