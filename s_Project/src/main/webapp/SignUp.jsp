
<!--  첫 번째 수정안 -->
<%@page import="javax.sql.DataSource"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="inc/header.jsp" %>


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

    if (request.getParameter("member_id") != null) { // member_id 파라미터가 있으면 회원가입 처리 시작
        String member_id_str = request.getParameter("member_id");
        String member_name = request.getParameter("member_name");
        String member_pass = request.getParameter("member_pass");
        String member_w = request.getParameter("member_w");

        // member_id가 숫자인지 확인 (SQL Injunction 방어 및 타입 일치)
        int member_id;
        try {
            member_id = Integer.parseInt(member_id_str);
        } catch (NumberFormatException e) {
%>
            <script>
                alert("회원 아이디는 숫자만 입력해주세요.");
                history.back();
            </script>
<%
            return; // 숫자가 아니면 더 이상 진행하지 않음
        }

        // ✅ 1. 중복 여부 확인
        String checkSql = "SELECT COUNT(*) FROM login_user WHERE member_id = ?";
        pstmt = conn.prepareStatement(checkSql);
        pstmt.setInt(1, member_id);
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
            pstmt.setInt(1, member_id);
            pstmt.setString(2, member_name);
            pstmt.setString(3, member_pass);
            pstmt.setString(4, member_w);
            pstmt.executeUpdate();
%>
            <script>
                alert("회원가입이 완료되었습니다!");
                location.href = "login.jsp"; // 회원가입 후 로그인 페이지로 이동!
            </script>
<%
        }
    }
} catch (Exception e) {
    e.printStackTrace();
    // 오류 발생 시 사용자에게 알림
%>
    <script>
        alert("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
        history.back();
    </script>
<%
} finally {
    if (rs != null) try { rs.close(); } catch (Exception e) {}
    if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
    if (conn != null) try { conn.close(); } catch (Exception e) {}
}
%>

<!-- 중간(SignUp.jsp) 내용 시작 -->
<div class="main-content-wrapper"> 
    <div class="signup-container">
        <div class="avatar">
            <img src="https://cdn-icons-png.flaticon.com/512/847/847969.png" alt="user icon">
        </div>

        <form action="SignUp.jsp" method="post" class="signup-form">
            <div class="input-box">
                <i class="icon">&#128100;</i>
                <input type="text" name="member_id" placeholder="회원 아이디(숫자만 입력)" required />
            </div>

            <div class="input-box">
                <i class="icon">&#128100;</i>
                <input type="text" name="member_name" placeholder="회원 닉네임" required />
            </div>

            <div class="input-box">
                <i class="icon">&#128274;</i>
                <input type="password" name="member_pass" placeholder="비밀번호" required />
            </div>

            <div class="input-box">
                <i class="icon">&#127968;</i>
                <input type="text" name="member_w" placeholder="회원 주소" required/>
            </div>

            <button type="submit" class="signup-btn">회원가입</button>
        </form>

        <div class="options">
            <a href="login.jsp">이미 회원이신가요?</a>
        </div>
    </div>
</div>
<!-- 중간(SignUp.jsp) 내용 끝 -->

<%@ include file="inc/footer.jsp" %>