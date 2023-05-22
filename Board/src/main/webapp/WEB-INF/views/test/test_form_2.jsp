<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% String ctxPath  = request.getContextPath(); %>
<!--  확인은 WATSON 을 눌러서 모듈을 확인해보면 된다.  Spring 은 첫글자 대문자가 모두 자동적으로 소문자로 바뀜.-->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입_2</title>
<script type="text/javascript" src="<%=ctxPath%>/resources/js/jquery-3.6.4.min.js"></script>
<script type="text/javascript">
		$(document).ready(function(){
			
			
			$("form[name='testFrm']").submit(function(){
				const no_val = $("input[name='no']").val();
				const name_val = $("input[name='name']").val();
				
				if ( no_val.trim() == "" || name_val.trim() == "" ){
					alert('번호와 성명 모두 입력하세요!! ');
					return false ;  //  전송하지 말라는 뜻이다.
				}
				
				
				
			});
			
		});

</script>
</head>
<body>

		<form name="testFrm" action="<%= ctxPath%>/test/test_form_2.action" method="POST"> 
		      번호_2 : <input type="text" name="no" /><br>
		      성명_2 : <input type="text" name="name" /><br>
		       <input type="submit" value="확인" />
		       <input type="reset"  value="취소" /> 
		  </form>

</body>
</html>