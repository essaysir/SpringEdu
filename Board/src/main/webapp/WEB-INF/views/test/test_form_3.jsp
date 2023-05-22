<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% String ctxPath  = request.getContextPath(); %>
<!--  확인은 WATSON 을 눌러서 모듈을 확인해보면 된다.  Spring 은 첫글자 대문자가 모두 자동적으로 소문자로 바뀜.-->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<style type="text/css">
		table, th , td {
			border : 1px gray solid ;
		}
</style>
<script type="text/javascript" src="<%=ctxPath%>/resources/js/jquery-3.6.4.min.js"></script>
<script type="text/javascript">
		$(document).ready(function(){
			func_ajax_select();  // 문서가 로딩되자마자 실행되어야 할 때
			
			$("button#btnOK").click( function(){
				const no_val = $("input#no").val();
				const name_val = $("input#name").val();
				
				if ( no_val.trim() == "" || name_val.trim() == "" ){
					alert('번호와 성명 모두 입력하세요!! ');
					return ;  //  함수를 종료하라는 뜻이다.
				}
				
				$.ajax({
					url:"<%=ctxPath%>/test/ajax_insert.action",
					type:"post",
					data:{ "no":no_val , "name":name_val },
					dataType:"json",
					success:function(json){
						// console.log("~~~ 확인용 : " + JSON.stringify(json)); 
						<%-- //   location.href="<%=ctxPath%>/test/test_select_modelAndView.action" ;--%> 
						if  ( json.n == "1"){
							func_ajax_select();	
							$('input#no').val("");
							$('input#name').val("");
						}
						else{
							alert("회원가입에 실패하셨습니다.");
						}
					},
					error: function(request, status, error){
			            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			          }
				});
				
			}); // end of $("button#btnOK").click( function(){
			
			$("button#btnCancel").click( function(){
				
			}); // end of $("button#btnCancel").click( function(){
				
			

			
		}); // END OF $(DOCUMENT).READY(FUNCTION(){

			// Function Declaration 
			function func_ajax_select (){
				$.ajax({
					url:"<%=ctxPath%>/test/ajax_select.action",
					dataType:"json",
					success:function(json){
						// console.log("~~ 확인용 " + JSON.stringify(json));
						let html = `<table>
											<tr>
												<th>번호</th>
												<th>입력번호</th>
												<th>성명</th>
												<th>작성일자</th>
											</tr> ` ;
						$.each(json,function(index, item){
							html += `											
							<tr>
								<td>\${index+1}</t>
								<td>\${item.no}</td>
								<td>\${item.name}</td>
								<td>\${item.writeday}</td>
							</tr> ` ;
						});
						/* 
						위의 JQUERY 문을 자바스크립트로 나타낸 것! \ 를 넣어야하는 것 조심하자
						json.forEach(function(item, index, array) {
						    html += `											
						    <tr>
						        <td>\${index+1}</td>
						        <td>\${item.no}</td>
						        <td>\${item.name}</td>
						        <td>\${item.writeday}</td>
						    </tr>`;
						}); 
						*/
						html += `</table>`;
						
						$("div#view").html(html);
						
					},
					error: function(request, status, error){
			            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			        }
				
				})
				
				
				
			}// end of function func_ajax_select
</script>
</head>
<body>
		<h2>Ajax 연습</h2>
		
		<p>
			안녕하세요? <br>
			여기는 /board/test/test_form_3.action 페이지 입니다. 
		</p>
		
		<form name="testFrm" action="<%= ctxPath%>/test/test_form.action" method="POST"> 
		      번호 : <input type="text" id="no" /><br>
		      성명 : <input type="text" id="name" /><br>
		       <button type="button" id="btnOK">확인</button>
		       <button type="button" id="btnCancel">취소</button>
		       <br><br>
		 		
		 </form>
		 
		 <div id="view"></div>

</body>
</html>