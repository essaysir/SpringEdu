----- **** Spring 기초 **** -----
show user ;
-- USER이(가) "MYMVC_USER"입니다.

create table spring_test
(no     number
,name   varchar2(100)
,writeday date default sysdate ) ;

-- Table SPRING_TEST이(가) 생성되었습니다.

select no , name , to_char(writeday , 'yyyy-mm-dd hh24:mi:ss' ) as writeday 
from spring_test 
order by writeday desc ;

select no , name 
from spring_test

delete from spring_test;
commit;

-----------------------------------------------------------------------------------

select * from tab ;
select * from TBL_MAIN_IMAGE ;
delete from TBL_MAIN_IMAGE where imgno>4 ;
commit;

select imgfilename
from TBL_MAIN_IMAGE
order by imgno desc ; 


select *
from tbl_member
where userid = '' and pwd = '' 

SELECT userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender , birthday 
						        , birthyyyy ,birthmm , birthdd , coin, point, registerday, pwdchangegap  
						        , nvl(lastlogingap , trunc( months_between(sysdate, registerday) , 0 )) AS lastlogingap 
						          FROM  
						 ( 
						select userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender , birthday 
						      , substr(birthday,1,4) AS birthyyyy , substr(birthday,6,2) AS birthmm , substr(birthday ,9,2) AS birthdd  
						      , coin , point , to_char(registerday, 'yyyy-mm-dd') AS registerday 
						      , trunc(months_between(sysdate,lastpwdchangedate) ,0) AS pwdchangegap 
						      from tbl_member   
						 where status = 1 and userid = 'leess' and pwd = '9695b88a59a1610320897fa84cb7e144cc51f2984520efb77111d94b402a8382'
						 )M 
						 CROSS JOIN 
						 ( 
						 select trunc( months_between(sysdate, max(logindate)) , 0 ) AS lastlogingap  
						 from tbl_loginhistory  
						 where fk_userid = 'leess' 
                         )H 
                         
select  * 
from tbl_member
where userid = 'iyou1' ;

select *
from tbl_loginhistory
where fk_userid = 'iyou1' ;

update tbl_member set registerday = add_months(sysdate , -12 ) , lastpwdchangedate = add_months(sysdate , -12 )
where userid = 'iyou1' ;
commit ;

update  tbl_member set idle = 0 
where userid = 'admin' or userid = 'leess';
commit ;

 where userid = 'eomjh' ;
commit ;
select * from tbl_loginhistory ;

						 select trunc( months_between(sysdate, max(logindate)) , 0 ) AS lastlogingap  
						 from tbl_loginhistory  
						 where fk_userid = 'kangkc' 


    ------- **** spring 게시판(답변글쓰기가 없고, 파일첨부도 없는) 글쓰기 **** -------

show user;
-- USER이(가) "MYMVC_USER"입니다.    
    
    
desc tbl_member;

create table tbl_board
(seq         number                not null    -- 글번호
,fk_userid   varchar2(20)          not null    -- 사용자ID
,name        varchar2(20)          not null    -- 글쓴이 
,subject     Nvarchar2(200)        not null    -- 글제목
,content     Nvarchar2(2000)       not null    -- 글내용   -- clob (최대 4GB까지 허용) 
,pw          varchar2(20)          not null    -- 글암호
,readCount   number default 0      not null    -- 글조회수
,regDate     date default sysdate  not null    -- 글쓴시간
,status      number(1) default 1   not null    -- 글삭제여부   1:사용가능한 글,  0:삭제된글
,constraint PK_tbl_board_seq primary key(seq)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_board_status check( status in(0,1) )
);
insert into ( seq ,  fk_userid , name , subject , content , pw )
					values ( boardSeq.nextval() , 'sonjs4' , '손주선'  , '테스트' , '테스트실험합니다' , 1234 )
--- 

insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status)
values ( boardSeq.nextval() , 'sonjs4' , '손주선'  , '테스트' , '테스트실험합니다' , 1234 , default , default ,)

create sequence boardSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;



select *
from tbl_board
order by seq desc;

select seq , fk_userid , name , subject , content , readcount , to_char(regdate , 'yyyy-mm-dd hh24:mi:ss' ) as regdate
from tbl_board
where status = 1
order by seq desc ;


---- 오류가 생기는 부분 Check ----
select lag(seq, 1) over(order by seq desc ) as previousseq,
lag(subject, 1) over(order by seq desc ) as previoussubject,
seq , fk_userid , name , subject , content , readcount 
, to_char(regdate , 'yyyy-mm-dd hh24:mi:ss' ) as regdate , pw  
, lead ( seq , 1 ) over ( order by seq desc ) as nextseq 
, lead ( subject , 1 ) over ( order by seq desc ) as nextsubject 
from tbl_board
where seq = 2 and status = 1  
-------------------------------------------------------------------------

select previousseq , previoussubject , seq , fk_userid , name , subject , content , readcount 
, regdate , pw , nextseq , nextsubject
from
(
select lag(seq, 1) over(order by seq desc ) as previousseq,
lag(subject, 1) over(order by seq desc ) as previoussubject,
seq , fk_userid , name , subject , content , readcount 
, to_char(regdate , 'yyyy-mm-dd hh24:mi:ss' ) as regdate , pw  
, lead ( seq , 1 ) over ( order by seq desc ) as nextseq 
, lead ( subject , 1 ) over ( order by seq desc ) as nextsubject 
from tbl_board
where status = 1  
) V 
where V.seq = 1 ;



update tbl_board set readcount = readcount + 1 where seq  = 1 ;
commit ;

update tbl_board set subject = '호호호' , content = '하하하 헤헤헤'
where seq = 3 and pw = 
delete from tbl_board where seq = 1 ;

rollback ;


-------------------------------------------------------------------------------

    ---- *** 댓글 게시판 *** ----
    
    -- 댓글 쓰기 ( tbl_comment 테이블)를 성공하면 원게시물(tbl_board 테이블) 에
    -- 댓글의 갯수( 1개씩 증가) 를 알려주는 컬럼 commentCount 을 추가하겠다. 
drop table tbl_board purge;

create table tbl_board
(seq           number                not null    -- 글번호
,fk_userid     varchar2(20)          not null    -- 사용자ID
,name          varchar2(20)          not null    -- 글쓴이 
,subject       Nvarchar2(200)        not null    -- 글제목
,content       Nvarchar2(2000)       not null    -- 글내용   -- clob (최대 4GB까지 허용) 
,pw            varchar2(20)          not null    -- 글암호
,readCount     number default 0      not null    -- 글조회수
,regDate       date default sysdate  not null    -- 글쓴시간
,status        number(1) default 1   not null    -- 글삭제여부   1:사용가능한 글,  0:삭제된글
,commentCount  number default 0      not null    -- 댓글의 개수
,constraint PK_tbl_board_seq primary key(seq)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_board_status check( status in(0,1) )
);

drop sequence boardSeq;

create sequence boardSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


----- **** 댓글 테이블 생성 **** -----
create table tbl_comment
(seq           number               not null   -- 댓글번호
,fk_userid     varchar2(20)         not null   -- 사용자ID
,name          varchar2(20)         not null   -- 성명
,content       varchar2(1000)       not null   -- 댓글내용
,regDate       date default sysdate not null   -- 작성일자
,parentSeq     number               not null   -- 원게시물 글번호
,status        number(1) default 1  not null   -- 글삭제여부
                                               -- 1 : 사용가능한 글,  0 : 삭제된 글
                                               -- 댓글은 원글이 삭제되면 자동적으로 삭제되어야 한다.
,constraint PK_tbl_comment_seq primary key(seq)
,constraint FK_tbl_comment_userid foreign key(fk_userid) references tbl_member(userid)
,constraint FK_tbl_comment_parentSeq foreign key(parentSeq) references tbl_board(seq) on delete cascade
,constraint CK_tbl_comment_status check( status in(1,0) ) 
);

create sequence commentSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

select *
from tbl_comment
order by seq desc;

select commentSeq.nextval
from dual ;

select *
from tbl_board
order by seq desc;

commit ;

-- ==== Transaction 처리를 위한 시나리오 만들기 ==== --
---- 회원들이 게시판에 글쓰기를 하면 글작성 1건당 POINT 를 100점을 준다.
---- 회원들이 게시판에서 댓글쓰기를 하면 댓글작성 1건당 POINT 를 50점을 준다.
---- 그런데 데이터베이스 오류 발생시 스프링에서 롤백해주는 Transaction 처리를 알아보려고 일부러 POINT 는 300을 초과할 수 없다고 하겠다.

select *
from tbl_member ;

update tbl_member set point = 0 ;
commit ;

-- tbl_member 테이블에 POINT 컬럼에 Check 제약을 추가한다.
alter table tbl_member
add constraint Ck_tbl_member_point check ( point between 0 and 300 ) ;
-- Table TBL_MEMBER이(가) 변경되었습니다.

update tbl_member set point = 301 
where userid = 'sonjs4';
-- ORA-02290: 체크 제약조건(MYMVC_USER.CK_TBL_MEMBER_POINT)이 위배되었습니다

update tbl_member set point = 300 
where userid = 'sonjs4';
rollback ;

select userid , point 
from tbl_member
where userid = 'sonjs4' ;

update tbl_member set point = 251 
where userid = 'sonjs4';
commit ;

select *
from tbl_comment
order by seq desc;

select *
from tbl_board
order by seq desc;

select name , content , to_char( regdate , 'yyyy-mm-dd hh24:mi:ss' ) AS regdate
from tbl_comment
where status = 1 and parentseq = 2 
order by seq desc ;

--- *** transaction 처리를 위해서 일부러 만들어 두었던 포인트 체크제약을 없애겠다. *** ---
--- *** tbl_member 테이블에 존재하는 제약조건 조회하기 *** ---
select *
from user_constraints
where table_name = 'TBL_MEMBER';

alter table tbl_member
drop constraint CK_TBL_MEMBER_POINT;
-- Table TBL_MEMBER이(가) 변경되었습니다.

select distinct name
from tbl_board
where status = 1 
and lower(name) like '%' || lower('정화') || '%' 
order by name asc;

select distinct name
from tbl_board
where status = 1 
and lower(name) like '%' || lower('정화') || '%' 
order by commentcount asc ;
-- select 문에서 distinct 와 order by 절을 함께 사용할때는 조심해야 한다.
-- order by 절에는 select 문에서 사용된 컬럼만 들어올 수가 있다.
-- 또는 order by 절을 사용하지 않아야 한다.
       
insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status)
values(boardSeq.nextval, 'sonjs4', '손주선', 'JSP 가 뭔가요?', '웹페이지를 작성하려고 합니다.', '1234', default, default, default);

insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status)
values(boardSeq.nextval, 'eomjh', '김정화', 'Korea VS Japan 라이벌 축구대결', '많은 시청 바랍니다.', '1234', default, default, default);       
commit ;

insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status)
values(boardSeq.nextval, 'eomjh', '엄정화', 'java가 재미 있나요?', '궁금합니다. JAVA가 뭔지요?', '1234', default, default, default);

begin
    for i in 1..100 loop
        insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status)
        values(boardSeq.nextval, 'leess', '이순신', '이순신 입니다'||i, '안녕하세요? 이순신'|| i ||' 입니다.', '1234', default, default, default);
    end loop;
end;


begin
    for i in 101..200 loop
        insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status)
        values(boardSeq.nextval, 'eomjh', '엄정화', '엄정화 입니다'||i, '안녕하세요? 엄정화'|| i ||' 입니다.', '1234', default, default, default);
    end loop;
end;

commit;

SELECT RNO ,  seq , fk_userid , name , subject , content , readcount , regdate, commentCount
FROM    
(
    select row_number() over (order by seq desc ) AS RNO  ,
    seq , fk_userid , name , subject , content , readcount , to_char(regdate , 'yyyy-mm-dd hh24:mi:ss' ) as regdate
    , commentCount
    from tbl_board
    where status = 1
    and lower (subject)  like '%' || lower( 'java' ) || '%' 
)V
WHERE RNO BETWEEN 1 AND 10 ;

select row_number() over(order by seq desc) AS rno,
seq, fk_userid, name, subject, readCount,
to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate, 
commentCount
from tbl_board
where status = 1 ;


--------------------------------------------------------------------------------------------
-- 검색한 글 내에서 이전 글 , 다음 글이 되도록 하는 SQL 문 
select previousseq , previoussubject , seq , fk_userid , name , subject , content , readcount 
, regdate , pw , nextseq , nextsubject
from
(
select lag(seq, 1) over(order by seq desc ) as previousseq,
lag(subject, 1) over(order by seq desc ) as previoussubject,
seq , fk_userid , name , subject , content , readcount 
, to_char(regdate , 'yyyy-mm-dd hh24:mi:ss' ) as regdate , pw  
, lead ( seq , 1 ) over ( order by seq desc ) as nextseq 
, lead ( subject , 1 ) over ( order by seq desc ) as nextsubject 
from tbl_board
where status = 1 and lower (subject)  like '%' || lower( 'java' ) || '%'    
) V 
where V.seq = 7 ;

delete from tbl_board 
where seq = 6 ;
commit ;



select name, content , regdate
from
(
select row_number() over (order by seq desc ) AS RNO  , parentSeq ,
name ,  content , to_char(regdate , 'yyyy-mm-dd hh24:mi:ss' ) as regdate
from tbl_comment
where status = 1 and parentSeq = 209 
) V 
where RNO BETWEEN 1 AND 5;

select ceil(count(*)/5) 
from tbl_comment
where status = 1 and parentSeq = 212; 

---------------------------------------------------------------------------------------------------------------------
       ------- **** 댓글 및 답변글 및 파일첨부가 있는 게시판 **** --------


--- **** 답변글쓰기는 일반회원은 불가하고 직원(관리파트)들만 답변글쓰기가 가능하도록 한다. **** ---

-- *** tbl_member 테이블에 gradelevel 이라는 컬럼을 추가하겠다. *** --
alter table tbl_member
add gradelevel number default 1;

-- *** 직원(관리자)들에게는 gradelevel 컬럼의 값을 10 으로 부여하겠다. gradelevel 컬럼의 값이 10 인 직원들만 답변글쓰기가 가능하다 *** --
update tbl_member set gradelevel = 10
where userid in('admin','sonjs4');

commit;

select *
from tbl_member
order by gradelevel desc;


drop table tbl_comment purge;
drop sequence commentSeq;
drop table tbl_board purge;
drop sequence boardSeq;


create table tbl_board
(seq           number                not null    -- 글번호
,fk_userid     varchar2(20)          not null    -- 사용자ID
,name          varchar2(20)          not null    -- 글쓴이 
,subject       Nvarchar2(200)        not null    -- 글제목
--,content     Nvarchar2(2000)       not null    -- 글내용
,content       clob                  not null    -- 글내용   CLOB(4GB 까지 저장 가능한 데이터 타입) 타입
,pw            varchar2(20)          not null    -- 글암호
,readCount     number default 0      not null    -- 글조회수
,regDate       date default sysdate  not null    -- 글쓴시간
,status        number(1) default 1   not null    -- 글삭제여부   1:사용가능한 글,  0:삭제된글
,commentCount  number default 0      not null    -- 댓글의 개수 

,groupno       number                not null    -- 답변글쓰기에 있어서 그룹번호 
                                                 -- 원글(부모글)과 답변글은 동일한 groupno 를 가진다.
                                                 -- 답변글이 아닌 원글(부모글)인 경우 groupno 의 값은 groupno 컬럼의 최대값(max)+1 로 한다.

,fk_seq         number default 0      not null   -- fk_seq 컬럼은 절대로 foreign key가 아니다.!!!!!!
                                                 -- fk_seq 컬럼은 자신의 글(답변글)에 있어서 
                                                 -- 원글(부모글)이 누구인지에 대한 정보값이다.
                                                 -- 답변글쓰기에 있어서 답변글이라면 fk_seq 컬럼의 값은 
                                                 -- 원글(부모글)의 seq 컬럼의 값을 가지게 되며,
                                                 -- 답변글이 아닌 원글일 경우 0 을 가지도록 한다.

,depthno        number default 0       not null  -- 답변글쓰기에 있어서 답변글 이라면
                                                 -- 원글(부모글)의 depthno + 1 을 가지게 되며,
                                                 -- 답변글이 아닌 원글일 경우 0 을 가지도록 한다.

,fileName       varchar2(255)                    -- WAS(톰캣)에 저장될 파일명(2022103109271535243254235235234.png)                                       
,orgFilename    varchar2(255)                    -- 진짜 파일명(강아지.png)  // 사용자가 파일을 업로드 하거나 파일을 다운로드 할때 사용되어지는 파일명 
,fileSize       number                           -- 파일크기  

,constraint PK_tbl_board_seq primary key(seq)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_board_status check( status in(0,1) )
);

create sequence boardSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


create table tbl_comment
(seq           number               not null   -- 댓글번호
,fk_userid     varchar2(20)         not null   -- 사용자ID
,name          varchar2(20)         not null   -- 성명
,content       varchar2(1000)       not null   -- 댓글내용
,regDate       date default sysdate not null   -- 작성일자
,parentSeq     number               not null   -- 원게시물 글번호
,status        number(1) default 1  not null   -- 글삭제여부
                                               -- 1 : 사용가능한 글,  0 : 삭제된 글
                                               -- 댓글은 원글이 삭제되면 자동적으로 삭제되어야 한다.
,constraint PK_tbl_comment_seq primary key(seq)
,constraint FK_tbl_comment_userid foreign key(fk_userid) references tbl_member(userid)
,constraint FK_tbl_comment_parentSeq foreign key(parentSeq) references tbl_board(seq) on delete cascade
,constraint CK_tbl_comment_status check( status in(1,0) ) 
);

create sequence commentSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


desc tbl_board;


begin
    for i in 1..100 loop
        insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status, groupno)
        values(boardSeq.nextval, 'leess', '이순신', '이순신 입니다'||i, '안녕하세요? 이순신'|| i ||' 입니다.', '1234', default, default, default, i);
    end loop;
end;


begin
    for i in 101..200 loop
        insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status, groupno)
        values(boardSeq.nextval, 'eomjh', '엄정화', '엄정화 입니다'||i, '안녕하세요? 엄정화'|| i ||' 입니다.', '1234', default, default, default, i);
    end loop;
end;

commit;

desc tbl_board ;

select *
from tbl_board
order by seq desc;


update tbl_board set subject = '문의드립니다. 자바가 뭔가요?'
where seq = 198;

update tbl_board set subject = '[문의드립니다]. 퍼킹 깃허브'
where seq = 195;

commit;

select nvl(max(groupno), 0)
from tbl_board;


select seq, fk_userid, name, subject, readCount, regDate, commentCount 
from 
(
   select row_number() over(order by seq desc) AS rno,
         seq, fk_userid, name, subject, readCount,
         to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate, 
         commentCount
   from tbl_board
   where status = 1
   --   and lower(subject) like '%'|| lower('정화') ||'%'
) V 
where rno between '1' and '10';


select seq, fk_userid, name, subject, readCount, regDate, commentCount, 
       groupno, fk_seq, depthno
from 
(
    select rownum AS rno,
           seq, fk_userid, name, subject, readCount, regDate, commentCount,
           groupno, fk_seq, depthno
    from 
    ( 
       select seq, fk_userid, name, subject, readCount,
              to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate, 
              commentCount,
              groupno, fk_seq, depthno 
        from tbl_board
        where status = 1
        --   and lower(subject) like '%'|| lower('정화') ||'%'
        start with fk_seq = 0
        connect by prior seq = fk_seq 
        order siblings by groupno desc, seq asc                   
    ) V 
) T 
where rno between '1' and '10';  -- 1페이지
-- order by 로 정렬할 경우는 select 되어진 모든 데이터를 가지고 정렬을 하는 것이고,
-- order siblings by 각 계층별로 정렬을 하는 것이다. 
-- order by 로만 정렬을 하게 되면 계층구조가 깨질수 있기 때문에 계층구조는 그대로 유지하면서 동일 부모를 가진 자식행들 끼리의 정렬 기준을 주는 것이 order siblings by 이다.

 


---- **** 댓글쓰기에 파일첨부까지 한 것 **** -----
alter table tbl_comment
add fileName varchar2(255); -- WAS(톰캣)에 저장될 파일명(2020120809271535243254235235234.png)

alter table tbl_comment
add orgFilename varchar2(255); -- 진짜 파일명(강아지.png)  // 사용자가 파일을 업로드 하거나 파일을 다운로드 할때 사용되어지는 파일명  

alter table tbl_comment
add fileSize number;   -- 파일크기 

select *
from tbl_comment
where userid = 'sonjs4';

desc tbl_comment ;

select seq, name, content, to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate
     , nvl(fileName,' ') AS fileName
     , nvl(orgFilename, ' ') AS orgFilename
     , nvl(to_char(fileSize), ' ') AS fileSize 
from tbl_comment
where parentSeq = 201
order by seq desc;


select *
from tbl_board
order by seq desc;

------------------------------------------------------------------------------
-- >> tbl_board 테이블에서 content 컬럼의 데이터타입을 CLOB 타입으로 변경하기 << --

create table tbl_board_copy
as
select *
from tbl_board;
-- Table TBL_BOARD_COPY이(가) 생성되었습니다.

desc tbl_board;
-- CONTENT      NOT NULL NVARCHAR2(2000)

-- >> CLOB(4GB 까지 저장 가능한 데이터 타입) 타입을 가지는 새로운 컬럼 추가하기 << --
alter table tbl_board
add imsi_content clob;
-- Table TBL_BOARD이(가) 변경되었습니다.


-- >> 데이터를 복사하기 << --
update tbl_board set imsi_content = content;
-- 210개 행 이(가) 업데이트되었습니다.
commit;
-- 커밋 완료.

-- >> 기존 컬럼 삭제하기 << --
alter table tbl_board 
drop column content;
-- Table TBL_BOARD이(가) 변경되었습니다.

-- >> 새로 추가한 컬럼의 이름을 변경하기 << --
alter table tbl_board
rename column imsi_content to content;

-- >> 새로 추가한 컬럼에 NOT NULL 로 설정하기 << --
alter table tbl_board
modify content not null;

desc tbl_board;
-- CONTENT      NOT NULL CLOB 

select *
from tbl_board
order by seq desc;

select seq, content, length(content)
from tbl_board
order by seq desc;

delete from tbl_board
where seq = 215;

commit;
-----------------------------------------------------------

select * from spring_test ;

delete from table_name ;

--------------------------------- 인사관리 --------------------------------------------
show user ; 

select *
from employees ;

select distinct (nvl( department_id , -9999 )) as department_id  from employees
order by department_id asc ;

			select E.department_id , D.department_name , E.employee_id , 
			       E.first_name || '' || E.last_name AS fullname , 
			       to_char(E.hire_date , 'yyyy-mm-dd' ) AS hire_date , 
			       nvl (E.salary + E.salary* e.commission_pct , E.salary ) as monthsal ,
			       func_gender (E.jubun) AS gender ,
			       func_age (E.jubun) AS age
			from employees E left join departments D
			on E.department_id = D.department_id
            where 1=1 
            and nvl(E.department_id ,-9999) in ('-9999','30') 
			and func_gender(E.jubun) = '여' 
            order by E.department_id , E.employee_id
			
select E.department_id , D.department_name , E.employee_id , 
			       E.first_name || '' || E.last_name AS fullname , 
			       to_char(E.hire_date , 'yyyy-mm-dd' ) AS hire_date , 
			       nvl (E.salary + E.salary* e.commission_pct , E.salary ) as monthsal ,
			         AS gender ,
			       func_age (E.jubun) AS age
			from employees E left join departments D
			on E.department_id = D.department_id
            where 1=1 and nvl(E.department_id ,-9999) in ('-9999','30') 
			order by E.department_id , E.employee_id
			
-------------------------------------------------------------------------------------------------------------

-- employees 테이블에서 부서명별 인원수 및 퍼센티지 가져오기 -- 
--- 부서별 
select nvl(D.department_name, '부서없음') as department_name , count(*) as cnt , round (count(*)/(select count(*) from employees )*100 , 2 ) as percent  
from employees E left join departments D
on E.department_id = D.department_id
group by D.department_name
order by cnt desc ;

--- 성별
select E.gender ,count(*) as cnt , round (count(*)/(select count(*) from employees )*100 , 2 ) as percent  
from 
(
select department_id  , func_gender( jubun ) as gender , jubun 
from employees 
)
E 
group by gender
order by cnt desc ;

-- 성별 입사년도별 인원수 알아오기  --
-- 내 풀이
select hire_date , E.gender ,count(*) as cnt , round (count(*)/(select count(*) from employees )*100 , 2 ) as percent  
from 
(
select department_id  , func_gender( jubun ) as gender , jubun , to_char(hire_date , 'yyyy' ) as hire_date
from employees 
)
E 
group by hire_date , gender
order by hire_date desc , cnt desc ;

-- 선생님 풀이  
select func_gender(jubun) AS gender ,
        , extract( year from hire_date ) 
from employees 
group by func_gender(jubun) 

select    extract( year from hire_date )
        , DECODE ( extract( year from hire_date ) , 2001 , 1 , 0 ) AS "Y2001"
        , DECODE ( extract( year from hire_date ) , 2002 , 1 , 0 ) AS "Y2002"
from employees 
where extract ( year from hire_date ) between 2001 and 2008


select func_gender(jubun) as gender  
       ,SUM ( DECODE ( extract( year from hire_date ) , 2001 , 1 , 0 )) AS "Y2001"
        , SUM ( DECODE ( extract( year from hire_date ) , 2002 , 1 , 0 )) AS "Y2002"
         , SUM ( DECODE ( extract( year from hire_date ) , 2003 , 1 , 0 )) AS "Y2003"
          , SUM ( DECODE ( extract( year from hire_date ) , 2004 , 1 , 0 )) AS "Y2004"
           , SUM ( DECODE ( extract( year from hire_date ) , 2005 , 1 , 0 )) AS "Y2005"
            , SUM ( DECODE ( extract( year from hire_date ) , 2006 , 1 , 0 )) AS "Y2006"
             , SUM ( DECODE ( extract( year from hire_date ) , 2007 , 1 , 0 )) AS "Y2007"
              , SUM ( DECODE ( extract( year from hire_date ) , 2008 , 1 , 0 )) AS "Y2008"
from employees 
where extract ( year from hire_date ) between 2001 and 2008
group by func_gender(jubun) 
order by 1 ;



desc employees; 

-- 특정부서명에 근무하는 직원들에 대해서 성별에 따른 인원수 및 퍼센티지 알아오기 --
select func_gender(jubun) AS gender ,
        count (*) as count , 
         round (count(*)/(select count(*) from employees )*100 , 2 ) as percent  
from employees E left join departments D
ON E.department_id = D.department_id 
where D.department_name = 'IT'
group by  func_gender(jubun)
order by gender ; 



------------- >>>>>>>> 일정관리(풀캘린더) 시작 <<<<<<<< -------------

-- *** 캘린더 대분류(내캘린더, 사내캘린더  분류) ***
create table tbl_calendar_large_category 
(lgcatgono   number(3) not null      -- 캘린더 대분류 번호
,lgcatgoname varchar2(50) not null   -- 캘린더 대분류 명
,constraint PK_tbl_calendar_large_category primary key(lgcatgono)
);
-- Table TBL_CALENDAR_LARGE_CATEGORY이(가) 생성되었습니다.

insert into tbl_calendar_large_category(lgcatgono, lgcatgoname)
values(1, '내캘린더');

insert into tbl_calendar_large_category(lgcatgono, lgcatgoname)
values(2, '사내캘린더');

commit;
-- 커밋 완료.

select * 
from tbl_calendar_large_category;


-- *** 캘린더 소분류 *** 
-- (예: 내캘린더중 점심약속, 내캘린더중 저녁약속, 내캘린더중 운동, 내캘린더중 휴가, 내캘린더중 여행, 내캘린더중 출장 등등) 
-- (예: 사내캘린더중 플젝주제선정, 사내캘린더중 플젝요구사항, 사내캘린더중 DB모델링, 사내캘린더중 플젝코딩, 사내캘린더중 PPT작성, 사내캘린더중 플젝발표 등등) 
create table tbl_calendar_small_category 
(smcatgono    number(8) not null      -- 캘린더 소분류 번호
,fk_lgcatgono number(3) not null      -- 캘린더 대분류 번호
,smcatgoname  varchar2(400) not null  -- 캘린더 소분류 명
,fk_userid    varchar2(40) not null   -- 캘린더 소분류 작성자 유저아이디
,constraint PK_tbl_calendar_small_category primary key(smcatgono)
,constraint FK_small_category_fk_lgcatgono foreign key(fk_lgcatgono) 
            references tbl_calendar_large_category(lgcatgono) on delete cascade
,constraint FK_small_category_fk_userid foreign key(fk_userid) references tbl_member(userid)            
);
-- Table TBL_CALENDAR_SMALL_CATEGORY이(가) 생성되었습니다.


create sequence seq_smcatgono
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_SMCATGONO이(가) 생성되었습니다.

 
select *
from tbl_calendar_small_category
order by smcatgono desc;


-- *** 캘린더 일정 *** 
create table tbl_calendar_schedule 
(scheduleno    number                 -- 일정관리 번호
,startdate     date                   -- 시작일자
,enddate       date                   -- 종료일자
,subject       varchar2(400)          -- 제목
,color         varchar2(50)           -- 색상
,place         varchar2(200)          -- 장소
,joinuser      varchar2(4000)         -- 공유자   
,content       varchar2(4000)         -- 내용   
,fk_smcatgono  number(8)              -- 캘린더 소분류 번호
,fk_lgcatgono  number(3)              -- 캘린더 대분류 번호
,fk_userid     varchar2(40) not null  -- 캘린더 일정 작성자 유저아이디
,constraint PK_schedule_scheduleno primary key(scheduleno)
,constraint FK_schedule_fk_smcatgono foreign key(fk_smcatgono) 
            references tbl_calendar_small_category(smcatgono) on delete cascade
,constraint FK_schedule_fk_lgcatgono foreign key(fk_lgcatgono) 
            references tbl_calendar_large_category(lgcatgono) on delete cascade   
,constraint FK_schedule_fk_userid foreign key(fk_userid) references tbl_member(userid) 
);
-- Table TBL_CALENDAR_SCHEDULE이(가) 생성되었습니다.

create sequence seq_scheduleno
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_SCHEDULENO이(가) 생성되었습니다.

select *
from tbl_calendar_schedule 
order by scheduleno desc;


-- 일정 상세 보기
select SD.scheduleno
     , to_char(SD.startdate,'yyyy-mm-dd hh24:mi') as startdate
     , to_char(SD.enddate,'yyyy-mm-dd hh24:mi') as enddate  
     , SD.subject
     , SD.color
     , nvl(SD.place,'-') as place
     , nvl(SD.joinuser,'공유자가 없습니다.') as joinuser
     , nvl(SD.content,'') as content
     , SD.fk_smcatgono
     , SD.fk_lgcatgono
     , SD.fk_userid
     , M.name
     , SC.smcatgoname
from tbl_calendar_schedule SD 
JOIN tbl_member M
ON SD.fk_userid = M.userid
JOIN tbl_calendar_small_category SC
ON SD.fk_smcatgono = SC.smcatgono
where SD.scheduleno = 21;

------------- >>>>>>>> 일정관리(풀캘린더) 끝 <<<<<<<< -------------

select * from tbl_calendar_small_category

------ ==== Spring Scheduler(스프링 스케줄러)를 사용한 email 자동 발송하기 ==== ------
show user;
-- USER이(가) "MYMVC_USER"입니다.

desc tbl_member;

create table tbl_reservation
(reservationSeq    number        not null
,fk_userid         varchar2(40)  not null
,reservationDate   date          not null
,mailSendCheck     number default 0 not null  -- 메일발송 했으면 1, 메일발송을 안했으면 0 으로 한다.
,constraint PK_tbl_reservation primary key(reservationSeq)
,constraint FK_tbl_reservation foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_reservation check(mailSendCheck in(0,1))
);


create sequence seq_reservation
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

select *
from tbl_member;

select userid, email
from tbl_member
where userid in('sonjs4','leess');

update tbl_member set email = (select email from tbl_member where userid = 's')
where userid = 'leess';

commit;

select to_char(sysdate, 'yyyy-mm-dd') AS 오늘날짜
from dual; -- 2023-06-01

insert into tbl_reservation(reservationSeq, fk_userid, reservationDate)
values(seq_reservation.nextval, 'sonjs4', to_date('2022-05-12 13:00','yyyy-mm-dd hh24:mi') );

insert into tbl_reservation(reservationSeq, fk_userid, reservationDate)
values(seq_reservation.nextval, 'leess', to_date('2022-05-12 14:00','yyyy-mm-dd hh24:mi') );

insert into tbl_reservation(reservationSeq, fk_userid, reservationDate)
values(seq_reservation.nextval, 'sonjs4', to_date('2022-05-13 11:00','yyyy-mm-dd hh24:mi') );

insert into tbl_reservation(reservationSeq, fk_userid, reservationDate)
values(seq_reservation.nextval, 'leess', to_date('2022-05-13 15:00','yyyy-mm-dd hh24:mi') );

commit;

select reservationSeq, fk_userid, 
       to_char(reservationDate, 'yyyy-mm-dd hh24:mi:ss') as reservationDate, 
       mailSendCheck
from tbl_reservation
order by reservationSeq desc;



-- *** 오늘로 부터 2일(이틀) 뒤에 예약되어진 회원들을 조회하기 *** --
select R.reservationSeq, M.userid, M.name, M.email, 
       to_char(R.reservationDate,'yyyy-mm-dd hh24:mi') as reservationDate
from tbl_member M join tbl_reservation R
on M.userid = R.fk_userid
where R.mailSendCheck = 0
and to_char(reservationDate, 'yyyy-mm-dd') = to_char(sysdate+2, 'yyyy-mm-dd');

/*
  update tbl_reservation set mailSendCheck = 1
  where reservationSeq IN ('1','2');
*/




















