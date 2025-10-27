package controller;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.BoardDAO;
import model.BoardDTO;


@WebServlet("/BoardListCon.do")
public class BoardListCon extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		reqPro(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		reqPro(request, response);
	}
	
	protected void reqPro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// ✅ 세션에서 로그인 여부 확인
        HttpSession session = request.getSession();
        String memberId = (String) session.getAttribute("member_id");

        if (memberId == null) {
            // 로그인 안 된 경우
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');");
            response.getWriter().println("location.href='login.jsp';");  // 로그인 페이지 경로 맞게 수정
            response.getWriter().println("</script>");
            return; // 이후 코드 실행 중단
        }
      
		
		// 총 게시글 수 저장
		int count =0;
		
		BoardDAO bdao = new BoardDAO();
		count = bdao.getallCount();
		
		
		//수정, 삭제시 받아오는 메시지
		String msg = (String)request.getAttribute("msg");
		
		//한 페이지에 보여줄 게시글 수
		int pageSize=10;
		
		String pageNum = request.getParameter("pageNum");
		if(pageNum == null) {
			pageNum = "1";
		}
		
		int number = 0;
		int currentPage = Integer.parseInt(pageNum);
		
		//페이지 시작번호, 페이지 끝번호
		//currentPage 1 -> 1~10번 게시글
		//currentPage 2 -> 11~20번 게시글
		int startRow = (currentPage -1 )*pageSize+1;
		int endRow = currentPage * pageSize;
		
		//11개의 글일 때 11 - (2-1)*10=1
		number = count - (currentPage -1 )*pageSize;
		
		Vector<BoardDTO> v = bdao.getAllBoard(startRow, endRow);
		
		request.setAttribute("count", count);
		request.setAttribute("msg", msg);
		request.setAttribute("v", v);
		request.setAttribute("pageSize", pageSize);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("number", number);
		
		RequestDispatcher dis = request.getRequestDispatcher("BoardList.jsp");
		dis.forward(request, response);
		
		
	}

}
