package main;

import javax.swing.SwingUtilities;

import frame.CalendarFrame01;

public class Main {
    public static void main(String[] args) {
        // Swing GUI는 Event Dispatch Thread(EDT)에서 생성하고 실행하는 것이 안전합니다.
        SwingUtilities.invokeLater(() -> {
            CalendarFrame01 planner = new CalendarFrame01();
            planner.setVisible(true);
        });
    }
}