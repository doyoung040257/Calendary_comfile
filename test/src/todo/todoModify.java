package todo;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class todoModify extends JFrame {

    private final todoListMake list;   // 공유 리스트
    private final int index;           // 수정할 리스트의 인덱스
    private final Runnable afterSave;  // 저장 후 호출(리스트 리렌더)

    private JLabel[] starLabels = new JLabel[3]; // 중요도 별
    private int selectedImportance = 0;          // 선택된 중요도 값

    public todoModify(todoListMake list, int index, Runnable afterSave) {
        this.list = list;
        this.index = index;
        this.afterSave = afterSave;
    }

    public void open() {
        // 기존 아이템 가져오기
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
        add(todoTitle);

        JTextField txt = new JTextField(item.getWork());
        txt.setBounds(150, 150, 200, 30);
        txt.setBorder(new LineBorder(Color.BLACK, 1));
        add(txt);

        // placeholder 효과
        txt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals("할 일 입력")) txt.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) txt.setText("할 일 입력");
            }
        });

        // 날짜
        JLabel daytitle = new JLabel("날짜", JLabel.CENTER);
        daytitle.setBounds(50, 200, 100, 30);
        add(daytitle);

        JButton datebtn = new JButton(item.getDay());
        datebtn.setBounds(150, 200, 200, 30);
        datebtn.setFocusPainted(false);
        add(datebtn);
        datebtn.addActionListener(e -> {
            new todoCalendar((year, month, day, dayWeek) ->
                datebtn.setText(year + "-" + month + "-" + day + "[" + dayWeek + "]")
            );
        });

        // 시간
        JLabel timetitle = new JLabel("시간", JLabel.CENTER);
        timetitle.setBounds(50, 250, 100, 30);
        add(timetitle);

        JButton timebtn = new JButton(item.getTime());
        timebtn.setBounds(150, 250, 200, 30);
        timebtn.setFocusPainted(false);
        add(timebtn);
        timebtn.addActionListener(e -> {
            new todoClock((hour, minute) ->
                timebtn.setText(hour + "시 " + minute + "분")
            );
        });

        // 중요도
        JLabel importancetitle = new JLabel("중요도", JLabel.CENTER);
        importancetitle.setBounds(50, 300, 100, 30);
        add(importancetitle);

        // 별 아이콘
        BufferedImage img1 = todoStarMake.createStarImage(40, 40); // 노란별
        BufferedImage img2 = todoStarMake2.createStarImage(40, 40); // 회색별
        ImageIcon ystar = new ImageIcon(img1);
        ImageIcon gstar = new ImageIcon(img2);

        int x = 170;
        for (int i = 0; i < starLabels.length; i++) {
            final int starIndex = i;
            starLabels[i] = new JLabel(gstar);
            starLabels[i].setBounds(x, 300, 40, 40);
            add(starLabels[i]);
            x += 60;

            starLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedImportance = starIndex + 1;
                    updateStars(ystar, gstar);
                }
            });
        }
        updateStars(ystar, gstar); // 초기 상태

        // 메모
        JLabel noteTitle = new JLabel("메모", JLabel.CENTER);
        noteTitle.setBounds(50, 350, 100, 30);
        add(noteTitle);

        JTextArea note = new JTextArea(item.getNote() != null ? item.getNote() : "메모");
        note.setBounds(150, 350, 200, 100);
        note.setBorder(new LineBorder(Color.BLACK, 1));
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        add(note);

        // placeholder 효과
        note.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (note.getText().equals("메모")) note.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (note.getText().isEmpty()) note.setText("메모");
            }
        });

        // 저장 버튼
        JButton save = new JButton("수정");
        save.setBounds(110, 550, 70, 50);
        save.setFocusPainted(false);
        add(save);

        save.addActionListener(e -> {
            String workStr = txt.getText().trim();
            String dayStr = datebtn.getText();
            String timeStr = timebtn.getText();
            String memoStr = note.getText().trim();

            // 아이템 업데이트
            item.setWork(workStr);
            item.setDay(dayStr);
            item.setTime(timeStr);
            item.setNote(memoStr);
            item.setImportance(selectedImportance); // 중요도 반영

            if (afterSave != null) afterSave.run();
            dispose();
        });

        // 닫기 버튼
        JButton cancel = new JButton("닫기");
        cancel.setBounds(200, 550, 70, 50);
        cancel.setFocusPainted(false);
        add(cancel);
        cancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    // 별 상태 업데이트
    private void updateStars(ImageIcon ystar, ImageIcon gstar) {
        for (int i = 0; i < starLabels.length; i++) {
            starLabels[i].setIcon(i < selectedImportance ? ystar : gstar);
        }
    }
}
