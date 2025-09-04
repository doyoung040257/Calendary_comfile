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

	//sharedList 개수만큼 버튼 채우기(list 패널 내부에 생성)
	private void renderList() {
        list.removeAll(); // 기존에 채워진 버튼 제거

        int y = 10; // 버튼의 시작 y좌표
        for (int i = 0; i < sharedList.getTodolist().size(); i++) {
            todo_list t = sharedList.getTodolist().get(i); // todo_list 변수 t에 sharedList의 첫번째 리스트 저장

            Button b = new Button(t.getWork()); // 버튼 b 생성, 버튼 이름은 t.getWork
            b.setBounds(10, y, 280, 40); // 버튼 위치 지정 (list 패널 내부)

            list.add(b); // 리스트에 버튼 추가
            
            final int idx = i;
            
            b.addActionListener(ev -> {
                new todo_modify(sharedList, idx, this::renderList).open(); // sharedList, 인덱스, 저장 후 콜백
            });
            
            y += 45; // y좌표 변경(버튼 위치 변경)
            
        }

        // AWT 컨테이너 갱신
        list.validate();
        list.repaint();
        
	}	
}

