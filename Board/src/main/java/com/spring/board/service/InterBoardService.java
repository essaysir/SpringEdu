package com.spring.board.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
import com.spring.board.model.MemberVO;
import com.spring.board.model.TestVO;

public interface InterBoardService {
	
	int test_insert();

	List<TestVO> test_select();

	int test_register(Map<String, String> paraMap);

	int test_register(TestVO tvo);

	/////////////////////////////////////////////////////////////////////////////////////
	
	//  mav = service.index(mav);
	ModelAndView index(ModelAndView mav);

	ModelAndView loginEnd(ModelAndView mav,HttpServletRequest request, Map<String, String> paraMap);
	
	/////////////////////////////////////////////////////////////////////////////////////
	// 시작페이지에서 메인 이미지를 보여주는 것
	List<String> getImgfilenameList();

	// 로그인 처리하기 
	MemberVO getLoginMember(Map<String, String> paraMap);

	// 글쓰기 ( 파일 첨부가 없는 글쓰기 ) 
	int add(BoardVO boardvo);

	// == 페이징 처리를 안한 검색어가 없는 전체 글 목록 보여주기 == // 
	List<BoardVO> boardListNoSearch();

	//  글 조회수 증가와 함께 글 1개를 조회를 해주는 것 // 
	BoardVO getView(Map<String, String> paraMap);

	// 글 조회수 증가는 없고 단순히 글 1개를 조회를 해주는 것 
	BoardVO getViewWithNoAddCount(Map<String, String> paraMap);

	// 글 1개를 수정해주는 것
	int edit(BoardVO bdvo);


	// // 글 1개를 삭제하는 것이다. 
	int del(Map<String, String> paraMap);

	// 답글을 추가하는 것이다.
	int addComment(CommentVO cmvo) throws Throwable;

	// 원게시물에 딸린 댓글들을 조회해오기
	List<CommentVO> getcmvoList(String parentSeq);

	// BoardAOP 클래스에서 사용하는 것으로 특정 회원에게 특정 점수만큼 포인트를 증가하기 위한 것이다.
	void pointPlus(Map<String, String> paraMap);

	// 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 
	List<BoardVO> boardListSearch(Map<String, String> paraMap);

	// 검색어 입력시 자동글 완성하기 
	//List<String> wordSearchShow(Map<String, String> paraMap);
	String wordSearchShow(Map<String, String> paraMap);

	// 총 게시물 건수( totalCount ) 구하기 - 검색이 있을 때와 없을때로 나뉜다. 
	int getTotalCount(Map<String, String> paraMap);

	// 페이징 처리한 글목록 가져오기 ( 검색이 있든지 , 검색이 없든지 모두 다 포함한 것 )
	List<BoardVO> boardListSearchWithPaging(Map<String, String> paraMap);

	// 원게시물에 딸린 댓글들을 페이징 처리해서 조회해오기 (Ajax로 처리) === 
	List<CommentVO> getCommentListPaging(Map<String, String> paraMap);

	// 원게시물에 딸린 댓글 TotalPage 조회 해오기 ( Ajax 로 처리) 
	String getCommentTotalPage(Map<String, String> paraMap);

	// 글쓰기(파일 첨부가 있는 글쓰기 ) 
	int add_withFile(BoardVO boardvo);


	// === #183. Spring Scheduler(스프링스케줄러3) === //
	   
	   // === Spring Scheduler 를 사용하여 특정 URL 사이트로 연결하기 === 
	   // !!<주의>!! 스프링스케줄러로 사용되는 메소드는 반드시 파라미터가 없어야 한다.!!!!!!!!!!
	   void branchTimeAlarm();
	
	   void reservationEmailSending() throws Exception;
}
