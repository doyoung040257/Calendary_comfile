<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="inc/header.jsp" %>
<link rel="stylesheet" href="./css/design.css" />
<c:choose>
			<c:when test="${empty sessionScope.member_id}">
				<h2>&nbsp;&nbsp;로그인을 해주세요.</h2>
			</c:when>
			<c:otherwise>
				<h2>&nbsp;&nbsp;${member_name }님 환영합니다</h2>
			</c:otherwise>
		</c:choose>

<%@ include file="inc/footer.jsp" %>
