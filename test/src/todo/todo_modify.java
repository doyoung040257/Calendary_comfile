package todo;

import java.awt.Color;
import javax.swing.*;


public class todo_modify {

    //
    private final makeList list;   // 공유 리스트
    private final int index;       // 수정할 리스트의 인덱스
    private final Runnable afterSave; // 저장 후 호출(리스트 리렌더)
    //Runnable 사용이유 - 저장 이후 화면 갱신을 위해
    //메서드 레퍼런스 사용 가능
    
    private String todoWork;
    private String todoDay;
    private Integer todoTime;
    private String todoNote;

    public todo_modify(makeList list, int index, Runnable afterSave) {
        this.list = list;
        this.index = index;
        this.afterSave = afterSave;
    }

    public void open() {
        //수정 모델 가져오기
        todo_list item = list.getTodolist().get(index);

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

        // 할 일
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(50, 150, 100, 30);
        fr.add(todoTitle);

        JTextArea txt = new JTextArea();
        txt.setBounds(150, 150, 200, 30);
        txt.setText(item.getWork()); // 초기값
        fr.add(txt);

        // 날짜
        JLabel daySelect = new JLabel("날짜", JLabel.CENTER);
        daySelect.setBounds(50, 200, 100, 30);
        daySelect.setBackground(Color.gray);
        fr.add(daySelect);

        String[] day = item.getDay().split(" "); // 0월 / 0일 분리
        JComboBox<String> month = new JComboBox<String>();
        for (int i = 1; i <= 12; i++) {
            month.addItem(i + "월"); 
        }
        month.setSelectedItem(day[0]); // 기존 월 선택
        month.setBounds(150, 200, 100, 30);
        fr.add(month);


        JComboBox<String> date = new JComboBox<String>();
        for (int i = 1; i <= 31; i++) {
            date.addItem(i + "일");
        }
        date.setSelectedItem(day[1]); // 기존 일 선택
        date.setBounds(250, 200, 100, 30);
        fr.add(date);


        // 시간
        JLabel timeSelect = new JLabel("시간", JLabel.CENTER);
        timeSelect.setBounds(50, 250, 100, 30);
        fr.add(timeSelect);

        JComboBox<String> time = new JComboBox<String>();
        for (int i = 0; i <= 23; i++) {
            time.addItem(i + "시");
        }
        time.setBounds(150, 250, 200, 30);
        fr.add(time);

        //기존 시간 선택
        int hour = item.getTime() != null ? item.getTime() : 0;
        if (hour >= 0 && hour <= 23) time.setSelectedIndex(hour);

        // 메모
        JTextArea note = new JTextArea("메모", 10, 60);
        note.setText(item.getNote() != null ? item.getNote() : "");
        note.setBounds(50, 350, 300, 160);
        fr.add(note);

        // 버튼들
        JButton saveBtn = new JButton("저장");
        saveBtn.setBounds(110, 550, 70, 50);
        fr.add(saveBtn);

        JButton cancelBtn = new JButton("취소");
        cancelBtn.setBounds(200, 550, 70, 50);
        fr.add(cancelBtn);

        // 저장 로직
        saveBtn.addActionListener(e -> {
            //할일 이름
            String workStr = txt.getText().trim();
            //날짜
            String monthStr = ((String)month.getSelectedItem()).replace("월", "");
            String dateStr  =((String)date.getSelectedItem()).replace("일", "");
            String dayStr   = monthStr + "월 " + dateStr + "일";
            //시간
            String timeStr  = ((String)time.getSelectedItem()).replace("시", "");
            Integer newHour = Integer.parseInt(timeStr);
            //메모
            String memoStr  = note.getText().trim();

            //선택 항목 업데이트
            item.setWork(workStr);
            item.setDay(dayStr);
            item.setTime(newHour);
            item.setNote(memoStr);

            // 콜백으로 리스트 리렌더
            if (afterSave != null) afterSave.run();

            fr.dispose();
        });
        //취소 버튼은 창만 닫음
        cancelBtn.addActionListener(e -> fr.dispose());

        fr.setVisible(true);
    }
}

