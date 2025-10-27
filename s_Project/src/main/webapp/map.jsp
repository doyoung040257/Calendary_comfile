<%@ page import="kr.soldesk.*" %>
<%@ page import="java.io.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="inc/header.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");

    String inputAddress = request.getParameter("address");
    AddressVO vo = null;
    MapService service = new MapService();
    String mapFilePath = null;

    if (inputAddress != null && !inputAddress.trim().equals("")) {
        vo = service.getAddress(inputAddress);
        if (vo != null) {
            File mapFile = service.getMapImageFile(vo, application.getRealPath("/maps"));
            if (mapFile != null) {
                mapFilePath = "maps/" + mapFile.getName();
            }
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>네이버 지도 JSP</title>
<style>
    body {
        font-family: '맑은 고딕', sans-serif;
        background-color: #f0f2f5;
        margin: 0;
        padding: 0;
    }

    .container {
        max-width: 800px;
        margin: 50px auto;
        background: #fff;
        padding: 30px 40px;
        border-radius: 12px;
        box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        text-align: center;
    }

    h2 {
        margin-bottom: 30px;
        color: #333;
    }

    form {
        margin-bottom: 25px;
    }

    input[type="text"] {
        width: 60%;
        padding: 8px 10px;
        border: 1px solid #ccc;
        border-radius: 6px;
        font-size: 16px;
    }

    input[type="submit"] {
        padding: 8px 15px;
        font-size: 16px;
        border: none;
        border-radius: 6px;
        background-color: #4CAF50;
        color: white;
        cursor: pointer;
    }

    input[type="submit"]:hover {
        background-color: #45a049;
    }

    .info {
        text-align: left;
        display: inline-block;
        margin-top: 20px;
        color: #333;
    }

    .info p {
        margin: 6px 0;
        font-size: 15px;
    }

    img {
        margin-top: 20px;
        border: 1px solid #ccc;
        border-radius: 10px;
        max-width: 100%;
        height: auto;
    }

    .alert {
        color: red;
        margin-top: 20px;
        font-weight: bold;
    }
</style>
</head>
<body>

<div class="container">

    <h2>지도 검색</h2>
	<%
	// 1️⃣ 로그인 세션에서 member_id 가져오기
    Object objId = session.getAttribute("member_id");
    int memberId = -1;

    if (objId == null) {
        out.println("<script>alert('로그인이 필요합니다.'); location.href='login.jsp';</script>");
        return;
    } else {
        try {
            memberId = Integer.parseInt(objId.toString());
        } catch (NumberFormatException e) {
            out.println("<script>alert('세션 ID 오류. 다시 로그인해주세요.'); location.href='login.jsp';</script>");
            return;
        }
    }
	%>
    <form method="get">
        <input type="text" name="address" placeholder="주소를 입력하세요" value="<%= (inputAddress != null) ? inputAddress : "" %>">
        <input type="submit" value="검색">
    </form>

    <% if (vo != null) { %>
        <div class="info">
            <p><b>도로명 주소:</b> <%= vo.getRoadAddress() %></p>
            <p><b>지번 주소:</b> <%= vo.getJibunAddress() %></p>
            <p><b>경도(X):</b> <%= vo.getX() %></p>
            <p><b>위도(Y):</b> <%= vo.getY() %></p>
        </div>

        <% if (mapFilePath != null) { %>
            <img src="<%= mapFilePath %>" alt="지도 이미지">
        <% } else { %>
            <p class="alert">지도를 불러올 수 없습니다.</p>
        <% } %>
    <% } %>

</div>

</body>
</html>
<%@ include file="inc/footer.jsp" %>