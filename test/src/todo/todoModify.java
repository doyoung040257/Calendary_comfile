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

		Font titleFont = new Font("맑은 고딕", Font.BOLD, 25);
        Font buttonFont = new Font("맑은 고딕", Font.BOLD, 14);
		
        setTitle("할 일 수정");
        setSize(350, 600);
        setLocationRelativeTo(null);
        setLayout(null);

        // 상단 제목
        JPanel title = createNavPanel();
        title.setBounds(10, 10, 313, 50);
        title.setBackground(Color.LIGHT_GRAY);
        
        JLabel todo = new JLabel("할 일 수정하기", JLabel.CENTER);
        todo.setFont(titleFont);
        title.add(todo);
        add(title);
        
        JPanel centerPanel = createNavPanel();
        centerPanel.setLayout(null);
        centerPanel.setBounds(10,70,313,420);
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel);
        
        // 할 일 - 그룹
        JPanel one = createNavPanel();
        one.setLayout(null);
        one.setBounds(10, 10, 290, 35);
        centerPanel.add(one);
        
        JLabel todoGroup = new JLabel("그룹", JLabel.CENTER);
        todoGroup.setBounds(5,5,50,25);
		todoGroup.setFont(buttonFont);
        one.add(todoGroup);
        
        JTextField txtGroup = new JTextField(item.getWork());
        txtGroup.setBounds(60,5,220,25);
        txtGroup.setBorder(new LineBorder(Color.BLACK, 1));
        one.add(txtGroup);

        // 할 일 - 제목
        JPanel two = createNavPanel();
        two.setLayout(null);
        two.setBounds(10, 55, 290, 35);
        centerPanel.add(two);
        
        JLabel todoTitle = new JLabel("할 일", JLabel.CENTER);
        todoTitle.setBounds(5,5,50,25);
		todoTitle.setFont(buttonFont);
        two.add(todoTitle);
        
        JTextField txt = new JTextField(item.getWork());
        txt.setBounds(60,5,220,25);
        txt.setBorder(new LineBorder(Color.BLACK, 1));
        two.add(txt);

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
        
        // 할 일 - 날짜
        JPanel three = createNavPanel();
        three.setLayout(null);
        three.setBounds(10, 100, 290, 35);
        centerPanel.add(three);
        
        JLabel daytitle = new JLabel("날짜", JLabel.CENTER);
        daytitle.setBounds(5,5,50,25);
		daytitle.setFont(buttonFont);
        three.add(daytitle);

        JButton datebtn = new JButton(item.getDay());
        datebtn.setBounds(100,5,130,25);
        datebtn.setFocusPainted(false);
        three.add(datebtn);
        datebtn.addActionListener(e -> {
            new todoCalendar((year, month, day, dayWeek) ->
                datebtn.setText(year + "-" + month + "-" + day + "[" + dayWeek + "]")
            );
        });
        
        // 할 일 - 시간
        JPanel four = createNavPanel();
        four.setLayout(null);
        four.setBounds(10, 145, 290, 35);
        centerPanel.add(four);
        
        JLabel timetitle = new JLabel("시간", JLabel.CENTER);
        timetitle.setBounds(5,5,50,25);
		timetitle.setFont(buttonFont);
        four.add(timetitle);

        JButton timebtn = new JButton(item.getTime());
        timebtn.setBounds(100,5,130,25);
        timebtn.setFocusPainted(false);
        four.add(timebtn);
        timebtn.addActionListener(e -> {
            new todoClock((hour, minute) ->
                timebtn.setText(hour + "시 " + minute + "분")
            );
        });
        
        // 할 일 - 중요도
        JPanel five = createNavPanel();
        five.setLayout(null);
        five.setBounds(10, 190, 290, 35);
        centerPanel.add(five);
        
        JLabel importancetitle = new JLabel("중요도", JLabel.CENTER);
        importancetitle.setBounds(5,5,50,25);
		importancetitle.setFont(buttonFont);
        five.add(importancetitle);

        // 별 아이콘
        BufferedImage img1 = todoStarMake.createStarImage(25,25); // 노란별
        BufferedImage img2 = todoStarMake2.createStarImage(25,25); // 회색별
        ImageIcon ystar = new ImageIcon(img1);
        ImageIcon gstar = new ImageIcon(img2);

        int x = 113;
        for (int i = 0; i < starLabels.length; i++) {
            final int starIndex = i;
            starLabels[i] = new JLabel(gstar);
            starLabels[i].setBounds(x, 5, 25, 25);
            five.add(starLabels[i]);
            x += 40;

            starLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedImportance = starIndex + 1;
                    updateStars(ystar, gstar);
                }
            });
        }
        updateStars(ystar, gstar); // 초기 상태

        // 할 일 - 메모
        JPanel six = createNavPanel();
        six.setLayout(null);
        six.setBounds(10, 235, 290, 175);
        centerPanel.add(six);
        
        JLabel noteTitle = new JLabel("메모", JLabel.CENTER);
		noteTitle.setFont(buttonFont);
        noteTitle.setBounds(5,5,50,150);
        six.add(noteTitle);

        JTextArea note = new JTextArea(item.getNote() != null ? item.getNote() : "메모");
        note.setBounds(60,12,220,150);
		todoGroup.setFont(buttonFont);
        note.setBorder(new LineBorder(Color.BLACK, 1));
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        six.add(note);

        // placeholder 효과
        note.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (note.getText().equals("메모를 입력하세요")) note.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (note.getText().isEmpty()) note.setText("메모를 입력하세요");
            }
        });
        
        // 하단 패널
        JPanel bottomPanel = createNavPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.setBounds(10, 500, 313, 50);
        bottomPanel.setBackground(Color.WHITE);
        add(bottomPanel);
        
        // 저장 버튼
        JButton save = createNavButton("수정",buttonFont);
        save.setPreferredSize(new Dimension(120, 40));
//        save.setFocusPainted(false);
        bottomPanel.add(save);

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
        JButton cancel = createNavButton("닫기",buttonFont);
        cancel.setPreferredSize(new Dimension(120, 40));
//        cancel.setFocusPainted(false);
        bottomPanel.add(cancel);
        cancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    // 별 상태 업데이트
    private void updateStars(ImageIcon ystar, ImageIcon gstar) {
        for (int i = 0; i < starLabels.length; i++) {
            starLabels[i].setIcon(i < selectedImportance ? ystar : gstar);
        }
    }

        private JButton createNavButton(String string, Font font) {
        JButton button = new JButton(string) {
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 배경 색상 (눌렸을 때 어둡게)
            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else {
                g2.setColor(getBackground());
            }
            // 둥근 사각형 배경
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
            // 버튼 텍스트 그대로 출력
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY); // 테두리 색상
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.dispose();
        }
    };
    	button.setFont(font);
	    button.setBackground(Color.WHITE);
	    button.setForeground(Color.BLACK);
	
	    // 기본 버튼 효과 제거
	    button.setContentAreaFilled(false);
	    button.setFocusPainted(false);
	    button.setBorderPainted(false);
	    button.setOpaque(false);
        
        return button;
    }
    
    public JPanel createNavPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // 안티앨리어싱 (부드럽게)
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 배경을 둥근 사각형으로 채우기
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); 
                // (x, y, w, h, arcW, arcH)
                g2.dispose();
            }
	      @Override
	      protected void paintBorder(Graphics g) {
	          Graphics2D g2 = (Graphics2D) g.create();
	          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	          g2.dispose();
	      }
	  };
	  	panel.setOpaque(false); // 네모난 기본 배경 칠하지 않도록
	  	return panel;
    }
}







