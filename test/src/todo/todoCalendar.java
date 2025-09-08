package todo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;

public class todoCalendar extends JFrame {
	
	private YearMonth currentYM = YearMonth.now();
	private todoCalendarListener listener;
	
    public todoCalendar(todoCalendarListener listener) {
    	this.listener = listener;

    	setUndecorated(true); // 타이틀바 제거
    	getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // 테두리
        setSize(300, 300);
        setLocationRelativeTo(null); // 화면 정중앙 위치
        setLayout(new BorderLayout());
        
    	JPanel p_north = new JPanel(new FlowLayout(FlowLayout.CENTER));
    	p_north.setBorder(new EmptyBorder(12,0,0,0));
    	p_north.setBackground(Color.WHITE);
    	JButton bt_prev = new JButton("이전");
    	bt_prev.setFocusPainted(false);
    	JLabel lb_title = new JLabel();
		JButton bt_next = new JButton("다음");
		bt_next.setFocusPainted(false);
		
		p_north.add(bt_prev);
		p_north.add(lb_title);
		p_north.add(bt_next);

        // 버튼 이름이 들어있는 배열
        String[] labels = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        JPanel panel = new JPanel(new GridLayout(0, 7, 8, 8)); // 행 자동, 열 7, 간격 8px
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.setBackground(Color.WHITE);

        //요일 라벨 생성
        for (int i = 0; i < labels.length; i++) {
        	JLabel l = new JLabel(labels[i], SwingConstants.CENTER);
        	panel.add(l);
        }
        
        //날짜 버튼 생성
        JButton[] buttons = new JButton[labels.length*6];
        for (int i = 0; i < labels.length*6; i++) {
            buttons[i] = new JButton();
            buttons[i].setFocusPainted(false);
            buttons[i].setFont(new Font("맑은 고딕", Font.PLAIN, 10)); // 버튼 글자 크기 조정
            buttons[i].setMargin(new Insets(0, 0, 0, 0)); // 여백 제거
            final int idx = i;
            buttons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					String[] parts = lb_title.getText().split("-");
					int year = Integer.parseInt(parts[0]);
					int month = Integer.parseInt(parts[1]);
					int day = Integer.parseInt(buttons[idx].getText());
					String dayWeek = dayOfWeek(idx);
					System.out.println(dayWeek);
					if (listener != null) {
						listener.onDateSelected(year,month,day,dayWeek);
					}
					dispose();
				}
			});
            panel.add(buttons[i]);
        }
        
        //요일 날짜 추가
        add(p_north, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER); 
        setVisible(true);
        
        // 타이틀 추가(년도-월)
        lb_title.setText(currentYM.getYear() + "-" + ZeroString(currentYM.getMonthValue()));      
        
        //
        fillCalendar(buttons, lb_title); 
        
        //이전 월 이동
        bt_prev.addActionListener(e -> {   
            currentYM = currentYM.minusMonths(1);
            fillCalendar(buttons, lb_title);
        });

        //다음 월 이동
        bt_next.addActionListener(e -> {  
            currentYM = currentYM.plusMonths(1);
            fillCalendar(buttons, lb_title);
        });
    }
    
    //첫 화면 뜨는 캘린더채우기(버튼 ,타이틀)
    private void fillCalendar(JButton[] buttons, JLabel lb_title) {
        // 타이틀 갱신
        lb_title.setText(currentYM.getYear() + "-" + ZeroString(currentYM.getMonthValue()));

        // 전부 초기화
        for (JButton b : buttons) {
            b.setText("");
            b.setEnabled(false);
            b.setBackground(null);
        }

        // 시작 요일 인덱스 계산 (Sun=0으로 맞춤)
        LocalDate firstDay = currentYM.atDay(1);
        int lengthOfMonth = currentYM.lengthOfMonth();
        int startIndex = firstDay.getDayOfWeek().getValue() % 7; // SUN(7)%7=0, MON(1)%7=1 ...

        // 해당 월 날짜 채우기 (버튼 수(35) 넘어가면 자동으로 잘림 방지)
        for (int day = 1; day <= lengthOfMonth; day++) {
            int idx = startIndex + (day - 1);
            if (idx >= buttons.length) break; // 35칸 초과 방지 (현재 구조 유지)
            JButton b = buttons[idx];
            b.setText(String.valueOf(day));
            b.setEnabled(true);
        }

        // (선택) 오늘 강조
        LocalDate today = LocalDate.now();
        if (today.getYear() == currentYM.getYear() && today.getMonth() == currentYM.getMonth()) {
            int idx = startIndex + (today.getDayOfMonth() - 1);
            if (idx >= 0 && idx < buttons.length) {
                buttons[idx].setBackground(new Color(255, 235, 150));
            }
        }
    }
    
    private String dayOfWeek(int i) {
        String[] w = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        return w[i % 7];
	}
   
    // 한 자리 수이면 앞에 0 붙이기
    public static String ZeroString(int n) {
    	return (n<10) ? "0"+n : Integer.toString(n);
    }

}

