<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style type="text/css">

   table#emptbl {
      width: 100%;
   }
   
   table#emptbl th, table#emptbl td {
      border: solid 1px gray;
      border-collapse: collapse;
   }
   
   table#emptbl th {
      text-align: center;
      background-color: #ccc;
   }
   
   form {
      margin: 0 0 30px 0;
   }
   
</style> 

<script type="text/javascript">

   $(document).ready(function(){
      
	   
      // 검색하기 버튼 클릭시
      $("button#btnSearch").click(function(){
    	  const arrDeptId = new Array();
    	  $("input:checkbox[name='deptId']").each(function(index, item){
    		  const bool =  $(item).prop("checked"); // 체크 박스에 체크 유무 검사
    		  if ( bool == true ){ // 체크박스에 체크가 되었으면
    			  arrDeptId.push($(item).val() );
    		  }
			     		  
    	  });
    	  const str_DeptId = arrDeptId.join();
    	  // console.log("~~~ 확인용  str_DeptId : " + str_DeptId ) ; 
    	  //  ~~~ 확인용  str_DeptId : -9999,20,30,40,60,70
    	  
    	 const frm = document.searchFrm ; 
    	  frm.str_DeptId.value = str_DeptId ; 
    	  frm.method = "get";
    	  frm.action = "empList.action" ;
    	  frm.submit();
      });
      
      
      // ======== 체크박스 유지시키기 시작 ======== //
      const str_DeptId = "${str_DeptId}"
	  // console.log(str_DeptId);  // 40,50,60,70
	  if ( str_DeptId != ""){
		  const arr_DeptId = str_DeptId.split(",");
		  
		  $("input:checkbox[name='deptId']").each(function(index, item){
			  for ( var i = 0  ; i<arr_DeptId.length ; i++ ){
				  if ( $(item).val() == arr_DeptId[i]){
					  $(item).prop('checked',true);
					  break ; 
				  }
			  }
			  
		  });
	  }
      // ======== 체크박스 유지시키기 끝 ======== //
      
      
      // ======== 성별 유지시키기 시작 ======== //
      const gender = "${gender}";
      $("select#gender").val(gender);
      // ======== 성별 유지시키기 끝 ======== //
      
      
      
      // ====== Excel 파일로 다운받기 시작 ====== //
      
      // ====== Excel 파일로 다운받기 끝 ====== //
      
      
      // ====== Excel 파일로 업로드하기 시작 ====== //
      
      
      // ====== Excel 파일로 업로드하기 끝 ====== //
      
   });// end of $(document).ready(function(){})-----------------------

</script>   


<div style="display: flex; margin-bottom: 50px;">   
<div style="width: 80%; min-height: 1100px; margin:auto; ">

   <h2 style="margin: 50px 0;">HR 사원정보 조회하기</h2>
	
   <form name="searchFrm">
       <c:if test="${not empty depIdList}">
       		<span style="display: inline-block; width: 150px; font-weight: bold;">부서번호선택</span> 
       		<c:forEach var="dep" items="${depIdList}" varStatus="status">
				<label for="chk_${status.count}">
					<c:if test="${dep == -9999}">
						부서없음
					</c:if>
					
					<c:if test="${dep != -9999}">
						${dep}
					</c:if>
				</label>
				<input id="chk_${status.count}" type="checkbox" name="deptId" value="${dep}" />&nbsp;&nbsp; 
       		</c:forEach>
       		
       </c:if>
       
       <input type="hidden" name="str_DeptId"/>
       
       <select name="gender" id="gender" style="height: 30px; width: 120px; margin: 10px 30px 0 0;">
         <option value="">성별선택</option>
         <option>남</option>
         <option>여</option>
      </select>
      <button type="button" class="btn btn-secondary btn-sm" id="btnSearch">검색하기</button>
      &nbsp;&nbsp;
      <button type="button" class="btn btn-success btn-sm" id="btnExcel">Excel파일로저장</button>
      
   </form>
   
   <br/>
   
   <!-- ==== 엑셀관련파일 업로드 하기 시작 ==== -->
   
   <!-- ==== 엑셀관련파일 업로드 하기 끝 ==== -->
   
   <table id="emptbl">
      <thead>
         <tr>
            <th>부서번호</th>
            <th>부서명</th>
            <th>사원번호</th>
            <th>사원명</th>
            <th>입사일자</th>
            <th>월급</th>
            <th>성별</th>
            <th>나이</th>
         </tr>
      </thead>
      <tbody> 
      		<c:if test="${not empty empList}">
      			<c:forEach var="map" items="${empList}">
      			<tr>
      				<td style="text-align:center;">${map.department_id}</td>
      				<td>${map.department_name}</td>
      				<td style="text-align:center;">${map.employee_id}</td>
      				<td>${map.fullname}</td>
      				<td style="text-align:center;">${map.hire_date}</td>
      				<td style="text-align:right;"><fmt:formatNumber value="${map.monthsal}" pattern="#,###"/> </td>
      				<td style="text-align:center;">${map.gender}</td>
      				<td style="text-align:center;">${map.age}</td>
      			</tr>
      			</c:forEach>
      		</c:if>
      </tbody>
   </table>     
</div>
</div>