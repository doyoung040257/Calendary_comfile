package todo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.*;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

public class todoAddition extends JFrame {

	private final todoListMake list; // 공유리스트
	private final Runnable afterSave; // 저장 후 콜백

    public todoAddition(todoListMake list, Runnable afterSave) {
    	this.list = list;
    	this.afterSave = afterSave;
    }

    public void todo_addition_page() {
        setTitle("할 일 추가");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        
        

        // 상단 제목
        JPanel title = new JPanel();
        title.setBounds(50, 25, 300, 80);
        title.setBackground(Color.LIGHT_GRAY);
        JLabel todo = new JLabel("할 일 추가하기",JLabel.CENTER);
        add(title);
        title.add(todo);

        // 할 일 - 이름
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(50, 150, 100, 30);
        todoTitle.setBackground(Color.gray);
        add(todoTitle);

        JTextField txt = new JTextField("할 일 입력");
        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
        txt.setBounds(150, 150, 200, 30);
        add(txt);
        
        //할 일 기능
        txt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals("할 일 입력")) {
                    txt.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    txt.setText("할 일 입력");
                }
            }
        });

        // 할 일 - 날짜
        JLabel daytitle = new JLabel("날짜", JLabel.CENTER);
        daytitle.setBounds(50, 200, 100, 30);
        daytitle.setBackground(Color.gray);
        add(daytitle);
        
        LocalDate today = LocalDate.now(); // 현재날짜
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일");
        JButton datebtn = new JButton();
        datebtn.setFocusPainted(false);
        datebtn.setBounds(150, 200, 200, 30);
        datebtn.setText(today.format(formatter));
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
        add(datebtn);

        // 할 일 - 시간
        JLabel timetitle = new JLabel("시간", JLabel.CENTER);
        timetitle.setBounds(50, 250, 100, 30);
        timetitle.setBackground(Color.gray);
        add(timetitle);

        LocalTime nowtime = LocalTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH시 mm분");
        
		
		JButton timebtn = new JButton();
		timebtn.setFocusPainted(false);
		timebtn.setBounds(150, 250, 200, 30);
		timebtn.setText(nowtime.format(formatter2));
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
		add(timebtn);

        // 할 일 - 중요도
		JLabel importancetitle = new JLabel("중요도", JLabel.CENTER);
        importancetitle.setBounds(50, 300, 100, 30);
        importancetitle.setBackground(Color.gray);
        add(importancetitle);
		
        
		BufferedImage img1 = todoStarMake.createStarImage(40, 40);
		BufferedImage img2 = todoStarMake2.createStarImage(40, 40);
		ImageIcon ystar = new ImageIcon(img1);
		ImageIcon gstar = new ImageIcon(img2);
		
		JLabel[] starLabels = new JLabel[3];
		
		int x = 170;
		for(int i=0; i<starLabels.length; i++) {
			starLabels[i] = new JLabel(gstar);
			starLabels[i].setBounds(x, 300, 40, 40);
			add(starLabels[i]);
			x += 60;
			
		    final int index = i;
		    starLabels[i].addMouseListener(new MouseAdapter() {
		        @Override
		        public void mouseClicked(MouseEvent e) {
		            // 현재 index 번째 별이 노란별인지 확인
		            boolean isYellow = starLabels[index].getIcon().equals(ystar);

		            if (isYellow) {
		                // 이미 노란별이면 → 전부 검은별로 초기화
		                for (int j = 0; j < starLabels.length; j++) {
		                    starLabels[j].setIcon(gstar);
		                }
		            } else {
		                // 검은별이면 → index 까지 노란별로 바꿔줌
		                for (int j = 0; j < starLabels.length; j++) {
		                    starLabels[j].setIcon(j <= index ? ystar : gstar);
		                }
		            }
		        }
		    });
		}
		
		

        // 할 일 - 메모
        JTextArea note = new JTextArea("메모", 10, 60);
        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
        note.setBounds(50, 350, 300, 160);
        add(note);
        
        // 노트 기능
        note.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (note.getText().equals("메모")) {
                	note.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (note.getText().isEmpty()) {
                	note.setText("메모");
                }
            }
        });

        JButton addition = new JButton("추가");
        addition.setFocusPainted(false);
        addition.setBounds(110, 550, 70, 50);
        add(addition);

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
                
                // 5) 중요도
                int importance = 0;
                for (int i = 0; i < starLabels.length; i++) {
                    if (starLabels[i].getIcon() == ystar) {
                        importance++;
                    }
                }
                // 리스트에 추가
                list.addTodo(workStr, dayStr, timeStr, memoStr, importance);
                if (afterSave != null) afterSave.run();
                
                dispose();
            }
        });
        
        //닫기 버튼
        JButton cancel = new JButton("닫기");
        cancel.setFocusPainted(false);
        cancel.setBounds(200, 550, 70, 50);
        add(cancel);
        
        //닫기 기능
        cancel.addActionListener(e -> dispose());
        
        setVisible(true);
    }
		
}

