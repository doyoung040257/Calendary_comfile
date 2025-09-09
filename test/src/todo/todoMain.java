package todo;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;
import GroupTest.MainFrame;
import Settings.SettingsMenu;
import lg.User;
import frame.CalendarFrame01; // 캘린더 데이터 접근을 위해 import
import frame.DateParser;      // 날짜 파싱 유틸리티 import

public class todoMain extends JFrame{

	private final static todoListMake sharedList = new todoListMake();
	private final java.util.List<JCheckBox> rowChecks = new java.util.ArrayList<>();

    private User currentUser;
	private User user; 
	
	private boolean showCheckboxes = false;
	
	public JFrame fr;
	public JButton addition;
	public JButton delete;
	private JPanel list;
	
	public todoMain() {
		
		fr = new JFrame();
		fr.setTitle("할 일");
		fr.setSize(480,800);
		getContentPane().setBackground(Color.white);
		fr.setLayout(null);// 위치 직접 설정
		fr.setLocationRelativeTo(null);
		fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
        Font titleFont = new Font("SansSerif", Font.BOLD, 22);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

		
		//상단패널
		JPanel topPanel = createNavPanel(); //동,서,남,북,중앙 배치
		topPanel.setLayout(new BorderLayout());
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
		topPanel.setBounds(10, 10, 450, 50);
		topPanel.setBackground(Color.decode("#D8BFD8")); 
		fr.add(topPanel);
		
		JLabel todo = new JLabel("할 일 작성하기", JLabel.CENTER);
		todo.setFont(titleFont);
		topPanel.add(todo, BorderLayout.CENTER);
		
		JButton settingsViewButton = createNavButton("설정",buttonFont);
		topPanel.add(settingsViewButton, BorderLayout.EAST);
        settingsViewButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "설정 화면으로 이동합니다.");
            new SettingsMenu(this.user).setVisible(true);
            dispose();
        });
		
        //할 일 리스트
        JScrollPane scrollPane = listScrollBox(); // listScrollBox() 사용
        scrollPane.setBounds(10, 70, 450, 570);
        fr.add(scrollPane);
        
        list = createNavPanel();
        list.setLayout(null);
        list.setBackground(Color.LIGHT_GRAY);
        scrollPane.setViewportView(list);
        
        //추가,삭제 버튼 및 패널
        JPanel bottomPanel1 = new JPanel(new FlowLayout());
        bottomPanel1.setBounds(10, 640, 450, 50);
        fr.add(bottomPanel1);
        JButton addition = createNavButton("추가", buttonFont);
        JButton delete = createNavButton("제거", buttonFont);
        bottomPanel1.add(addition);
        bottomPanel1.add(delete);
        
		renderList();
		
		//추가기능
        addition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
	            todoAddition addi = new todoAddition(sharedList, todoMain.this::renderList);
	            addi.todo_addition_page();	
			}
		});
        
        //삭제기능
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showCheckboxes) {
                    showCheckboxes = true;
                    renderList();
                    return;
                }
                boolean any = false;
                for (int i = rowChecks.size() - 1; i >= 0; i--) {
                    if (rowChecks.get(i).isSelected()) {
                    	
                    	//캘린더 데이터 동기화
                        todoList itemToDelete = sharedList.getTodolist().get(i); // 삭제 전 항목 가져오기
                        removeFromCalendarTasks(itemToDelete); // 캘린더 데이터에서 삭제
                        
                        sharedList.getTodolist().remove(i); // 기존 목록에서 삭제
                        any = true;
                    }
                }
                if (!any) {
                    JOptionPane.showMessageDialog(fr, "삭제할 항목을 선택하세요.");
                } else {
                	showCheckboxes = false;
                	renderList();
                }
            }
        });
        
        JPanel bottomPanel = createNavPanel();
        bottomPanel.setLayout(new GridLayout(1, 3, 10, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        bottomPanel.setBounds(10, 690, 450, 60);
	    bottomPanel.setBackground(Color.decode("#D8BFD8"));
	    fr.add(bottomPanel);
	
		JButton homeButton = createNavButton("홈", buttonFont);
		JButton todoButton = createNavButton("할일", buttonFont);
		JButton groupButton = createNavButton("그룹", buttonFont);
		bottomPanel.add(homeButton);
		bottomPanel.add(todoButton);
		bottomPanel.add(groupButton);
	    
	    homeButton.addActionListener(e -> {
	          // 현재 화면이 이미 홈이므로 메시지를 표시
	    	JOptionPane.showMessageDialog(this, "이미 홈 화면입니다.");
	    });
	
	    todoButton.addActionListener(e -> {
	          
	    	JOptionPane.showMessageDialog(this, "할일 화면으로 이동합니다.");
	      	todoMain todomain = new todoMain();
	      	dispose();
	    });

        groupButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "그룹 관리 화면으로 이동합니다.");
            SwingUtilities.invokeLater(() -> new MainFrame("사용자")); // 기본 사용자명 전달
			dispose();
        });

		fr.setVisible(true);
	}

//sharedList 개수만큼 버튼 채우기(list 패널 내부에 생성)
	private void renderList() {
        list.removeAll();
        
        for (JCheckBox c : rowChecks) {
            fr.getContentPane().remove(c);
        }
        rowChecks.clear();

        int y = 10; // 버튼의 시작 y좌표
        for (int i = 0; i < sharedList.getTodolist().size(); i++) {
            todoList t = sharedList.getTodolist().get(i);

            JButton b = new JButton(t.getWork());
            b.setBounds(10, y, 405, 40); //10, 70, 450, 570

            JCheckBox cb = new JCheckBox();
    		cb.setBounds(421, y+5, 30, 30);
    		cb.setOpaque(false);
    		cb.setVisible(showCheckboxes);
    		rowChecks.add(cb);
    		 
    		list.add(cb);
            list.add(b);
            
            final int idx = i;
            
            b.addActionListener(ev -> {
                new todoModify(sharedList, idx, this::renderList).open();
            });
            
            y += 45; // 다음 버튼 아래로
        }
        list.setPreferredSize(new java.awt.Dimension(280, y));
        list.validate();
        list.repaint();
        fr.getContentPane().revalidate();
        fr.getContentPane().repaint();    
	}

	private JScrollPane listScrollBox() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
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
    
    //캘린더 데이터 삭제 헬퍼 메서드
    private void removeFromCalendarTasks(todoList itemToDelete) {
        if (itemToDelete == null) return;
        
        LocalDate todoDate = DateParser.parseDate(itemToDelete.getDay());
        if (todoDate != null) {
            List<CalendarFrame01.TodoEntry> tasksForDay = CalendarFrame01.dailyTasks.get(todoDate);
            if (tasksForDay != null) {
                // 할일 제목(work)이 같은 항목을 캘린더 목록에서 찾아 삭제
                tasksForDay.removeIf(entry -> entry.title.equals(itemToDelete.getWork()));
            }
        }
    }
    
    public static void main(String[] args) {
		new todoMain();
		
	}
}
