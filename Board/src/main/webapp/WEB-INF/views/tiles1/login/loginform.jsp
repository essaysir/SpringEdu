<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% String ctxPath = request.getContextPath();  %>

<script type="text/javascript">
		$(document).ready(function(){
				$("button#btnLOGIN").click(function(){
					func_Login();
					
					
				});
				
				$("input#pwd").keydown(function(e){
					if ( e.keyCode == 13 ) {// 엔터를 했을 경우
						func_Login();
					}
					
				});
			
		}); // END OF $(DOCUMENT).REDAY(FUNCTION())

		// Function Declaration 
		function func_Login() {
			const userid = $("input#userid").val(); 
		     const pwd = $("input#pwd").val(); 
		   
		     if(userid.trim()=="") {
		        alert("아이디를 입력하세요!!");
		       $("input#userid").val(""); 
		       $("input#userid").focus();
		       return; // 종료 
		     }
		   
		     if(pwd.trim()=="") {
		       alert("비밀번호를 입력하세요!!");
		       $("input#pwd").val(""); 
		       $("input#pwd").focus();
		       return; // 종료 
		     }
		     
		     const frm = document.loginFrm ; 
		     
		     frm.action = "<%=ctxPath%>/loginEnd.action";
		     frm.method= "POST";
		     frm.submit();
		
		
		}// END OF FUNCTION FUNC_LOGIN() {
		
</script>



		<div class="container">
		   <div class="row" style="display: flex; border: solid 0px red;">
		      <div class="col-md-8 col-md-offset-2" style="margin: auto; border: solid 0px blue;">
		         <h2 class="text-muted">로그인</h2>
		         <hr style="border: solid 1px orange">
		         
		         <form name="loginFrm" class="mt-5">
		         <div class="form-row">    
		             <div class="form-group col-md-6">
		               <label class="text-muted font-weight-bold" for="userid">아이디</label>
		               <input type="text" class="form-control" name="userid" id="userid" value=""/> <%-- 부트스트랩에서 input 태그에는 항상 class form-control 이 사용되어져야 한다. --%>
		               </div>
		   
		            <div class="form-group col-md-6">
		               <label class="text-muted font-weight-bold text-muted" for="pwd">비밀번호</label>
		               <input type="password" class="form-control" name="pwd" id="pwd" value="" /> 
		            </div>
		         </div>
		         </form>
		      </div>
		      <div class="col-md-8 col-md-offset-2" style="margin: auto; display: flex; border: solid 0px blue;">
		         <div style="margin: auto; border: solid 0px blue;">
		            <button style="width: 150px; height: 40px;" class="btn btn-primary" type="button" id="btnLOGIN">확인</button>
		         </div>
		      </div>
		   </div>
		</div>