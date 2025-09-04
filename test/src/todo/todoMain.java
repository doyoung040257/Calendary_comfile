package todo;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class todoMain extends Frame{

	private final static makeList sharedList = new makeList();
	
	public Button addition;
	public Button delete;
	
	private Panel list;
	
	public todoMain() {
		
		JFrame fr = new JFrame();
		
		fr.setTitle("할 일");
		fr.setSize(500,800);
		fr.setLayout(null); // 위치 직접 설정
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		//제목
		Panel title = new Panel();
		title.setBounds(100, 25, 300, 80);
		title.setBackground(Color.LIGHT_GRAY);
		Label todo = new Label("할 일", Label.CENTER);
		title.add(todo);
		fr.add(title);
		
		//리스트
		list = new Panel();
		list.setLayout(null);
		list.setBounds(100, 150, 300, 450);
		list.setBackground(Color.LIGHT_GRAY);
		fr.add(list);
		
		
		
		//추가, 삭제
		addition = new Button("추가");
		delete = new Button("삭제");
		addition.setBounds(100, 650, 150, 80); // 위치(가로), 위치(세로), 버큰 크기(가로), 버튼 크기(높이)
		delete.setBounds(250, 650, 150, 80);
		fr.add(addition);
		fr.add(delete);
		
		renderList();
		
        addition.addActionListener(e -> {
            todo_addition addi = new todo_addition(sharedList, this::renderList);
            addi.todo_addition_page();
        });
		
		fr.setVisible(true);

	}
	
	private void renderList() {
        list.removeAll();

        int y = 10; // 버튼의 시작 y좌표
        for (int i = 0; i < sharedList.getTodolist().size(); i++) {
            todo_list t = sharedList.getTodolist().get(i);

            Button b = new Button(t.getWork()); // 라벨을 work로
            b.setBounds(10, y, 280, 40);
            // 필요하면 클릭 시 상세 보기/수정 등 리스너 추가 가능
            // b.addActionListener(ev -> System.out.println(t));

            list.add(b);
            
            final int idx = i;
            
            b.addActionListener(ev -> {
                // ★ 수정 창 오픈: sharedList, 선택된 인덱스, 저장 후 콜백
                new todo_modify(sharedList, idx, this::renderList).open();
            });
            
            y += 45; // 다음 버튼 아래로
            
        }

        // AWT 컨테이너 갱신
        list.validate();
        list.repaint();
        
	}	
}
