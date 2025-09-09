package todo;

import java.awt.*;
import javax.swing.*;

import GroupTest.MainFrame;

public class mainFrame extends JFrame {
	
	private JPanel currentPanel;
	
	public mainFrame() {
		
		setTitle("프로그램 이름");
		setSize(480,800);
		setSize(480,800);
		getContentPane().setBackground(Color.white);
		setLayout(null);// 위치 직접 설정
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
        Font titleFont = new Font("SansSerif", Font.BOLD, 22);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);
		
		JPanel topPanel = createNavPanel(); //동,서,남,북,중앙 배치
		topPanel.setLayout(new BorderLayout());
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
		topPanel.setBounds(10, 10, 450, 50);
		topPanel.setBackground(Color.decode("#D8BFD8")); 
		add(topPanel);
		
        JPanel bottomPanel = createNavPanel();
        bottomPanel.setLayout(new GridLayout(1, 3, 10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        bottomPanel.setBounds(10, 690, 450, 60);
	    bottomPanel.setBackground(Color.decode("#D8BFD8"));
	    add(bottomPanel);
	
		JButton homeButton = createNavButton("홈", buttonFont);
		JButton todoButton = createNavButton("할일", buttonFont);
		JButton groupButton = createNavButton("그룹", buttonFont);
		bottomPanel.add(homeButton);
		bottomPanel.add(todoButton);
		bottomPanel.add(groupButton);
	    
	    homeButton.addActionListener(e -> {
	          // 현재 화면이 이미 홈이므로 메시지를 표시
	    	JOptionPane.showMessageDialog(this, "홈 화면으로 이동합니다.");
	    });
	
	    todoButton.addActionListener(e -> {
	    	JOptionPane.showMessageDialog(this, "이미 할 일 화면입니다.");
	    });

        groupButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "그룹 관리 화면으로 이동합니다.");
        });

		setVisible(true);
		
	}
	
	// 스크롤 패널 사용 시 사용
//	private JScrollPane listScrollBox() {
//		JScrollPane scrollPane = new JScrollPane();
//		scrollPane.setBorder(null);
//		// 스크롤바를 완전히 숨김
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
//		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		// 마우스 휠 스크롤만 가능
//		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
//		return scrollPane;
//	}
	
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
    
    public static void main(String[] args) {
		new mainFrame();
	}
}
