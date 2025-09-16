package statistics;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import todo.todoListMake;
import todo.SetFrame;
import todo.todoList;
import todo.SetFrame;

public class statisticsPanel extends JPanel{
	
    private JPanel container;
    private CardLayout cardLayout;
    private SetFrame parentFrame;
	
    private int intWork;
    private int intHealth;
    private int intStudy;
    private int intHobby;
    private int intFinance;
    private int intOthers;
    
	public JLabel preMonth;
	public JLabel year_month;
	public JLabel nextMonth;
	public JButton work2;
	public JButton health2;
	public JButton study2;
	public JButton hobby2;
	public JButton finance2;
	public JButton others2;
	
	private java.time.YearMonth currentYearMonth;
	private todoListMake todoListData;
	
	private JProgressBar progressBarWork;
	private JProgressBar progressBarHealth;
	private JProgressBar progressBarStudy;
	private JProgressBar progressBarHobby;
	private JProgressBar progressBarFinance;
	private JProgressBar progressBarOthers;
	private statisticsGraph staticgraph;
	
	
	public statisticsPanel(JPanel container, CardLayout cardLayout, todoListMake list, SetFrame parentFrame) {

        this.container = container;
        this.cardLayout = cardLayout;
        this.todoListData = list;
        this.parentFrame = parentFrame;
		
		statisticsPart part = new statisticsPart();
		
		currentYearMonth = YearMonth.now();
		
		Font titleFont = new Font("맑은 고딕", Font.BOLD, 22);
	    Font buttonFont = new Font("맑은 고딕", Font.BOLD, 18);
		
        Color[] colors = {
        	    Color.decode("#DC3912"),
        	    Color.decode("#FF9900"),
        	    Color.decode("#EDC948"),
        	    Color.decode("#109618"),
        	    Color.decode("#3366CC"),
        	    Color.decode("#990099") 
        };
        staticgraph = new statisticsGraph(new double[]{0,0,0,0,0,0}, colors);
        staticgraph.setPreferredSize(new Dimension(200, 200));
        
		// 전체 패널
		setLayout(null);
		setBackground(Color.black);
		
		//
		// 상단 패널(목록,년+월)
		//
		JPanel topPanel = part.createNavPanel();
		topPanel.setBounds(10, 10, 445, 50);
		topPanel.setBackground(Color.RED);
		add(topPanel);
		
		// 상단 목록
		JPanel inventory = part.createNavPanel();
		inventory.setLayout(new FlowLayout());
		inventory.setBackground(Color.WHITE);
		topPanel.add(inventory, BorderLayout.CENTER);
		
		// 이전 버튼
		preMonth = new JLabel("◀"); 
		preMonth.setFont(titleFont);
		inventory.add(preMonth);
		
		inventory.add(Box.createHorizontalStrut(20));
		
		// 년+월
		year_month = new JLabel();
		year_month.setFont(titleFont);
		updateYearMonthLabel();
		inventory.add(year_month);
		
		inventory.add(Box.createHorizontalStrut(20));
		
		// 다음 버튼
		nextMonth = new JLabel("▶");
		nextMonth.setFont(titleFont);
		inventory.add(nextMonth);
		
        // ◀ 버튼
        preMonth.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                currentYearMonth = currentYearMonth.minusMonths(1);
                updateYearMonthLabel();
                updateStatistics();
            }
        });

        // ▶ 버튼
        nextMonth.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                currentYearMonth = currentYearMonth.plusMonths(1);
                updateYearMonthLabel();
                updateStatistics();
            }
        });
		
		//
		// 중간 패널(통계+카테고리)
		//
		JPanel middlePanel = part.createNavPanel();
		middlePanel.setBounds(10, 70, 445, 215);
		middlePanel.setLayout(new GridLayout(1,2));
		middlePanel.setBackground(Color.WHITE);
		add(middlePanel, BorderLayout.CENTER);
		
		// 중간 패널 왼쪽 (통계 그림)
		JPanel middleLeftPanel = new JPanel();
		middleLeftPanel.setOpaque(false);
		middleLeftPanel.add(staticgraph);
		middlePanel.add(middleLeftPanel);
		
		// 중간 패널 오른쪽 (통계 목록 간략)
		JPanel middleRightPanel = new JPanel();
		middleRightPanel.setLayout(new GridLayout(6,1)); // 6행 1열
		middleRightPanel.setOpaque(false);
		middlePanel.add(middleRightPanel);
		
		// 통계목록(업무,건강,공부,취미,금융,기타)
		JPanel one = new JPanel();
		JPanel two = new JPanel();
		JPanel three = new JPanel();
		JPanel four = new JPanel();
		JPanel five = new JPanel();
		JPanel six = new JPanel();
		
		one.setOpaque(false);
		two.setOpaque(false);
		three.setOpaque(false);
		four.setOpaque(false);
		five.setOpaque(false);
		six.setOpaque(false);
		
		middleRightPanel.add(one);
		middleRightPanel.add(two);
		middleRightPanel.add(three);
		middleRightPanel.add(four);
		middleRightPanel.add(five);
		middleRightPanel.add(six);
		
		JPanel colorWork = new JPanel();
		JPanel colorHealth = new JPanel();
		JPanel colorStudy = new JPanel();
		JPanel colorHobby = new JPanel();
		JPanel colorFinance = new JPanel();
		JPanel colorOthers = new JPanel();
		
		colorWork.setBackground(Color.decode("#DC3912"));
		colorHealth.setBackground(Color.decode("#FF9900"));
		colorStudy.setBackground(Color.decode("#EDC948"));
		colorHobby.setBackground(Color.decode("#109618"));
		colorFinance.setBackground(Color.decode("#3366CC"));
		colorOthers.setBackground(Color.decode("#990099"));
		
		JLabel work = new JLabel("업무");
		JLabel health = new JLabel("건강");
		JLabel study = new JLabel("공부");
		JLabel hobby = new JLabel("취미");
		JLabel finance = new JLabel("금융");
		JLabel others = new JLabel("기타");
		
		progressBarWork = new JProgressBar(0, 100);
		progressBarHealth = new JProgressBar(0, 100);
		progressBarStudy = new JProgressBar(0, 100);
		progressBarHobby = new JProgressBar(0, 100);
		progressBarFinance = new JProgressBar(0, 100);
		progressBarOthers = new JProgressBar(0, 100);
		
		progressBarWork.setStringPainted(true);
		progressBarHealth.setStringPainted(true);
		progressBarStudy.setStringPainted(true);
		progressBarHobby.setStringPainted(true);
		progressBarFinance.setStringPainted(true);
		progressBarOthers.setStringPainted(true);
		
		one.add(colorWork); one.add(work); one.add(progressBarWork);
		two.add(colorHealth); two.add(health); two.add(progressBarHealth);
		three.add(colorStudy); three.add(study); three.add(progressBarStudy);
		four.add(colorHobby); four.add(hobby); four.add(progressBarHobby);
		five.add(colorFinance); five.add(finance); five.add(progressBarFinance);
		six.add(colorOthers); six.add(others); six.add(progressBarOthers);
		
		//
		// 하단 패널(카테로리 버튼)
		//
		JPanel bottomPanel = part.createNavPanel();
		bottomPanel.setBounds(10, 295, 445, 385);
		bottomPanel.setLayout(new GridLayout(6,1,0,10));
		bottomPanel.setPreferredSize(new Dimension(0, 450));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		bottomPanel.setBackground(Color.BLUE);
		add(bottomPanel, BorderLayout.SOUTH);
		
		JPanel workBox = part.createNavPanel();
		JPanel healthBox = part.createNavPanel();
		JPanel studyBox = part.createNavPanel();
		JPanel hobbyBox = part.createNavPanel();
		JPanel financeBox = part.createNavPanel();
		JPanel othersBox = part.createNavPanel();
		
		bottomPanel.add(workBox);
		bottomPanel.add(healthBox);
		bottomPanel.add(studyBox);
		bottomPanel.add(hobbyBox);
		bottomPanel.add(financeBox);
		bottomPanel.add(othersBox);
		
		work2 = part.createNavButton("업무(" + intWork +")", buttonFont);
		health2 = part.createNavButton("건강(" + intHealth +")" , buttonFont);
		study2 = part.createNavButton("공부(" + intStudy +")", buttonFont);
		hobby2 = part.createNavButton("취미(" + intHobby +")", buttonFont);
		finance2 = part.createNavButton("금융(" + intFinance +")", buttonFont);
		others2 = part.createNavButton("기타(" + intOthers +")", buttonFont);
		
		work2.setPreferredSize(new Dimension(170, 40));
		health2.setPreferredSize(new Dimension(170, 40));
		study2.setPreferredSize(new Dimension(170, 40));
		hobby2.setPreferredSize(new Dimension(170, 40));
		finance2.setPreferredSize(new Dimension(170, 40));
		others2.setPreferredSize(new Dimension(170, 40));
		
		ImageIcon workicon = new ImageIcon(getClass().getResource("/images/work.png"));
		ImageIcon healthicon = new ImageIcon(getClass().getResource("/images/health.png"));
		ImageIcon studyicon = new ImageIcon(getClass().getResource("/images/study.png"));
		ImageIcon hobbyicon = new ImageIcon(getClass().getResource("/images/hobby.png"));
		ImageIcon financeicon = new ImageIcon(getClass().getResource("/images/finance.png"));
		ImageIcon othersicon = new ImageIcon(getClass().getResource("/images/others.png"));
		
		workicon = new ImageIcon(workicon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
		healthicon = new ImageIcon(healthicon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
		studyicon = new ImageIcon(studyicon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
		hobbyicon = new ImageIcon(hobbyicon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
		financeicon = new ImageIcon(financeicon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
		othersicon = new ImageIcon(othersicon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

		work2.setIconTextGap(10);
		health2.setIconTextGap(10);
		study2.setIconTextGap(10);
		hobby2.setIconTextGap(10);
		finance2.setIconTextGap(10);
		others2.setIconTextGap(10);
		
		work2.setHorizontalTextPosition(SwingConstants.RIGHT);
		work2.setVerticalTextPosition(SwingConstants.CENTER);
		health2.setHorizontalTextPosition(SwingConstants.RIGHT);
		health2.setVerticalTextPosition(SwingConstants.CENTER);
		study2.setHorizontalTextPosition(SwingConstants.RIGHT);
		study2.setVerticalTextPosition(SwingConstants.CENTER);
		hobby2.setHorizontalTextPosition(SwingConstants.RIGHT);
		hobby2.setVerticalTextPosition(SwingConstants.CENTER);
		finance2.setHorizontalTextPosition(SwingConstants.RIGHT);
		finance2.setVerticalTextPosition(SwingConstants.CENTER);
		others2.setHorizontalTextPosition(SwingConstants.RIGHT);
		others2.setVerticalTextPosition(SwingConstants.CENTER);
		
		work2.setIcon(workicon);
		health2.setIcon(healthicon);
		study2.setIcon(studyicon);
		hobby2.setIcon(hobbyicon);
		finance2.setIcon(financeicon);
		others2.setIcon(othersicon);
		
		work2.setBackground(Color.decode("#DC3912"));
		health2.setBackground(Color.decode("#FF9900"));
		study2.setBackground(Color.decode("#EDC948"));
		hobby2.setBackground(Color.decode("#109618"));
		finance2.setBackground(Color.decode("#3366CC"));
		others2.setBackground(Color.decode("#990099"));
		
		// 버튼 이벤트
		work2.addActionListener(e -> parentFrame.showCategoryPanel("업무", currentYearMonth));
        health2.addActionListener(e -> parentFrame.showCategoryPanel("건강", currentYearMonth));
        study2.addActionListener(e -> parentFrame.showCategoryPanel("공부", currentYearMonth));
        hobby2.addActionListener(e -> parentFrame.showCategoryPanel("취미", currentYearMonth));
        finance2.addActionListener(e -> parentFrame.showCategoryPanel("금융", currentYearMonth));
        others2.addActionListener(e -> parentFrame.showCategoryPanel("기타", currentYearMonth));
		
		workBox.add(work2);
		healthBox.add(health2);
		studyBox.add(study2);
		hobbyBox.add(hobby2);
		financeBox.add(finance2);
		othersBox.add(others2);
		
		updateStatistics();
	}
	
    // 년/월 라벨 이벤트
    private void updateYearMonthLabel() {
        int year = currentYearMonth.getYear();
        int month = currentYearMonth.getMonthValue();
        year_month.setText(year + "년 " + String.format("%02d", month) + "월");
    }
    
    public void updateStatistics() {
		intWork = 0;
		intHealth = 0;
		intStudy = 0;
		intHobby = 0;
		intFinance = 0;
		intOthers = 0;

		for (todoList item : todoListData.getTodolist()) {
			try {
			    String dateStr = item.getDay().split("\\[")[0]; // "2025-09-01" 또는 "2025-9-9"

			    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
			    LocalDate localDate = LocalDate.parse(dateStr, formatter);

			    YearMonth itemYM = YearMonth.from(localDate);

				if (itemYM.equals(currentYearMonth)) {
					switch (item.getGroup()) {
						case "업무": intWork++; break;
						case "건강": intHealth++; break;
						case "공부": intStudy++; break;
						case "취미": intHobby++; break;
						case "금융": intFinance++; break;
						case "기타": intOthers++; break;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		int sum = intWork + intHealth + intStudy + intHobby + intFinance + intOthers;
		if (sum == 0) sum = 1;

		progressBarWork.setValue((int)((double)intWork/sum*100));
		progressBarHealth.setValue((int)((double)intHealth/sum*100));
		progressBarStudy.setValue((int)((double)intStudy/sum*100));
		progressBarHobby.setValue((int)((double)intHobby/sum*100));
		progressBarFinance.setValue((int)((double)intFinance/sum*100));
		progressBarOthers.setValue((int)((double)intOthers/sum*100));

		double[] values = {intWork,intHealth,intStudy,intHobby,intFinance,intOthers};
		staticgraph.setValues(values); // ★ staticGraph에 setValues() 메서드 필요
		staticgraph.repaint();
		
		//버튼 숫자 갱신
	    work2.setText("업무(" + intWork + ")");
	    health2.setText("건강(" + intHealth + ")");
	    study2.setText("공부(" + intStudy + ")");
	    hobby2.setText("취미(" + intHobby + ")");
	    finance2.setText("금융(" + intFinance + ")");
	    others2.setText("기타(" + intOthers + ")");
	}
}

