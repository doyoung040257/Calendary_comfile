package frame;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 다양한 형식의 날짜 문자열을 LocalDate 객체로 변환하는 유틸리티 클래스
 */
public class DateParser {

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            if (dateStr.contains("[")) {
                // "2025-9-8[Mon]" 형식 처리
                String datePart = dateStr.substring(0, dateStr.indexOf('['));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                return LocalDate.parse(datePart, formatter);
            } else {
                // "09월 09일" 형식 처리 (연도는 현재 연도로 자동 설정)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일", Locale.KOREAN);
                MonthDay monthDay = MonthDay.parse(dateStr, formatter);
                return monthDay.atYear(LocalDate.now().getYear());
            }
        } catch (Exception e) {
            System.err.println("날짜 형식 파싱 오류: " + dateStr);
            e.printStackTrace();
            return null;
        }
    }
}