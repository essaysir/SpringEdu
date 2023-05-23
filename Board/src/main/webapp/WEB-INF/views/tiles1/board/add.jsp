<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%
   String ctxPath = request.getContextPath();
%>   

<style type="text/css">

</style>

<script type="text/javascript">

  $(document).ready(function(){
	  
 	<%-- === #166. 스마트 에디터 구현 시작 === --%>
	  
	  //전역변수
      var obj = [];
    
      //스마트에디터 프레임생성
      nhn.husky.EZCreator.createInIFrame({
          oAppRef: obj,
          elPlaceHolder: "content",
          sSkinURI: "<%= ctxPath%>/resources/smarteditor/SmartEditor2Skin.html",
          htParams : {
              // 툴바 사용 여부 (true:사용/ false:사용하지 않음)
              bUseToolbar : true,            
              // 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음)
              bUseVerticalResizer : true,    
              // 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음)
              bUseModeChanger : true,
          }
      });
	  
	  <%-- === #166. 스마트 에디터 구현 끝 === --%>
	  
     	$("button#btnWrite").click(function(){
     		
     	 	<%-- === #166. 스마트 에디터 구현 시작 === --%>
			// ID 가 CONTENT 인 textarea 에 에디터 대입 
			  obj.getById["content"].exec("UPDATE_CONTENTS_FIELD", []);
     	 	
     	 	
     	 	<%-- === #166. 스마트 에디터 구현 끝 === --%>

	     	<%-- // 글제목 유효성 검사   --%>
	            const subject = $("input#subject").val().trim();
	            if(subject == "") {
	               alert("글제목을 입력하세요!!");
	               return;
	            }
	            
	            <%--// 글내용 유효성 검사(스마트 에디터 사용 안 할 경우)
	            const content = $("textarea#content").val().trim();
	            if(content == "") {
	               alert("글내용을 입력하세요!!");
	               return;
	            } --%>
	        	
	        	<%-- === 글내용 유효성 검사(스마트 에디터 사용 할 경우) 시작 === --%>
	        	 const content = $("textarea#content").val().trim();
	        	 //  console.log(content);  <p>213213</p>  
	        	 // <p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</p>
	        	 // 이와같이  스마트에디터를 사용할 경우에 
	        	 //  태그 들이 함께 보여진다. 이를 해결하기 위해 밑에서 와 같이 한다. 
	        	 
	        	var contentval = $("textarea#content").val();
			        
				 // 글내용 유효성 검사 하기 
	         	// alert(contentval); // content에  공백만 여러개를 입력하여 쓰기할 경우 알아보는것.
	        	 // <p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</p> 이라고 나온다.
	         
	            contentval = contentval.replace(/&nbsp;/gi, ""); // 공백을 "" 으로 변환
	         /*    
			         대상문자열.replace(/찾을 문자열/gi, "변경할 문자열");
			     ==> 여기서 꼭 알아야 될 점은 나누기(/)표시안에 넣는 찾을 문자열의 따옴표는 없어야 한다는 점입니다. 
			                  그리고 뒤의 gi는 다음을 의미합니다.
			
			 	 g : 전체 모든 문자열을 변경 global
			 	 i : 영문 대소문자를 무시, 모두 일치하는 패턴 검색 ignore
			*/ 
			
	      //   alert(contentval);
	      //   <p>             </p>
	         
	           contentval = contentval.substring(contentval.indexOf("<p>")+3);   // "             </p>"
	           contentval = contentval.substring(0, contentval.indexOf("</p>")); // "             "
	                  
	           if(contentval.trim().length == 0) {
	        	   alert("글내용을 입력하세요!!");
	               return;
	           }
			 <%-- === 글내용 유효성 검사(스마트 에디터 사용 할 경우) 끝 === --%>
			 
			 
	         // 글암호 유효성 검사
	            const pw = $("input#pw").val();
	            if(pw == "") {
	               alert("글암호를 입력하세요!!");
	               return;
	            }
	            
	            // 폼(form)을 전송(submit)
	            const frm = document.addFrm;
	            frm.method = "POST";
	            frm.action = "<%= ctxPath%>/addEnd.action";
	       		frm.submit();
     	});
     
  });// end of $(document).ready(function(){})-------------------------------

</script>

<div style="display: flex;">
<div style="margin: auto; padding-left: 3%;">
<%-- == 원글쓰기 인 경우 ==  --%>
<c:if test="${fk_seq eq '' }">
	<h2 style="margin-bottom: 30px;">글쓰기</h2>
</c:if>
<c:if test="${fk_seq ne ''}">
<h2 style="margin-bottom: 30px; color:red;" >답변글쓰기</h2>
</c:if>

<%--  <form name="addFrm">  --%>
<%--  === # 149. 파일첨부하기 === 
			먼저 위의  <form name="addFrm"> 을 주석처리한 이후에 아래오 ㅏ같이 한다.
			 enctype="multipart/form-data 를 해주어야만 파일 첨부가 되어진다,
	  --%>
	  
<form name="addFrm" enctype="multipart/form-data">
   <table style="width: 1024px" class="table table-bordered">
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">성명</th>
         <td>
            	<input type="text" name="name"  value="${sessionScope.loginuser.name}" readonly />
            	<input type="hidden" name="fk_userid"  value="${sessionScope.loginuser.userid}" readonly />            	
         </td>
      </tr>
      
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">제목</th>
         <td>
         <c:if test="${fk_seq eq '' }">
		       <input type="text" name="subject" id="subject"  size=100pt  />
         	</c:if>
			<c:if test="${fk_seq ne ''}">
		       <input type="text" name="subject" id="subject"  size=100pt value="${subject }" readonly  />
			</c:if>
         </td>
      </tr>
      
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">내용</th>
         <td>
            	<textarea style="width:100%; height: 612px;" name="content" id="content"></textarea>
         </td>
      </tr>
      <%-- === #150. 파일첨부 타입 추가하기 ===  --%>
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">파일첨부</th>
         <td>
             <input type="file" name="attach" />
         </td>
      </tr>
      
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">글암호</th>
         <td>
             <input type="password" name="pw" id="pw" size=100pt />
         </td>
      </tr>
   </table>
   <%-- === #143. 답변 글쓰기가 추가된 경우 ,  시작--%>
   	<input type="hidden" name="fk_seq" value="${fk_seq }"/>
   	<input type="hidden" name="groupno" value="${groupno}"/>
   	<input type="hidden" name="depthno" value="${depthno }"/>
   	
   
   
   
   
   <%-- === #143. 답변 글쓰기가 추가된 경우 ,  끝--%>
   
   <div style="margin: 20px;">
      <button type="button" class="btn btn-secondary btn-sm mr-3" id="btnWrite">글쓰기</button>
      <button type="button" class="btn btn-secondary btn-sm" onclick="javascript:history.back()">취소</button>
   </div>
   
</form>   
</div>
</div>
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    