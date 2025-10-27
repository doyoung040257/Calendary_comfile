package kr.soldesk;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ConcertData {

    // 🎯 기간별 전체 공연 목록 가져오기 (시작일 빠른순 + 페이지 나누기)
    public static List<Map<String, String>> getConcertListByDate(String stdate, String eddate, int page, int rows) {
        List<Map<String, String>> allData = new ArrayList<>();

        try {
            String apiKey = "b11ef33ac04c4ca79dd745d66c733e09";

            // ✅ 최대 10페이지까지 반복 요청 (필요 시 조정 가능)
            for (int cpage = 1; cpage <= 10; cpage++) {
                String urlStr = "http://www.kopis.or.kr/openApi/restful/pblprfr?service="
                        + URLEncoder.encode(apiKey, "UTF-8")
                        + "&stdate=" + stdate
                        + "&eddate=" + eddate
                        + "&cpage=" + cpage
                        + "&rows=" + rows;

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));

                NodeList list = doc.getElementsByTagName("db");
                if (list.getLength() == 0) break; // 더 이상 데이터 없음

                for (int i = 0; i < list.getLength(); i++) {
                    Element e = (Element) list.item(i);
                    String name = getTagValue(e, "prfnm");
                    String from = getTagValue(e, "prfpdfrom");
                    String to = getTagValue(e, "prfpdto");
                    String place = getTagValue(e, "fcltynm");

                    // 점(.) 제거
                    String fromClean = from.replace(".", "").trim();

                    // ✅ stdate보다 시작일이 이전인 경우 제외
                    if (fromClean.compareTo(stdate) < 0) continue;

                    Map<String, String> map = new HashMap<>();
                    map.put("공연명", name);
                    map.put("기간", from + " ~ " + to);
                    map.put("시작일", fromClean);
                    map.put("공연장소", place);

                    allData.add(map);
                }

            }

            // ✅ 전체 데이터 정렬 (공연 시작일 빠른 순)
            Collections.sort(allData, Comparator.comparing(m -> m.get("시작일")));

            // ✅ 요청한 페이지에 해당하는 데이터만 반환
            int startIdx = (page - 1) * rows;
            int endIdx = Math.min(startIdx + rows, allData.size());

            if (startIdx >= allData.size()) return new ArrayList<>();
            return allData.subList(startIdx, endIdx);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return allData;
    }

    private static String getTagValue(Element e, String tag) {
        NodeList n = e.getElementsByTagName(tag);
        if (n.getLength() == 0 || n.item(0) == null) return "";
        return n.item(0).getTextContent();
    }
}
