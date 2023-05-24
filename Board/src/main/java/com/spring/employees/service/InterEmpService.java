package com.spring.employees.service;

import java.util.List;
import java.util.Map;

public interface InterEmpService {

	List<String> depIdList(); 	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 

	List<Map<String, String>> empList(	Map<String, Object> paraMap); 

}
