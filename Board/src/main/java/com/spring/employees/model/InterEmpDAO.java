package com.spring.employees.model;

import java.util.List;
import java.util.Map;

public interface InterEmpDAO {
	
	List<String> depIdList(); 	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 

	List<Map<String, String>> empList(	Map<String, Object> paraMap);

	int addEmpList(List<Map<String, String>> paraMapList);  // Excel 파일을 업로드 하면 엑셀데이터를 데이터베이스 테이블에 insert 하는 예제 ===

	List<Map<String, String>> employeeCntByDeptname(); // 차트그리기 ( Ajax ) 부서명 별 인원수 및 퍼센티지 가져오기 ===

	List<Map<String, String>> employeeCntByGender();

	List<Map<String, String>> employeeCntByGenderHireYear();

	List<Map<String, String>> genderCntSpecialDeptname(String deptname);

	void insert_accessTime(Map<String, String> paraMap); // 인사관리 페이지에 접속한 페이지URL, 사용자ID, 접속IP주소, 접속시간을 기록으로 DB에 insert 하도록 한다. 

}
