package com.spring.employees.model;

import java.util.List;
import java.util.Map;

public interface InterEmpDAO {
	
	List<String> depIdList(); 	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 

	List<Map<String, String>> empList(	Map<String, Object> paraMap);

	int addEmpList(List<Map<String, String>> paraMapList);  // Excel 파일을 업로드 하면 엑셀데이터를 데이터베이스 테이블에 insert 하는 예제 ===

}