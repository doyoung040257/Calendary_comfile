package todo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class todoModify extends JFrame {

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

        setTitle("할 일 수정");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setLayout(null);


        // 상단 제목
        JPanel title = new JPanel();
        title.setBounds(50, 25, 300, 80);
        title.setBackground(Color.LIGHT_GRAY);
        JLabel todo = new JLabel("할 일 수정하기", JLabel.CENTER);
        title.add(todo);
        add(title);
        
        // 할 일 - 이름
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(50, 150, 100, 30);
        add(todoTitle);

        JTextField txt = new JTextField ();
        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
        txt.setBounds(150, 150, 200, 30);
        txt.setText(item.getWork()); // 초기값
        add(txt);
        
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

        JButton datebtn = new JButton("여기에 현재 날짜 넣어야지");
        datebtn.setFocusPainted(false);
        datebtn.setBounds(150, 200, 200, 30);
        add(datebtn);
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
        add(timetitle);

		JButton timebtn = new JButton("여기에는 현재시간 넣어야지");
		timebtn.setFocusPainted(false);
		timebtn.setBounds(150, 250, 200, 30);
        add(timebtn);
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
        add(importancetitle);
        
		BufferedImage img1 = todoStarMake.createStarImage(40, 40);
		BufferedImage img2 = todoStarMake2.createStarImage(40, 40);
		ImageIcon ystar = new ImageIcon(img1);
		ImageIcon gstar = new ImageIcon(img2);
		
		JLabel[] starLabels = new JLabel[3];
		int savedImportance = item.getImportance();
		
		int x = 170;
		for(int i=0; i<starLabels.length; i++) {
			starLabels[i] = new JLabel(gstar);
			starLabels[i].setIcon(i < savedImportance ? ystar : gstar);
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

        // 메모
        JTextArea note = new JTextArea("메모", 10, 60);
        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
        note.setText(item.getNote() != null ? item.getNote() : "");
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

        // 저장버튼
        JButton save = new JButton("수정");
        save.setFocusPainted(false);
        save.setFocusPainted(false);
        save.setBounds(110, 550, 70, 50);
        add(save);

        // 저장 기능
        save.addActionListener(e -> {
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

            dispose();
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
		private BufferedImage createStarImage(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}
}

//수정을 했는데 그냥 올리면 꼬일거 같아서 여기서 주석으로 올림
/*
// todoModify.java
package todo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class todoModify extends JFrame {

    private final todoListMake list;   
    private final int index;       
    private final Runnable afterSave; 
    
    // 별표 표시를 위한 변수
    private JLabel[] starLabels = new JLabel[3];
    private int selectedImportance = 0;

    public todoModify(todoListMake list, int index, Runnable afterSave) {
        this.list = list;
        this.index = index;
        this.afterSave = afterSave;
    }

    public void open() {
        todoList item = list.getTodolist().get(index);
        selectedImportance = item.getImportance();

        setTitle("할 일 수정");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setLayout(null);

        // 상단 제목
        JPanel title = new JPanel();
        title.setBounds(50, 25, 300, 80);
        title.setBackground(Color.LIGHT_GRAY);
        JLabel todo = new JLabel("할 일 수정하기", JLabel.CENTER);
        title.add(todo);
        add(title);
        
        // 할 일 - 이름
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(50, 150, 100, 30);
        todoTitle.setBackground(Color.GRAY);
        add(todoTitle);

        JTextField txt = new JTextField(item.getWork());
        txt.setBounds(150, 150, 150, 30);
        txt.setBorder(new LineBorder(Color.BLACK, 1));
        add(txt);

        // 날짜
        JLabel dateTitle = new JLabel("날짜", JLabel.CENTER);
        dateTitle.setBounds(50, 200, 100, 30);
        dateTitle.setBackground(Color.GRAY);
        add(dateTitle);
        
        JButton datebtn = new JButton(item.getDay());
        datebtn.setBounds(150, 200, 150, 30);
        datebtn.setFocusPainted(false);
        add(datebtn);

        // 시간
        JLabel timeTitle = new JLabel("시간", JLabel.CENTER);
        timeTitle.setBounds(50, 250, 100, 30);
        timeTitle.setBackground(Color.GRAY);
        add(timeTitle);
        
        JButton timebtn = new JButton(item.getTime());
        timebtn.setBounds(150, 250, 150, 30);
        timebtn.setFocusPainted(false);
        add(timebtn);

        // 중요도
        JLabel importanceTitle = new JLabel("중요도", JLabel.CENTER);
        importanceTitle.setBounds(50, 300, 100, 30);
        add(importanceTitle);

        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        starPanel.setBounds(150, 300, 150, 30);
        starPanel.setOpaque(false);
        add(starPanel);

        // 별표 라벨 생성 및 리스너 추가
        for (int i = 0; i < starLabels.length; i++) {
            final int starIndex = i;
            starLabels[i] = new JLabel("★"); // 별표 문자 사용
            starLabels[i].setFont(new Font("Malgun Gothic", Font.BOLD, 20));
            starLabels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            starLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedImportance = starIndex + 1;
                    updateStars();
                }
            });
            starPanel.add(starLabels[i]);
        }
        updateStars(); // 초기 별표 상태 설정

        // 메모
        JLabel noteTitle = new JLabel("메모", JLabel.CENTER);
        noteTitle.setBounds(50, 350, 100, 30);
        noteTitle.setBackground(Color.GRAY);
        add(noteTitle);

        JTextArea note = new JTextArea(item.getNote());
        note.setBounds(150, 350, 150, 100);
        note.setBorder(new LineBorder(Color.BLACK, 1));
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        add(note);
        
        // 저장버튼
        JButton save = new JButton("수정");
        save.setFocusPainted(false);
        save.setBounds(110, 550, 70, 50);
        add(save);

        // 저장 기능
        save.addActionListener(e -> {
            String workStr = txt.getText().trim();
            String dayStr = datebtn.getText();
            String timeStr  = timebtn.getText();
            String memoStr  = note.getText().trim();

            item.setWork(workStr);
            item.setDay(dayStr);
            item.setTime(timeStr);
            item.setNote(memoStr);
            item.setImportance(selectedImportance); // 중요도 수정 반영

            if (afterSave != null) afterSave.run();

            dispose();
        });
         
        //닫기 버튼
        JButton cancel = new JButton("닫기");
        cancel.setFocusPainted(false);
        cancel.setBounds(200, 550, 70, 50);
        add(cancel);
        
        cancel.addActionListener(e -> dispose());
        
        setVisible(true);
    }
    
    // 별표 UI 업데이트 메소드
    private void updateStars() {
        for (int i = 0; i < starLabels.length; i++) {
            if (i < selectedImportance) {
                starLabels[i].setForeground(Color.decode("#FFD700")); // 노란색
            } else {
                starLabels[i].setForeground(Color.GRAY); // 회색
            }
        }
    }
}
*/
