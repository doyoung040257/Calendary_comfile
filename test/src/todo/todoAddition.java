package todo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class todoAddition extends JFrame {

	private final todoListMake list; // 공유리스트
	private final Runnable afterSave; // 저장 후 콜백

    public todoAddition(todoListMake list, Runnable afterSave) {
    	this.list = list;
    	this.afterSave = afterSave;
    }

    public void todo_addition_page() {
        JFrame fr = new JFrame();
        fr.setTitle("할 일 추가");
        fr.setSize(400, 700);
        fr.setLayout(null);

        // 상단 제목
        JPanel title = new JPanel();
        title.setBounds(50, 25, 300, 80);
        title.setBackground(Color.LIGHT_GRAY);
        JLabel todo = new JLabel("할 일 추가하기", JLabel.CENTER);
        title.add(todo);

        // 할 일 - 이름
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(50, 150, 100, 30);
        todoTitle.setBackground(Color.gray);
        fr.add(todoTitle);

        JTextField txt = new JTextField("할 일 입력");
        txt.setBounds(150, 150, 200, 30);
        fr.add(txt);

        // 할 일 - 날짜
        JLabel daytitle = new JLabel("날짜", JLabel.CENTER);
        daytitle.setBounds(50, 200, 100, 30);
        daytitle.setBackground(Color.gray);
        fr.add(daytitle);
        
        JButton datebtn = new JButton("여기에 현재 날짜 넣어야지");
        datebtn.setBounds(150, 200, 200, 30);
        datebtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new todoCalendar(new todoCalendarListener() {
					@Override
					public void onDateSelected(int year, int month, int day, String dayWeek) {
						datebtn.setText(year + "-" + month + "-" + day + "[" + dayWeek + "]");
					}
				});
				
			}
		});
        fr.add(datebtn);

        // 할 일 - 시간
        JLabel timetitle = new JLabel("시간", JLabel.CENTER);
        timetitle.setBounds(50, 250, 100, 30);
        timetitle.setBackground(Color.gray);
        fr.add(timetitle);

		
		JButton timebtn = new JButton("여기에는 현재 시간 넣어야지");
		timebtn.setBounds(150, 250, 200, 30);
		timebtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new todoClock(new todoClockListener() {
			        @Override
			        public void onTimeSelected(int hour, int minute) {
			        	timebtn.setText(hour + "시 " + minute + "분");
			        }
			    });
				
			}
        });
		fr.add(timebtn);

        // 할 일 - 중요도

        // 할 일 - 메모
        JTextArea note = new JTextArea("메모", 10, 60);
        note.setBounds(50, 350, 300, 160);
        fr.add(note);

        JButton addition = new JButton("추가");
        addition.setBounds(150, 550, 70, 50);
        fr.add(addition);

        // 저장
        addition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1) 할 일
                String workStr = txt.getText().trim();

                // 2) 날짜
                String dayStr = datebtn.getText();

                // 3) 시간 (스코프 바깥 선언)
                String timeStr = timebtn.getText();
                //Integer hour = Integer.parseInt(timeStr);

                // 4) 메모
                String memoStr = note.getText().trim();
             
                // 리스트에 추가
                list.addTodo(workStr, dayStr, timeStr, memoStr);
                JOptionPane.showMessageDialog(fr, "저장 완료!");
                if (afterSave != null) afterSave.run();
                
                fr.dispose();
            }
        });
        
        fr.setVisible(true);
    }
    

}
