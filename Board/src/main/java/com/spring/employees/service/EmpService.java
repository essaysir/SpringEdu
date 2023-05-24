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
		
}
