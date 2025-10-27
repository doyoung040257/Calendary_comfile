<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="inc/header.jsp" %>

<!-- 중간(login.jsp) 내용 시작 -->
<div class="main-content-wrapper"> 
    <div class="login-container">
        <div class="avatar">
            <img src="https://cdn-icons-png.flaticon.com/512/847/847969.png" alt="user icon">
        </div>

        <form action="loginProc.jsp" method="post" class="login-form">
            <div class="input-box">
                <i class="icon">&#128100;</i> 
                <input type="text" name="member_id" placeholder="아이디(고유번호)" required>
            </div>
            <div class="input-box">
                <i class="icon">&#128274;</i>
                <input type="password" name="member_pass" placeholder="비밀번호" placeholder="비밀번호" required>
            </div>
            <button type="submit" class="login-btn">로그인</button>
        </form>


        <div class="options">
            <a href="SignUp.jsp">혹시 처음 가입하셨나요?</a>
        </div>
    </div>
</div>
<%@ include file="inc/footer.jsp" %>