<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="inc/header.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Board List</title>
<link rel="stylesheet" href="./css/design.css" />
</head>
<body>
<div id="container">
	<c:if test="${msg == 0 }">
		<script>
			alert("수정시 비밀번호가 일치 하지 않습니다.");
		</script>
	</c:if>
	<c:if test="${msg == 1 }">
		<script>
			alert("삭제시 비밀번호가 일치 하지 않습니다.");
		</script>
	</c:if>
<%--    
   <div id="list">
      <b>게시판(전체글 : ${count } )</b>
   </div>
    --%>
   <div id="write">
      <a href="BoardWriteForm.jsp" style="text-decoration: none">글쓰기</a>
   </div>
   <div>
      <table class="table table-striped table-bordered table-hover">
         <thead>
            <tr height="40">
               <th width="150">번호</th>
               <th width="500">제목</th>
               <th width="150">작성자</th>
               <th width="150">작성일</th>
               <th width="150">조회</th>
            </tr>
         </thead>
         <tbody>
         	<c:set var="number" value="${number }"/>
        	<c:forEach var="bean" items="${v }">
               <tr height="40">
                  <td width="50" align="center">${number }</td> 
                  
                  <td width="300" align="left">
                     <c:if test="${bean.re_step>1 }"> 
                        <c:forEach var="j" begin="1" end="${(bean.re_step-1)*5 }">
                           &nbsp;
                        </c:forEach>
                     </c:if>
                     
                     <a href="BoardInfoControl.do?num=${bean.num }">
                        ${bean.subject }
                     </a>
                  </td>
              <td width="100" align="center">${bean.writer }</td>
                            <td width="150" align="center">${bean.reg_date }</td>
                            <td width="150" align="center">${bean.readcount }</td>
               </tr>
               <c:set var="number" value="${number-1 }" /> 
            </c:forEach>
         </tbody>
      </table>
      <p></p>
      <!-- 페이징처리 구현 -->
       <center>
         <c:if test="${count>0 }">
            <c:set var = "pageCount" value="${count/pageSize + (count%pageSize==0? 0:1) }" /> 
            <!-- 시작 페이지 숫자 지정 -->
               <c:set var="startPage" value="1" />
               
               <c:if test="${currentPage%10 != 0 }">  
                  <fmt:parseNumber var="result" value="${currentPage/10 }" integerOnly="true"/> 
                  <c:set var="startPage" value="${result*10+1 }" /> 
               </c:if>              
               
               <!-- 한 번에 보여줄 페이지 블록 10개의 게시글 -->
               <c:set var="pageBlock" value="10" />
               <c:set var="endPage" value="${startPage+pageBlock-1 }" />                 
               <c:if test="${endPage>pageCount }">
                  <c:set var="endPage" value="${pageCount}" />
               </c:if>
               
               <!-- 이전 페이지로 이동 -->
               <c:if test="${startPage>10 }">
                  <a href="BoardListCon.do?pageNum=${startPage-10 }" style="text-decoration:none">
                     [이전]
                  </a>
               </c:if>
               
               
               <c:forEach var="i" begin="${startPage }" end="${endPage }">
                  <a href="BoardListCon.do?pageNum=${i }" style="text-decoration: none">
                     [${i }]
                  </a>
               </c:forEach>
               
               <!-- 다음 페이지로 이동 -->
               <c:if test="${endPage<pageCount }">
                  <a href="BoardListCon.do?pageNum=${startPage+10 }" style="text-decoration:none">
                     [다음]
                  </a>
               </c:if>
         
         </c:if>
      </center>
</div>
</div>
</body>
</html>
<%@ include file="inc/footer.jsp" %>