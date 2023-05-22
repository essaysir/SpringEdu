package com.spring.board.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

// === #52. VO 생성하기 === // 
// 		먼저, 오라클에서 tbl_board 테이블을 생성해야 한다.
@Data
public class BoardVO {
	   private String seq;          // 글번호 
	   private String fk_userid;    // 사용자ID
	   private String name;         // 글쓴이 
	   private String subject;      // 글제목
	   private String content;      // 글내용 
	   private String pw;           // 글암호
	   private String readCount;    // 글조회수
	   private String regDate;      // 글쓴시간
	   private String status;       // 글삭제여부   1:사용가능한 글,  0:삭제된글 
	   
	   private String previousseq;      // 이전글번호
	   private String previoussubject;  // 이전글제목
	   private String nextseq;          // 다음글번호
	   private String nextsubject;      // 다음글제목   
	
	   // === #81. 댓글형 게시판을 위한 commentCount 필드 추가하기 
	   //          먼저 tbl_board 테이블에 commentCount 라는 컬럼이 존재해야 한다.
	   private String commentCount ; 	 // 댓글 수 
	   
	// === #137. 답변글쓰기 게시판을 위한 필드 추가하기
	   //     먼저, 오라클에서 tbl_comment 테이블과  tbl_board 테이블을 drop 한 이후에 
	   //     tbl_board 테이블 및 tbl_comment 테이블을 재생성 한 이후에 아래처럼 해야 한다.
	   private String groupno;
	   /*
	         답변글쓰기에 있어서 그룹번호 
	              원글(부모글)과 답변글은 동일한 groupno 를 가진다.
	              답변글이 아닌 원글(부모글)인 경우 groupno 의 값은 groupno 컬럼의 최대값(max)+1 로 한다.
	    */
	   
	   private String fk_seq;
	   /*
	       fk_seq 컬럼은 절대로 foreign key가 아니다.!!!!!!
	        fk_seq 컬럼은 자신의 글(답변글)에 있어서 
	                원글(부모글)이 누구인지에 대한 정보값이다.
	                답변글쓰기에 있어서 답변글이라면 fk_seq 컬럼의 값은 
	                원글(부모글)의 seq 컬럼의 값을 가지게 되며,
	                답변글이 아닌 원글일 경우 0 을 가지도록 한다. 
	    */
	   
	   private String depthno;
	   /*
	          답변글쓰기에 있어서 답변글 이라면
	               원글(부모글)의 depthno + 1 을 가지게 되며,
	               답변글이 아닌 원글일 경우 0 을 가지도록 한다. 
	   */
	   /*
	      === #152. 파일을 첨부하도록 VO 수정하기
	                먼저, 오라클에서 tbl_board 테이블에 3개 컬럼(fileName, orgFilename, fileSize)을 추가한 다음에 아래의 작업을 한다. 
	   */
	   private MultipartFile attach;
	   /* form 태그에서 type="file" 인 파일을 받아서 저장되는 필드이다. 
	         진짜파일 ==> WAS(톰캣) 디스크에 저장됨.
	             조심할것은 MultipartFile attach 는 오라클 데이터베이스 tbl_board 테이블의 컬럼이 아니다.   
	      /Board/src/main/webapp/WEB-INF/views/tiles1/board/add.jsp 파일에서 input type="file" 인 name 의 이름(attach)과  
	        동일해야만 파일첨부가 가능해진다.!!!!
	 */
	   private String fileName;    // WAS(톰캣)에 저장될 파일명(2022103109271535243254235235234.png) 
	   private String orgFilename; // 진짜 파일명(강아지.png)  // 사용자가 파일을 업로드 하거나 파일을 다운로드 할때 사용되어지는 파일명
	   private String fileSize;    // 파일크기 
	   
	 
	   
}
