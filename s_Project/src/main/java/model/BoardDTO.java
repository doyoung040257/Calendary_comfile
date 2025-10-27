package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDTO {
	
	private int num;
	private String writer;
	private String email;
	private String subject;
	private String password;
	private String reg_date;
	private int ref; //원글 번호
	private int re_step; //글 계층
	private int re_level;// 글 순서
	private int readcount;
	private String content;
	
	
}

/*
 				 ref re_step re_level
 2		안녕	  2		1		1 				
 1		수요일    1	    1	    1
 4		[re]댓글  1		2		2
 3		[re]댓글  1		2		3
 */
