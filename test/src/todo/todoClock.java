package todo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class todoClock extends JFrame {

    private int hour = 0;
    private int minute = 0;
    private JLabel hourLab;
	private JLabel minuteLab;
	private todoClockListener listener;

    //위치 지정 보류!!!
    public todoClock(todoClockListener listener) {
    	this.listener = listener;
    	
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        JPanel clock = new JPanel();
        clock.setBackground(Color.gray);
        add(clock, BorderLayout.CENTER);
        
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

        JButton confirm = new JButton("확인");
        JButton cancle = new JButton("취소");
        
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
        
        add(confirm, BorderLayout.SOUTH);
        add(cancle, BorderLayout.SOUTH);
        setVisible(true);        
    }
}

