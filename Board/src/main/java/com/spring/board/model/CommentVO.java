package com.spring.board.model;

import lombok.Data;

@Data
public class CommentVO {
	   private String seq;          // 댓글번호
	   private String fk_userid;    // 사용자ID
	   private String name;         // 성명
	   private String content;      // 댓글내용
	   private String regDate;      // 작성일자
	   private String parentSeq;    // 원게시물 글번호
	   private String status;       // 글삭제여부

	  
}
