
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

    private final Font buttonFont = new Font("맑은 고딕", Font.BOLD, 14);
    private final Dimension mainButtonSize = new Dimension(140, 40);
    private final Color highlightColor = new Color(180, 150, 200);
    
    private RoundedButton deleteBtn; // 버튼 객체 전역화 -> 수정 
    
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
   	setLayout(null);
        setBackground(Color.BLACK);

        Font titleFont = new Font("맑은 고딕", Font.BOLD, 22);
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
        
        // ----------------- 상단 -----------------
        JPanel topPanel = createNavPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBounds(10, 10, 445, 50);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel title = new JLabel("그룹 관리", JLabel.CENTER);
        title.setFont(titleFont);
        topPanel.add(title, BorderLayout.CENTER);

        // 수정
		JButton settingsViewButton = createNavButton("설정",buttonFont);
		topPanel.add(settingsViewButton, BorderLayout.EAST);
        settingsViewButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(this.currentUser).setVisible(true);
        });
        
//        addHoverClickEffect(settingsViewButton, new Color(100, 149, 237)); 나중에 추가
        add(topPanel, BorderLayout.NORTH);

        // ----------------- 그룹 버튼 컨테이너 -----------------
        JScrollPane scrollPane = listScrollBox();
        scrollPane.setBounds(10, 70, 445, 550);
        
        groupButtonContainer = createNavPanel();
        groupButtonContainer.setLayout(new BoxLayout(groupButtonContainer, BoxLayout.Y_AXIS));
        groupButtonContainer.setBackground(Color.WHITE);
        scrollPane.setViewportView(groupButtonContainer);
        
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        // ----------------- 하단 버튼 -----------------
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBounds(10, 636, 445, 50);

        JPanel groupButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        groupButtonPanel.setOpaque(false);


        RoundedButton createBtn = new RoundedButton("그룹 만들기", 20);
        createBtn.setFont(buttonFont);
		createBtn.setPreferredSize(new Dimension(140, 55));
        createBtn.setBackground(new Color(180, 150, 200));
//        createBtn.setForeground(Color.WHITE);
//        addHoverClickEffect(createBtn, new Color(180, 150, 200));

        RoundedButton deleteBtn = new RoundedButton("그룹 삭제", 20);
        deleteBtn.setFont(buttonFont);
		deleteBtn.setPreferredSize(new Dimension(140, 55));
        deleteBtn.setBackground(new Color(180, 150, 200));
//        deleteBtn.setForeground(Color.WHITE);
//        addHoverClickEffect(deleteBtn, new Color(180, 150, 200));

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
        // 그룹 이름 입력 필드
        JTextField groupNameField = new JTextField();

        // 사용자 목록 불러오기 (currentUser 제외)
        java.util.List<String> userIds = new ArrayList<>(lg.UserDatabase.userDatabase.keySet());
        userIds.remove(currentUser.getId());

        // 테이블 모델 생성 (아이디 + 체크박스)
        String[] columnNames = {"사용자 ID", "선택"};
        Object[][] data = new Object[userIds.size()][2];
        for (int i = 0; i < userIds.size(); i++) {
            data[i][0] = userIds.get(i);  // 사용자 아이디
            data[i][1] = false;           // 기본은 미선택
        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 1) ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // 체크박스만 수정 가능
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        // 다이얼로그 UI
        Object[] message = {
                "그룹 이름:", groupNameField,
                "그룹 멤버 선택:", scrollPane
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "그룹 생성",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String groupNameText = groupNameField.getText().trim();
            if (groupNameText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "그룹 이름을 입력해주세요.");
                return;
            }

            // 체크된 사용자 수집
            java.util.List<String> selectedMembers = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                Boolean checked = (Boolean) model.getValueAt(i, 1);
                if (checked != null && checked) {
                    selectedMembers.add((String) model.getValueAt(i, 0));
                }
            }

            // 1️⃣ 그룹장 포함 모든 멤버 리스트
            java.util.List<String> allMembers = new ArrayList<>(selectedMembers);
            allMembers.add(currentUser.getId()); // 그룹 생성자 포함

            // 2️⃣ 그룹 객체 한 번만 생성 (leader = currentUser)
            Group newGroup = new Group(groupNameText, currentUser.getId());

            // 그룹장 외 다른 멤버 추가
            for (String m : selectedMembers) {
                newGroup.addMember(m);
            }

            // 3️⃣ currentUser 자신의 GroupList에 추가 (null 방어)
            if (currentUser.getGroupList() == null) {
                currentUser.setGroupList(new GroupList());
            }
            currentUser.getGroupList().addGroup(newGroup);
            lg.UserDatabase.addUser(currentUser); // 저장

            // 4️⃣ 나머지 멤버들에게 동일 객체 추가 (null 방어)
            for (String memberId : selectedMembers) {
                lg.User member = lg.UserDatabase.getUser(memberId);
                if (member != null) {
                    if (member.getGroupList() == null) {
                        member.setGroupList(new GroupList());
                    }
                    member.getGroupList().addGroup(newGroup);
                    lg.UserDatabase.addUser(member);
                }
            }

            lg.UserDatabase.saveUsers(); // 전체 저장

            // 5️⃣ UI 갱신
            loadExistingGroups();
            MemberPanel mp = frame.getCurrentMemberPanel(groupNameText);
            if (mp != null) mp.updateMemberList();

            JOptionPane.showMessageDialog(this, "그룹이 생성되었습니다.");
        }
    }


    // ----------------- 그룹 삭제 -----------------
    private void toggleDeleteMode() {
        deleteMode = !deleteMode;

        // 모든 그룹 버튼에 체크박스 표시/숨김
        for (Component comp : groupButtonContainer.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;

                JCheckBox cb = null;
                for (Component c : panel.getComponents()) {
                    if (c instanceof JCheckBox) cb = (JCheckBox) c;
                }

                if (cb == null) {
                    cb = new JCheckBox();
                    cb.setVisible(deleteMode);
                    cb.setBackground(panel.getBackground());
                    panel.add(cb, BorderLayout.EAST);
                } else {
                    cb.setVisible(deleteMode);
                }
            }
        }

        groupButtonContainer.revalidate();
        groupButtonContainer.repaint();

        if (!deleteMode) {
            // 체크박스 모드에서 해제될 때 선택된 그룹 삭제
            List<String> toDelete = new ArrayList<>();
            for (Component comp : groupButtonContainer.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    JButton groupBtn = (JButton) panel.getComponent(0);
                    JCheckBox cb = null;
                    for (Component c : panel.getComponents()) {
                        if (c instanceof JCheckBox) cb = (JCheckBox) c;
                    }
                    if (cb != null && cb.isSelected()) toDelete.add(groupBtn.getText());
                }
            }

            if (!toDelete.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "선택한 그룹을 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    for (String g : toDelete) {
                        // ★ MODIFIED: 그룹명만 추출 (그룹 버튼 텍스트에 리더 표시 포함 가능)
                        String groupName = g.split(" \\(그룹장:")[0].trim();
                        frame.deleteGroup(groupName);
                    }
                    loadExistingGroups();
                    JOptionPane.showMessageDialog(this, "선택한 그룹이 삭제되었습니다.");
                }
            }
        } else {
            // 체크박스 모드로 전환
            JOptionPane.showMessageDialog(this, "삭제할 그룹 체크 후, 다시 '그룹 삭제' 버튼을 클릭하세요.");
        }
    }

    private void addGroupButton(String groupName) {
        JPanel panel = createNavPanel();
        panel.setBackground(new Color(0,0,0,0));
        panel.setLayout(new BorderLayout(5, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton groupBtn = createNavButton(groupName, new Font("맑은 고딕", Font.BOLD, 16));
//        groupBtn.setFocusPainted(false);
//        addHoverClickEffect(groupBtn, new Color(200, 200, 255));
        groupBtn.addActionListener(e -> openMemberPanel(groupName));

        JCheckBox deleteBox = new JCheckBox();
        deleteBox.setVisible(deleteMode);
        deleteBox.setOpaque(false);
        deleteBox.setContentAreaFilled(false);
        deleteBox.setBorderPainted(false);
        deleteBox.setFocusPainted(false);

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

		private JScrollPane listScrollBox() {
	    JScrollPane scrollPane = new JScrollPane() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g); // ← 기존 배경을 지우고 시작
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	            // 둥근 배경 채우기
	            g2.setColor(getBackground());
	            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

	            // 테두리
	            g2.setColor(Color.GRAY);
	            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

	            g2.dispose();
	        }
	    };

	    scrollPane.setBorder(null);
	    scrollPane.setOpaque(false);
	    scrollPane.getViewport().setOpaque(false);

	    // 스크롤바를 완전히 숨김
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	    // 마우스 휠 스크롤만 가능
	    scrollPane.getVerticalScrollBar().setUnitIncrement(20);

	    return scrollPane;
	}

        private JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text) {
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 배경 색상 (눌렸을 때 어둡게)
            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else {
                g2.setColor(getBackground());
            }
            // 둥근 사각형 배경
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
            // 버튼 텍스트 그대로 출력
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY); // 테두리 색상
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.dispose();
        }
    };
    
	    button.setFont(font);
	    button.setBackground(Color.WHITE);
	    button.setForeground(Color.BLACK);
	
	    // 기본 버튼 효과 제거
	    button.setContentAreaFilled(false);
	    button.setFocusPainted(false);
	    button.setBorderPainted(false);
	    button.setOpaque(false);
        
        return button;
    }
    
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

