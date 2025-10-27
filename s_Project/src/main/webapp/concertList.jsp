<%@ page import="kr.soldesk.ConcertData, java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="inc/header.jsp" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공연 목록 (기간 검색 + 페이지네이션)</title>
<style>
body {
    font-family: 'Arial', sans-serif;
    background-color: #f7f3ef;
}
#container {
    width: 90%;
    max-width: 1400px;
    height: calc(95vh - 130px);
    margin: 40px auto 80px auto;
    background-color: #fffdf9;
    padding: 10px;
    border: 1px solid #cccccc;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    box-sizing: border-box;

    /* ✅ 추가 */
    overflow-x: auto;    /* 가로 스크롤 생김 (내용이 넘칠 경우) */
    overflow-y: auto;    /* 세로도 넘칠 경우 스크롤 생김 */
}
table {
    width: 80%;
    margin: 30px auto;
    border-collapse: collapse;
    background: white;
    box-shadow: 0 3px 10px rgba(0,0,0,0.1);
}
th, td {
    border: 1px solid #ddd;
    padding: 10px 15px;
    text-align: center;
}
th { background-color: #c49b63; color: white; }
tr:hover { background-color: #f6f1e7; }
.pagination {
    text-align: center;
    margin: 25px 0;
}
.pagination a {
    display: inline-block;
    padding: 8px 12px;
    margin: 0 4px;
    border: 1px solid #ccc;
    border-radius: 4px;
    text-decoration: none;
    color: #333;
    background-color: white; /* ← 기본은 흰색 */
}

.pagination .active {
    background-color: #c49b63;
    color: white;
    pointer-events: none;
}

button {
    padding: 6px 14px;
    background: #c49b63;
    border: none;
    border-radius: 5px;
    color: white;
    cursor: pointer;
}
button:hover { background: #a67c48; }
</style>
</head>
<body>

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

<div id="container">
	<form method="get" class="search-box" align="center">
	    <label>시작일: <input type="date" name="stdate" required></label>
	    <label>종료일: <input type="date" name="eddate" required></label>
	    <button type="submit">검색</button>
	</form>
	
	<%
	    // ✅ 입력값
	    String stdate = request.getParameter("stdate");
	    String eddate = request.getParameter("eddate");
	    if (stdate == null || eddate == null) {
	        stdate = "20250101";
	        eddate = "20251231";
	    } else {
	        stdate = stdate.replace("-", "");
	        eddate = eddate.replace("-", "");
	    }
	
	    // ✅ 현재 페이지
	    int pageNum = 1;
	    if (request.getParameter("page") != null) {
	        pageNum = Integer.parseInt(request.getParameter("page"));
	    }
	
	    int rowsPerPage = 10; // 한 페이지당 공연 수
	    int pageBlock = 5;    // 한 번에 보여줄 페이지 수
	
	    // ✅ API 데이터 가져오기 
	    List<Map<String, String>> concerts = ConcertData.getConcertListByDate(stdate, eddate, pageNum, rowsPerPage);
	
	    // ✅ 전체 개수 (임시로 300개 기준, 실제는 ConcertData에서 계산 가능)
	    int totalCount = 200;
	    int pageCount = (int) Math.ceil((double) totalCount / rowsPerPage);
	
	    int startPage = ((pageNum - 1) / pageBlock) * pageBlock + 1;
	    int endPage = startPage + pageBlock - 1;
	    if (endPage > pageCount) endPage = pageCount;
	%>
	
	<table>
	    <tr>
	        <th>공연명</th>
	        <th>공연기간</th>
	        <th>공연장소</th>
	    </tr>
	    <%
	        if (concerts.isEmpty()) {
	    %>
	        <tr><td colspan="3">해당 기간의 공연 정보가 없습니다.</td></tr>
	    <%
	        } else {
	            for (Map<String, String> c : concerts) {
	    %>
	        <tr>
	            <td><%= c.get("공연명") %></td>
	            <td><%= c.get("기간") %></td>
	            <td><%= c.get("공연장소") %></td>
	        </tr>
	    <%
	            }
	        }
	    %>
	</table>
	
	<div class="pagination">
	
	
	
	    <!-- ✅ [이전] (한 페이지 뒤로 이동) -->
	    <% if (pageNum > 1) { %>
	        <a class="prev" href="concertList.jsp?page=<%= pageNum - 1 %>&stdate=<%= stdate %>&eddate=<%= eddate %>">이전</a>
	    <% } else { %>
	        <a class="prev active">이전</a>
	    <% } %>
	
	    <!-- ✅ 페이지 번호 표시 -->
	    <% if (!concerts.isEmpty()) {
	           for (int i = startPage; i <= endPage; i++) {
	               if (i == pageNum) { %>
	                   <a class="active"><%= i %></a>
	               <% } else { %>
	                   <a href="concertList.jsp?page=<%= i %>&stdate=<%= stdate %>&eddate=<%= eddate %>"><%= i %></a>
	               <% }
	           }
	       } %>
	
	    <!-- ✅ [다음] -->
	    <% if (pageNum < pageCount) { %>
	        <a href="concertList.jsp?page=<%= pageNum + 1 %>&stdate=<%= stdate %>&eddate=<%= eddate %>">다음</a>
	    <% } else { %>
	        <a class="active">다음</a>
	    <% } %>
	    
	    <br>
	    <br>
	    
	    <!-- ✅ [이전페이지] (5개 단위 블록 이동) -->
	    <% if (startPage > 1) { %>
	        <a class="prevpage" href="concertList.jsp?page=<%= startPage - pageBlock %>&stdate=<%= stdate %>&eddate=<%= eddate %>">이전페이지</a>
	    <% } else { %>
	        <a class="prevpage active">이전페이지</a>
	    <% } %>
	
	    <!-- ✅ [다음페이지] -->
	    <% if (endPage < pageCount) { %>
	        <a href="concertList.jsp?page=<%= startPage + pageBlock %>&stdate=<%= stdate %>&eddate=<%= eddate %>">다음페이지</a>
	    <% } else { %>
	        <a class="active">다음페이지</a>
	    <% } %>
	</div>
</div>

</body>
</html>

<%@ include file="inc/footer.jsp" %>
