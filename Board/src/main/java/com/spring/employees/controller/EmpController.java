package com.spring.employees.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.employees.service.InterEmpService;

@Controller
public class EmpController {

		@Autowired
		private InterEmpService service ;
	
		// === #175. 다중 체크박스를 사용시 sql문에서 in 절을 처리하는 예제 === //
		@RequestMapping(value="/emp/empList.action")
		public String employeeInfoView(HttpServletRequest request) {
			// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 
			List<String> depIdList = service.depIdList(); 
			
			String str_DeptId = request.getParameter("str_DeptId");
			// System.out.println("=== 확인용 str_DeptId  : " + str_DeptId);
			// === 확인용 str_DeptId  : null
			// 뷰단에서 체크 되어진 값을 유지시키기 위한 것이다. 
			request.setAttribute("str_DeptId", str_DeptId);
			
			String gender = request.getParameter("gender");
			// System.out.println("=== 확인용 gender  : " + gender);
			// === 확인용 gender  : null
			
			Map<String, Object> paraMap = new HashMap<>();
			
			if (  str_DeptId != null && str_DeptId !="") {
				String[] arr_DeptId = str_DeptId.split("\\,");	
				paraMap.put("arr_DeptId", arr_DeptId) ;
				
			
			}
			
			if ( gender != null && gender !="") {
				paraMap.put("gender", gender );
				// 뷰단에서 체크되어진 값을 유지시키기 위한 것이다.
				request.setAttribute("gender", gender);
			}
			
			List<Map<String,String>> empList = service.empList(paraMap);
			
			request.setAttribute("depIdList", depIdList);
			request.setAttribute("empList", empList);
			
			return "emp/empList.tiles2";
		}
	
}
