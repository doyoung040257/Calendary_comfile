package GroupTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import Settings.SettingsMenu;
import Settings.ThemeManager;
import lg.SessionManager;
import lg.User;
import todo.SetFrame;
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

	public void initUI() { // 여기
		setLayout(null);
		setBackground(new Color(240, 248, 255)); // AliceBlue

		Font titleFont = new Font("맑은 고딕", Font.BOLD, 22);
		Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);

		// ----------------- 상단 -----------------
		JPanel topPanel = createNavPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		topPanel.setBounds(10, 10, 445, 50);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// 타이틀
		JLabel title = new JLabel("그룹 관리", JLabel.CENTER);
		title.setFont(titleFont);
		topPanel.add(title, BorderLayout.CENTER);

		// 네모난 기본 배경 칠하지 않도록
		topPanel.putClientProperty("excludeTheme", Boolean.FALSE);
		topPanel.putClientProperty("roundPanel", Boolean.TRUE);
		ThemeManager.register("groupA", topPanel);
		topPanel.setOpaque(false);

		// 좌측에 빈 스페이서 추가 (설정 버튼과 같은 크기) -> 그룹 관리 타이틀 중앙으로 배치하기 위한 용도
		JButton leftSpacer = new JButton();
		leftSpacer.setOpaque(false);
		leftSpacer.setContentAreaFilled(false);
		leftSpacer.setBorderPainted(false);
		leftSpacer.setPreferredSize(new Dimension(60, 50)); // 설정 버튼 크기와 맞추기
		topPanel.add(leftSpacer, BorderLayout.WEST);

		// 설정 버튼
		JButton settingsViewButton = createNavButton("설정", buttonFont);
		topPanel.add(settingsViewButton, BorderLayout.EAST);
		settingsViewButton.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
			new SettingsMenu(this.currentUser).setVisible(true);
		});

		add(topPanel, BorderLayout.NORTH);

		// ----------------- 그룹 버튼 컨테이너 -----------------
		JScrollPane scrollPane = listScrollBox();
		scrollPane.setBounds(10, 70, 445, 550);

		groupButtonContainer = createNavPanel();
		groupButtonContainer.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		groupButtonContainer.setLayout(new BoxLayout(groupButtonContainer, BoxLayout.Y_AXIS));

		// ✅ 무조건 흰색 유지
		groupButtonContainer.setBackground(Color.WHITE);

		// 네모난 기본 배경 칠하지 않도록
		groupButtonContainer.putClientProperty("excludeTheme", Boolean.FALSE);
		groupButtonContainer.putClientProperty("roundPanel", Boolean.TRUE);

		ThemeManager.register("groupA", groupButtonContainer);
		groupButtonContainer.setOpaque(false);

		scrollPane.setViewportView(groupButtonContainer);

		scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(scrollPane, BorderLayout.CENTER);

		// ----------------- 하단 버튼 -----------------
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);
		bottomPanel.setBounds(10, 617, 445, 70);

		JPanel groupButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		groupButtonPanel.setOpaque(false);

		RoundedButton createBtn = new RoundedButton("그룹 만들기", 20);
		createBtn.setFont(buttonFont);
		createBtn.setPreferredSize(new Dimension(140, 50));
		Color buttonColor = new Color(173, 216, 230); // LightBlue
		createBtn.setBackground(buttonColor);
		createBtn.setForeground(Color.WHITE);
		addHoverClickEffect(createBtn, new Color(173, 216, 230));

		RoundedButton deleteBtn = new RoundedButton("그룹 삭제", 20);
		deleteBtn.setFont(buttonFont);
		deleteBtn.setPreferredSize(new Dimension(140, 50));
		Color buttonColor1 = new Color(173, 216, 230); // LightBlue
		deleteBtn.setBackground(buttonColor1);
		deleteBtn.setForeground(Color.WHITE);
		addHoverClickEffect(deleteBtn, new Color(173, 216, 230));

		groupButtonPanel.add(createBtn);
		groupButtonPanel.add(deleteBtn);
		bottomPanel.add(groupButtonPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.SOUTH);

		// ----------------- 이벤트 처리 -----------------
		createBtn.addActionListener(e -> createGroupAction());
		deleteBtn.addActionListener(e -> toggleDeleteMode());

		createBtn.putClientProperty("excludeTheme", Boolean.TRUE);
		deleteBtn.putClientProperty("excludeTheme", Boolean.TRUE);

		ThemeManager.register("background", this);
		ThemeManager.applyTheme();

		loadExistingGroups(); // ★ MODIFIED: 초기 로드
		setVisible(true);
	}

	// ----------------- 그룹 생성 -----------------

	private void createGroupAction() {
		// 그룹 이름 입력 필드
		JTextField groupNameField = new JTextField();

		// 사용자 목록 불러오기 (currentUser 제외)
		java.util.List<User> users = new ArrayList<>(lg.UserDatabase.userDatabase.values());
		users.remove(currentUser);

		// 테이블 모델 생성 (이름 + 체크박스)
		String[] columnNames = { "사용자 이름", "선택" };
		Object[][] data = new Object[users.size()][2];
		for (int i = 0; i < users.size(); i++) {
			data[i][0] = users.get(i).getName(); // ✅ 이름 표시
			data[i][1] = false;
		}

		javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columnNames) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return (columnIndex == 1) ? Boolean.class : String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};

		JTable table = new JTable(model);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(300, 200));

		// 다이얼로그 UI
		Object[] message = { "그룹 이름:", groupNameField, "그룹 멤버 선택:", scrollPane };

		int option = JOptionPane.showConfirmDialog(this, message, "그룹 생성", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (option == JOptionPane.OK_OPTION) {
			String groupNameText = groupNameField.getText().trim();
			if (groupNameText.isEmpty()) {
				if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
					JOptionPane.showMessageDialog(this, "그룹 이름을 입력해주세요.");
				}
				return;
			}

			// 체크된 사용자 수집
			java.util.List<String> selectedMembers = new ArrayList<>();
			for (int i = 0; i < model.getRowCount(); i++) {
				Boolean checked = (Boolean) model.getValueAt(i, 1);
				if (checked != null && checked) {
					User selectedUser = users.get(i); // ✅ 인덱스로 User 가져오기
					selectedMembers.add(selectedUser.getId()); // 내부 저장은 ID
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
			if (mp != null)
				mp.updateMemberList();

			if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
				JOptionPane.showMessageDialog(this, "그룹이 생성되었습니다.");
			}
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
					if (c instanceof JCheckBox)
						cb = (JCheckBox) c;
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
						if (c instanceof JCheckBox)
							cb = (JCheckBox) c;
					}
					if (cb != null && cb.isSelected())
						toDelete.add(groupBtn.getText());
				}
			}
			if (!toDelete.isEmpty()) {
				int confirm = JOptionPane.YES_OPTION; // 알림 꺼져 있으면 자동 YES

				if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
					confirm = JOptionPane.showConfirmDialog(this, "선택한 그룹을 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
				}

				if (confirm == JOptionPane.YES_OPTION) {
					for (String g : toDelete) {
						// ★ MODIFIED: 그룹명만 추출 (그룹 버튼 텍스트에 리더 표시 포함 가능)
						String groupName = g.split(" \\(그룹장:")[0].trim();
						frame.deleteGroup(groupName);
					}
					loadExistingGroups();

					if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
						JOptionPane.showMessageDialog(this, "선택한 그룹이 삭제되었습니다.");
					}
				}
			}
		} else {
			if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
				JOptionPane.showMessageDialog(this, "삭제할 그룹 체크 후, 다시 '그룹 삭제' 버튼을 클릭하세요.");
			}
		}
	}

	private void addGroupButton(String groupName) {
		JPanel panel = createNavPanel();

		// 그룹 박스는 테마 제외, 배경 흰색 고정
		panel.putClientProperty("excludeTheme", true);
		panel.setBackground(Color.WHITE);
		panel.setOpaque(true);

		panel.setBackground(new Color(0, 0, 0, 0));
		panel.setLayout(new BorderLayout(5, 5));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

		JButton groupBtn = createNavButton(groupName, new Font("맑은 고딕", Font.BOLD, 16));
		groupBtn.setBackground(Color.WHITE); // 항상 흰색
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
			parentFrame.showMemberPanel(groupName); // ★ SetFrame에 직접 위임
		}
	}

	private void disposeAndOpenCalendar() {
		frame.dispose();
		new CalendarFrame01().setVisible(true);
	}

	private void addHoverClickEffect(JButton btn, Color baseColor) {
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(baseColor.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(baseColor);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				btn.setBackground(baseColor.darker().darker());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				btn.setBackground(baseColor);
			}
		});
	}

	public void loadExistingGroups() {
		groupButtonContainer.removeAll(); // ★ MODIFIED: 기존 버튼 제거

		if (currentUser == null || currentUser.getGroupList() == null)
			return;
		for (Group g : currentUser.getGroupList().getGroups())
			addGroupButton(g.getName());

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
				g2.setColor(getBackground());
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
				if (getModel().isArmed()) {
					g2.setColor(getBackground().darker());
				} else {
					g2.setColor(getBackground());
				}
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				g2.dispose();
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.GRAY);
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

		// 버튼 테마 제외
		button.putClientProperty("excludeTheme", true);

		// ✅ 여기서 바로 hover 효과 추가
		addHoverClickEffect(button, button.getBackground());

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
