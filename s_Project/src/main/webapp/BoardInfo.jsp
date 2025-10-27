<%@page import="model.BoardDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8">
<title>Board Info</title>
<link rel="stylesheet" href="./css/design.css" />

</head>
<body>
<div id="container" align="center">
      <h2>게시글 보기</h2>
      <table class="table table-striped table-bordered table-hover" width="600">
         <tr height="40">
            <th width="120" align="center">글번호</th>
            <td width="180" align="left">${bean.num }</td>
            <th width="120" align="center">조회수</th>
            <td width="180" align="left">${bean.readcount }</td>
         </tr>
         <tr height="40">
            <th width="120" align="center">작성자</th>
            <td width="180" align="left">${bean.writer }</td>
            <th width="120" align="center">작성일</th>
            <td width="180" align="left">${bean.reg_date }</td>
         </tr>
         <tr height="40">
            <th width="120" align="center">제목</th>
            <td width="180" align="left" colspan="3">${bean.subject }</td>
         </tr>
         <tr height="40">
            <th width="120" align="center">글내용</th>
            <td width="180" align="left" colspan="3">${bean.content }</td>
         </tr>
         <tr height="40">
            <th align="center" colspan="4">
               <input type="button" value="답글달기"  onclick="location.href='BoardReWriteCon.do?num=${bean.num }&ref=${bean.ref }&re_step=${bean.re_step }&re_level=${bean.re_level}'"/>&nbsp;
               <input type="button" value="글수정" onclick="location.href='BoardUpdateCon.do?num=${bean.num }'"/>&nbsp;
               <input type="button" value="글삭제" onclick="location.href='BoardDeleteCon.do?num=${bean.num }'"/>&nbsp;
               <input type="button" value="목록보기" onclick="location.href='BoardListCon.do'"/>&nbsp;
            </th>
         </tr>
      </table>
   </div>
   
</body>
</html>