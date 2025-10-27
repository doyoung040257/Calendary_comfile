<!--  첫 번째 수정안 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>지도 프로젝트</title>

<%-- 외부 CSS 파일들을 여기에 모두 링크해주는 게 제일 좋아! --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/SignUp.css" />


<style>

    nav {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #4A3F35;
        padding: 12px 40px;
        box-shadow: 0 2px 6px rgba(0,0,0,0.1);
    }

    /* 왼쪽 메뉴 */
    .nav-links a {
        color: #fff;
        text-decoration: none;
        font-weight: 600;
        margin-right: 25px;
        font-size: 15px;
        transition: color 0.2s, transform 0.2s;
    }

    .nav-links a:hover {
        color: #89D9D6; /* hover 색상 변경: 좀 더 시원하고 밝은 색으로! */
        transform: translateY(-2px);
    }

    /* 오른쪽 로그인/로그아웃 */
    .nav-user a {
        color: #fff;
        text-decoration: none;
        font-weight: 500;
        background-color: rgba(255,255,255,0.15);
        padding: 6px 14px;
        border-radius: 20px;
        transition: background 0.3s;
        margin-left: 10px; /* 링크 사이 간격 추가 */
    }

    .nav-user a:hover {
        background-color: rgba(255,255,255,0.3);
    }

    /* 사용자 이름 강조 */
    .nav-user span {
        font-weight: 700;
        color:#ffffff; /* 이름 강조색을 노란색 계열로 바꿔봤어! 더 눈에 띌 거야! */
    }

    /* 새로 추가할 컨텐츠 래퍼 스타일 (모든 로그인/회원가입 폼 등을 감쌀 거야!) */
    .main-content-wrapper {
        min-height: calc(100vh - 60px); /* 화면 전체 높이 - (네비 높이 + 푸터 높이 예상치) */
/*         display: flex;
        justify-content: center; /* 가로 가운데 정렬 */
        align-items: center; /* 세로 가운데 정렬 */ */
        padding: 20px;
        box-sizing: border-box; /* 패딩이 높이에 포함되도록! */
    }
</style>
</head>
<body>
    <nav>
        <div class="nav-links">
            <a href="index.jsp">홈</a>
            <a href="BoardListCon.do">게시판</a>
            <a href="map.jsp">지도</a> <%-- 지도 페이지 링크 추가 --%>
            <a href="concertList.jsp">콘서트/연극</a>
        </div>

        <div class="nav-user">
            <c:choose>
                <c:when test="${empty sessionScope.member_id}">
                    <a href="login.jsp">로그인</a> <%-- login.jsp로 바로 이동 --%>
                    <a href="SignUp.jsp">회원가입</a> <%-- 회원가입 링크 추가! --%>
                </c:when>
                <c:otherwise>
                    <a href="logout.jsp">로그아웃</a>
                    <a href="Mypage.jsp">마이페이지 (<span>${sessionScope.member_name}</span>)</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>
    <%-- ★★★ 여기서는 <body>와 <html> 태그를 닫지 않는다! ★★★ --%>
    <%-- 이 뒤에 login.jsp나 SignUp.jsp의 내용이 들어간다 --%>