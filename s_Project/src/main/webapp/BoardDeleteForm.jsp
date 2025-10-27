<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Board Delete</title>
<link rel="stylesheet" href="./css/design.css" />
<style>
input[type="text"],
input[type="email"],
input[type="password"],
textarea {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box;
}
textarea {
  resize: vertical;
  min-height: 120px;
}

</style>

</head>
<body>
  <div id="container">
    <h2>&nbsp;&nbsp;&nbsp;게시글 삭제하기</h2>
    <form action="BoardDeleteProcCon.do" method="post">
      <table class="table table-striped table-bordered table-hover">
        <tr>
          <th>패스워드</th>
          <td colspan="3">
            <input type="password" name="password" size="60"
                   style="border: none; background: transparent">
          </td>
        </tr>
        <tr>
          <td colspan="4" class="text-center">
            <input type="hidden" name="num" value="${bean.num }">
            <input type="hidden" name="pass" value="${bean.password }">

            <input type="submit" value="글삭제" />
            <button type="reset">취소</button>
            <button type="button" onclick="location.href='BoardListCon.do'">전체글보기</button>
          </td>
        </tr>
      </table>
    </form>
  </div>
</body>
</html>
