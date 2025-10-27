drop table login_user;
create table login_user(
member_id number PRIMARY KEY, --회원 고유 번호
member_name  varchar2(100), --회원 이름
member_pass varchar2(50), --회원 비번
member_w varchar2(500) --회원 주소
);

SELECT * FROM login_user;

drop sequence board_seq;

create SEQUENCE board_seq
start with 1
increment by 1
minvalue 1
maxvalue 1000
nocache;

drop table board;
create table board(
num number primary key,
writer varchar2(20) not null,
email varchar2(50) not null,
subject varchar2(20) not null,
password varchar2(10) not null,
reg_date date,
ref number not null,
re_step number not null,
re_level number not null,
readcount number not null,
content varchar2(500) not null
);

SELECT * FROM board;