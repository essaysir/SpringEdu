package com.spring.board.model;

import java.util.List;
import java.util.Map;

public interface InterBoardDAO {
		
		int test_insert(); // MyMVC_USER.spring_test 테이블에 insert 하기
									  // HR.spring_exam  테이블에 insert 하기

		List<TestVO> test_select();  // spring_test 테이블에 select 하기

		int test_register(Map<String, String> paraMap); // view 단의 form 태그에서 입력받은 값을 Map에 넣어서 spring_test 테이블에 insert 하기 

		int test_register(TestVO tvo); // view 단의 form 태그에서 입력받은 값을  VO에 넣어서 spring_test 테이블에 insert 하기 

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//  기초 끝 !! 게시판 시작 !! 
		
		List<String> getImgfilenameList(); // 	// 시작페이지에서 메인 이미지를 보여주는 것 

		MemberVO getLoginMember(Map<String, String> paraMap); // 로그인 처리하기 

		int updateIdle(String userid);// tbl_member 의 idle 값을 1로 update 하기  

		int add(BoardVO boardvo); //  tbl_board 에 insert 하기 = >  글쓰기 ( 파일첨부가 없는 글쓰기 )

		List<BoardVO> boardListNoSearch(); // tbl_board 에 전체 내용 select 하기 => 페이징과 검색없는 글목록 조회

		BoardVO getView(Map<String, String> paraMap); // tbl_board 에서 seq 에 해당하는 하나의 행 select 하기 
		void setAddReadCount(String seq); //  tbl_board 에서 readCount 증가 시키기 

		int edit(BoardVO bdvo); // tbl_board 에서 subject 와 content update 하기


		int del(Map<String, String> paraMap); // tbl_board 에서 특정 seq 에 해당하는 글 삭제하기 

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		int addComment(CommentVO cmvo);  // 댓글 쓰기 (tbl_comment 테이블에 insert 를 한다 )
		int updateCommentCount(String  parentSeq); // tbl_board 테이블에 commentCount 컬럼이 1증가(update) 하도록 요청한다.
		int updateMemberPoint(Map<String, String> paraMap); // // tbl_member 테이블의 point 컬럼의 값을 50점을 증가(update)

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		List<CommentVO> getcmvoList(String parentSeq); // 원게시물에 딸린 댓글들을 조회해오기

		void pointPlus(Map<String, String> paraMap); // BoardAOP 클래스에서 사용하는 것으로 특정 회원에게 특정 점수만큼 포인트를 증가하기 위한 것이다.

		List<BoardVO> boardListSearch(Map<String, String> paraMap); // 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 

		List<String> wordSearchShow(Map<String, String> paraMap); // 검색어 입력시 자동글 완성하기  

		int getTotalCount(Map<String, String> paraMap); // // === 총 게시물 건수( totalCount ) 구하기 - 검색이 있을 때와 없을때로 나뉜다. 

		List<BoardVO> boardListSearchWithPaging(Map<String, String> paraMap); // 페이징 처리한 글목록 가져오기 ( 검색이 있든지 , 검색이 없든지 모두 다 포함한 것 )

		List<CommentVO> getCommentListPaging(Map<String, String> paraMap); // 원게시물에 딸린 댓글들을 페이징 처리해서 조회해오기 (Ajax로 처리) ===  

		int getCommentTotalPage(Map<String, String> paraMap); // 원게시물에 딸린 댓글 TotalPage 조회 해오기 ( Ajax 로 처리) 

		int getGroupno_max();  // tbl_board 테이블에서 groupno 컬럼의 최대값 알아오기 

		int add_withFile(BoardVO boardvo); // 글쓰기 파일첨부가 있는 경우 

		List<Map<String, String>> getReservationList(); // === Spring Scheduler(스프링스케줄러)를 사용한 email 발송하기 ===  

		void updateMailSendCheck(Map<String, String[]> paraMap); // e메일을 발송한 행은 발송했다는 표시해주기 
		
		
		
}
