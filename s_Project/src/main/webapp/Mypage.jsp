<%@ page import="kr.soldesk.*" %>
<%@ page import="java.io.*, java.net.*, java.sql.*, javax.naming.*, javax.sql.*, javax.net.ssl.HttpsURLConnection" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="inc/header.jsp" %>

<%
request.setCharacterEncoding("UTF-8");

// 1️⃣ 로그인 체크
String memberIdStr = (String) session.getAttribute("member_id");
if (memberIdStr == null || memberIdStr.isEmpty()) {
    response.sendRedirect("login.jsp");
    return;
}

int memberId = -1;
try {
    memberId = Integer.parseInt(memberIdStr);
} catch (NumberFormatException e) {
    response.sendRedirect("login.jsp");
    return;
}

// 2️⃣ DB에서 회원 주소 가져오기
String memberAddress = "";
try {
    Context initContext = new InitialContext();
    Context envContext = (Context) initContext.lookup("java:/comp/env");
    DataSource ds = (DataSource) envContext.lookup("jdbc/xe");
    try (Connection conn = ds.getConnection();
         PreparedStatement pstmt = conn.prepareStatement("SELECT member_w FROM login_user WHERE member_id = ?")) {
        
        pstmt.setInt(1, memberId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                memberAddress = rs.getString("member_w");
            }
        }
    }
} catch (Exception e) {
    e.printStackTrace();
}

// 3️⃣ 주소 → 좌표 변환 및 지도 이미지 가져오기
String roadAddress = "", jibunAddress = "", x = "", y = "", mapImageUrl = "";

if (memberAddress != null && !memberAddress.trim().isEmpty()) {
    MapService service = new MapService();
    AddressVO vo = service.getAddress(memberAddress);
    
    if (vo != null) {
        roadAddress = vo.getRoadAddress();
        jibunAddress = vo.getJibunAddress();
        x = vo.getX();
        y = vo.getY();
        
        // 지도 이미지
        try {
            String clientId = "z1usuihobh";
            String clientSecret = "szv154xERXbGz6mVUXf3cUpMTWJqrT33vZpVtfAp";
            String pos = URLEncoder.encode(x + " " + y, "UTF-8");
            String label = URLEncoder.encode(roadAddress, "UTF-8");
            String reqUrl = "https://maps.apigw.ntruss.com/map-static/v2/raster?"
                    + "scale=1&format=png&w=700&h=500"
                    + "&markers=type:t|pos:" + pos + "|label:" + label;
            
            URL url = new URL(reqUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-ncp-apigw-api-key-id", clientId);
            con.setRequestProperty("x-ncp-apigw-api-key", clientSecret);

            if (con.getResponseCode() == 200) {
                byte[] bytes = con.getInputStream().readAllBytes();
                mapImageUrl = "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
%>

<html>
<head>
<title>마이페이지</title>
<style>
body { font-family: 'Noto Sans KR', sans-serif; background-color: #f7f7f7; margin:0; padding:0; }
.mypage-container { max-width: 600px; margin: 60px auto; background:white; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.1); padding:30px; }
h2 { text-align:center; color:#333; }
.info { margin-top:20px; }
.info div { margin-bottom:15px; font-size:16px; }
.info label { font-weight:bold; display:inline-block; width:120px; color:#555; }
</style>
</head>
<body>

<div class="mypage-container">
    <h2>마이페이지</h2>

    <div class="info">
        <div><label>아이디:</label> <%= session.getAttribute("member_id") %></div>
        <div><label>이름:</label> <%= session.getAttribute("member_name") %></div>
        <div><label>회원 주소:</label> <%= memberAddress != null && !memberAddress.isEmpty() ? memberAddress : "주소 없음" %></div>
        <div><label>도로명주소:</label> <%= roadAddress %></div>
        <div><label>지번주소:</label> <%= jibunAddress %></div>
        <div><label>경도(X):</label> <%= x %></div>
        <div><label>위도(Y):</label> <%= y %></div>
    </div>

    <% if (mapImageUrl != null && !mapImageUrl.isEmpty()) { %>
        <img src="<%= mapImageUrl %>" alt="지도 이미지" width="500" height="300">
    <% } else { %>
        <p>지도 이미지를 불러올 수 없습니다.</p>
    <% } %>

</div>

</body>
</html>

<%@ include file="inc/footer.jsp" %>
