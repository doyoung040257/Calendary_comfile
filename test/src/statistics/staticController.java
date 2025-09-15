package statistics;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import todo.SetFrame;
import todo.todoList;
import todo.todoListMake;

public class statisticsController extends JPanel {
	
	private JPanel list;
	
	public statisticsController(String category, YearMonth ym, todoListMake todoListData, SetFrame parentFrame) {
		statisticsPart part = new statisticsPart();
		
	    Font titleFont = new Font("맑은 고딕", Font.BOLD, 22);
	    Font buttonFont = new Font("맑은 고딕", Font.BOLD, 16);
		
		setLayout(null);
		setBackground(Color.black);
		
		//
		//상단 패널
		//
		JPanel topPanel = part.createNavPanel();
		topPanel.setBounds(10, 10, 445, 50);
		topPanel.setBackground(Color.RED);
		add(topPanel);
		
		JPanel titlePanel = part.createNavPanel();
		titlePanel.setBackground(Color.YELLOW);
		topPanel.add(titlePanel);
		
		JLabel title = new JLabel();
		title.setText(category + " 목록");
		title.setFont(titleFont);
		titlePanel.add(title);

		//
		//중간 패널
		//
		JScrollPane scrollPane = part.listScrollBox();
		scrollPane.setBounds(10, 70, 445, 550);
		add(scrollPane);
		
        list = part.createNavPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.BLUE);
        list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setViewportView(list);
        
        // 리스트 출력
        for (todoList item : todoListData.getTodolist()) {
            try {
                String dateStr = item.getDay();
                if (dateStr.contains("[")) {
                    dateStr = dateStr.substring(0, dateStr.indexOf("[")); 
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                YearMonth itemYM = YearMonth.from(localDate);


                if (item.getGroup().equals(category) && itemYM.equals(ym)) {
                    JLabel todoLabel = new JLabel(" " + item.getWork() + " | " + item.getDay() + " | " + item.getTime());
                    todoLabel.setFont(buttonFont);

                    // 📌 아이템 패널 (고정 크기 40px)
                    JPanel itemPanel = part.createNavPanel();
                    itemPanel.setLayout(new BorderLayout());
                    itemPanel.setBackground(Color.WHITE);
                    itemPanel.setPreferredSize(new Dimension(400, 40)); // 고정 높이
                    itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                    itemPanel.add(todoLabel, BorderLayout.CENTER);

                    list.add(itemPanel);
                    list.add(Box.createVerticalStrut(5)); // 간격 (선택)
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //
        //하단 패널
        //
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBounds(10, 625, 445, 60);
        bottomPanel.setOpaque(false);
        add(bottomPanel);
        
        JButton backBtn = part.createNavButton("뒤로가기", buttonFont);
        backBtn.setPreferredSize(new Dimension(140, 50));
        bottomPanel.add(backBtn);
        
        backBtn.addActionListener(e -> {
            parentFrame.getCardLayout().show(parentFrame.getCardPanel(), "STATISTICS");
        });
	}
}
