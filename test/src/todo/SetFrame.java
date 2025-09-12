package todo;

import java.awt.*;
import javax.swing.*;

import GroupTest.MainFrame;
import GroupTest.MainPanel;
import GroupTest.MemberPanel;
import frame.CalendarFrame01;
import lg.User;

public class SetFrame extends JFrame {
	
	private JPanel cardPanel;
    private User currentUser;
    private CardLayout cardLayout;; 
	private MainFrame groupFrame;
	
    public SetFrame(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
    	setTitle("프로그램 이름");
		setSize(480,800);
		getContentPane().setBackground(Color.white);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
        Font titleFont = new Font("SansSerif", Font.BOLD, 22);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);
		
		todoMain panel = new todoMain(currentUser);
        
        //카드레이아웃적용패널
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        this.groupFrame = new MainFrame(currentUser);
        MainPanel groupPanel = new MainPanel(groupFrame, this, currentUser); // ★ this 전달
        
		// 카드 추가
        cardPanel.add(new CalendarFrame01(), "HOME");
        cardPanel.add(new todoMain(currentUser), "TODO");
        cardPanel.add(groupPanel, "GROUP");
        
        add(cardPanel, BorderLayout.CENTER);;
        
        cardLayout.show(cardPanel, "HOME");
        
        JPanel bottomPanel = createNavPanel();
        bottomPanel.setLayout(new GridLayout(1, 3, 10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
	    bottomPanel.setBackground(Color.decode("#D8BFD8"));
	    bottomPanel.setPreferredSize(new Dimension(0, 70));
	    add(bottomPanel, BorderLayout.SOUTH);
	
	    // ✅ 아이콘 불러오기 (resources/images 안에 넣어야 함)
        ImageIcon homeIcon = resizeIcon(new ImageIcon(getClass().getResource("/images/hh.png")), 32, 32);
        ImageIcon todoIcon = resizeIcon(new ImageIcon(getClass().getResource("/images/rr.png")), 32, 32);
        ImageIcon groupIcon = resizeIcon(new ImageIcon(getClass().getResource("/images/gg.png")), 32, 32);
	
        JButton homeButton = createNavButton(homeIcon);
        JButton todoButton = createNavButton(todoIcon);
        JButton groupButton = createNavButton(groupIcon);
		
		bottomPanel.add(homeButton);
		bottomPanel.add(todoButton);
		bottomPanel.add(groupButton);
	    
	    homeButton.addActionListener(e -> {
	    	JOptionPane.showMessageDialog(this,"");
	    	cardLayout.show(cardPanel,"HOME");
	    });
	
	    todoButton.addActionListener(e -> {
	    	JOptionPane.showMessageDialog(this, "");
	    	cardLayout.show(cardPanel,"TODO");
	    });

        groupButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "");
            cardLayout.show(cardPanel,"GROUP");
        });

		setVisible(true);
    }
    
    public void showMemberPanel(String groupName) {
        MemberPanel mp = new MemberPanel(groupFrame, groupName, groupFrame.getMainPanel(), currentUser, this);
        cardPanel.add(mp, "Member_" + groupName);
        cardLayout.show(cardPanel, "Member_" + groupName);

        // 레이아웃 갱신 강제
        cardPanel.revalidate();
        cardPanel.repaint();
    }
    
    // MemberPanel -> 이전 화면 (GROUP)
    public void showGroupPanel() {
        cardLayout.show(cardPanel, "GROUP");
    }

    // 일정 보기 -> SchedulePanel 표시
    public void showSchedulePanel(String groupName, String member, JPanel schedulePanel) {
        cardPanel.add(schedulePanel, "Schedule_" + groupName + "_" + member);
        cardLayout.show(cardPanel, "Schedule_" + groupName + "_" + member);
    }

    
	
	// 스크롤 패널 사용 시 사용
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
        // 아이콘 크기 조정
    private ImageIcon resizeIcon(ImageIcon icon, int w, int h) {
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}

