<%@page import="javax.sql.DataSource"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<%
Connection conn = null;
PreparedStatement pstmt = null;
ResultSet rs = null;

try {
    request.setCharacterEncoding("UTF-8");
    String member_id = request.getParameter("member_id");
    String member_pass = request.getParameter("member_pass");

    // JNDI로 DB 연결 (sqldeveloper 연동)
    Context initContext = new InitialContext();
    Context envContext = (Context) initContext.lookup("java:/comp/env");
    DataSource ds = (DataSource) envContext.lookup("jdbc/xe");
    conn = ds.getConnection();

    // ✅ 입력된 ID와 비밀번호 확인 쿼리
    String sql = "SELECT member_name, member_w FROM login_user WHERE member_id = ? AND member_pass = ?";
    pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, Integer.parseInt(member_id));
    pstmt.setString(2, member_pass);
    rs = pstmt.executeQuery();

    if (rs.next()) {
        // ✅ 로그인 성공
        String member_name = rs.getString("member_name");
		String member_w = rs.getString("member_w");

        // 세션에 사용자 정보 저장
        session.setAttribute("member_id", member_id);
        session.setAttribute("member_name", member_name);
        session.setAttribute("member_w", member_w);
%>
        <script>
            alert("<%= member_name %>님, 로그인 성공!");
            location.href = "index.jsp"; // 로그인 후 이동할 페이지
        </script>
<%
    } else {
        // ❌ 로그인 실패
%>
        <script>
            alert("아이디 또는 비밀번호가 일치하지 않습니다.");
            history.back();
        </script>
<%
    }

} catch (Exception e) {
    e.printStackTrace();
} finally {
    if (rs != null) try { rs.close(); } catch (Exception e) {}
    if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
    if (conn != null) try { conn.close(); } catch (Exception e) {}
}
%>
