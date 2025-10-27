<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<sql:query var="rs" dataSource="jdbc/xe">
		select * from login_user
	</sql:query>
	<h2>DB에 회원 정보가 잘 저장됬는지 확인</h2>
	<hr />
	<c:forEach var="row" items="${rs.rows }">
		<p>
			회원 아이디(고유 번호) : ${row.member_id } <br />
			회원 닉네임 : ${row.member_name } <br />
			회원 비번 : ${row.member_pass } <br />
			회원 주소 : ${row.member_w } <br />
		</p>
	</c:forEach>
</body>
</html>