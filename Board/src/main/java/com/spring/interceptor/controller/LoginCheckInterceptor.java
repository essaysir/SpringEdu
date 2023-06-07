package com.spring.interceptor.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
// import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

// public class LoginCheckInterceptor extends HandlerInterceptorAdapter {
   //spring 5.3 미만 버전시 사용하는 것

public class LoginCheckInterceptor implements HandlerInterceptor {
	// spring 5.3 이상 버전에서는 HandlerInterceptorAdapter 를 더이상 사용하지 않는다(deprecated)고 함.
	// extends HandlerInterceptorAdapter 대신에  implements HandlerInterceptor 를 사용해야 함.
	
	// preHandle() 메소드는 지정된 컨트롤러의 동작 이전에 가로채는 역할을 해주는 것이다.
	// Object handler는 Dispatcher의 HandlerMapping 이 찾아준 Controller Class 객체
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
			throws Exception { 
		
		//로그인 여부 검사
		HttpSession session = request.getSession();
		
		if(session.getAttribute("loginuser") == null) {
			
			//로그인이 되지 않은 상태
			String message = "먼저 로그인 하세요(인터셉터활용)~~~";
			String loc = request.getContextPath()+"/login.action";
			
			request.setAttribute("message", message);
			request.setAttribute("loc", loc);
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/msg.jsp");
			try {
				dispatcher.forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
			
			return false;
		}
		
		return true;
	}
	/*
	   다음으로  /WEB-INF/spring/appServlet/servlet-context.xml 파일에 가서 
	  LoginCheckInterceptor 클래스를 빈으로 올려주어야 한다.
	*/
}

