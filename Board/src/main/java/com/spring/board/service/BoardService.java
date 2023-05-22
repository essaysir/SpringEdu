package com.spring.board.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.spring.board.common.AES256;
import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
import com.spring.board.model.InterBoardDAO;
import com.spring.board.model.MemberVO;
import com.spring.board.model.TestVO;

//==== #31. 컨트롤러 선언 ==== 
// 트랙잭션 처리를 담당하는 곳, 업무( 데이터베이스와 관련된 일 ) 를 처리하는 곳
// 비즈니스 ( Business ) 단 


@Service
public class BoardService  implements InterBoardService {
	// BoardService 클래스는 클래스명에서 첫글자만 소문자로 된 boardService 이름으로 bean 객체가 생성된다.
	
	
	
	// ==== #34. 의존객체 주입하기 ( DI : Dependency Injection ) ====
	@Autowired
	private InterBoardDAO dao ;  // BEAN 으로 이미 올라가져있기 때문에 

		// === #45. 양방향 암호화 알고리즘인 AES256 를 사용하여 복호화 하기 위한 클래스 의존객체 주입하기(DI: Dependency Injection) ===
	   @Autowired
	   private AES256 aes;
	   // Type 에 따라 Spring 컨테이너가 알아서 bean 으로 등록된 com.spring.board.common.AES256 의 bean 을  aes 에 주입시켜준다. 
	    // 그러므로 aes 는 null 이 아니다.
	   // com.spring.board.common.AES256 의 bean 은 /webapp/WEB-INF/spring/appServlet/servlet-context.xml 파일에서 bean 으로 등록시켜주었음. 
	
	@Override
	public int test_insert() {
		int n = dao.test_insert();
		
		return n;
	}

	@Override
	public List<TestVO> test_select() {
			List<TestVO> testvoList = dao.test_select();
		
		return testvoList;
	}

	@Override
	public int test_register(Map<String, String> paraMap) {
		int n = dao.test_register(paraMap);
		
		return n;
	}

	@Override
	public int test_register(TestVO tvo) {
	int n = dao.test_register(tvo);
		
		return n;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ModelAndView index(ModelAndView mav) {
		List<String> imgfilenameList = dao.getImgfilenameList() ;
		mav.addObject("imgfilenameList", imgfilenameList);
		mav.setViewName("main/index.tiles1");
		return mav;
	}
	
	@Override
	public ModelAndView loginEnd(ModelAndView mav, HttpServletRequest request , Map<String, String> paraMap) {
		MemberVO loginuser = dao.getLoginMember(paraMap);
		// === #48. aes 의존객체를 사용하여 로그인 되어진 사용자(loginuser)의 이메일 값을 복호화 하도록 한다. === 
	    //          또한 암호변경 메시지와 휴면처리 유무 메시지를 띄우도록 업무처리를 하도록 한다.
			
		if ( loginuser != null &&  loginuser.getPwdchangegap() >= 3) {
			// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 
			loginuser.setRequirePwdChange(true); // 로그인시 암호를 변경해라는 alert 를 띄우도록 한다.
			
		}
		
		if ( loginuser != null && loginuser.getIdle() == 0 &&  loginuser.getLastlogingap() >= 12) {
			// 마지막으로 로그인 한 날짜 시간이 현재 시각으로 부터 1년이 지났으면 휴면으로 지정 
			loginuser.setIdle(1);
			
			// === tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기
			int n = dao.updateIdle(paraMap.get("userid")) ;
		}
		if ( loginuser != null ) {
				try {
					loginuser.setEmail(aes.decrypt(loginuser.getEmail()));
				} catch (UnsupportedEncodingException | GeneralSecurityException  e ) {
						e.printStackTrace();
				}
		}
		
		if ( loginuser == null ) { // 로그인 실패시 
			String message = " 아이디 또는 암호가 틀립니다";
			String loc = "javascript:history.back()";
			
			mav.addObject("message", message);
			mav.addObject("loc", loc);
			mav.setViewName("msg");
			// /WEB-INF/views/msg.jsp 
		}
		else { // 아이디와 그에 맞는 비밀번호를 입력한 경우
			
			if ( loginuser.getIdle() == 1 ) { // 로그인 한지 1년이 경과한 경우
				 String message = " 로그인을 한지 1년이 지나서 휴면상태도 되었습니다. \\n  관리자에게 문의 바랍니다.";
					String loc = request.getContextPath()+"/index.action" ;
					// 원래는 위와 같이 index.action 이 아니라 휴면인 계정을 풀어주는 페이지로 잡아주어야 한다.
					
					mav.addObject("message", message);
					mav.addObject("loc", loc);
					mav.setViewName("msg");
			}
			else { // 로그인 한지 1년 이내인 경우
				HttpSession session = request.getSession(); 
				// 메모리에 생성되어져 있는 session 을 불러오는 것이다 . 
				session.setAttribute("loginuser", loginuser);
				// session(세션)에 로그인 되어진 사용자 정보인 loginuser 을 키이름을 "loginuser" 으로 저장시켜두는 것이다. 
				
				 if ( loginuser.isRequirePwdChange() == true ) { // 암호를 마지막으로 변경한 것이 3개월 경과한 경우
					 String message = " 비밀번호를 변경하신지 3개월이 지났습니다. \\n 암호를 변경하시는 것을 추천드립니다. ";
						String loc = request.getContextPath()+"/index.action" ;
						// 원래는 위와 같이 index.action 이 아니라 사용자의 암호를 변경해주는 페이지로 잡아주어야 한다.
						
						mav.addObject("message", message);
						mav.addObject("loc", loc);
						mav.setViewName("msg");
				 }
				 else { // 로그인 한지 1년 이내이고 암호를 마지막으로 변경한 것이 3개월 이내인 경우 
					   // 로그인을 해야만 접근할 수 있는 페이지에 로그인을 하지 않은 상태에서 접근을 시도한 경우 
		               // "먼저 로그인을 하세요!!" 라는 메시지를 받고서 사용자가 로그인을 성공했다라면
		               // 화면에 보여주는 페이지는 시작페이지로 가는 것이 아니라
		               // 조금전 사용자가 시도하였던 로그인을 해야만 접근할 수 있는 페이지로 가기 위한 것이다
						/*if  ( session.getAttribute("url") != null) {
							mav.setViewName("redirect:"+session.getAttribute("url"));
						}*/
					 	String url = (String)session.getAttribute("url") ;
					 	if ( url != null ) {
					 		mav.setViewName("redirect:"+url);
					 		session.removeAttribute("url"); // 삭제하는 거 까먹지 말자
					 		// 삭제하지 않는다면, 로그아웃하고 다시 로그인 할 경우에 이동하는 곳이 달라 질 수 있다.
					 	}
					 	              
						else {
							// mav.setViewName("main/index.tiles1");
							mav.setViewName("redirect:/index.action");
						}
				 }
			}
			
		
		}
		
		return mav ;			
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		//  === #37. 시작페이지에서 메인 이미지를 보여주는 것 === // 
		@Override
		public List<String> getImgfilenameList() {
			
			List<String> imgfilenameList = dao.getImgfilenameList() ;
			return imgfilenameList ;
		}

		//  === #42.로그인 처리하기 
		@Override
		public MemberVO getLoginMember(Map<String, String> paraMap) {
			
			MemberVO loginuser = dao.getLoginMember(paraMap);
			// === #48. aes 의존객체를 사용하여 로그인 되어진 사용자(loginuser)의 이메일 값을 복호화 하도록 한다. === 
		    //          또한 암호변경 메시지와 휴면처리 유무 메시지를 띄우도록 업무처리를 하도록 한다.
				
			if ( loginuser != null &&  loginuser.getPwdchangegap() >= 3) {
				// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 
				loginuser.setRequirePwdChange(true); // 로그인시 암호를 변경해라는 alert 를 띄우도록 한다.
				
			}
			
			if ( loginuser != null && loginuser.getIdle() == 0 &&  loginuser.getLastlogingap() >= 12) {
				// 마지막으로 로그인 한 날짜 시간이 현재 시각으로 부터 1년이 지났으면 휴면으로 지정 
				loginuser.setIdle(1);
				
				// === tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기
				int n = dao.updateIdle(paraMap.get("userid")) ;
			}
			if ( loginuser != null ) {
					try {
						loginuser.setEmail(aes.decrypt(loginuser.getEmail()));
					} catch (UnsupportedEncodingException | GeneralSecurityException  e ) {
							e.printStackTrace();
					}
			}
		return loginuser ;
	}
		// === #55. 글쓰기 ( 파일첨부가 없는 글쓰기 ) === //
		@Override
		public int add(BoardVO boardvo) {
			  // === #144. 글쓰기가 원글쓰기인지 아니면 답변글쓰기인지를 구분하여 
		      //           tbl_board 테이블에 insert 를 해주어야 한다.
		      //           원글쓰기 이라면 tbl_board 테이블의 groupno 컬럼의 값은 
		      //           groupno 컬럼의 최대값(max)+1 로 해서 insert 해야하고,
		      //           답변글쓰기 이라면 넘겨받은 값(boardvo)을 그대로 insert 해주어야 한다. 
		      
		      // === 원글쓰기인지, 답변글쓰기인지 구분하기 시작 === //
			  if ( "".equals(boardvo.getFk_seq())) {
				  // 원글쓰기 
				  // groupno 컬럼의 값은 groupno 컬럼의 최대값(max) +1 로 해야 한다.
				  int groupno = dao.getGroupno_max()+1;
				  boardvo.setGroupno(String.valueOf(groupno));
			  }
				
			int n = dao.add(boardvo);
			
			return n;
		}

		// == #59.페이징 처리를 안한 검색어가 없는 전체 글 목록 보여주기 == // 
		@Override
		public List<BoardVO> boardListNoSearch() {
			List<BoardVO> boardList = dao.boardListNoSearch();
			
			return boardList ;
		}

		// == #63. 글조회수 증가와 함께 글 1 개를 조회를 해주는 것 == //
		// ( 먼저 , 로그인을 한 상태에서 다른 사람의 글을 조회할 경우에는 글 조회수 컬럼의 값을 증가시켜준다. )
		@Override
		public BoardVO getView(Map<String, String> paraMap) {
			// 글 1개 조회하기
			BoardVO bdvo = dao.getView(paraMap);
			
			String login_userid = paraMap.get("login_userid");
			// 위는 로그인을 한 상태이라면 로그인한 사용자의 아이디 이고
			// 만약에 로그인을 하지 않은 상태라면 위의 값는 null 이 될 것이다.
			
			if ( login_userid != null && bdvo != null && !login_userid.equals(bdvo.getFk_userid())) {
				// 글 조회수 증가는 로그인을 한 상태에서 다른 사람의 글을 읽을때만 증가하도록 한다. 
				
				dao.setAddReadCount(bdvo.getSeq()); // 글 조회수 1 증가하기
				bdvo = dao.getView(paraMap);		
			}
			
			
			
			return bdvo ;
		}

		@Override
		public BoardVO getViewWithNoAddCount(Map<String, String> paraMap) {
			// 글 1개 조회하기
			BoardVO bdvo = dao.getView(paraMap);
			
			return bdvo;
		}
		
		// === #73. 1개 글 수정하기 === //
		@Override
		public int edit(BoardVO bdvo) {
			
			int n = dao.edit(bdvo);
			
			return n;
		}


		@Override
		public int del(Map<String, String> paraMap) {
			int n  = dao.del(paraMap);
			
			return n;
		}

		// === #85. 댓글쓰기(transaction 처리) === //
		   // tbl_comment 테이블에 insert 된 다음에 
		   // tbl_board 테이블에 commentCount 컬럼이 1증가(update) 하도록 요청한다.
		   // 이어서 회원의 포인트를 50점을 증가하도록 한다.
		   // 즉, 2개이상의 DML 처리를 해야하므로 Transaction 처리를 해야 한다. (여기서는 3개의 DML 처리가 일어남)
		   // >>>>> 트랜잭션처리를 해야할 메소드에 @Transactional 어노테이션을 설정하면 된다. 
		   // rollbackFor={Throwable.class} 은 롤백을 해야할 범위를 말하는데 Throwable.class 은 error 및 exception 을 포함한 최상위 루트이다. 즉, 해당 메소드 실행시 발생하는 모든 error 및 exception 에 대해서 롤백을 하겠다는 말이다.
		@Override
		@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED, rollbackFor= {Throwable.class})
		public int addComment(CommentVO cmvo)  throws Throwable {
			int n = 0 , m = 0 ,  result = 0  ;
			n = dao.addComment(cmvo); // 댓글 쓰기 (tbl_comment 테이블에 insert 를 한다 )
			System.out.println("~~~ 확인용 n : "+n);
			
			if ( n == 1 ) {
				m = dao.updateCommentCount(cmvo.getParentSeq()); //  tbl_board 테이블에 commentCount 컬럼이 1증가(update) 하도록 요청한다.
				System.out.println("~~~ 확인용 m : "+m);
				if ( m == 1 ) {
					Map<String, String> paraMap = new HashMap<>();
					paraMap.put("userid", cmvo.getFk_userid());
					paraMap.put("point", "50");
					
					result = dao.updateMemberPoint(paraMap); // tbl_member 테이블의 point 컬럼의 값을 50점을 증가(update)
					System.out.println("~~~ 확인용 result : "+result);
				}
			}
			return result;
		}

		// === #91. 원게시물에 딸린 댓글들을 조회해오기 === // 
		@Override
		public List<CommentVO> getcmvoList(String parentSeq) {
			List<CommentVO> cmvo  = dao.getcmvoList(parentSeq) ;
			return cmvo ;
		}

		// === #98. BoardAOP 클래스에서 사용하는 것으로 특정 회원에게 특정 점수만큼 포인트를 증가하기 위한 것이다.
		@Override
		public void pointPlus(Map<String, String> paraMap) {
				dao.pointPlus(paraMap);
			
		}

		// === #103. 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 
		@Override
		public List<BoardVO> boardListSearch(Map<String, String> paraMap) {
			List<BoardVO> boardList = dao.boardListSearch(paraMap);
			return boardList;
		}

		// === #109. // 검색어 입력시 자동글 완성하기
		
		/*
		  @Override
		public List<String> wordSearchShow(Map<String, String> paraMap) {
			List<String> list = dao.wordSearchShow(paraMap);
			return list ;
		}
		*/
		  @Override
		public String wordSearchShow(Map<String, String> paraMap) {
			List<String> list = dao.wordSearchShow(paraMap);
			
			JSONArray jsonArr = new JSONArray();
			
			if ( list != null  && list.size() != 0) {
				for (String input : list) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("input", input);
					jsonArr.put(jsonObj);
				}
			}
			
			return jsonArr.toString() ;
		}

		// === #115.  총 게시물 건수( totalCount ) 구하기 - 검색이 있을 때와 없을때로 나뉜다. 
		@Override
		public int getTotalCount(Map<String, String> paraMap) {
			int n = dao.getTotalCount(paraMap);
			return n ;
		}

		 // === #118.  페이징 처리한 글목록 가져오기 ( 검색이 있든지 , 검색이 없든지 모두 다 포함한 것 )
		@Override
		public List<BoardVO> boardListSearchWithPaging(Map<String, String> paraMap) {
			List<BoardVO> bdvoList = dao.boardListSearchWithPaging(paraMap);
			
			return bdvoList;
		}

		// === #129. 원게시물에 딸린 댓글들을 페이징 처리해서 조회해오기 (Ajax로 처리) === 
		@Override
		public List<CommentVO> getCommentListPaging(Map<String, String> paraMap) {
			List<CommentVO> cmvoList = dao.getCommentListPaging(paraMap);
			
			return cmvoList ;
		}

		// === #133. 원게시물에 딸린 댓글 TotalPage 조회 해오기 ( Ajax 로 처리) 
		@Override
		public String getCommentTotalPage(Map<String, String> paraMap) {
			int totalPage = dao.getCommentTotalPage(paraMap);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("totalPage", totalPage);
			
			return jsonObj.toString() ;
		}

		// === #157. 글쓰기 ( 파일첨부가 있는 글쓰기) ===  // 
		@Override
		public int add_withFile(BoardVO boardvo) {
			  if ( "".equals(boardvo.getFk_seq())) {
				  // 원글쓰기 
				  // groupno 컬럼의 값은 groupno 컬럼의 최대값(max) +1 로 해야 한다.
				  int groupno = dao.getGroupno_max()+1;
				  boardvo.setGroupno(String.valueOf(groupno));
			  }
				
			int n = dao.add_withFile(boardvo); // 첨부파일이 있는 경우 
			
			return n;
		}
		



		
}
