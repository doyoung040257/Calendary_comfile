<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Board Update</title>
<link rel="stylesheet" href="./css/design.css" />
<style>

h2, b {
    color: #333;
}
</style>

</head>
<body>
    <div id="container">
        <b>게시글 수정하기</b>
        <p />
        <form action="BoardUpdateProcCon.do" method="post">
            <center>
                <table class="table table-striped table-bordered table-hover">
                    <tbody>
                        <tr height="50">
                            <th width="150" align="center">작성자</th>
                            <td width="400" align="left">
                                ${bean.writer}
                            </td>
                        </tr>
                        <tr height="50">
                            <th width="150" align="center">작성일</th>
                            <td width="400" align="left">
                                ${bean.reg_date}
                            </td>
                        </tr>
                        <tr height="50">
                            <th width="150" align="center">제목</th>
                            <td width="400" align="left">
                                <input type="text" name="subject" value="${bean.subject}" size="60" style="background: transparent;"/>
                            </td>
                        </tr>
                        <tr height="50">
                            <th width="150" align="center">패스워드</th>
                            <td width="400" align="left">
                                <input type="password" name="pass" size="60" style="background: transparent;"/>
                            </td>
                        </tr>
                        <tr height="50">
                            <th width="150" align="center">글내용</th>
                            <td width="400" align="left">
                                <textarea name="content" rows="10" cols="50">${bean.content}</textarea>
                            </td>
                        </tr>
                        <tr height="50">
                            <th align="center" colspan="2">
                                <input type="hidden" name="num" value="${bean.num}"/>
                                <input type="hidden" name="password" value="${bean.password}"/>
                                <input type="submit" value="수정하기"/>&nbsp;&nbsp;
                                <input type="reset" value="취소"/>&nbsp;&nbsp;
                                <button type="button" onclick="location.href='BoardListCon.do'">전체글보기</button>
                            </th>
                        </tr>
                    </tbody>
                </table>
            </center>
        </form>
    </div>
</body>
</html>
