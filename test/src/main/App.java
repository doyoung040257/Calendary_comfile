// 이 클래스가 main 패키지에 속해 있음을 선언합니다.
package main;

// 다른 패키지(frame)에 있는 CalendarFrame 클래스를 사용하기 위해 import 합니다.
import frame.CalendarFrame01;
import frame.MonthlyCalendarView;

public class App {

    
    public static void main(String[] args) {
        
            // frame 패키지에 있는 CalendarFrame 클래스의 객체(인스턴스)를 생성합니다.
            CalendarFrame01 appFrame = new CalendarFrame01(null);
            appFrame.setVisible(true);
    		
    }

}
