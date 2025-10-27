package kr.soldesk;

import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;

import org.json.*;

public class MapService {

    // 네이버 API 키
    private final String clientId = "z1usuihobh";
    private final String clientSecret = "szv154xERXbGz6mVUXf3cUpMTWJqrT33vZpVtfAp";

    // --------------------------
    // 1. 주소 → 좌표 변환
    // --------------------------
    public AddressVO getAddress(String inputAddress) {
        AddressVO vo = null;
        try {
            String addr = URLEncoder.encode(inputAddress, "utf-8");
            String reqUrl = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;

            URL url = new URL(reqUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-ncp-apigw-api-key-id", clientId);
            con.setRequestProperty("x-ncp-apigw-api-key", clientSecret);

            if (con.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                JSONTokener tokener = new JSONTokener(br);
                JSONObject object = new JSONObject(tokener);
                JSONArray arr = object.getJSONArray("addresses");

                if (arr.length() > 0) {
                    JSONObject temp = arr.getJSONObject(0);
                    vo = new AddressVO(
                        temp.optString("roadAddress"),
                        temp.optString("jibunAddress"),
                        temp.optString("x"),
                        temp.optString("y")
                    );
                }
            } else {
                System.out.println("주소 요청 실패: " + con.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    // --------------------------
    // 2. 좌표 → 지도 이미지 (Swing용)
    // --------------------------
    public ImageIcon getMapImage(AddressVO vo) {
        ImageIcon image = null;
        try {
            String pos = URLEncoder.encode(vo.getX() + " " + vo.getY(), "UTF-8");
            String label = URLEncoder.encode(vo.getRoadAddress(), "UTF-8");

            String reqUrl = "https://maps.apigw.ntruss.com/map-static/v2/raster?"
                    + "scale=1&format=png&w=700&h=500"
                    + "&markers=type:t|pos:" + pos + "|label:" + label;

            URL url = new URL(reqUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-ncp-apigw-api-key-id", clientId);
            con.setRequestProperty("x-ncp-apigw-api-key", clientSecret);

            if (con.getResponseCode() == 200) {
                // 임시 파일로 저장 후 ImageIcon 생성
                File file = File.createTempFile("map_", ".png");
                try (InputStream is = con.getInputStream();
                     FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(is.readAllBytes());
                }
                image = new ImageIcon(file.getAbsolutePath());
            } else {
                System.out.println("지도 이미지 요청 실패: " + con.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    // --------------------------
    // 3. 좌표 → 지도 이미지 (JSP용, 파일 반환)
    // --------------------------
    public File getMapImageFile(AddressVO vo, String saveDirPath) {
        File file = null;
        try {
            String pos = URLEncoder.encode(vo.getX() + " " + vo.getY(), "UTF-8");
            String label = URLEncoder.encode(vo.getRoadAddress(), "UTF-8");

            String reqUrl = "https://maps.apigw.ntruss.com/map-static/v2/raster?"
                    + "scale=1&format=png&w=700&h=500"
                    + "&markers=type:t|pos:" + pos + "|label:" + label;

            URL url = new URL(reqUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-ncp-apigw-api-key-id", clientId);
            con.setRequestProperty("x-ncp-apigw-api-key", clientSecret);

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                File dir = new File(saveDirPath);
                if (!dir.exists()) dir.mkdirs();

                file = new File(dir, "map_" + System.currentTimeMillis() + ".png");
                try (InputStream is = con.getInputStream();
                     FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(is.readAllBytes());
                    System.out.println("지도 이미지 저장 완료: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("지도 요청 실패: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}
