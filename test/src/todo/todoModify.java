package todo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class todoModify {

    private final todoListMake list;   // 공유 리스트
    private final int index;       // 수정할 리스트의 인덱스
    private final Runnable afterSave; // 저장 후 호출(리스트 리렌더)
    //Runnable 사용이유 - 저장 이후 화면 갱신을 위해
    //메서드 레퍼런스 사용 가능

    public todoModify(todoListMake list, int index, Runnable afterSave) {
        this.list = list;
        this.index = index;
        this.afterSave = afterSave;
    }

    public void open() {
        //list 모델 가져오기
        todoList item = list.getTodolist().get(index);

        JFrame fr = new JFrame();
        fr.setTitle("할 일 수정");
        fr.setSize(400, 700);
        fr.setLayout(null);

        // 상단 제목
        JPanel title = new JPanel();
        title.setBounds(50, 25, 300, 80);
        title.setBackground(Color.LIGHT_GRAY);
        JLabel todo = new JLabel("할 일 수정하기", JLabel.CENTER);
        title.add(todo);
        fr.add(title);

        // 할 일 - 이름
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(50, 150, 100, 30);
        fr.add(todoTitle);

        JTextField txt = new JTextField ();
        txt.setBounds(150, 150, 200, 30);
        txt.setText(item.getWork()); // 초기값
        fr.add(txt);

        // 할 일 - 날짜
        JLabel daytitle = new JLabel("날짜", JLabel.CENTER);
        daytitle.setBounds(50, 200, 100, 30);
        daytitle.setBackground(Color.gray);
        fr.add(daytitle);

        JButton datebtn = new JButton("여기에 현재 날짜 넣어야지");
        datebtn.setBounds(150, 200, 200, 30);
        fr.add(datebtn);
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
        datebtn.setText(item.getDay());


        // 시간
        JLabel timetitle = new JLabel("시간", JLabel.CENTER);
        timetitle.setBounds(50, 250, 100, 30);
        timetitle.setBackground(Color.gray);
        fr.add(timetitle);

		JButton timebtn = new JButton("여기에는 현재시간 넣어야지");
		timebtn.setBounds(150, 250, 200, 30);
        fr.add(timebtn);
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
        //기존 시간 선택
        timebtn.setText(item.getTime());

		//할 일 - 중요도
		JLabel importancetitle = new JLabel("중요도", JLabel.CENTER);
        importancetitle.setBounds(50, 300, 100, 30);
        importancetitle.setBackground(Color.gray);
        fr.add(importancetitle);
        
		BufferedImage img1 = todoStarMake.createStarImage(40, 40);
		BufferedImage img2 = todoStarMake2.createStarImage(40, 40);
		ImageIcon ystar = new ImageIcon(img1);
		ImageIcon gstar = new ImageIcon(img2);
		
		JLabel[] starLabels = new JLabel[3];
		
		int x = 170;
		for(int i=0; i<starLabels.length; i++) {
			starLabels[i] = new JLabel(gstar);
			starLabels[i].setBounds(x, 300, 40, 40);
			fr.add(starLabels[i]);
			x += 60;
		}

        // 메모
        JTextArea note = new JTextArea("메모", 10, 60);
        note.setText(item.getNote() != null ? item.getNote() : "");
        note.setBounds(50, 350, 300, 160);
        fr.add(note);

        // 저장버튼
        JButton saveBtn = new JButton("저장");
        saveBtn.setBounds(110, 550, 70, 50);
        fr.add(saveBtn);

        // 저장 기능
        saveBtn.addActionListener(e -> {
            //할일 이름
            String workStr = txt.getText().trim();
            //날짜
            String dayStr = datebtn.getText();
            //시간
            String timeStr  = timebtn.getText();
            //메모
            String memoStr  = note.getText().trim();

            //선택 항목 업데이트
            item.setWork(workStr);
            item.setDay(dayStr);
            item.setTime(timeStr);
            item.setNote(memoStr);

            // 콜백으로 리스트 리렌더
            if (afterSave != null) afterSave.run();

            fr.dispose();
        });
         
        //취소버튼
        JButton cancelBtn = new JButton("취소");
        cancelBtn.setBounds(200, 550, 70, 50);
        fr.add(cancelBtn);
        
        //취소 기능
        cancelBtn.addActionListener(e -> fr.dispose());

        fr.setVisible(true);
    }
		private BufferedImage createStarImage(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}
}



