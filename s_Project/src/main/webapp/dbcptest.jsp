<%@page import="javax.sql.DataSource"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<%
Connection conn = null;
PreparedStatement pstmt = null;
ResultSet rs = null;

try {
	//서버 환경 설정 진입
    Context initContext = new InitialContext();
    Context envContext = (Context) initContext.lookup("java:/comp/env");
  	//DBCP POOL 연결 가져옴
    DataSource ds = (DataSource) envContext.lookup("jdbc/xe");
    conn = ds.getConnection();
    
    request.setCharacterEncoding("UTF-8");

    if (request.getParameter("member_id") != null) {
        String member_id = request.getParameter("member_id");
        String member_name = request.getParameter("member_name");
        String member_pass = request.getParameter("member_pass");
        String member_w = request.getParameter("member_w");

        // ✅ 1. 중복 여부 확인
        String checkSql = "SELECT COUNT(*) FROM login_user WHERE member_id = ?";
        pstmt = conn.prepareStatement(checkSql);
        pstmt.setInt(1, Integer.parseInt(member_id));
        rs = pstmt.executeQuery();

        boolean isDuplicate = false;
        if (rs.next() && rs.getInt(1) > 0) {
            isDuplicate = true;
        }
        rs.close();
        pstmt.close();

        if (isDuplicate) {
            // ✅ 중복일 경우 경고창 띄우기
%>
            <script>
                alert("이미 존재하는 회원 ID입니다. 다른 ID를 사용해주세요.");
                history.back();
            </script>
<%
        } else {
            // ✅ 2. 중복이 아닐 경우 INSERT 실행
            String insertSql = "INSERT INTO login_user VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, Integer.parseInt(member_id));
            pstmt.setString(2, member_name);
            pstmt.setString(3, member_pass);
            pstmt.setString(4, member_w);
            pstmt.executeUpdate();
%>
            <script>
                alert("회원가입이 완료되었습니다!");
                location.href = "dbcptest.jsp"; // 등록 후 새로고침 또는 페이지 이동
            </script>
<%
        }
    }
} catch (Exception e) {
    e.printStackTrace();
} finally {
    if (rs != null) try { rs.close(); } catch (Exception e) {}
    if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
    if (conn != null) try { conn.close(); } catch (Exception e) {}
}
%>

<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
</head>
<body>
    <h2>회원가입 화면</h2>
    <hr />
    <form action="dbcptest.jsp" method="post">
        회원 아이디(고유 번호) : <input type="text" name="member_id" required /> <br />
        회원 닉네임 : <input type="text" name="member_name" required /> <br />
        회원 비번 : <input type="password" name="member_pass" required /> <br />
        회원 주소 : <input type="text" name="member_w" required /> <br />
        <input type="submit" value="등록" />
    </form>
    <hr />
</body>
</html>
