package com.spring.employees.model;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.xmlbeans.impl.inst2xsd.SalamiSliceStrategy;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmpDAO implements InterEmpDAO {
	@Resource
	private SqlSessionTemplate sqlsession ; // 로컬 DB  my_mvcuser 에 연결한 것
	
	@Resource
	private SqlSessionTemplate sqlsession_2 ; // 로컬 DB  hr 에 연결한 것

	@Override
	public List<String> depIdList() {
		List<String> depIdList = sqlsession_2.selectList("hr.depIdList");
		
		return depIdList;
	}

	@Override
	public List<Map<String, String>> empList(	Map<String, Object> paraMap) {
		List<Map<String, String>> empList = sqlsession_2.selectList("hr.empList" , paraMap);
		return empList;
	}

	@Override
	public int addEmpList(List<Map<String, String>> paraMapList) {
		int insert_count = 0  ;
		if ( paraMapList != null && paraMapList.size() > 0 ) {
			for ( Map<String,String> paraMap : paraMapList) {
				int n = sqlsession_2.insert("hr.addEmpList", paraMap);
				insert_count += n ;
			}
			
			
		}
		
		return insert_count ;
	}
	// 차트그리기 ( Ajax ) 부서명 별 인원수 및 퍼센티지 가져오기 ===
	@Override
	public List<Map<String, String>> employeeCntByDeptname() {
		List<Map<String,String>> mapList = sqlsession_2.selectList("hr.employeeCntByDeptname");
		
		return mapList;
	}

	@Override
	public List<Map<String, String>> employeeCntByGender() {
		List<Map<String,String>> mapList = sqlsession_2.selectList("hr.employeeCntByGender");
		
		return mapList;
	}

	@Override
	public List<Map<String, String>> employeeCntByGenderHireYear() {
	List<Map<String,String>> mapList = sqlsession_2.selectList("hr.employeeCntByGenderHireYear");
		
		return mapList;
	}

	@Override
	public List<Map<String, String>> genderCntSpecialDeptname(String deptname) {
		List<Map<String, String>> List = sqlsession_2.selectList("hr.genderCntSpecialDeptname" , deptname );
		
		return List;
	}
	
	
	
	
	
	
	
	
	
}
