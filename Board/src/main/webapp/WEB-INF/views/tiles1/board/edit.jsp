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
     	$("button#btnEdit").click(function(){
	     	// 글제목 유효성 검사
	            const subject = $("input#subject").val().trim();
	            if(subject == "") {
	               alert("글제목을 입력하세요!!");
	               return;
	            }
	            
	         // 글내용 유효성 검사(스마트 에디터 사용 안 할 경우)
	            const content = $("textarea#content").val().trim();
	            if(content == "") {
	               alert("글내용을 입력하세요!!");
	               return;
	            }
	            
	         // 글암호 유효성 검사
	            const pw = $("input#pw").val();
	            if(pw == "") {
	               alert("글암호를 입력하세요!!");
	               return;
	            }
	            // console.log('${bdvo.pw}')
	            
	           //  if ( pw == '${bdvo.pw}'){
		            // 폼(form)을 전송(submit)
				const frm = document.editFrm;
		        frm.method = "post";
		        frm.action = "<%= ctxPath %>/editEnd.action";
		        frm.submit();
	            // }
	            

     	});
     
  });// end of $(document).ready(function(){})-------------------------------


</script>

<div style="display: flex;">
<div style="margin: auto; padding-left: 3%;">

<h2 style="margin-bottom: 30px;">글 수정</h2>

<form name="editFrm">
   <table style="width: 1024px" class="table table-bordered">
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">성명</th>
         <td>
            	${sessionScope.loginuser.name}
            	<input type="hidden" name="seq"  value="${bdvo.seq}" readonly />
         </td>
      </tr>
      
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">제목</th>
         <td>
                <input type="text" name="subject" id="subject"  size=100pt  value="${bdvo.subject}" />
         </td>
      </tr>
      
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">내용</th>
         <td>
            	<textarea style="width:100%; height: 612px;" name="content" id="content">${bdvo.content}</textarea>
         </td>
      </tr>
      
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">글암호</th>
         <td>
             <input type="password" name="pw" id="pw" size=100pt />
             
         </td>
      </tr>
   </table>
   
   <div style="margin: 20px;">
      <button type="button" class="btn btn-secondary btn-sm mr-3" id="btnEdit">글수정</button>
      <button type="button" class="btn btn-secondary btn-sm" onclick="javascript:history.back()">취소</button>
   </div>
   
</form>   
</div>
</div>
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    