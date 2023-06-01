package com.spring.board.service;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.spring.board.common.AES256;
import com.spring.board.common.FileManager;
import com.spring.board.common.GoogleMail;
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
		// === #155. 파일업로드 및 다운로드를 해주는 FileManager 클래스 의존객체 주입하기(DI : Dependency Injection) ===  
	   @Autowired     // Type에 따라 알아서 Bean 을 주입해준다.
	   private FileManager fileManager;
	   
		// === #45. 양방향 암호화 알고리즘인 AES256 를 사용하여 복호화 하기 위한 클래스 의존객체 주입하기(DI: Dependency Injection) ===
	   @Autowired
	   private AES256 aes;
	   // Type 에 따라 Spring 컨테이너가 알아서 bean 으로 등록된 com.spring.board.common.AES256 의 bean 을  aes 에 주입시켜준다. 
	    // 그러므로 aes 는 null 이 아니다.
	   // com.spring.board.common.AES256 의 bean 은 /webapp/WEB-INF/spring/appServlet/servlet-context.xml 파일에서 bean 으로 등록시켜주었음. 
	
	   // === #187. Spring Scheduler(스프링스케줄러7) === //
	   // === Spring Scheduler(스프링스케줄러)를 사용한 email 발송하기 === 
	   @Autowired   // Type에 따라 알아서 Bean 을 주입해준다.
	   private GoogleMail mail;
	   
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
			
			// === #165. 파일첨부가 된 글이라면 글 삭제시 먼저 첨부파일을 삭제 해주어야 한다.  
			if ( n == 1 ) {
				String path = paraMap.get("path");
				String fileName = paraMap.get("fileName");
				
				if ( fileName != null && !"".equals(fileName)) {
					try {
						fileManager.doFileDelete(fileName, path);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			
			}
			
			////////////////////////////////////////////////////////////////////////
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

		// === #184. Spring Scheduler(스프링스케줄러4)는 서비스단에서 작업을 하는 것이다. === //
		   /*
		   스케줄은 3가지 종류  cron, fixedDelay, fixedRate 가 있다. 
		   
		   @Scheduled(cron="0 0 0 * * ?")
		   cron 스케줄에 따라서 일정기간에 시작한다. 매일 자정마다 (00:00:00)에 실행한다.
		   
		   >>> cron 표기법 <<<
		   
		   문자열의 좌측부터 우측까지 아래처럼 의미가 부여되고 각 항목은 공백 문자로 구분한다.
		   
		   순서는 초 분 시 일 월 요일명 이다.
		   ----------------------------------------------------------------------------------------------------------------    
		   의미             초               분              시             일                         월             요일명                                                                   년도
		   ----------------------------------------------------------------------------------------------------------------
		   사용가능한   0~59     0~59     0~23      1~31           1~12      1~7 (1=>일요일, 2=>월요일, ... 7=>토요일)     1970 ~ 2099 
		   값           - * /    - * /    - * /     - * ? / L W    - * /     - * ? / L #
		   
		   * 는 모든 수를 의미.
		   
		   ? 는 해당 항목을 사용하지 않음.  
		   일에서 ?를 사용하면 월중 날짜를 지정하지 않음. 요일명에서 ?를 사용하면 주중 요일을 지정하지 않음.
		   
		   - 는 기간을 설정. 시에서 10-12이면 10시, 11시, 12시에 동작함.
		   분에서 57-59이면 57분, 58분, 59분에 동작함.
		   
		   , 는 특정 시간을 지정함. 요일명에서 2,4,6 은 월,수,금에만 동작함.
		   
		   / 는 시작시간과 반복 간격 설정함. 초위치에 0/15로 설정하면 0초에 시작해서 15초 간격으로 동작함. 
		   분위치에 5/10으로 설정하면 5분에 시작해서 10분 간격으로 동작함.
		   
		   L 는 마지막 기간에 동작하는 것으로 일과 요일명에서만 사용함. 일위치에 사용하면 해당월의 마지막 날에 동작함.
		   요일명에 사용하면 토요일에 동작함.
		   
		   W 는 가장 가까운 평일에 동작하는 것으로 일에서만 사용함.  일위치에 15W로 설정하면 15일이 토요일이면 가장 가까운 14일 금요일에 동작함.
		   일위치에 15W로 설정하고 15일이 일요일이면 16일에 동작함.
		   일위치에 15W로 설정하고 15일이 평일이면 15일에 동작함.
		   
		   LW 는 L과 W의 조합.                             그달의 마지막 평일에 동작함.
		   
		   # 는 몇 번째 주와 요일 설정함. 요일명에서만 사용함.    요일명위치에 6#3이면 3번째 주 금요일에 동작함.
		   요일명위치에 4#2이면 2번째 주 수요일에 동작함.
		   
		   
		   ※ cron 스케줄 사용 예
		   0 * * * * *             ==> 매 0초마다 실행(즉, 1분마다 실행함)
		   
		   * 0 * * * *             ==> 매 0분마다 실행(즉, 1시간마다 실행함)
		   
		   0 * 14 * * *            ==> 14시에 0분~59분까지 1분 마다 실행
		   
		   * 10,50 * * * *         ==> 매 10분, 50분 마다 실행
		   , : 여러 값 지정 구분에 사용 
		   
		   0 0/10 14 * * *         ==> 14시 0분 부터 시작하여 10분 간격으로 실행(즉, 6번 실행함)
		   / : 초기값과 증가치 설정에 사용
		   * 
		   0 0/10 14,18 * * *      ==> 14시 0분 부터 시작하여 10분 간격으로 실행(6번 실행함) 그리고 
		   ==> 18시 0분 부터 시작하여 10분 간격으로 실행(6번 실행함)
		   / : 초기값과 증가치 설정에 사용 
		   , : 여러 값 지정 구분에 사용 
		   
		   0 0 12 * * *            ==> 매일 12(정오)시에 실행
		   0 15 10 * * *           ==> 매일 오전 10시 15분에 실행
		   0 0 14 * * *            ==> 매일 14시에 실행
		   
		   0 0 0/6 * * *        ==> 매일 0시 6시 12시 18시 마다 실행
		   - : 범위 지정에 사용  / : 초기값과 증가치 설정에 사용
		   
		   0 0/5 14-18 * * *    ==> 매일 14시 부터 18시에 시작해서 0분 부터 매 5분간격으로 실행
		   / : 증가치 설정에 사용
		   
		   0 0-5 14 * * *          ==> 매일 14시 0분 부터 시작해서 14시 5분까지 1분마다 실행   
		   - : 범위 지정에 사용
		   
		   0 0 8 * * 2-6           ==> 평일 08:00 실행 (월,화,수,목,금)  
		   
		   0 0 10 * * 1,7          ==> 토,일 10:00 실행 (토,일) 
		   
		   0 0/5 14 * * ?          ==> 아무요일, 매월, 매일 14:00부터 14:05분까지 매분 0초 실행 (6번 실행됨)
		   
		   0 15 10 ? * 6L          ==> 매월 마지막 금요일 아무날의 10:15:00에 실행
		   
		   0 15 10 15 * ?          ==> 아무요일, 매월 15일 10:15:00에 실행 
		   
		   * /1 * * * *            ==> 매 1분마다 실행
		   
		   * /10 * * * *           ==> 매 10분마다 실행 
		   
		   
		   >>> fixedDelay <<<
		   이전에 실행된 task의 종료시간으로부터 정의된 시간만큼 지난 후 다음에 task를 실행함. 단위는 밀리초임.
		   @Scheduled(fixedDelay=1000)
		   
		   >>> fixedRate <<<
		   이전에 실행된 task의 시작 시간으로부터 정의된 시간만큼 지난 후 다음 task를 실행함. 단위는 밀리초임.
		   @Scheduled(fixedRate=1000)

		*/   
		   
		   // === Spring Scheduler 를 사용하여 특정 URL 사이트로 연결하기 === 
		   @Override
		   @Scheduled(cron="0 50 12 * * *")
		   //@Scheduled(cron="0 * * * * *")
		   public void branchTimeAlarm() {
		      // !!! <주의> !!!
		      // 스케줄러로 사용되어지는 메소드는 반드시 파라미터는 없어야 한다.!!!!!
		      
		      // == 현재 시각을 나타내기 ==
		      Calendar currentDate = Calendar.getInstance(); // 현재날짜와 시간을 얻어온다.
		      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		      String currentTime = dateFormat.format(currentDate.getTime());
		      
		      System.out.println("현재시각 => " + currentTime);
		      
		      // !!!! === 특정 사이트의 웹페이지를 보여주기 위해 기본브라우저를 띄운다. === !!!!
		      // 조심할 것은 http:// 를 주소에 꼭 붙여야 한다.
		      // 즉, 특정 사이트 웹페이지를 실행시키는 것이다.
		      
		      try { 
		         Desktop.getDesktop().browse(new URI("http://localhost:9090/board/branchTimeAlarm.action")); 
		         // WAS 컴퓨터에서만 특정 웹페이지를 실행시켜주는 것이지, WAS에 접속한 다른 클라이언트 컴퓨터에서는 특정 웹사이트를 실행시켜주지 않는다.
		      } catch (IOException e) { 
		         e.printStackTrace(); 
		      } catch (URISyntaxException e) {
		         e.printStackTrace(); 
		      }
				
		      
		      
		   }
		   
		   
		   // === #188. Spring Scheduler(스프링스케줄러8) === //
		   // === Spring Scheduler(스프링스케줄러)를 사용한 email 발송하기 === 
		   // <주의> 스케줄러로 사용되어지는 메소드는 반드시 파라미터가 없어야 한다.!!!!
		    // 매일 새벽 4시 마다 고객이 예약한 2일전에 고객에게 예약이 있다는 e메일을 자동 발송 하도록 하는 예제를 만들어 본다. 
		    // 고객들의 email 주소는 List<String(e메일주소)> 으로 만들면 된다.
		    // 또는 e메일 자동 발송 대신에 휴대폰 문자를 자동 발송하는 것도 가능하다. 
		   
		   @Override
		  @Scheduled(cron="0 0 4 * * *")
		   //@Scheduled(cron="0 41 11 * * *")
		   public void reservationEmailSending() throws Exception {
		      // !!! <주의> !!!
		      // 스케줄러로 사용되어지는 메소드는 반드시 파라미터는 없어야 한다.!!!!!
		      
		      // == 현재 시각을 나타내기 ==
		      Calendar currentDate = Calendar.getInstance(); // 현재날짜와 시간을 얻어온다.
		      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		      String currentTime = dateFormat.format(currentDate.getTime());
		      
		      System.out.println("현재시각 => " + currentTime);
		      
		      // === e메일을 발송할 회원 대상 알아오기 === //
		      List<Map<String, String>> reservationList = dao.getReservationList();
		      
		      // **** e메일 발송하기 **** //
		      if(reservationList != null && reservationList.size() > 0) {
		         
		         String[] arr_reservationSeq = new String[reservationList.size()];
		         // String[] arr_reservationSeq 을 생성하는 이유는 
		            // e메일 발송 후 tbl_reservation 테이블의 mailSendCheck 컬럼의 값을 0 에서 1로 update 하기 위한 용도로 
		            // update 되어질 예약번호를 기억하기 위한 것임.
		         
		         for(int i=0; i<reservationList.size(); i++) {
		         
		            String emailContents = "사용자 ID: " + reservationList.get(i).get("USERID") + "<br/>" +
		                                 "예약자명: " + reservationList.get(i).get("NAME") +"님의 방문 예약일은 <span style='color:red;'>" + reservationList.get(i).get("RESERVATIONDATE") + "</span> 입니다.";  
		            
		            mail.sendmail_Reservation( aes.decrypt(reservationList.get(i).get("EMAIL")), emailContents);
		            
		            arr_reservationSeq[i] = reservationList.get(i).get("RESERVATIONSEQ");
		         }// end of for------------------------------
		         
		         // e메일을 발송한 행은 발송했다는 표시해주기
		         Map<String, String[]> paraMap = new HashMap<>();
		         paraMap.putIfAbsent("arr_reservationSeq", arr_reservationSeq);
		         
		         /*
		             Map<String, String> map = new HashMap<>();
		             
		             map.put("a","안녕");
		             map.put("b","잘들어가");
		             
		             map.put("a","건강해");         // map.get("a") ==> "건강해"
		             map.putIfAbsent("b","또보자"); // map.get("b") ==> "잘들어가" 
		          */
		         
		         dao.updateMailSendCheck(paraMap);
		      }// end of if----------------------------------------------

		   }



		
}
