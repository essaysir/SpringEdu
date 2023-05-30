<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%-- 
 <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=발급받은 APP KEY(JavaScript 키)를 넣으시면 됩니다.&​libraries=services"></script> 
--%> 
 <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=db58f833f2809ccc7bd15095fff1df92&libraries=services"></script> 
 
 <script type="text/javascript">
   $(document).ready(function(){
	   
	   $("button#btn").click(function(){
		   func_geocoder($("input#address").val());
	   });   
		  
	   $("input#address").bind("keyup", function(e){
		  if(e.keyCode == 13) {
			  func_geocoder($("input#address").val()); 
		  } 
	   });
   });
   
   function func_geocoder(address) {
	   // 주소-좌표 변환 객체를 생성합니다
	   var geocoder = new kakao.maps.services.Geocoder();

	   geocoder.addressSearch(address, function(result, status) {

		   if (status === kakao.maps.services.Status.OK) {
			    // 주소가 정상적으로 검색이 완료됐으면
		    	$("span#lat").text(result[0].y); // result[0].y ==> 위도
		    	$("span#lng").text(result[0].x); // result[0].x ==> 경도
		    } 
		}); 
   }

</script>

<form name="address_frm">
	<input type="text" style="display: none;"/>
	주소 : <input type="text" id="address"/>&nbsp;&nbsp;
	<button type="button" id="btn">위/경도조회</button>
</form>
<div style="margin-top: 20px">
	위도 : <span id="lat"></span><br>
	경도 : <span id="lng"></span><br>
</div>

    