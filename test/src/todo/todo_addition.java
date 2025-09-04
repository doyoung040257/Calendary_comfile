package todo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class todo_addition extends JFrame {

	private final makeList list; // 공유리스트
	private final Runnable afterSave; // 저장 후 콜백

    private String todoWork;
    private String todoDay;
    private Integer todoTime;
    private String todoNote;

    public todo_addition(makeList list, Runnable afterSave) {
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

        // 할 일 - 이름
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(50, 150, 100, 30);
        todoTitle.setBackground(Color.gray);

        JTextArea txt = new JTextArea("할 일 입력", 10, 30);
        txt.setBounds(150, 150, 200, 30);

        // 할 일 - 날짜
        JLabel daySelect = new JLabel("날짜", JLabel.CENTER);
        daySelect.setBounds(50, 200, 100, 30);
        daySelect.setBackground(Color.gray);

        JComboBox<String> month = new JComboBox<String>();
        for (int i = 1; i <= 12; i++) {
            month.addItem(i + "월");
        }
        month.setBounds(150, 200, 100, 30);

        JComboBox<String> date = new JComboBox<String>();
        for (int i = 1; i <= 31; i++) {
            date.addItem(i + "일");
        }
        date.setBounds(250, 200, 100, 30);

        // 할 일 - 시간
        JLabel timeSelect = new JLabel("시간", JLabel.CENTER);
        timeSelect.setBounds(50, 250, 100, 30);
        timeSelect.setBackground(Color.gray);

        JComboBox<String> time = new JComboBox<String>();
        for (int i = 0; i <= 23; i++) {
            time.addItem(i + "시");
        }
        time.setBounds(150, 250, 200, 30);

        // 할 일 - 중요도

        // 할 일 - 메모
        JTextArea note = new JTextArea("메모", 10, 60);
        note.setBounds(50, 350, 300, 160);

        JButton addition = new JButton("추가");
        addition.setBounds(150, 550, 70, 50);

        title.add(todo);
        fr.add(todoTitle);
        fr.add(title);
        fr.add(txt);
        fr.add(daySelect);
        fr.add(month);
        fr.add(date);
        fr.add(timeSelect);
        fr.add(time);
        fr.add(note);
        fr.add(addition);


        // 저장
        addition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1) 할 일
                String workStr = txt.getText().trim();
//                if (workStr.isEmpty() || workStr.equals("할 일")) {
//                    JOptionPane.showMessageDialog(fr, "할 일을 입력하세요.");
//                    txt.requestFocus();
//                    return;
//                }

                // 2) 날짜
                String monthStr = ((String) month.getSelectedItem()).replace("월", "");
                String dateStr  = ((String) date.getSelectedItem()).replace("일", "");
                String dayStr   = monthStr + "월 " + dateStr + "일";

                // 3) 시간 (스코프 바깥 선언)
                String timeStr = ((String)time.getSelectedItem()).replace("시", "");
                Integer hour = Integer.parseInt(timeStr);

                // 4) 메모
                String memoStr = note.getText().trim();

                // (A) 현재 this 객체의 필드에 저장하고 싶다면:
                setTodoWork(workStr);
                setTodoDay(dayStr);
                setTodoTime(hour);
                setTodoNote(memoStr);

//                // 출력 (this의 필드)
//                System.out.println("할 일: " + getTodoWork());
//                System.out.println("날짜: " + getTodoDay());
//                System.out.println("시간: " + getTodoTime() + "시");
//                System.out.println("메모: " + getTodoNote());
//
//                JOptionPane.showMessageDialog(fr,
//                    "저장 완료!\n"
//                  + "할 일: " + getTodoWork() + "\n"
//                  + "날짜: " + getTodoDay() + "\n"
//                  + "시간: " + getTodoTime() + "시");
                

                list.addTodo(workStr, dayStr, hour, memoStr);
                JOptionPane.showMessageDialog(fr, "저장 완료!");
                if (afterSave != null) afterSave.run();
                
                fr.dispose();
            }
        });
        
        fr.setVisible(true);
    }
    
    

    public String getTodoWork() {
        return todoWork;
    }

    public void setTodoWork(String todoWork) {
        this.todoWork = todoWork;
    }

    public String getTodoDay() {
        return todoDay;
    }

    public void setTodoDay(String todoDay) {
        this.todoDay = todoDay;
    }

    public Integer getTodoTime() {
        return todoTime;
    }

    public void setTodoTime(Integer todoTime) {
        this.todoTime = todoTime;
    }

    public String getTodoNote() {
        return todoNote;
    }

    public void setTodoNote(String todoNote) {
        this.todoNote = todoNote;
    }
}


/*
Panel → JPanel
Button → JButton
Choice → JComboBox<String>
한 줄 입력은 JTextArea보다 **JTextField**가 적합
*/