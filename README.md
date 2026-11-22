<!-- ✏️ 업로드 전에 TODO 표시(기간 · 팀 구성 · 담당 역할 · 스크린샷)를 채워 주세요 -->

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0f172a,50:03C75A,100:2563eb&height=200&section=header&text=Map%20%26%20Board&fontSize=56&fontColor=ffffff&fontAlignY=36&desc=JSP%20%C2%B7%20Servlet%20MVC2%20%EA%B2%8C%EC%8B%9C%ED%8C%90%20%2B%20%EB%84%A4%EC%9D%B4%EB%B2%84%20%EC%A7%80%EB%8F%84%20%EC%9B%B9%20%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8&descAlignY=60&descSize=17" width="100%"/>

![Java](https://img.shields.io/badge/Java_21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![JSP](https://img.shields.io/badge/JSP_·_Servlet-E34F26?style=for-the-badge&logo=java&logoColor=white)
![Tomcat](https://img.shields.io/badge/Tomcat_9-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)
![Oracle](https://img.shields.io/badge/Oracle_XE-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Naver Maps](https://img.shields.io/badge/NAVER_Maps_API-03C75A?style=for-the-badge&logo=naver&logoColor=white)

</div>

<br/>

## 📌 프로젝트 소개

로그인한 회원이 **게시판**에서 글을 쓰고 답글로 소통하며, 회원가입 때 등록한 **주소를 네이버 지도**로 바로 확인할 수 있는 JSP 웹 애플리케이션입니다.
Servlet 컨트롤러 · DAO/DTO · JSP 뷰로 역할을 나눈 **MVC2 패턴**과 Tomcat **DBCP(JNDI) 커넥션 풀**을 직접 구성해 보며 웹 백엔드의 기본 구조를 익혔습니다.

| 항목 | 내용 |
|---|---|
| 기간 | TODO (예: 2025.07 ~ 2025.08) |
| 팀 구성 | TODO (예: 팀 프로젝트 n명 / 개인 프로젝트) |
| 담당 역할 | TODO (예: 팀장 · 게시판 CRUD · 지도 연동) |
| 교육 과정 | 솔데스크 「자바 파이썬 챗GPT를 활용한 AI 개발자 양성 과정」 |

<br/>

## ✨ 주요 기능

| 기능 | 설명 |
|---|---|
| 🔐 **회원가입 · 로그인** | 회원 ID 중복 검사 후 가입, 로그인 성공 시 세션(`member_id`, `member_name`)에 저장. 로그아웃 시 세션 종료 |
| 🚪 **접근 제어** | 로그인하지 않은 사용자가 게시판·지도에 들어오면 로그인 페이지로 이동 |
| 📝 **게시판 CRUD** | 글쓰기, 목록, 상세 보기(조회수 증가), 수정, 삭제 |
| 🔑 **비밀번호 확인** | 글 작성 때 입력한 비밀번호가 일치해야 수정·삭제 가능 |
| 💬 **계층형 답글** | `ref`(원글 그룹), `re_step`(깊이), `re_level`(순서)로 답글을 원글 아래에 들여쓰기해서 표시 |
| 📄 **페이징** | 한 페이지 10개, 페이지 블록 10개 단위. Oracle `ROWNUM`으로 필요한 범위만 조회 |
| 🗺️ **내 주소 지도 보기** | 회원 주소 → 네이버 **Geocoding API**로 좌표 변환 → **Static Map API**로 마커가 찍힌 지도 이미지를 받아 Base64로 화면에 출력 |

<br/>

## 🖥️ 화면

<!-- TODO: 스크린샷을 docs/images 폴더에 넣고 아래 경로를 맞춰 주세요 -->
| 게시판 목록 | 게시글 상세 · 답글 | 내 주소 지도 |
|---|---|---|
| <img src="docs/images/board-list.png" alt="게시판 목록"/> | <img src="docs/images/board-info.png" alt="게시글 상세"/> | <img src="docs/images/map.png" alt="지도"/> |

<br/>

## 🛠️ 기술 스택

| 구분 | 기술 |
|---|---|
| **Language** | Java 21 |
| **Web** | JSP, Servlet (`@WebServlet`), JSTL 1.2 |
| **Server** | Apache Tomcat 9.0 (DBCP · JNDI `jdbc/xe`, UTF-8 인코딩 필터) |
| **Database** | Oracle XE (`ojdbc8`), 시퀀스 `board_seq` |
| **Library** | Lombok, org.json |
| **External API** | NAVER Cloud Maps (Geocoding, Static Map) |
| **IDE** | Eclipse (Dynamic Web Project) |

<br/>

## 🏗️ 구조 (MVC2)

```
 [Browser]
     │  *.do 요청
     ▼
 [Controller]  Servlet ─ BoardListCon / BoardInfoControl / BoardWriteProcCon
     │                   BoardUpdateCon·ProcCon / BoardDeleteCon·ProcCon
     │                   BoardReWriteCon·ProcCon
     ▼
 [Model]       BoardDAO (SQL 실행) ◀──▶ BoardDTO (데이터 전달)
     │              │
     │              └─ DBCP 커넥션 풀 (JNDI: java:/comp/env/jdbc/xe) ─▶ Oracle XE
     ▼
 [View]        BoardList.jsp · BoardInfo.jsp · Board*Form.jsp  (JSTL로 출력)

 [Map]         map.jsp ─▶ MapService ─▶ NAVER Geocoding API (주소 → x, y)
                                     └▶ NAVER Static Map API (좌표 → 지도 이미지)
```

```
s_Project/src/main
├── java
│   ├── controller/      # 게시판 Servlet 9개 (*.do)
│   ├── model/           # BoardDAO, BoardDTO
│   └── kr/soldesk/      # MapService (네이버 지도 API), AddressVO
└── webapp
    ├── index.jsp · login.jsp · loginProc.jsp · SignUp.jsp · logout.jsp
    ├── Board*.jsp       # 목록 · 상세 · 작성 · 수정 · 삭제 · 답글 폼
    ├── map.jsp          # 회원 주소 지도
    ├── inc/             # header · footer
    └── WEB-INF/         # web.xml, lib (jstl, ojdbc8, lombok, json)
```

<br/>

## 🗄️ 데이터베이스

코드의 SQL을 기준으로 정리한 테이블 구조입니다. (컬럼 타입은 예시)

```sql
CREATE TABLE login_user (
  member_id    NUMBER PRIMARY KEY,   -- 회원 고유번호 (로그인 ID)
  member_name  VARCHAR2(50),         -- 닉네임
  member_pass  VARCHAR2(100),        -- 비밀번호
  member_w     VARCHAR2(300)         -- 주소 (지도 표시에 사용)
);

CREATE TABLE board (
  num        NUMBER PRIMARY KEY,
  writer     VARCHAR2(50),
  email      VARCHAR2(100),
  subject    VARCHAR2(200),
  password   VARCHAR2(100),
  reg_date   DATE,
  ref        NUMBER,   -- 원글 그룹 번호
  re_step    NUMBER,   -- 답글 깊이 (들여쓰기)
  re_level   NUMBER,   -- 같은 그룹 안에서의 순서
  readcount  NUMBER DEFAULT 0,
  content    VARCHAR2(4000)
);
CREATE SEQUENCE board_seq;
```

### 💡 계층형 답글은 이렇게 동작해요

| num | 제목 | ref | re_step | re_level |
|---|---|---|---|---|
| 2 | 안녕 | 2 | 1 | 1 |
| 1 | 수요일 | 1 | 1 | 1 |
| 4 | └ [re] 댓글 | 1 | 2 | 2 |
| 3 | └ [re] 댓글 | 1 | 2 | 3 |

1. 답글을 쓰면 먼저 같은 `ref` 그룹에서 부모보다 `re_level`이 큰 글들을 한 칸씩 뒤로 밉니다.
2. 새 답글은 `re_step + 1`, `re_level + 1`로 저장합니다.
3. 목록은 `ORDER BY ref DESC, re_step ASC`로 조회하고, `re_step`만큼 들여쓰기해서 보여줍니다.

<br/>

## 🚀 실행 방법

1. Eclipse에서 **File → Import → Existing Projects into Workspace**로 `s_Project`를 불러옵니다.
2. Oracle XE에 위 SQL로 테이블과 시퀀스를 만듭니다.
3. Tomcat 9의 `context.xml`에 커넥션 풀을 등록합니다.
   ```xml
   <Resource name="jdbc/xe" auth="Container" type="javax.sql.DataSource"
             driverClassName="oracle.jdbc.OracleDriver"
             url="jdbc:oracle:thin:@localhost:1521:xe"
             username="계정" password="비밀번호" maxTotal="20" maxIdle="10"/>
   ```
4. NAVER Cloud Platform에서 Maps API 키를 발급받아 설정합니다.
5. Tomcat으로 실행하고 `http://localhost:8080/s_Project/index.jsp`에 접속합니다.

<br/>

## 🧩 배운 점과 개선할 점

**배운 점**
- 요청(Controller) · 데이터(Model) · 화면(View)을 나누면 기능을 추가하거나 고칠 위치가 분명해진다는 점
- 커넥션 풀(DBCP)을 쓰면 요청마다 DB 연결을 새로 만들지 않아도 된다는 점
- 외부 API 응답(JSON)을 파싱해서 다음 API 요청의 입력으로 이어 붙이는 흐름

**다음에 개선할 점** (이후 Spring Boot 프로젝트 [COMPILE](https://github.com/doyoung040257/Crypto_Project)에서 적용)
| 지금 | 개선 방향 |
|---|---|
| 비밀번호를 평문으로 저장·비교 | BCrypt 같은 해시로 저장 (Spring Security) |
| API 키가 코드에 직접 들어 있음 | 설정 파일 · 환경변수로 분리하고 `.gitignore`로 제외 |
| 로그인 · 회원가입 · 지도 로직이 JSP 안에 섞여 있음 | Servlet/Service 계층으로 옮겨 JSP는 화면만 담당 |
| `con.close()`가 일부 메서드에만 있음 | try-with-resources로 모든 연결을 자동 반환 |
| 로그인 확인 코드를 Servlet마다 반복 | 공통 `Filter` 하나로 접근 제어 |
| 목록 정렬이 `ref DESC, re_step ASC`라서 같은 깊이의 답글 순서가 보장되지 않음 | `ORDER BY ref DESC, re_level ASC`로 바꿔 `re_level` 순서대로 표시 |

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:2563eb,50:03C75A,100:0f172a&height=110&section=footer" width="100%"/>

</div>
