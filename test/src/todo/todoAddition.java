package todo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import frame.CalendarFrame01; // 캘린더 데이터 접근을 위해 import
import frame.DateParser;      // 날짜 파싱 유틸리티 import

public class todoAddition extends JFrame {

	private final todoListMake list;
	private final Runnable afterSave;

    public todoAddition(todoListMake list, Runnable afterSave) {
    	this.list = list;
    	this.afterSave = afterSave;
    }

    public void todo_addition_page() {
        setTitle("할 일 추가");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        
        JPanel title = new JPanel();
        title.setBounds(50, 25, 300, 80);
        title.setBackground(Color.LIGHT_GRAY);
        JLabel todo = new JLabel("할 일 추가하기",JLabel.CENTER);
        add(title);
        title.add(todo);

        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(50, 150, 100, 30);
        todoTitle.setBackground(Color.gray);
        add(todoTitle);

        JTextField txt = new JTextField("할 일 입력");
        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
        txt.setBounds(150, 150, 200, 30);
        add(txt);
        
        txt.addFocusListener(new FocusListener() {
            @Override public void focusGained(FocusEvent e) { if (txt.getText().equals("할 일 입력")) txt.setText(""); }
            @Override public void focusLost(FocusEvent e) { if (txt.getText().isEmpty()) txt.setText("할 일 입력"); }
        });

        JLabel daytitle = new JLabel("날짜", JLabel.CENTER);
        daytitle.setBounds(50, 200, 100, 30);
        daytitle.setBackground(Color.gray);
        add(daytitle);
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일");
        JButton datebtn = new JButton();
        datebtn.setFocusPainted(false);
        datebtn.setBounds(150, 200, 200, 30);
        datebtn.setText(today.format(formatter));
        datebtn.addActionListener(e -> new todoCalendar((year, month, day, dayWeek) -> datebtn.setText(year + "-" + month + "-" + day + "[" + dayWeek + "]")));
        add(datebtn);

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
		timebtn.addActionListener(e -> new todoClock((hour, minute) -> timebtn.setText(hour + "시 " + minute + "분")));
		add(timebtn);

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
		            boolean isYellow = starLabels[index].getIcon().equals(ystar);
		            if (isYellow) {
		                for (int j = 0; j < starLabels.length; j++) starLabels[j].setIcon(gstar);
		            } else {
		                for (int j = 0; j < starLabels.length; j++) starLabels[j].setIcon(j <= index ? ystar : gstar);
		            }
		        }
		    });
		}

        JTextArea note = new JTextArea("메모", 10, 60);
        SwingUtilities.invokeLater(() -> getRootPane().requestFocusInWindow());
        note.setBounds(50, 350, 300, 160);
        add(note);
        
        note.addFocusListener(new FocusListener() {
            @Override public void focusGained(FocusEvent e) { if (note.getText().equals("메모")) note.setText(""); }
            @Override public void focusLost(FocusEvent e) { if (note.getText().isEmpty()) note.setText("메모"); }
        });

        JButton addition = new JButton("추가");
        addition.setFocusPainted(false);
        addition.setBounds(110, 550, 70, 50);
        add(addition);

        addition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String workStr = txt.getText().trim();
                String dayStr = datebtn.getText();
                String timeStr = timebtn.getText();
                String memoStr = note.getText().trim();
                
                int importance = 0;
                for (JLabel starLabel : starLabels) {
                    if (starLabel.getIcon() == ystar) importance++;
                }

                // 1. 기존 todoListMake에 추가
                list.addTodo(workStr, dayStr, timeStr, memoStr, importance);
                String uuid = list.getTodolist().get(list.getTodolist().size()-1).getId();
                
                // ============[추가된 부분] 캘린더 데이터 동기화=====================
//                LocalDate todoDate = DateParser.parseDate(dayStr);
//                if (todoDate != null) {
//                    List<CalendarFrame01.TodoEntry> tasksForDay =
//                        CalendarFrame01.currentUser.getDailyTasks().computeIfAbsent(todoDate, k -> new ArrayList<>());
//                    
//                    CalendarFrame01.TodoEntry newEntry = new CalendarFrame01.TodoEntry(
//                        uuid ,workStr, false, new Color(255, 255, 204)
//                    );
//                    tasksForDay.add(newEntry);
//                }
//                
//             // ✅ 3. UserDatabase에 저장 (중요!)
//                lg.User currentUser = lg.SessionManager.getCurrentUser();
//                if (currentUser != null) {
//                    currentUser.setTodolist(list);
//                    lg.UserDatabase.userDatabase.put(currentUser.getId(), currentUser);
//                    lg.UserDatabase.saveUsers();
//                }
                
                LocalDate todoDate = frame.DateParser.parseDate(dayStr);
                
                lg.User currentUser = lg.SessionManager.getCurrentUser();
                if (currentUser != null) {
                    List<CalendarFrame01.TodoEntry> tasksForDay =
                        currentUser.getDailyTasks().computeIfAbsent(todoDate, k -> new ArrayList<>());

                    CalendarFrame01.TodoEntry newEntry = new CalendarFrame01.TodoEntry(
                        uuid, workStr, false, new Color(255, 255, 204)
                    );
                    tasksForDay.add(newEntry);

                    // UserDatabase 갱신
                    currentUser.setTodolist(list);
                    lg.UserDatabase.userDatabase.put(currentUser.getId(), currentUser);
                    lg.UserDatabase.saveUsers();
                }

				// ✅ 추가
				 if (SessionManager.getCurrentUser().isNotificationsEnabled()) {
                    JOptionPane.showMessageDialog(todoAddition.this, "할 일이 추가되었습니다!");
                }
				
                if (afterSave != null) afterSave.run();
                dispose();
            }
        });
        
        JButton cancel = new JButton("닫기");
        cancel.setFocusPainted(false);
        cancel.setBounds(200, 550, 70, 50);
        add(cancel);
        
        cancel.addActionListener(e -> dispose());
        setVisible(true);
    }
}

