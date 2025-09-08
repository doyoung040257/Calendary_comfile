package todo;

import java.awt.Color;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.*;

public class todoMain{

	private final static todoListMake sharedList = new todoListMake();
	private final java.util.List<JCheckBox> rowChecks = new java.util.ArrayList<>();

	public JFrame fr;
	public JButton addition;
	public JButton delete;
	private JPanel list;
	private boolean showCheckboxes = false;
	
	public todoMain() {
		
		fr = new JFrame();
		fr.setTitle("할 일");
		fr.setSize(500,800);
		fr.setLayout(null);// 위치 직접 설정
		fr.setLocationRelativeTo(null);
		fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		//제목
		JPanel title = new JPanel();
		title.setBounds(100, 25, 300, 80);
		title.setBackground(Color.LIGHT_GRAY);
		Label todo = new Label("할 일", Label.CENTER);
		title.add(todo);
		fr.add(title);
		
		//리스트
		list = new JPanel();
		list.setLayout(null);
		list.setBackground(Color.LIGHT_GRAY);
		
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBounds(87, 150, 325, 450);
		scrollPane.setBorder(null);
		// 스크롤바를 완전히 숨김
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// 마우스 휠 스크롤만 가능
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		fr.add(scrollPane);
		
		//추가, 삭제
		addition = new JButton("추가");
		addition.setFocusPainted(false);
		addition.setBounds(100, 650, 150, 80); // 위치(가로), 위치(세로), 버큰 크기(가로), 버튼 크기(높이)
		delete = new JButton("삭제");
		delete.setFocusPainted(false);
		delete.setBounds(250, 650, 150, 80);
		fr.add(addition);
		fr.add(delete);
		
		renderList();
		
        addition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
	            todoAddition addi = new todoAddition(sharedList, todoMain.this::renderList);
	            addi.todo_addition_page();	
			}
		});
        
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showCheckboxes) { // 체크박스 보이게 설정
                    showCheckboxes = true;
                    renderList();
                    return;
                }
                boolean any = false;
                for (int i = rowChecks.size() - 1; i >= 0; i--) {   //
                    if (rowChecks.get(i).isSelected()) {
                        sharedList.getTodolist().remove(i);
                        any = true;
                    }
                }
                if (!any) {
                    JOptionPane.showMessageDialog(fr, "삭제할 항목을 선택하세요.");
                }else {
                	showCheckboxes = false;
                	renderList();
                }
                
            }
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
            b.setBounds(10, y, 280, 40);

            JCheckBox cb = new JCheckBox();
    		cb.setBounds(295, y+5, 30, 30);
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
}


