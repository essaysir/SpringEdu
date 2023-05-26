package com.spring.employees.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.employees.model.InterEmpDAO;

@Service
public class EmpService implements InterEmpService{

		@Autowired
		private InterEmpDAO dao ;

		@Override
		public List<String> depIdList() {
			List<String> depIdList = dao.depIdList();  	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 
			return depIdList;
		}

		@Override
		public List<Map<String, String>> empList(	Map<String, Object> paraMap) {
			List<Map<String, String>> empList = dao.empList(paraMap);
			return empList;
		}
		//  Excel 파일을 업로드 하면 엑셀데이터를 데이터베이스 테이블에 insert 하는 예제 ===
		@Override
		public int addEmpList(List<Map<String, String>> paraMapList) {
			int n = dao.addEmpList(paraMapList);
			return n;
		}

		 // 차트그리기 ( Ajax ) 부서명 별 인원수 및 퍼센티지 가져오기 ===
		@Override
		public List<Map<String, String>> employeeCntByDeptname() {
			List<Map<String,String>> mapList = dao.employeeCntByDeptname();
			
			return mapList ;
		}

		@Override
		public List<Map<String, String>> employeeCntByGender() {
			List<Map<String,String>> mapList = dao.employeeCntByGender();
			
			return mapList ;
		}

		@Override
		public List<Map<String, String>> employeeCntByGenderHireYear() {
			List<Map<String,String>> mapList = dao.employeeCntByGenderHireYear();
			
			return mapList ;
		}
		
}
