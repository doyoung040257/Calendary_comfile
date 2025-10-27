<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Board ReWrite</title>
<link rel="stylesheet" href="./css/design.css" />
<style>
h2 {
    text-align: center;
    color: #333;
}
</style>

</head>
<body>
   <div id="container">
      <b>게시글 입력하기</b>
      <p />
      <form action="BoardReWriteProcCon.do" method="post">
         <center>
            <table class="table table-striped table-bordered table-hover">
               <tbody>
                  <tr height="50">
                     <th width="150" align="center">작성자</th>
                     <td width="400" align="left">
                        <input type="text" name="writer" value=" ${member_name }" readonly size="60" style="background: transparent;"/>
                     </td>
                  </tr>
                  <tr height="50">
                     <th width="150" align="center">제목</th>
                     <td width="400" align="left">
                        <input type="text" name="subject" size="60" value="[답변]" style="background: transparent;"/>
                     </td>
                  </tr>
                  <tr height="50">
                     <th width="150" align="center">이메일</th>
                     <td width="400" align="left">
                        <input type="email" name="email" size="60" style="background: transparent;"/>
                     </td>
                  </tr>
                  <tr height="50">
                     <th width="150" align="center">비밀번호</th>
                     <td width="400" align="left">
                        <input type="password" name="password" size="60" style="background: transparent;"/>
                     </td>
                  </tr>
                  <tr height="50">
                     <th width="150" align="center">글내용</th>
                     <td width="400" align="left">
                        <textarea rows="10" cols="50" name="content"></textarea>
                     </td>
                  </tr>
                  <tr height="50">
                     <th align="center" colspan="2">
                        <input type="hidden" name="ref" value="${ref }"/>
                        <input type="hidden" name="re_step" value="${re_step }"/>
                        <input type="hidden" name="re_level" value="${re_level }"/>
                        <input type="submit" value="답글작성"/>&nbsp;&nbsp;
                        <input type="reset" value="다시작성"/>&nbsp;&nbsp;
                        <button type="button" onclick="location.href='BoardListCon.do'">목록보기</button>
                     </th>
                  </tr>
               
               </tbody>
            </table>
         </center>
      </form>
   </div>
</body>
</html>