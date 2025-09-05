package todo;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class todoStarMake2 {

    // 별 이미지를 생성해서 반환하는 메서드
    public static BufferedImage createStarImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double cx = width / 2.0;
        double cy = height / 2.0;
        double outerR = width * 0.4;
        double innerR = outerR * 0.5;

        Shape star = createStar(cx, cy, outerR, innerR, 5, -Math.PI / 2);

        g2.setColor(Color.GRAY);
        g2.fill(star);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.draw(star);

        g2.dispose();
        return img;
    }

    // 별 좌표 계산
    private static Shape createStar(double cx, double cy,
                                    double outerR, double innerR,
                                    int numRays, double startAngleRad) {
        Path2D path = new Path2D.Double();
        double delta = Math.PI / numRays;
        for (int i = 0; i < numRays * 2; i++) {
            double angle = startAngleRad + i * delta;
            double r = (i % 2 == 0) ? outerR : innerR;
            double x = cx + Math.cos(angle) * r;
            double y = cy + Math.sin(angle) * r;
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.closePath();
        return path;
    }
}


