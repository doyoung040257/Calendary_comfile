package statistics;

import javax.swing.*;
import java.awt.*;

public class statisticsGraph extends JPanel {
    private double[] values;   // 외부에서 전달받는 데이터
    private Color[] colors;    // 색상 배열
    private String centerText; // 중앙에 표시할 문자열

    public void setValues(double[] values) {
		this.values = values;
	}

	// 생성자
    public statisticsGraph(double[] values, Color[] colors) {
        this.values = values;
        this.colors = colors;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) - 40; // 바깥 원 크기

        int x = (width - size) / 2;
        int y = (height - size) / 2;

        // 전체 합 구하기
        double total = 0;
        for (double v : values) total += v;
        
        if (total == 0) {
            // ★ 값이 없을 때는 회색 도넛 출력
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillArc(x, y, size, size, 0, 360);

            // 중앙 구멍
            int innerSize = size / 2;
            int innerX = (width - innerSize) / 2;
            int innerY = (height - innerSize) / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(innerX, innerY, innerSize, innerSize);

            g2.dispose();
            return;
        }

        // 도넛 파이 조각 그리기
        double curAngle = 0;
        for (int i = 0; i < values.length; i++) {
            double angle = 360 * values[i] / total;
            g2.setColor(colors[i % colors.length]); // 색상 순환
            g2.fillArc(x, y, size, size, (int) curAngle, (int) angle);
            curAngle += angle;
        }

        // 도넛 안쪽 (흰색 원)
        int holeSize = size / 2;
        int hx = (width - holeSize) / 2;
        int hy = (height - holeSize) / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(hx, hy, holeSize, holeSize);

    }
}

