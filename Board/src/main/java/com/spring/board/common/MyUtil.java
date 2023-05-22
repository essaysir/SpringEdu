package com.spring.board.common;

import javax.servlet.http.HttpServletRequest;

public class MyUtil {
	
	// *** ? 다음의 데이터까지 포함한 현재 URL 주소를 알려주는 메소드를 생성한다. *** // 
	public static String getCurrentURL(HttpServletRequest request) {
		
		// 웹브라우저 주소 입력창에서 
		// http://localhost:9090/MyMVC/member/memberList.up?searchType=name&searchWord=%EC%9C%A0&sizePerPage=10 와 같이 입력되었다면
		String currentURL = request.getRequestURL().toString(); // 리턴 타입이 StringBuffer 이다.
		// System.out.println("~~ 확인용 URL => "+ url);
		// ~~ 확인용 URL => http://localhost:9090/MyMVC/member/memberList.up
		// ? (물음표) 앞의 내용까지만 가져오게 된다.
		
		String queryString = request.getQueryString();
		// System.out.println("## 확인용 QUERYSTRING => "+ queryString);
		// ## 확인용 QUERYSTRING => searchType=name&searchWord=%EC%9C%A0&sizePerPage=10 
		// 물음표 뒤의 내용이 나오게 된다. ( GET 방식일 경우 ) 
		// ## 확인용 QUERYSTRING => null 
		// POST 방식인 경우에는 NULL 이 나오게 된다. 
		
		if ( queryString != null ) {
			currentURL += "?"+queryString ;
			
		}
		
		// http://localhost:9090/MyMVC/member/memberList.up?searchType=name&searchWord=%EC%9C%A0&sizePerPage=10
		// 위의 것을 아래와 같이 바꾸어야 한다.
		// /member/memberList.up?searchType=name&searchWord=%EC%9C%A0&sizePerPage=10
		
		String ctxPath = request.getContextPath(); // /MyMVC 
		
		int beginIndex = currentURL.indexOf(ctxPath)+ctxPath.length();
		// currentURL.indexOf(ctxPath) => 다음과 같이하면 /MyMVC 중 / 가 처음 나오는 index 값을 호출해준다.
		currentURL = currentURL.substring(beginIndex);
		
		return currentURL;
	}
	
}
