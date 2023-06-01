package com.spring.board.model;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

// ==== #32.DAO 선언 ====
@Component
@Repository
public class BoardDAO implements InterBoardDAO {
		// === #33. 의존객체 주입하기(DI: Dependency Injection) ( 배터리) => DB ( 휴대폰 ) ===
	   // >>> 의존 객체 자동 주입(Automatic Dependency Injection)은
	   //     스프링 컨테이너가 자동적으로 의존 대상 객체를 찾아서 해당 객체에 필요한 의존객체를 주입하는 것을 말한다. 
	   //     단, 의존객체는 스프링 컨테이너속에 bean 으로 등록되어 있어야 한다. 

	   //     의존 객체 자동 주입(Automatic Dependency Injection)방법 3가지 
	   //     1. @Autowired ==> Spring Framework에서 지원하는 어노테이션이다. 
	   //                       스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.
	   //    private SqlSessionTemplate abc ;  SqlSessionTemplate 타입이 두개 인 경우 ( HR 과 MYMVC_USER) 에는
	   //    Autowired 는 펑 터질수도 있다. 그러하여, @Resource를 사용한다.
	
	
	   //     2. @Resource  ==> Java 에서 지원하는 어노테이션이다.
	   //                       스프링 컨테이너에 담겨진 의존객체를 주입할때 필드명(이름)을 찾아서 연결(의존객체주입)한다.
	   //         막 적으면 안된다는 것이다.
	  //
	
	   //     3. @Inject    ==> Java 에서 지원하는 어노테이션이다.
	    //                       스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.   
				 // Inject 와 Autowired 는 차이점이 존재하지 않는다. 단순히 , 스프링인지 자바인지의 차이만 존재한다. 
				// But , Resource 는 다르다.  Resource 는 필드명을 입력해주어야한다.
		/*
		 * @Autowired private SqlSessionTemplate abc ;
		 */
		
		// Type 에 따라 Spring 컨테이너가 알아서 root-context.xml 에 
		// 생성된 org.mybatis.spring.SqlSessionTemplate 의 bean 을  abc 에 주입시켜준다. 
	    // 그러므로 abc 는 null 이 아니다.
		
		// 따로 재정의 할 필요가 없다. 예컨대  ,  NEW 필요 없음 왜냐하면, BEAN 에 올라가 있기 때문이다.
		//  @Autowired 가 없으면 , 위의 필드는 null 이고 , @Autowired 가 존재하면 null 이 아니다.
		// DAO 의 의존 객체는 MyBatis 이다. 
		@Resource
		private SqlSessionTemplate sqlsession ; // 로컬 DB  my_mvcuser 에 연결한 것
		
		@Resource
		private SqlSessionTemplate sqlsession_2 ; // 로컬 DB  hr 에 연결한 것
		
		// Type 에 따라 Spring 컨테이너가 알아서 root-context.xml 에 생성된 org.mybatis.spring.SqlSessionTemplate 의 
		// sqlsession bean 을  sqlsession 에 주입시켜준다. 
	    // 그러므로 sqlsession 는 null 이 아니다.
	
		@Override
		public int test_insert() {
			int n_1 = sqlsession.insert("member.test_insert");
			// int n_2 = sqlsession_2.insert("hr.test_insert");
			// insert( 네임스페이스 ) ; 
			return n_1 ;
		}

		@Override
		public List<TestVO> test_select() {
			List<TestVO> testvoList = sqlsession.selectList("board.test_select");
			
			//  select 문을 통해서 한줄만 뽑아오려는 경우 
			// TestVO tvo = sqlsession.selectOne("board.test_select");
			return testvoList;
		}

		@Override
		public int test_register(Map<String, String> paraMap) {
			int n = sqlsession.insert("board.test_register", paraMap);
			
			return n;
		}

		@Override
		public int test_register(TestVO tvo) {
			int n = sqlsession.insert("board.test_register_vo", tvo);
			
			return n;
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		
		// ==== #38. 시작페이지에서 메인 이미지를 보여주는 것 === // 
		@Override
		public List<String> getImgfilenameList() {
				List<String> imgfilenameList = sqlsession.selectList("board.getImgfilenameList");
			
			
			return imgfilenameList ;
		}

		// === #46. 로그인 처리하기 === // 
		@Override
		public MemberVO getLoginMember(Map<String, String> paraMap) {
			MemberVO loginuser = sqlsession.selectOne("board.getLoginMember", paraMap);
			
			return loginuser ;
		}

		@Override
		public int updateIdle(String userid) {
			int n = sqlsession.update("board.updateIdle", userid) ;	
			
			return n;
		}

		// === #55. 글쓰기 ( 파일첨부가 없는 글쓰기 ) === //
		@Override
		public int add(BoardVO boardvo) {
			int n = sqlsession.insert("board.add" , boardvo) ;
			return n;
		}

		// == #60.페이징 처리를 안한 검색어가 없는 전체 글 목록 보여주기 == // 
		@Override
		public List<BoardVO> boardListNoSearch() {
		List<BoardVO> boardList	= sqlsession.selectList("board.boardListNoSearch");
			
			return boardList;
		}

		// == #64. 글 1개 조회하기 === // 
		@Override
		public BoardVO getView(Map<String, String> paraMap) {
			BoardVO bdvo = sqlsession.selectOne("board.getView" , paraMap);
			return bdvo;
		}
		@Override // == #65. 글 조회수 1 증가 시키기
		public void setAddReadCount(String seq) {
			sqlsession.update("board.setAddReadCount" , seq);	
		}

		@Override
		public int edit(BoardVO bdvo) {
			int n  = sqlsession.update( "board.edit", bdvo);
			
			return n;
		}


		@Override
		public int del(Map<String, String> paraMap) {
			int n = sqlsession.delete("board.del",paraMap);
			
			return  n ;
		}

		 // ==== #86. 댓글 쓰기 (tbl_comment 테이블에 insert 를 한다 )
		@Override
		public int addComment(CommentVO cmvo) {
			int n =  sqlsession.insert("board.addComment" , cmvo);
			return n;
		}
		
		// ==== #87.-1  tbl_board 테이블에 commentCount 컬럼이 1증가(update) 하도록 요청한다.
		@Override
		public int updateCommentCount(String parentSeq) {
			int n = sqlsession.update("board.updateCommentCount" , parentSeq );
			return n;
		}
		 // ==== #87.-2  tbl_member 테이블의 point 컬럼의 값을 50점을 증가(update)
		@Override
		public int updateMemberPoint(Map<String, String> paraMap) {
			int n = sqlsession.update("board.updateMemberPoint" , paraMap);
			return n;
		}

		// === #92. 원게시물에 딸린 댓글들을 조회해오기 === // 
		@Override
		public List<CommentVO> getcmvoList(String parentSeq) {
			List<CommentVO> cmvo = sqlsession.selectList("board.getcmvoList" , parentSeq) ;
			return cmvo;
		}

		// === #99. BoardAOP 클래스에서 사용하는 것으로 특정 회원에게 특정 점수만큼 포인트를 증가하기 위한 것이다.
		@Override
		public void pointPlus(Map<String, String> paraMap) {
			sqlsession.update("board.pointPlus" , paraMap) ;
		}

		// === #104. 페이지 처리를 안한 검색어가 있는 전체 글목록 보여주기 === // 
		@Override
		public List<BoardVO> boardListSearch(Map<String, String> paraMap) {
			List<BoardVO> boardList = sqlsession.selectList("board.boardListSearch",paraMap);
			return boardList ;
		}

		// === #110. 검색어 입력시 자동글 완성하기 
		@Override
		public List<String> wordSearchShow(Map<String, String> paraMap) {
			List<String> list = sqlsession.selectList("board.wordSearchShow" , paraMap);
			return list;
		}
		
		// === #116.  총 게시물 건수( totalCount ) 구하기 - 검색이 있을 때와 없을때로 나뉜다. 
		@Override
		public int getTotalCount(Map<String, String> paraMap) {
			int n = sqlsession.selectOne("board.getTotalCount" , paraMap);
			return  n ;
		}

		 // === #119.  페이징 처리한 글목록 가져오기 ( 검색이 있든지 , 검색이 없든지 모두 다 포함한 것 )
		@Override
		public List<BoardVO> boardListSearchWithPaging(Map<String, String> paraMap) {
			List<BoardVO> bdvoList = sqlsession.selectList("board.boardListSearchWithPaging" , paraMap );
			
			return bdvoList;
		}

		// === #130. 원게시물에 딸린 댓글들을 페이징 처리해서 조회해오기 (Ajax로 처리) === 
		@Override
		public List<CommentVO> getCommentListPaging(Map<String, String> paraMap) {
				
			List <CommentVO> cmvoList = sqlsession.selectList("board.getCommentListPaging", paraMap);
			return cmvoList; 
		}

		// === #134. 원글 글번호에 해당하는 댓글의 totalPage 알아오기 === // 
		@Override
		public int getCommentTotalPage(Map<String, String> paraMap) {
			int totalPage = sqlsession.selectOne("board.getCommentTotalPage", paraMap );
			return totalPage ;
		}

		// ==== #145. tbl_board 테이블에서 groupno 컬럼의 최대값 알아오기  ==== //
		@Override
		public int getGroupno_max() {
			int maxgroupno = sqlsession.selectOne("board.getGroupno_max");	
			return maxgroupno ;
		}

		@Override
		public int add_withFile(BoardVO boardvo) {
			int n = sqlsession.insert("board.add_withFile"  , boardvo); 
			
			return n ;
		}

		// === #189. Spring Scheduler(스프링스케줄러9) === //
		   // === Spring Scheduler(스프링스케줄러)를 사용한 email 발송하기 === 
		   @Override
		   public List<Map<String, String>> getReservationList() {
		      List<Map<String, String>> reservationList = sqlsession.selectList("board.getReservationList");
		      return reservationList;
		   }
		// e메일을 발송한 행은 발송했다는 표시해주기 
		   @Override
		   public void updateMailSendCheck(Map<String, String[]> paraMap) {
		      sqlsession.update("board.updateMailSendCheck", paraMap);
		   }
		   
		
}
