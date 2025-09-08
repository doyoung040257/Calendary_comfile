package todo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class todoClock extends JFrame {

    private int hour = 0;
    private int minute = 0;
    private JLabel hourLab;
	private JLabel minuteLab;
	private todoClockListener listener;

    public todoClock(todoClockListener listener) {
    	this.listener = listener;
    	
    	setUndecorated(true); // 타이틀바 제거
    	getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // 테두리
        setSize(150, 100);
        setLayout(new GridLayout(0,1));
        setLocationRelativeTo(null);
        
        JPanel clock = new JPanel();
        add(clock);
        
        hourLab = new JLabel(String.format("%02d", hour));
        hourLab.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        clock.add(hourLab);

        // 가운데 콜론 표시용 라벨
        JLabel colon = new JLabel(":");
        colon.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        clock.add(colon);
        
        // 분
        minuteLab = new JLabel(String.format("%02d", minute));
        minuteLab.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        clock.add(minuteLab);
        
        JPanel btn = new JPanel();
        JButton confirm = new JButton("확인");
        confirm.setBorder(new EmptyBorder(3,5,3,5)); //위 왼쪽 아래 오른쪽
        confirm.setFocusPainted(false);
        JButton cancel = new JButton("취소");
        cancel.setBorder(new EmptyBorder(3,5,3,5));
        cancel.setFocusPainted(false);
        
        hourLab.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
	            int dir = e.getWheelRotation();            
	            int step = e.isShiftDown() ? 3 : 1;        
	            hour = (hour + (dir < 0 ? step : -step) + 24) % 24;
	            hourLab.setText(String.format("%02d", hour));
	            e.consume();
			}
		});
        
        minuteLab.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
	            int dir = e.getWheelRotation();
	            int step = e.isShiftDown() ? 10 : 1;
	            minute = (minute + (dir < 0 ? step : -step) + 60) % 60;
	            minuteLab.setText(String.format("%02d", minute));
	            e.consume();
			}
		});
        
        confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listener != null) {
	                listener.onTimeSelected(hour, minute);
	            }
	            dispose();
			}
		});
        
        cancel.addActionListener(e -> dispose());
        
        add(btn);
        btn.add(confirm);
        btn.add(cancel);
        setVisible(true);        
    }
}
