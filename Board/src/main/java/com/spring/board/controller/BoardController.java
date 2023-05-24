package com.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.spring.board.common.FileManager;
import com.spring.board.common.MyUtil;
import com.spring.board.common.Sha256;
import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
import com.spring.board.model.MemberVO;
import com.spring.board.model.TestVO;
import com.spring.board.service.InterBoardService;

/*
		사용자 웹브라우저 요청(View)  ==> DispatcherServlet(web.xml) ==> @Controller 클래스 <==>> Service단(핵심업무로직단, business logic단) <==>> Model단[Repository](DAO, DTO) <==>> myBatis <==>> DB(오라클)           
		(http://...  *.action)                                  |                                                                                                                              
		 ↑                                                View Resolver
		 |                                                      ↓
		 |                                                View단(.jsp 또는 Bean명)
		 -------------------------------------------------------| 
		
		사용자(클라이언트)가 웹브라우저에서 http://localhost:9090/board/test/test_insert.action 을 실행하면
		배치서술자인 web.xml 에 기술된 대로  org.springframework.web.servlet.DispatcherServlet 이 작동된다.
		DispatcherServlet 은 bean 으로 등록된 객체중 controller 빈을 찾아서  URL값이 "/test_insert.action" 으로
		매핑된 메소드를 실행시키게 된다.                                               
		Service(서비스)단 객체를 업무 로직단(비지니스 로직단)이라고 부른다.
		Service(서비스)단 객체가 하는 일은 Model단에서 작성된 데이터베이스 관련 여러 메소드들 중 관련있는것들만을 모아 모아서
		하나의 트랜잭션 처리 작업이 이루어지도록 만들어주는 객체이다.
		여기서 업무라는 것은 데이터베이스와 관련된 처리 업무를 말하는 것으로 Model 단에서 작성된 메소드를 말하는 것이다.
		이 서비스 객체는 @Controller 단에서 넘겨받은 어떤 값을 가지고 Model 단에서 작성된 여러 메소드를 호출하여 실행되어지도록 해주는 것이다.
		실행되어진 결과값을 @Controller 단으로 넘겨준다.
*/

// ==== #30. 컨트롤러 선언 ==== 
// @Component  생략이 가능하다.
/* XML에서 빈을 만드는 대신에 클래스명 앞에 @Component 어노테이션을 적어주면 해당 클래스는 bean으로 자동 등록된다. 
그리고 bean의 이름(첫글자는 소문자)은 해당 클래스명이 된다. 
즉, 여기서 bean의 이름은 boardController 이 된다. 
여기서는 @Controller 를 사용하므로 @Component 기능이 이미 있으므로 @Component를 명기하지 않아도 BoardController 는 bean 으로 등록되어 스프링컨테이너가 자동적으로 관리해준다. 
*/
@Controller
public class BoardController {
	// === #35. 의존객체 주입하기(DI: Dependency Injection) ===
	// ※ 의존객체주입(DI : Dependency Injection)
	// ==> 스프링 프레임워크는 객체를 관리해주는 컨테이너를 제공해주고 있다.
	// 스프링 컨테이너는 bean으로 등록되어진 BoardController 클래스 객체가 사용되어질때,
	// BoardController 클래스의 인스턴스 객체변수(의존객체)인 BoardService service 에
	// 자동적으로 bean 으로 등록되어 생성되어진 BoardService service 객체를
	// BoardController 클래스의 인스턴스 변수 객체로 사용되어지게끔 넣어주는 것을 의존객체주입(DI : Dependency
	// Injection)이라고 부른다.
	// 이것이 바로 IoC(Inversion of Control == 제어의 역전) 인 것이다.
	// 즉, 개발자가 인스턴스 변수 객체를 필요에 의해 생성해주던 것에서 탈피하여 스프링은 컨테이너에 객체를 담아 두고,
	// 필요할 때에 컨테이너로부터 객체를 가져와 사용할 수 있도록 하고 있다.
	// 스프링은 객체의 생성 및 생명주기를 관리할 수 있는 기능을 제공하고 있으므로, 더이상 개발자에 의해 객체를 생성 및 소멸하도록 하지 않고
	// 객체 생성 및 관리를 스프링 프레임워크가 가지고 있는 객체 관리기능을 사용하므로 Inversion of Control == 제어의 역전
	// 이라고 부른다.
	// 그래서 스프링 컨테이너를 IoC 컨테이너라고도 부른다.

	// IOC(Inversion of Control) 란 ?
	// ==> 스프링 프레임워크는 사용하고자 하는 객체를 빈형태로 이미 만들어 두고서 컨테이너(Container)에 넣어둔후
	// 필요한 객체사용시 컨테이너(Container)에서 꺼내어 사용하도록 되어있다.
	// 이와 같이 객체 생성 및 소멸에 대한 제어권을 개발자가 하는것이 아니라 스프링 Container 가 하게됨으로써
	// 객체에 대한 제어역할이 개발자에게서 스프링 Container로 넘어가게 됨을 뜻하는 의미가 제어의 역전
	// 즉, IOC(Inversion of Control) 이라고 부른다.

	// === 느슨한 결합 ===
	// 스프링 컨테이너가 BoardController 클래스 객체에서 BoardService 클래스 객체를 사용할 수 있도록
	// 만들어주는 것을 "느슨한 결합" 이라고 부른다.
	// 느스한 결합은 BoardController 객체가 메모리에서 삭제되더라도 BoardService service 객체는 메모리에서 동시에
	// 삭제되는 것이 아니라 남아 있다.

	// ===> 단단한 결합(개발자가 인스턴스 변수 객체를 필요에 의해서 생성해주던 것)
	// private InterBoardService service = new BoardService();
	// ===> BoardController 객체가 메모리에서 삭제 되어지면 BoardService service 객체는 멤버변수(필드)이므로
	// 메모리에서 자동적으로 삭제되어진다.

	@Autowired // TYPE 에 따라서 , SPRING 에서 알아서 BEAN 을 주입 해준다.
	private InterBoardService service;
	// BEAN 으로 올라가져 있는 것은 어떻게 아는 것인가? => 다형성으로 인해 BoardService 가
	// Component 되 있기 때문이라고 생각이드는데 맞는지 확인해볼걸

	/*
	 * @Resource// TYPE 에 따라서 , SPRING 에서 알아서 BEAN 을 주입 해준다. private
	 * InterBoardService boardService ; // Resource 는 아무런 이름을 쓰면 안되고,
	 * InterBoardService 를 부모로 가지고 있는 BoardService 클래스의 이름으로 해야하고, 첫문자는 무조건 소문자로
	 * 해야한다.
	 * 
	 * @Inject// TYPE 에 따라서 , SPRING 에서 알아서 BEAN 을 주입 해준다. private InterBoardService
	 * service ; // 뒤의 이름은 아무렇게나 써도 된다.
	 */
		
		// === #155. 파일업로드 및 다운로드를 해주는 FileManager 클래스 의존객체 주입하기(DI : Dependency Injection) ===  
	   @Autowired     // Type에 따라 알아서 Bean 을 주입해준다.
	   private FileManager fileManager;
	
	
	// ==== **** Spring 기초 시작 **** ==== //
	@RequestMapping(value = "/test/test_insert.action")
	public String test_insert(HttpServletRequest request) {
		// Service 단
		int n = service.test_insert();

		String message = "";
		if (n == 1) {
			message = " 데이터 입력성공 ";
		} else {
			message = " 데이터 입력 실패 ";
		}

		request.setAttribute("message", message);
		request.setAttribute("n", n);

		// View 단
		return "test/test_insert";
		// WEB-INF/views/test/test_insert.jsp 를 만들어야 한다.
		// 접두어와 접미어 를 확인 할 수 있는 곳은 servlet-context.xml 에서 확인할 수 있다.

	}

	@RequestMapping(value = "/test/test_select.action")
	public String test_select(HttpServletRequest request) {
		List<TestVO> testvoList = service.test_select();

		request.setAttribute("testvoList", testvoList);
		return "test/test_select";
		// WEB-INF/views/test/test_select.jsp 를 만들어야 한다.
		// 접두어와 접미어 를 확인 할 수 있는 곳은 servlet-context.xml 에서 확인할 수 있다.
	}
	// ==== **** Spring 기초 끝 **** ==== //

	// @RequestMapping(value="/test/test_form.action", method =
	// {RequestMethod.POST})// 오로지 POST 방식 만 허락하는 것임
	// 만약에, 위와 같이 하고서 , GET 방식으로 들어가면 , 405 에러가 생긴다. 그러면서 , 허락하지 않는 메소드라고 오류가 뜬다.

	// @RequestMapping(value="/test/test_form.action", method =
	// {RequestMethod.GET})// 오로지 GET 방식 만 허락하는 것임
	@RequestMapping(value = "/test/test_form.action") // GET 방식 및 POST 방식 을 모두 허락하는 것임.
	public String test_form(HttpServletRequest request) {
		String method = request.getMethod();
		if ("GET".equalsIgnoreCase(method)) { // GET방식이라면
			return "test/test_form";
		}

		else { // POST 방식이라면
			String no = request.getParameter("no");
			String name = request.getParameter("name");
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("no", no);
			paraMap.put("name", name);

			// int n = service.test_register(no , name ); 은 작동하지 않는다. 무조건 Map 으로 보내줘야 한다.
			int n = service.test_register(paraMap);

			if (n == 1) {
				return "redirect:/test/test_select.action";
				// /test/test_select.action 해당 URL(페이지) 로 Redirect ( 페이지 이동 ) 하라는 말이다.
			} else {
				return "redirect:/test/test_form.action";
			}
		}

	} // end of test_form

	@RequestMapping(value = "/test/test_form_vo.action") // GET 방식 및 POST 방식 을 모두 허락하는 것임.
	public String test_form_vo(HttpServletRequest request, TestVO tvo) {
		String method = request.getMethod();
		if ("GET".equalsIgnoreCase(method)) { // GET방식이라면
			return "test/test_form_vo";
		}

		else { // POST 방식이라면
			int n = service.test_register(tvo);

			if (n == 1) {
				return "redirect:/test/test_select.action";
				// /test/test_select.action 해당 URL(페이지) 로 Redirect ( 페이지 이동 ) 하라는 말이다.
			} else {
				return "redirect:/test/test_form_vo.action";
			}
		}

	} // end of test_form

	@RequestMapping(value = "/test/test_form_2.action", method = { RequestMethod.GET }) // 오로지 GET 방식 만 허락하는 것임
	public String test_form_2() {
		return "test/test_form_2"; // view 단 페이지를 띄워라
	}

	// 메소드 이름은 똑같으면 안됨!. But, 메소드의 오버로딩은 괜찮다.
	@RequestMapping(value = "/test/test_form_2.action", method = { RequestMethod.POST }) // 오로지 GET 방식 만 허락하는 것임
	public String test_form_2(HttpServletRequest request, TestVO tvo) {
		int n = service.test_register(tvo);

		if (n == 1) {
			return "redirect:/test/test_select.action";
			// /test/test_select.action 해당 URL(페이지) 로 Redirect ( 페이지 이동 ) 하라는 말이다.
		} else {
			return "redirect:/test/test_form_2.action";
		}

	}// END OF TEST_FORM_2

	// === return 타입을 String 대신에 ModelAndView 를 사용해 보겠습니다.
	@RequestMapping(value = "/test/test_form_vo_modelAndView.action") // GET 방식 및 POST 방식 을 모두 허락하는 것임.
	public ModelAndView test_form_vo_modelAndView(HttpServletRequest request, TestVO tvo, ModelAndView mav) {
		// ModelAndView 스프링에서 제공하는 부분이다.

		String method = request.getMethod();
		if ("GET".equalsIgnoreCase(method)) { // GET방식이라면
			mav.setViewName("test/test_form_vo_modelAndView");
		}

		else { // POST 방식이라면
			int n = service.test_register(tvo);

			if (n == 1) {
				mav.setViewName("redirect:/test/test_select_modelAndView.action");
				// /test/test_select.action 해당 URL(페이지) 로 Redirect ( 페이지 이동 ) 하라는 말이다.
			} else {
				mav.setViewName("redirect:/test/test_form_vo_modelAndView");
			}
		}

		return mav;
	} // end of test_form

	@RequestMapping(value = "/test/test_select_modelAndView.action", method = { RequestMethod.GET }) // 오로지 GET 방식 만
																										// 허락하는 것임
	public ModelAndView test_select_modelAndView(ModelAndView mav) {
		List<TestVO> testvoList = service.test_select();

		mav.addObject("testvoList", testvoList);
		// request.setAttribute 와 똑같은 것이다.

		mav.setViewName("test/test_select_modelAndView");
		// /WEB-INF/views/test/test_select_modelAndView.jsp 페이지로 이동하는 것이다.
		return mav; // view 단 페이지를 띄워라
	}

	// #### AJAX 연습 시작 #### //
	@RequestMapping(value = "/test/test_form_3.action", method = { RequestMethod.GET }) // 오로지 GET 방식 만 허락하는 것임
	public ModelAndView test_form_3(ModelAndView mav) {
		mav.setViewName("test/test_form_3");
		// /WEB-INF/views/test/test_select_modelAndView.jsp 페이지로 이동하는 것이다.
		return mav; // view 단 페이지를 띄워라
	}

	/*
	 * @ResponseBody 란? 메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단
	 * 페이지를 통해서 출력되는 것이 아니라 return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 일반적으로 JSON
	 * 값을 Return 할때 많이 사용된다.
	 */

	// AJAX 는 RETURN 타입이 ModelAndView 가 가능하지 않는다. 이유는 ?
	@ResponseBody
	@RequestMapping(value = "/test/ajax_insert.action", method = { RequestMethod.POST }) // 오로지 POST 방식 만 허락하는 것임
	public String ajax_insert(HttpServletRequest request) {
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);

		int n = service.test_register(paraMap);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);

		return jsonObj.toString();
	}

	/*
	 * @ResponseBody 란? 메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단
	 * 페이지를 통해서 출력되는 것이 아니라 return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 일반적으로 JSON
	 * 값을 Return 할때 많이 사용된다. 그냥 쓰면 , 한글이 깨져버리는 경우가 생긴다. f >>> 스프링에서 json 또는 gson을
	 * 사용한 ajax 구현시 데이터를 화면에 출력해 줄때 한글로 된 데이터가 '?'로 출력되어 한글이 깨지는 현상이 있다. 이것을 해결하는
	 * 방법은 @RequestMapping 어노테이션의 속성 중 produces="text/plain;charset=UTF-8" 를 사용하면 응답
	 * 페이지에 대한 UTF-8 인코딩이 가능하여 한글 깨짐을 방지 할 수 있다. <<<
	 * 
	 */

	@ResponseBody
	@RequestMapping(value = "/test/ajax_select.action", method = {
			RequestMethod.GET }, produces = "text/plain;charset=UTF-8")
	public String ajax_select() {
		List<TestVO> testvoList = service.test_select();
		JSONArray jsonArr = new JSONArray(); // []
		if (testvoList != null) {
			for (TestVO vo : testvoList) {
				JSONObject jsonObj = new JSONObject(); // { }
				jsonObj.put("no", vo.getNo());
				jsonObj.put("name", vo.getName());
				jsonObj.put("writeday", vo.getWriteday());

				jsonArr.put(jsonObj);
			} // end of for 문
		}
		return jsonArr.toString();
	}

	// #### AJAX 연습 끝 #### //

	// ==== ***** tiles 연습 시작 ***** ==== //
	@RequestMapping(value = "/test/tiles_test_1.action")
	public String tiles_test_1() {

		return "tiles_test_1.tiles1";
		// /WEB-INF/views/tiles1/tiles1/tiles_test
	}

	@RequestMapping(value = "/test/tiles_test_2.action")
	public String tiles_test_2() {

		return "test/tiles_test_2.tiles1";
	}

	@RequestMapping(value = "/test/tiles_test_3.action")
	public String tiles_test_3() {

		return "test/sample/tiles_test_3.tiles1";
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	@RequestMapping(value = "/test/tiles_test_4.action")
	public ModelAndView tiles_test_4(ModelAndView mav) {
		mav.setViewName("tiles_test_4.tiles2");
		// /WEB-INF/views/tiles2/{1}.jsp

		return mav;
	}

	@RequestMapping(value = "/test/tiles_test_5.action")
	public ModelAndView tiles_test_5(ModelAndView mav) {
		mav.setViewName("test/tiles_test_5.tiles2");
		return mav;
	}

	@RequestMapping(value = "/test/tiles_test_6.action")
	public ModelAndView tiles_test_6(ModelAndView mav) {

		mav.setViewName("test/sample/tiles_test_6.tiles2");
		return mav;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/test/tiles_test_7.action")
	public ModelAndView tiles_test_7(ModelAndView mav) {
		mav.setViewName("test7/tiles_test_7.tiles3");
		// /WEB-INF/views/tiles3/{1}/content/{2}.jsp
		// /WEB-INF/views/tiles3/{1}/side.jsp

		return mav;
	}

	@RequestMapping(value = "/test/tiles_test_8.action")
	public ModelAndView tiles_test_8(ModelAndView mav) {
		mav.setViewName("test8/tiles_test_8.tiles3");
		// /WEB-INF/views/tiles3/{1}/content/{2}/{3}.jsp
		// /WEB-INF/views/tiles3/{1}/side.jsp

		return mav;
	}

	@RequestMapping(value = "/test/tiles_test_9.action")
	public ModelAndView tiles_test_9(ModelAndView mav) {
		mav.setViewName("test/tiles_test_9.tiles4");
		// /WEB-INF/views/tiles4/{1}/content/{2}.jsp

		return mav;
	}

	@RequestMapping(value = "/test/tiles_test_10.action")
	public ModelAndView tiles_test_10(ModelAndView mav) {
		mav.setViewName("test/test10");
		// /WEB-INF/views/test/test10.jsp 페이지를 만들어야 한다.

		return mav;
	}

	// ==== ***** tiles 연습 끝 ***** ==== //

	////////////////////////////////////////////////////////////////////////////////////////////////////

	// ========== **** 게시판 시작 **** ========== //

	// === #36. 메인 페이지 요청 === //
	// 먼저 com.spring.board.HomeController 클래스에 가서 @Controller 를 주석처리 한다.

	@RequestMapping(value = "/")
	public ModelAndView home(ModelAndView mav) {

		mav.setViewName("redirect:/index.action");
		// /WEB-INF/views/tiles1/{1}/{2}.jsp

		return mav;
	}

	/*
	 * @ RequestMapping(value="/index.action") public ModelAndView index (
	 * ModelAndView mav) { List<String> imgfilenameList =
	 * service.getImgfilenameList();
	 * 
	 * 
	 * mav.addObject("imgfilenameList", imgfilenameList) ;
	 * mav.setViewName("main/index.tiles1"); // /WEB-INF/views/tiles1/{1}/{2}.jsp
	 * 
	 * return mav ; }
	 */

	// 또는
	@RequestMapping(value = "/index.action")
	public ModelAndView index(ModelAndView mav) {
		mav = service.index(mav);
		return mav;
	}

	// === #40. 로그인 홈페이지 요청 === //
	@RequestMapping(value = "/login", method = { RequestMethod.GET })
	public ModelAndView login(ModelAndView mav) {
		mav.setViewName("login/loginform.tiles1");
//		  		/WEB-INF/views/tiles1/{1}/{2}.jsp 

		return mav;
	}

	// === #41. 로그인 처리하기 === //
	/*
	 * @ RequestMapping(value="/loginEnd.action" , method = {RequestMethod.POST})
	 * public ModelAndView loginEnd (HttpServletRequest request,ModelAndView mav) {
	 * String userid = request.getParameter("userid"); String pwd =
	 * request.getParameter("pwd");
	 * 
	 * Map<String,String> paraMap = new HashMap<>(); paraMap.put("userid", userid );
	 * paraMap.put("pwd", Sha256.encrypt(pwd));
	 * 
	 * MemberVO loginuser = service.getLoginMember(paraMap);
	 * 
	 * if ( loginuser == null ) { // 로그인 실패시 String message = " 아이디 또는 암호가 틀립니다";
	 * String loc = "javascript:history.back()";
	 * 
	 * mav.addObject("message", message); mav.addObject("loc", loc);
	 * mav.setViewName("msg"); // /WEB-INF/views/msg.jsp } else { // 아이디와 그에 맞는
	 * 비밀번호를 입력한 경우
	 * 
	 * if ( loginuser.getIdle() == 1 ) { // 로그인 한지 1년이 경과한 경우 String message =
	 * " 로그인을 한지 1년이 지나서 휴면상태도 되었습니다. \\n  관리자에게 문의 바랍니다."; String loc =
	 * request.getContextPath()+"/index.action" ; // 원래는 위와 같이 index.action 이 아니라
	 * 휴면인 계정을 풀어주는 페이지로 잡아주어야 한다.
	 * 
	 * mav.addObject("message", message); mav.addObject("loc", loc);
	 * mav.setViewName("msg"); } else { // 로그인 한지 1년 이내인 경우 HttpSession session =
	 * request.getSession(); // 메모리에 생성되어져 있는 session 을 불러오는 것이다 .
	 * session.setAttribute("loginuser", loginuser); // session(세션)에 로그인 되어진 사용자 정보인
	 * loginuser 을 키이름을 "loginuser" 으로 저장시켜두는 것이다.
	 * 
	 * if ( loginuser.isRequirePwdChange() == true ) { // 암호를 마지막으로 변경한 것이 3개월 경과한
	 * 경우 String message = " 비밀번호를 변경하신지 3개월이 지났습니다. \\n 암호를 변경하시는 것을 추천드립니다. ";
	 * String loc = request.getContextPath()+"/index.action" ; // 원래는 위와 같이
	 * index.action 이 아니라 사용자의 암호를 변경해주는 페이지로 잡아주어야 한다.
	 * 
	 * mav.addObject("message", message); mav.addObject("loc", loc);
	 * mav.setViewName("msg"); } else { // 로그인 한지 1년 이내이고 암호를 마지막으로 변경한 것이 3개월 이내인
	 * 경우 // 로그인을 해야만 접근할 수 있는 페이지에 로그인을 하지 않은 상태에서 접근을 시도한 경우 // "먼저 로그인을 하세요!!" 라는
	 * 메시지를 받고서 사용자가 로그인을 성공했다라면 // 화면에 보여주는 페이지는 시작페이지로 가는 것이 아니라 // 조금전 사용자가 시도하였던
	 * 로그인을 해야만 접근할 수 있는 페이지로 가기 위한 것이다 /*if ( session.getAttribute("url") != null)
	 * { mav.setViewName("redirect:"+session.getAttribute("url")); }
	 */
	/*
	 * String url = (String)session.getAttribute("url") ; if ( url != null ) {
	 * mav.setViewName("redirect:"+url); session.removeAttribute("url"); // 삭제하는 거
	 * 까먹지 말자 // 삭제하지 않는다면, 로그아웃하고 다시 로그인 할 경우에 이동하는 곳이 달라 질 수 있다. }
	 * 
	 * else { // mav.setViewName("main/index.tiles1");
	 * mav.setViewName("redirect:/index.action"); } } }
	 * 
	 * 
	 * }
	 * 
	 * return mav ; }
	 */
	// 또는
	@RequestMapping(value = "/loginEnd.action", method = { RequestMethod.POST })
	public ModelAndView loginEnd(HttpServletRequest request, ModelAndView mav) {
		String userid = request.getParameter("userid");
		String pwd = request.getParameter("pwd");
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("pwd", Sha256.encrypt(pwd));

		mav = service.loginEnd(mav, request, paraMap);

		return mav;
	}

	// === #50. 로그아웃 처리하기 === //
	@RequestMapping(value = "/logout.action")
	public ModelAndView logout(HttpServletRequest request, ModelAndView mav) {
		HttpSession session = request.getSession();
		// 첫번째 방법 : 세션을 그대로 존재하게 끔 해두고 세션에 저장되어진 어떤 값(지금은 로그인 되어진 회원 객체)을 삭제하기
		// session.removeAttribute("loginuser");

		// 두번째 방법 : WAS 메모리 상에서 세션을 아예 삭제해버리기
		// 두번째 방법이 오히려 나음. 메모리 부과가 첫번째 방법보다 덜하다는 장점이 존재.
		session.invalidate();

		String message = " 로그아웃 되었습니다.";
		String loc = request.getContextPath() + "/index.action";
		// 원래는 위와 같이 index.action 이 아니라 휴면인 계정을 풀어주는 페이지로 잡아주어야 한다.

		mav.addObject("message", message);
		mav.addObject("loc", loc);
		mav.setViewName("msg");
		return mav;
	}

	// === #51. 게시판 글쓰기 홈페이지 요청 === //
	@RequestMapping(value = "/add.action", method = { RequestMethod.GET })
	public ModelAndView requiredLogin_add(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		// ! ! ! 중요 ! ! ! AOP 에 관해서 !! => Advice 에 보내주는 파라미터는 무조건 위치가 똑같아야 한다. 그러므로,
		// request를 첫번째로 옮긴다

		
		// === #142. 답변글쓰기가 추가된 경우 시작 === //
		String  subject  =request.getParameter("subject");
		String fk_seq = request.getParameter("fk_seq");		
		String groupno = request.getParameter("groupno");	
		String depthno = request.getParameter("depthno");		
		
		subject = "[답변]" + subject ;
		
		if ( fk_seq == null ) {
			fk_seq = "";
		}
		/*
        view.jsp 에서 "답변글쓰기" 를 할때 글제목에 [ 또는 ] 이 들어간 경우 아래와 같은 오류가 발생한다.
              
        HTTP 상태 400 – 잘못된 요청
           메시지 요청 타겟에서 유효하지 않은 문자가 발견되었습니다. 유효한 문자들은 RFC 7230과 RFC 3986에 정의되어 있습니다.
           
           해결책은 
           톰캣의 C:\apache-tomcat-9.0.55\conf\server.xml 에서 
        <Connector port="9090" URIEncoding="UTF-8" protocol="HTTP/1.1"
             connectionTimeout="20000"
             redirectPort="8443" /> 
                    에 가서
          <Connector port="9090" URIEncoding="UTF-8" protocol="HTTP/1.1"
             connectionTimeout="20000"
             redirectPort="8443"
             relaxedQueryChars="[]()^|&quot;" />  
                   
                    와 같이 relaxedQueryChars="[]()^|&quot;" 을 추가해주면 된다.    
    */
		
		mav.addObject("groupno",groupno);
		mav.addObject("depthno", depthno);
		mav.addObject("subject", subject) ;
		mav.addObject("fk_seq" , fk_seq);
		// === #142. 답변글쓰기가 추가된 경우 끝 === //
		
		mav.setViewName("board/add.tiles1");
		return mav;
	}

	// === #54. 게시판 글쓰기 완료 요청 === //
	@RequestMapping(value = "/addEnd.action", method = { RequestMethod.POST })
		//public ModelAndView addEnd(ModelAndView mav , BoardVO boardvo) {   <== After Advice 를 사용하기 전  
		// public ModelAndView pointPlus_addEnd(Map<String, String> paraMap, ModelAndView mav, BoardVO boardvo) { // <== After Advice 를 사용하기
		public ModelAndView pointPlus_addEnd(Map<String, String> paraMap, ModelAndView mav, BoardVO boardvo , MultipartHttpServletRequest mreRequest) { // <== 파일 첨부하기 추가
		// MultipartHttpServletRequest 은 SPRING 에서 제공하는 것이다. 
		
		// form 태그의 name 명과 BoardVO 의 필드명이 같다라면 ,
		// request.getParameter( "form태그의 name 명"); 을 사용하지 않더라도
		// 자동적으로 BoardVO boardvo 에 SET 되어지는 것이다.

		/*
	      === #151. 파일첨부가 된 글쓰기 이므로  
	              먼저 위의  public ModelAndView pointPlus_addEnd(Map<String,String> paraMap, ModelAndView mav, BoardVO boardvo) { 을 
	              주석처리 한 이후에 아래와 같이 한다.
	          MultipartHttpServletRequest mrequest 를 사용하기 위해서는 
	              먼저 /Board/src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml 에서     
	          #21 파일업로드 및 파일다운로드에 필요한 의존객체 설정하기 를 해두어야 한다.  
	   */
		
		/*
        웹페이지에 요청 form이 enctype="multipart/form-data" 으로 되어있어서 Multipart 요청(파일처리 요청)이 들어올때 
        컨트롤러에서는 HttpServletRequest 대신 MultipartHttpServletRequest 인터페이스를 사용해야 한다.
     	MultipartHttpServletRequest 인터페이스는 HttpServletRequest 인터페이스와  MultipartRequest 인터페이스를 상속받고있다.
		즉, 웹 요청 정보를 얻기 위한 getParameter()와 같은 메소드와 Multipart(파일처리) 관련 메소드를 모두 사용가능하다.     
		 */   
		
		// ===  사용자가 쓴 글에 파일이 첨부되어 있는 것인지 , 아니면 , 파일첨부가 안된 것인지 알아 보아야 한다.
		// === #153. ! ! ! 첨부파일이 있는 경우 작업시작 ! ! ! ===
		MultipartFile attach = boardvo.getAttach();
		
		if ( !attach.isEmpty()) {
				// attach( 첨부파일 ) 가 비어 있지 않으면  ( 즉, 첨부파일이 있는 경우 라면 )
		
			/*
            1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다. 
            >>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
                       우리는 WAS의 webapp/resources/files 라는 폴더로 지정해준다.
                       조심할 것은  Package Explorer 에서  files 라는 폴더를 만드는 것이 아니다.       
         	// WAS 의 webapp 의 절대경로를 알아와야 한다.
         */
		 HttpSession session = mreRequest.getSession();
		 String root = session.getServletContext().getRealPath("/"); // webapp 의 절대경로를 알아오는 것이다.
		
		 // System.out.println("~~~ 확인용 webapp 의 절대경로  : " + root  );
		 // ~~~ 확인용 webapp 의 절대경로  : C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\
		 
		 String path = root + "resources" + File.separator+"files";
		 
		 /* File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
         운영체제가 Windows 이라면 File.separator 는  "\" 이고,
         운영체제가 UNIX, Linux, 매킨토시(맥) 이라면  File.separator 는 "/" 이다. 
		  */
		// System.out.println("~~~ 확인용 path 의 절대경로  : " + path );
		// ~~~ 확인용 path 의 절대경로  : C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\files

		 /*
		   		2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기 
		   		
		  */
		 	String newFileName = "";
		 	// WAS(톰캣) 의 디스크에 저장될 파일명 
		 	byte[] bytes = null ;  // 첨부파일의 내용물을 담는 것
		 	
		 	long fileSize = 0  ;  // 첨부파일의 크기 
		 	
		 	try {
				bytes = attach.getBytes(); // 첨부파일의 내용물을 읽어오는 것
				
				String originalFilename = attach.getOriginalFilename(); 
				// attach.getOriginalFilename() 이 첨부파일명의 파일명(예: 강아지.png) 이다.
				
				// System.out.println("=== 확인용 originalFilename :  "+ originalFilename ); 
				// === 확인용 originalFilename :  제품11.jpg
				
				newFileName = fileManager.doFileUpload(bytes, originalFilename, path);
				// 첨부되어진 파일을 업로드 하는 것이다. 
				// System.out.println("~~~ 확인용 newFileName 의 절대경로  : " + newFileName  );
				// ~~~ 확인용 newFileName 의 절대경로  : 20230522103602265395795259899.jpg
				
				/*
				  		3.  BoardVO boardvo 에 fileName 값과 orgFilename 값과 fileSize 값을 넣어주기 
				  		
				 */
				 boardvo.setFileName(newFileName);
				 // WAS(톰캣) 에 저장된 파일명 ()
				 
				 boardvo.setOrgFilename(originalFilename);
				// 게시판 페이지에서 첨부된 파일(강아지.png)을 보여줄 때 사용.
		        // 또한 사용자가 파일을 다운로드 할때 사용되어지는 파일명으로 사용.
				 
				boardvo.setFileSize(String.valueOf(attach.getSize())); 
				 // 첨부파일의 크기 ( 단위는 byte 임 )
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
		 	
		}
		
		
		
		
		
		// ===  ! ! ! 첨부파일이 있는 경우 작업끝 ! ! ! ===
		
		// int n = service.add(boardvo); // <== 파일첨부가 없는 글쓰기
		
		// === #156. 파일첨부가 있는 글쓰기 또는 파일첨부가 없는 글쓰기로 나뉘어서 service 호출하기 === // 
		// 		먼저 위의  int n = service.add(boardvo); 부분을 주석처리 하고서 아래와 같이한다.
		int n =  0  ; 
		if ( attach.isEmpty() ) {
			// 파일 첨부가 없는 경우라면
			n = service.add(boardvo);
		}
		else {
			// 파일 첨부가 있는 경우라면
			n = service.add_withFile(boardvo);
		}
		
		if (n == 1) {
			mav.setViewName("redirect:/list.action");
		} else {
			mav.setViewName("board/error/add_error.tiles1");
			// */*/*.tiles1
			// /WEB-INF/views/tiles1/{1}/{2}/{3}.jsp
		}

		// ==== #96. After Advice 를 사용하기 === //
		// 글 쓰기를 한 이후에 회원의 포인트를 100점 증가
		// === After Advice 를 사용하기 위해 파라미터를 생성하는 것임. ===
		paraMap.put("userid", boardvo.getFk_userid());
		paraMap.put("point", "100");

		return mav;
	}

	// === #58. 글 목록 보기 페이지 요청 ===
	/*
	 * @ RequestMapping(value="/list.action" ) public ModelAndView list(ModelAndView
	 * mav , HttpServletRequest request) { List<BoardVO> boardList = null ; // ==
	 * 페이징 처리를 안한 검색어가 없는 전체 글 목록 보여주기 == //
	 * ////////////////////////////////////////////////////// // === #69.
	 * 글조회수(readCount)증가 (DML문 update)는 // 반드시 목록보기에 와서 해당 글제목을 클릭했을 경우에만 증가되고, //
	 * 웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 되지 않도록 해야 한다. // 이것을 하기 위해서는 session 을 사용하여
	 * 처리하면 된다. HttpSession session = request.getSession();
	 * session.setAttribute("readCountPermission", "yes");
	 * 
	 * /* session 에 "readCountPermission" 키값으로 저장된 value값은 "yes" 이다. session 에
	 * "readCountPermission" 키값에 해당하는 value값 "yes"를 얻으려면 반드시 웹브라우저에서 주소창에
	 * "/list.action" 이라고 입력해야만 얻어올 수 있다.
	 * 
	 * //////////////////////////////////////////////////////
	 * 
	 * // boardList = service.boardListNoSearch();
	 * 
	 * // === #102. 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 == // String searchType =
	 * request.getParameter("searchType"); String searchWord =
	 * request.getParameter("searchWord"); if ( searchType == null ) { searchType =
	 * ""; } if (searchWord == null ) { searchWord = ""; } if ( searchWord != null )
	 * { searchWord = searchWord.trim(); } Map<String, String> paraMap = new
	 * HashMap<>(); paraMap.put("searchType", searchType) ;
	 * paraMap.put("searchWord", searchWord) ;
	 * 
	 * boardList = service.boardListSearch(paraMap);
	 * 
	 * // 아래는 검색대상 컬럼과 검색어를 유지시키기 위한 것임. if (! "".equals(searchType) &&
	 * !"".equals(searchWord) ) { mav.addObject("searchType",searchType);
	 * mav.addObject("searchWord",searchWord); }
	 * 
	 * mav.addObject("boardList", boardList) ;
	 * 
	 * mav.setViewName("board/list.tiles1");
	 * 
	 * return mav ; }
	 */

	// === #58. 글목록 보기 페이지 요청 === //
	   @RequestMapping(value="/list.action")
	   public ModelAndView list(ModelAndView mav, HttpServletRequest request) {
		   
		   List<BoardVO> boardList = null; // 글이 없을 수 있기 떄문에 null로 설정
		   
		   //////////////////////////////////////////////////////
		   // === #69. 글조회수(readCount)증가 (DML문 update)는
		   //          반드시 목록보기에 와서 해당 글제목을 클릭했을 경우에만 증가되고,
		   //          웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 되지 않도록 해야 한다.
		   //          이것을 하기 위해서는 session 을 사용하여 처리하면 된다.
		   HttpSession session = request.getSession();
		   session.setAttribute("readCountPermission", "yes");
		   /*
		       session 에  "readCountPermission" 키값으로 저장된 value값은 "yes" 이다.
		       session 에  "readCountPermission" 키값에 해당하는 value값 "yes"를 얻으려면 
		       반드시 웹브라우저에서 주소창에 "/list.action" 이라고 입력해야만 얻어올 수 있다. 
		   */
	      //////////////////////////////////////////////////////
		   
		   
		   // == 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 == //
		   // boardList = service.boardListNoSearch();
		   
		   // === #102. 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 === //
	   /*
		   String searchType = request.getParameter("searchType");
		   String searchWord = request.getParameter("searchWord");
		   
		   if(searchType == null) {
			   searchType = "";
			   
		   }
		   if(searchWord == null) {
			   searchWord = "";
		   }
		   
		   if(searchWord != null) {
			   searchWord = searchWord.trim();
			   // "                   " ==> ""
		   }
		   
		   Map<String, String> paraMap = new HashMap<>();
		   paraMap.put("searchType", searchType);
		   paraMap.put("searchWord", searchWord);
		   
		   boardList = service.boardListSearch(paraMap);  // 유저에게 입력받은 검색어를 보내준다.
		   
		   // 아래는 검색대상 컬럼과 검색어를 유지시키기 위한 것임.
		   if( !"".equals(searchType) && !"".equals(searchWord) ) {
			   mav.addObject("paraMap", paraMap);
		   }
	    */
		   ///////////////////////////////////////////////////////////////////////////
		   
		   // === #114. 페이징 처리를 한 검색어가 있는 전체 글목록 보여주기 === //
		   /* 페이징 처리를 통한 글목록 보여주기는 
	       
	       예를 들어 3페이지의 내용을 보고자 한다라면 검색을 할 경우는 아래와 같이
	       list.action?searchType=subject&searchWord=안녕&currentShowPageNo=3 와 같이 해주어야 한다.
	       
	       또는 
	       
	       검색이 없는 전체를 볼때는 아래와 같이 
	       list.action 또는 
	       list.action?searchType=&searchWord=&currentShowPageNo=3 또는 
	       list.action?searchType=subject&searchWord=&currentShowPageNo=3 또는
	       list.action?searchType=name&searchWord=&currentShowPageNo=3 와 같이 해주어야 한다.
		*/
		   String searchType = request.getParameter("searchType");
		   String searchWord = request.getParameter("searchWord");
		   String str_currentShowPageNo = request.getParameter("currentShowPageNo");
		   
		   if  ( searchType == null || (!"subject".equals(searchType) && !"name".equals(searchType) )) {
			   searchType = "";
		   }
		   
		   if  ( searchWord == null || "".equals(searchWord) || searchWord.trim().isEmpty() ) {
			   searchWord = "";
		   }	
		   
		   Map<String, String> paraMap = new HashMap<>();
		   paraMap.put("searchType", searchType);
		   paraMap.put("searchWord", searchWord);
		   
		   // 먼저 총 게시물 건수(totalCount)를 구해와야 한다.
		   // 총 게시물 건수(totalCount)는 검색조건이 있을 때와 없을때로 나뉘어진다. 
		   int totalCount = 0;        // 총 게시물 건수
		   int sizePerPage = 10;       // 한 페이지당 보여줄 게시물 건수 
		   int currentShowPageNo = 0; // 현재 보여주는 페이지번호로서, 초기치로는 1페이지로 설정함.
		   int totalPage = 0;         // 총 페이지수(웹브라우저상에서 보여줄 총 페이지 개수, 페이지바)
		      
		   int startRno = 0; // 시작 행번호
		   int endRno = 0;   // 끝 행번호
		   
		   // 총 게시물 건수(totalCount) 
		   totalCount = service.getTotalCount(paraMap);
		   // System.out.println(" ~~~ 확인용 totalCount ~~~ :  "+ totalCount);
		   
		   // 만약에 총 게시물 건수( totalCount) 가 212 개라면 
		   // 총 페이지수(totalPage) 는 22개가 되어야 한다.
		   // totalPage = (int)Math.ceil( (double)(totalCount/sizePerPage)) ;
		  
		   totalPage = (int)Math.ceil((double)totalCount / sizePerPage);
		   if (str_currentShowPageNo == null ) {
			   currentShowPageNo = 1 ;
		   }
		   else {
			   try {	
				   currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
				   
				   if ( currentShowPageNo < 1 || currentShowPageNo > totalPage) {
					   currentShowPageNo = 1 ;
				   }
				   
				   	  // **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
				      /*
				           currentShowPageNo      startRno     endRno
				          --------------------------------------------
				               1 page        ===>    1           10
				               2 page        ===>    11          20
				               3 page        ===>    21          30
				               4 page        ===>    31          40
				               ......                ...         ...
				       */
				   	
				      
			   }catch(NumberFormatException e) {
				   currentShowPageNo = 1 ;
			   }
		   }

		   startRno = ((currentShowPageNo - 1) * sizePerPage) + 1;
		   endRno = startRno + sizePerPage - 1;
		   
		   paraMap.put("startRno", String.valueOf(startRno)) ;
		   paraMap.put("endRno", String.valueOf(endRno)) ;
		   
		   boardList = service.boardListSearchWithPaging(paraMap);  // 유저에게 입력받은 검색어를 보내준다.
		   // 페이징 처리한 글목록 가져오기 ( 검색이 있든지 , 검색이 없든지 모두 다 포함한 것 )
		   
		   mav.addObject("boardList", boardList); // request.setAttribute(); 와 같다
		   
		   if( !"".equals(searchType) && !"".equals(searchWord) ) {
			   mav.addObject("paraMap", paraMap);
		   }
		   
		   // === #121. 페이지바 만들기 ==== 
		   int blockSize = 10;
		      // blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수이다.
		      /*
		                       1  2  3  4  5  6  7  8  9 10 [다음][마지막]  -- 1개블럭
		         [맨처음][이전]  11 12 13 14 15 16 17 18 19 20 [다음][마지막]  -- 1개블럭
		         [맨처음][이전]  21 22 23
		      */
		      
		      int loop = 1;
		      /*
		          loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		       */
		      
		      int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
		      // *** !! 공식이다. !! *** //
		         
		      String pageBar = "<ul style='list-style: none;'>";
		      String url = "list.action";
		      // System.out.println("확인용 : pageNo = " + pageNo);
		      // System.out.println("확인용 : currentShowPageNo = " + currentShowPageNo);
		      // System.out.println("확인용 : totalPage = " + totalPage);
		      
		      // === [맨처음][이전] 만들기 === //
		      if(pageNo != 1) {
		         pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'>[맨처음]</a></li>";
		         pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'>[이전]</a></li>";
		      }
		      
		      while( !(loop > blockSize || pageNo > totalPage) ) {
		         
		         if(pageNo == currentShowPageNo) {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";  
		         }
		         else {
		            pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>"; 
		         }
		         
		         loop++;
		         pageNo++;
		         
		      }// end of while-----------------------
		      
		      
		      // === [다음][마지막] 만들기 === //
		      if( pageNo <= totalPage ) {
		         pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>[다음]</a></li>";
		         pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'>[마지막]</a></li>"; 
		      }
		      
		      pageBar += "</ul>";
		      
		      // 다시 돌아옴 
		      // === #123. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
		      //           사용자가 "검색된결과목록보기" 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
		      //           현재 페이지 주소를 뷰단으로 넘겨준다.
		      String gobackURL = MyUtil.getCurrentURL(request);
		      // System.out.println(" ~~~~ 확인용 goBackURL : "+ gobackURL);
		      
		      // : /list.action?searchType=&searchWord=&currentShowPageNo=8
		      // /list.action?searchType=name&searchWord=%EC%9D%B4%EC%88%9C%EC%8B%A0
		      // %EC%9D%B4%EC%88%9C%EC%8B%A0 검색어가 한글로 되어진 이순신이다. 
		      
		      
		      
		      mav.addObject("gobackURL", gobackURL.replaceAll("&", " "));
		      
		      
		      mav.addObject("pageBar", pageBar);
			  mav.setViewName("board/list.tiles1");
			  // /WEB-INF/views/tiles1/board/list.jsp 페이지를 만들어야 한다.
			  
		   return mav;
	   }
	   
	   
	// === #62. 글 1개를 보여주는 페이지 요청 === //
	@RequestMapping(value = "/view.action")
	public ModelAndView view(HttpServletRequest request, ModelAndView mav) {

		// 조회하고자 하는 글번호 받아오기
		String seq = request.getParameter("seq");

		// 글목록에서 검색되어진 글내용일 경우 이전글제목, 다음글제목은 검색되어진 결과물내의 이전글과 다음글이 나오도록 하기 위한 것이다.  시작  //
		 String searchType = request.getParameter("searchType");	
		 String searchWord = request.getParameter("searchWord");	
				
		if ( searchType == null ) {
			searchType = "" ;
		}
		
		if ( searchWord == null ) {
			searchWord= "" ;
		}
		 
		
		// 글목록에서 검색되어진 글내용일 경우 이전글제목, 다음글제목은 검색되어진 결과물내의 이전글과 다음글이 나오도록 하기 위한 것이다.  끝  //
		
		// === #125. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
        //           사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
        //           현재 페이지 주소를 뷰단으로 넘겨준다.
      String gobackURL = request.getParameter("gobackURL");
      //System.out.println("~~~ 확인용 gobackURL : " + gobackURL);
      /*
         /list.action
         /list.action?searchType=&searchWord=&currentShowPageNo=2
         /list.action?searchType=subject&searchWord=JAVA
         /list.action?searchType=name&searchWord=%EC%9D%B4%EC%88%9C%EC%8B%A0&currentShowPageNo=3
      */
      if ( gobackURL != null && gobackURL.contains(" ")) {
    	  	gobackURL = gobackURL.replaceAll(" ", "&");
      }
      // System.out.println("~~~~ view1의 gobackURL : "+gobackURL);
      
      mav.addObject("gobackURL", gobackURL);
		
		
		try {
			Integer.parseInt(seq);
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
			
			paraMap.put("searchType", searchType); // view.jsp 에서 이전글제목 및 다음글제목 클릭시 사용하기 위해서 임.
			paraMap.put("searchWord", searchWord); // view.jsp 에서 이전글제목 및 다음글제목 클릭시 사용하기 위해서 임.
			
			mav.addObject("paraMap" , paraMap) ;
			
			HttpSession session = request.getSession();
			MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
			String login_userid = null;
			if (loginuser != null) {
				login_userid = loginuser.getUserid();

			}

			paraMap.put("login_userid", login_userid);

			// === #68. !!! 중요 !!!
			// 글1개를 보여주는 페이지 요청은 select 와 함께
			// DML문(지금은 글조회수 증가인 update문)이 포함되어져 있다.
			// 이럴경우 웹브라우저에서 페이지 새로고침(F5)을 했을때 DML문이 실행되어
			// 매번 글조회수 증가가 발생한다.
			// 그래서 우리는 웹브라우저에서 페이지 새로고침(F5)을 했을때는
			// 단순히 select만 해주고 DML문(지금은 글조회수 증가인 update문)은
			// 실행하지 않도록 해주어야 한다. !!! === //
			BoardVO bdvo = null;
			// 위의 글목록보기 #69. 에서 session.setAttribute("readCountPermission", "yes"); 다음과 같이
			// 해두었다.
			if ("yes".equals(session.getAttribute("readCountPermission"))) {
				// 글목록 보기를 클릭한 다음에 특정글을 조회해 온 경우이다 .
				bdvo = service.getView(paraMap);
				// 글 조회수 증가와 함께 글 1개를 조회를 해주는 것
				session.removeAttribute("readCountPermission");
				// !! 중요함 !! session 에 저장된 readCountPermission 을 삭제한다.
			} else {
				// 웹브라우저에서 새로고침(F5)을 해온 경우이다
				// 또는 URL에 해당 경로를 직접 입력한 경우이다.
				bdvo = service.getViewWithNoAddCount(paraMap);
				// 글 조회수 증가는 없고 단순히 글 1개를 조회를 해주는 것
			}
			mav.addObject("bdvo", bdvo);

		} catch (NumberFormatException e) {
			// 무응답
		}

		mav.setViewName("board/view.tiles1");
		return mav;
	}

	// === #62. 글 1개를 보여주는 페이지 요청 === //
	@RequestMapping(value = "/view_2.action")
	public ModelAndView view_2(HttpServletRequest request, ModelAndView mav) {

		// 조회하고자 하는 글번호 받아오기
		String seq = request.getParameter("seq");

		 String searchType = request.getParameter("searchType");	
		 String searchWord = request.getParameter("searchWord");	
		 String gobackURL = request.getParameter("gobackURL");
		 
		 // System.out.println(" ~~~~ view_2 의 searchType :  " +  searchType);
		 // System.out.println(" ~~~~ view_2 의 searchWord :  " +  searchWord);
		 // System.out.println(" ~~~~ view_2 의 gobackURL :  " +  gobackURL);

		//  redirect:/ 를 할때 한글데이터는 한글이 ? 처럼 깨지므로 아래와 같이 한글깨짐을 방지해주어야 한다.
		
		 try {
			searchWord = URLEncoder.encode( searchWord , "UTF-8"); // 한글이 웹브라우저 주소창에서 사용되어질때 한글이 ? 처럼 안깨지게 하려고 하는 것임.
			gobackURL = URLEncoder.encode( gobackURL , "UTF-8");  // 한글이 웹브라우저 주소창에서 사용되어질때 한글이 ? 처럼 안깨지게 하려고 하는 것임.
			 // System.out.println(" ~~~~ view_2 의 URLEncoder.encodesearch Word :  " +  searchWord);
			 // System.out.println(" ~~~~ view_2 의URLEncoder.encode  gobackURL :  " +  gobackURL);
		 } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		 
		 
		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes");
		
		
		mav.setViewName("redirect:/view.action?seq=" + seq+"&searchType="+searchType+"&searchWord="+searchWord+"&gobackURL="+gobackURL);
		
		
		return mav;
	}

	// edit.action
	@RequestMapping(value = "/edit.action")
	public ModelAndView requiredLogin_edit(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {

		// 조회하고자 하는 글번호 받아오기
		String seq = request.getParameter("seq");

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("searchType", "");
		paraMap.put("searchWord", "");
	
		
		// 글 수정해야 할 글1개 내용 가져오기
		BoardVO bdvo = service.getViewWithNoAddCount(paraMap);
		HttpSession session = request.getSession();
		MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");

		// 글쓴이와 로그인한 사람이 같은 경우
		if (bdvo.getFk_userid().equals(loginuser.getUserid())) {
			// 자신의 글을 수정할 경우
			// 가져온 1 개 글을 수정할 폼이 있는 view 단으로 보내준다.
			mav.addObject("bdvo", bdvo);
			mav.setViewName("board/edit.tiles1");

		}

		else { // 글쓴이와 로그인한 사람이 다른 경우
			String message = " 다른 사용자의 글은 수정이 불가합니다..";
			String loc = "javascript:history.back()";
			// 원래는 위와 같이 index.action 이 아니라 휴면인 계정을 풀어주는 페이지로 잡아주어야 한다.

			mav.addObject("message", message);
			mav.addObject("loc", loc);
			mav.setViewName("msg");
		}

		return mav;
	}

	// === #72. 글 수정 페이지 완료하기 === //
	@RequestMapping(value = "/editEnd.action", method = { RequestMethod.POST })
	public ModelAndView editEnd(ModelAndView mav, BoardVO bdvo) {
		// 글 수정을 하려면 원본글의 글 암호와 수정시 입력해준 암호가 일치할때만 글 수정이 가능하도록 해야한다.
		int n = service.edit(bdvo);
		// n 이 1 이라면 정삭적으로 변경됨
		if (n == 1) {
			mav.setViewName("redirect:/view.action?seq=" + bdvo.getSeq());
		}
		// n 이 0 이라면 글수정에 필요한 글 암호가 틀린 경우임.
		else {
			String message = " 글 암호가 틀렸습니다. ";
			String loc = "javascript:history.back()";
			// 원래는 위와 같이 index.action 이 아니라 휴면인 계정을 풀어주는 페이지로 잡아주어야 한다.

			mav.addObject("message", message);
			mav.addObject("loc", loc);
			mav.setViewName("msg");
		}

		return mav;
	}

	// === #76. 글 삭제 페이지 요청하기 === //
	@RequestMapping(value = "/del.action")
	public ModelAndView requiredLogin_del(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {

		// 조회하고자 하는 글번호 받아오기
		String seq = request.getParameter("seq");
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("searchType", "");
		paraMap.put("searchWord", "");

		
		// 삭제해야할 글 1개 내용 가져와서 로그인한 사람이 쓴 글이라면 글 삭제가 가능하지만
		// 다른 사람의 글은 삭제가 불가합니다.
		BoardVO bdvo = service.getViewWithNoAddCount(paraMap);
		HttpSession session = request.getSession();
		MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");

		// 글쓴이와 로그인한 사람이 같은 경우
		if (bdvo.getFk_userid().equals(loginuser.getUserid())) {
			// 자신의 글을 삭제할 경우
			// 글 작성시 입력해준 글암호와 일치하는 지 여부를 알아오도록 암호를 입력받아주는 del.jsp 를 받아오도록 한다.
			mav.addObject("pw", bdvo.getPw());
			mav.addObject("seq", seq);
			mav.setViewName("board/del.tiles1");

		}

		else { // 글쓴이와 로그인한 사람이 다른 경우
			String message = " 다른 사용자의 글은 삭제가 불가합니다..";
			String loc = "javascript:history.back()";
			// 원래는 위와 같이 index.action 이 아니라 휴면인 계정을 풀어주는 페이지로 잡아주어야 한다.

			mav.addObject("message", message);
			mav.addObject("loc", loc);
			mav.setViewName("msg");
		}

		return mav;
	}

	@RequestMapping(value = "/delEnd.action")
	public ModelAndView delEnd(HttpServletRequest request, ModelAndView mav) {
		String seq = request.getParameter("seq");
		// System.out.println("CONT 확인용 : " + seq);
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// === #164.  파일첨부가 된 글이라면 글 삭제시 먼저 첨부 파일을 삭제해주어야 한다. 
		paraMap.put("searchType", "");
		paraMap.put("searchWord", "");
		
		BoardVO bdvo = service.getViewWithNoAddCount(paraMap);
		String fileName = bdvo.getFileName();
		
		if ( fileName != null && !"".equals(fileName) ) {
			HttpSession session = request.getSession();
			String root = session.getServletContext().getRealPath("/");
			String path = root + "resources" + File.separator + "files" ;
			
			paraMap.put("path", path); // 삭제해야할 파일이 저장된 경로
			paraMap.put("fileName", fileName) ; //삭제해야할 파일 이름
		}
		
		// 끝 
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		int n = service.del(paraMap);
		// System.out.println(n);
		if (n == 1) {
			mav.setViewName("redirect:/list.action");
		}

		// n 이 0 이라면 글수정에 필요한 글 암호가 틀린경우임.
		else {
			String message = " 글 삭제 도중에 에러가 발생했습니다.. ";
			String loc = "javascript:history.back()"; // 원래는 위와 같이 index.action 이 아니라 휴면인 계정을 풀어주는 페이지로 잡아주어야 한다.

			mav.addObject("message", message);
			mav.addObject("loc", loc);
			mav.setViewName("msg");
		}

		return mav;
	}

	// === #84. 댓글쓰기 (Ajax로 처리 ) === //
	@ResponseBody
	@RequestMapping(value = "/addComment.action", method = {
			RequestMethod.POST }, produces = "text/plain;charset=UTF-8")
	public String addComment(HttpServletRequest request, CommentVO cmvo) {
		// 댓글 쓰기에 첨부파일이 없는 경우
		int n = 0;
		try {
			n = service.addComment(cmvo);
			// 댓글쓰기(insert) 및 원게시물(tbl_board 테이블)에 댓글의 개수 증가(update 1씩 증가)하기
			// 이어서 회원의 포인트를 50점을 증가하도록 한다. (tbl_member 테이블에 point 컬럼의 값을 50 증가하도록 update
			// 한다.)
		} catch (Throwable e) {
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.put("n", n);
		json.put("name", cmvo.getName());

		return json.toString();
	}

	// === #90. 원게시물에 딸린 댓글들을 조사해보기(Ajax 로 처리 ) === //
	@ResponseBody
	@RequestMapping(value = "/readComment.action", method = {
			RequestMethod.GET }, produces = "text/plain;charset=UTF-8")
	public String readComment(HttpServletRequest request) {
		String parentSeq = request.getParameter("parentSeq");
		List<CommentVO> cmvoList = service.getcmvoList(parentSeq);

		JSONArray jsonArr = new JSONArray();
		if (cmvoList != null) {
			for (CommentVO cmvo : cmvoList) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("name", cmvo.getName());
				jsonObj.put("content", cmvo.getContent());
				jsonObj.put("regdate", cmvo.getRegDate());
				jsonArr.put(jsonObj);
			}

		}
		return jsonArr.toString();
	}

	/*
	 * // === #108. // 검색어 입력시 자동글 완성하기
	 * 
	 * @ResponseBody
	 * 
	 * @RequestMapping(value="/wordSearchShow.action" , method= {RequestMethod.GET}
	 * , produces="text/plain;charset=UTF-8" ) public String
	 * wordSearchShow(HttpServletRequest request ) { String searchWord =
	 * request.getParameter("searchWord"); String searchType=
	 * request.getParameter("searchType");
	 * 
	 * Map<String, String> paraMap = new HashMap<>(); paraMap.put("searchWord",
	 * searchWord); paraMap.put("searchType", searchType);
	 * 
	 * List<String> list = service.wordSearchShow(paraMap);
	 * 
	 * JSONArray jsonArr = new JSONArray(); if ( list != null && list.size() != 0) {
	 * for (String input : list) { JSONObject jsonObj = new JSONObject();
	 * jsonObj.put("input", input); jsonArr.put(jsonObj); } }
	 * 
	 * 
	 * return jsonArr.toString() ; }
	 */

	// 또는
	// === #108. // 검색어 입력시 자동글 완성하기
	@ResponseBody
	@RequestMapping(value = "/wordSearchShow.action", method = {RequestMethod.GET }, produces = "text/plain;charset=UTF-8")
	public String wordSearchShow(HttpServletRequest request) {
		String searchWord = request.getParameter("searchWord");
		String searchType = request.getParameter("searchType");

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchWord", searchWord);
		paraMap.put("searchType", searchType);

		String json = service.wordSearchShow(paraMap);

		return json;
	}

	// === #128. 원게시물에 딸린 댓글들을 페이징 처리해서 조회해오기 (Ajax로 처리) ===
	@ResponseBody
	@RequestMapping(value = "/commentList.action", method = {RequestMethod.GET }, produces = "text/plain;charset=UTF-8")
	public String commentList(HttpServletRequest request) {
		String parentSeq = request.getParameter("parentSeq");
		String currentShowPageNo = request.getParameter("currentShowPageNo");
		
		if ( currentShowPageNo == null ) {
			currentShowPageNo = "1" ;
		}
		int sizePerPage = 5;  // 한 페이지당 5개의 댓글을 보여줄 것임
		
		int startRno = ((Integer.parseInt(currentShowPageNo) - 1) * sizePerPage) + 1;
		int  endRno = startRno + sizePerPage - 1;
		
		Map<String, String > paraMap = new HashMap<>() ;
		paraMap.put("parentSeq", parentSeq);
		paraMap.put("startRno", String.valueOf(startRno));
		paraMap.put("endRno", String.valueOf(endRno));

		
		List<CommentVO> cmvoList = service.getCommentListPaging(paraMap);
		
		JSONArray jsonArr = new JSONArray() ; 
		if ( cmvoList != null ) {
			for (CommentVO cvo :cmvoList) {
				JSONObject jsonObj = new JSONObject() ;
				jsonObj.put("name" ,  cvo.getName());
				jsonObj.put("content",  cvo.getContent());
				jsonObj.put("regdate", cvo.getRegDate());
				jsonObj.put("orgFilename", cvo.getOrgFilename());
				jsonObj.put("fileSize", cvo.getFileSize());
				jsonObj.put("seq", cvo.getSeq()) ;
				
				// === 댓글 읽어오기에 있어서 첨부파일 기능을 넣은 경우 시작 === //
				
				// === 댓글 읽어오기에 있어서 첨부파일 기능을 넣은 경우 끝 === //
				
				
				jsonArr.put(jsonObj);
			}// end of for문 
		}
		
		
		
		return jsonArr.toString();
	}
	
	// === #132. 원게시물에 딸린 댓글 TotalPage 조회 해오기 ( Ajax 로 처리) 
	@ResponseBody
	@RequestMapping(value = "/getCommentTotalPage.action", method = {RequestMethod.GET })
	public String getCommentTotalPage(HttpServletRequest request) {
			String parentSeq = request.getParameter("parentSeq");
			String 	sizePerPage = request.getParameter("sizePerPage");
			
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("parentSeq" ,parentSeq );
			paraMap.put("sizePerPage" ,sizePerPage );
			
			// 원글 글번호에 해당하는 댓글의 totalPage 수를 알아오기 ;
			String totalPage= service.getCommentTotalPage(paraMap);
			
			return totalPage ;
	}
	
	
	// === #163. 첨부파일 다운로드 받기 === //  
	@RequestMapping(value = "/download.action", method = {RequestMethod.GET })
	public void requiredLogin_download(HttpServletRequest request , HttpServletResponse response) {
		String seq = request.getParameter("seq");	
		// 첨부파일이 있는 글번호 
		/*
        
        첨부파일이 있는 글번호에서
     	20230522111235267588661237600.jpg 처럼
       	이러한 fileName 값을 DB에서 가져와야 한다.
       	또한 orgFilename 값도  DB에서 가져와야 한다.
		
		*/
		
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("searchWord", "");
		paraMap.put("searchType", "");
		response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = null ;
        // out 은 웹브라우저에 기술하는 대상체라고 생각하자.
		try {
			Integer.parseInt(seq);
			BoardVO bdvo = service.getViewWithNoAddCount(paraMap);
			
			if ( bdvo == null || (bdvo != null && bdvo.getFileName() == null ) ) {
				out = response.getWriter();

				out.println("<script type='text/javascript'>alert('존재하지 않는 글번호 이거나 첨부파일이 없으므로 파일다운로드가 불가합니다.'); history.back();</script>");
	            return ; // 종료
				
			}
			else { // 정상적으로 파일이 존재할 경우 
				String fileName = bdvo.getFileName(); // 20230522111235267588661237600.jpg  이것이 바로 WAS(톰캣) 디스크에 저장된 파일명이다. 
				String orgFilename = bdvo.getOrgFilename(); // 제품7.jpg
				
				// 첨부파일이 저장되어 있는 WAS(톰캣)의 디스크 경로명을 알아와야만 다운로드를 해줄수 있다. 
	            // 이 경로는 우리가 파일첨부를 위해서 /addEnd.action 에서 설정해두었던 경로와 똑같아야 한다.
	            // WAS 의 webapp 의 절대경로를 알아와야 한다.
	            HttpSession session = request.getSession();
	            String root = session.getServletContext().getRealPath("/");
	            // System.out.println("~~~ 확인용 webapp 의 절대경로  : " + root  );
	            // ~~~ 확인용 webapp 의 절대경로  : C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\
	   		 
	            String path = root + "resources" + File.separator+"files";
	   		 
	            /* File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
	            	운영체제가 Windows 이라면 File.separator 는  "\" 이고,
	            	운영체제가 UNIX, Linux, 매킨토시(맥) 이라면  File.separator 는 "/" 이다. 
	             */
	            // System.out.println("~~~ 확인용 path 의 절대경로  : " + path );
	            // ~~~ 확인용 path 의 절대경로  : C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\files
	            
	            // **** file 다운로드 하기 **** // 
	            boolean flag = false ;  // file 다운로드 성공 , 실패를 알려주는 용도 
	            flag = fileManager.doFileDownload(fileName, orgFilename, path, response);
	            // file 다운로드 성공시 flag 는 true, 
	            // file 다운로드 실패시 flag 는 false 를 가진다. \
	            
	            if(!flag) {
	                // 다운로드가 실패할 경우 메시지를 띄워준다.
	                out = response.getWriter();
	                // out 은 웹브라우저에 기술하는 대상체라고 생각하자.
	                
	                out.println("<script type='text/javascript'>alert('파일다운로드가 실패되었습니다.'); history.back();</script>");
	             }
	            
	            
			}
			
		}catch(NumberFormatException | IOException e) {
			try {
				out = response.getWriter();
				
				out.println("<script type='text/javascript'>alert('파일다운로드가 불가합니다.'); history.back();</script>");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	} 
	
	  // ==== #168.스마트에디터. 드래그앤드롭을 사용한 다중사진 파일업로드 ====
	   @RequestMapping(value="/image/multiplePhotoUpload.action", method= {RequestMethod.POST} )
		public void multiplePhotoUpload(HttpServletRequest request, HttpServletResponse response) {
			
			/*
			   1. 사용자가 보낸 파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
			   >>>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
			        우리는 WAS 의 webapp/resources/photo_upload 라는 폴더로 지정해준다.
			*/
			
			// WAS 의 webapp 의 절대경로를 알아와야 한다.
			HttpSession session = request.getSession();
			String root = session.getServletContext().getRealPath("/");
			String path = root + "resources"+File.separator+"photo_upload";
			// path 가 첨부파일들을 저장할 WAS(톰캣)의 폴더가 된다.
			
		//	System.out.println("~~~~ 확인용 path => " + path);
			// ~~~~ 확인용  webapp 의 절대경로 => C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\photo_upload 
			
			File dir = new File(path);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			try {
				String filename = request.getHeader("file-name"); // 파일명(문자열)을 받는다 - 일반 원본파일명
				// 네이버 스마트에디터를 사용한 파일업로드시 싱글파일업로드와는 다르게 멀티파일업로드는 파일명이 header 속에 담겨져 넘어오게 되어있다. 
				
				/*
				    [참고]
				    HttpServletRequest의 getHeader() 메소드를 통해 클라이언트 사용자의 정보를 알아올 수 있다. 
		
					request.getHeader("referer");           // 접속 경로(이전 URL)
					request.getHeader("user-agent");        // 클라이언트 사용자의 시스템 정보
					request.getHeader("User-Agent");        // 클라이언트 브라우저 정보 
					request.getHeader("X-Forwarded-For");   // 클라이언트 ip 주소 
					request.getHeader("host");              // Host 네임  예: 로컬 환경일 경우 ==> localhost:9090    
				*/
				
			//	System.out.println(">>> 확인용 filename ==> " + filename);
				// >>> 확인용 filename ==> berkelekle%EB%8B%A8%EA%B0%80%EB%9D%BC%ED%8F%AC%EC%9D%B8%ED%8A%B803.jpg 
				
				InputStream is = request.getInputStream(); // is는 네이버 스마트 에디터를 사용하여 사진첨부하기 된 이미지 파일임.
				
				String newFilename = fileManager.doFileUpload(is, filename, path);
				
				int width = fileManager.getImageWidth(path+File.separator+newFilename);
				
			    if(width > 600) {
			       width = 600;
			    }
			    
			 // System.out.println(">>>> 확인용 width ==> " + width);
			 // >>>> 확인용 width ==> 600
			 // >>>> 확인용 width ==> 121
				
				String ctxPath = request.getContextPath(); //  /board
				String strURL = "";
				strURL += "&bNewLine=true&sFileName="+newFilename; 
				strURL += "&sWidth="+width;
				strURL += "&sFileURL="+ctxPath+"/resources/photo_upload/"+newFilename;
				// === 웹브라우저 상에 사진 이미지를 쓰기 === //
				PrintWriter out = response.getWriter();
				out.print(strURL);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	   
	   
		@ResponseBody
		@RequestMapping(value = "/addComment_withAttach.action", method = {
				RequestMethod.POST }, produces = "text/plain;charset=UTF-8")
		public String addComment_withAttach(CommentVO cmvo , MultipartHttpServletRequest mre) {
			// 댓글 쓰기에 첨부파일이 있는 경우
			// 먼저 , CommentVO 클래스에 가서 첨부파일을 위한 필드
			// ======== !!! 첨부파일 업로드 시작 !!! ========
		      MultipartFile attach = cmvo.getAttach();
		      System.out.println("~~ 확인용 "+attach);
		      if( !attach.isEmpty() ) {
		      //   System.out.println("~~~~ 댓글쓰기에서 첨부파일 업로드 시작  ~~~~~~");
		         // attach(첨부파일)가 비어 있지 않으면(즉, 첨부파일이 있는 경우라면)
		         
		         /*
		            1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다. 
		            >>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
		                       우리는 WAS의 webapp/resources/files 라는 폴더로 지정해준다.
		                       조심할 것은  Package Explorer 에서  files 라는 폴더를 만드는 것이 아니다.       
		         */
		         // WAS 의 webapp 의 절대경로를 알아와야 한다.
		         HttpSession session = mre.getSession();
		         String root = session.getServletContext().getRealPath("/");
		         
		         //   System.out.println("~~~~ 확인용  webapp 의 절대경로 => " + root);
		         // ~~~~ 확인용  webapp 의 절대경로 => C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\ 
		         
		         String path = root+"resources"+File.separator+"files";
		         /* File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
		                  운영체제가 Windows 이라면 File.separator 는  "\" 이고,
		                  운영체제가 UNIX, Linux 이라면  File.separator 는 "/" 이다. 
		          */
		         
		         // path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
		      //   System.out.println("~~~~ 확인용  path => " + path);
		         // ~~~~ 확인용  path => C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\files  
		      
		         
		      /*
		         2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기 
		      */
		         String newFileName = "";
		         // WAS(톰캣)의 디스크에 저장될 파일명 
		         
		         byte[] bytes = null;
		         // 첨부파일의 내용물을 담는 것 
		         
		         long fileSize = 0;
		         // 첨부파일의 크기 
		         
		         try {
		            bytes = attach.getBytes();
		            // 첨부파일의 내용물을 읽어오는 것
		            
		            String originalFilename = attach.getOriginalFilename();
		          // attach.getOriginalFilename() 이 첨부파일명의 파일명(예: 강아지.png) 이다.
		         //   System.out.println("~~~~ 확인용 originalFilename => " + originalFilename);
		            // ~~~~ 확인용 originalFilename => LG_싸이킹청소기_사용설명서.pdf
		            
		            newFileName = fileManager.doFileUpload(bytes, originalFilename, path);
		            // 첨부되어진 파일을 업로드 하도록 하는 것이다. 
		            
		         //   System.out.println(">>> 확인용 newFileName => " + newFileName);
		            // >>> 확인용 newFileName => 20220429123036877439302653900.pdf
		         
		      /*
		         3. CommentVO commentvo 에 fileName 값과 orgFilename 값과 fileSize 값을 넣어주기 
		      */
		            cmvo.setFileName(newFileName);
		            // WAS(톰캣)에 저장될 파일명(2022042912181535243254235235234.png)
		            
		            cmvo.setOrgFilename(originalFilename);
		            // 게시판 페이지에서 첨부된 파일(강아지.png)을 보여줄 때 사용.
		            // 또한 사용자가 파일을 다운로드 할때 사용되어지는 파일명으로 사용.
		            
		            fileSize = attach.getSize(); // 첨부파일의 크기(단위는 byte임)
		            cmvo.setFileSize(String.valueOf(fileSize));
		            
		         } catch (Exception e) {
		            e.printStackTrace();
		         }
		         
		      }
		      // ======== !!! 첨부파일 업로드 끝 !!! ========
			int n = 0;
			try {
				n = service.addComment(cmvo);
				// 댓글쓰기(insert) 및 원게시물(tbl_board 테이블)에 댓글의 개수 증가(update 1씩 증가)하기
				// 이어서 회원의 포인트를 50점을 증가하도록 한다. (tbl_member 테이블에 point 컬럼의 값을 50 증가하도록 update
				// 한다.)
			} catch (Throwable e) {
				e.printStackTrace();
			}

			JSONObject json = new JSONObject();
			json.put("n", n);
			json.put("name", cmvo.getName());

			return json.toString();
		}
	
		// === #171. 파일첨부가 있는 댓글쓰기에서 파일 다운로드 받기 === // 
		@RequestMapping(value="/downloadComment.action")
		public void requiredLogin_downloadComment(HttpServletRequest request , HttpServletResponse response)	{
			
			 
		
		}
} // end of BoardController 
