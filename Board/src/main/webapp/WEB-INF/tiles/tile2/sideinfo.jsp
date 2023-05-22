<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- ======= #28. tile2 중 sideinfo 페이지 만들기  ======= --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<% String ctxPath = request.getContextPath(); %>

<style type="text/css">

   
</style>



<script type="text/javascript">

   $(document).ready(function() {
	   loopshowNowTime();
   
   }); // end of $(document).ready(); ---------------------------------

   function showNowTime() {
      
      var now = new Date();
   
      var month = now.getMonth() + 1;
      if(month < 10) {
         month = "0"+month;
      }
      
      var date = now.getDate();
      if(date < 10) {
         date = "0"+date;
      }
      
      var strNow = now.getFullYear() + "-" + month + "-" + date;
      
      var hour = "";
       if(now.getHours() < 10) {
           hour = "0"+now.getHours();
       } 
       else {
          hour = now.getHours();
       }
      
       
      var minute = "";
      if(now.getMinutes() < 10) {
         minute = "0"+now.getMinutes();
      } else {
         minute = now.getMinutes();
      }
      
      var second = "";
      if(now.getSeconds() < 10) {
         second = "0"+now.getSeconds();
      } else {
         second = now.getSeconds();
      }
      
      strNow += " "+hour + ":" + minute + ":" + second;
      
      $("span#clock").html(strNow);
   
   }// end of function showNowTime() -----------------------------


   function loopshowNowTime() {
      showNowTime();
      
      var timejugi = 1000;   // 시간을 1초 마다 자동 갱신하려고.
      
      setTimeout(function() {
                  loopshowNowTime();   
               }, timejugi);
      
   }// end of loopshowNowTime() --------------------------

   
   // ------ 기상청 날씨정보 공공API XML데이터 호출하기 -------- //
   
</script>

<div style="min-height: 1100px; margin-bottom: 50px;">
   <div style="text-align: center; font-size: 16pt;">
      현재시각 :&nbsp; <span id="clock" style="color:green; font-weight:bold;"></span>
   </div>
   
   <div id="displayWeather" style="min-width: 90%; max-height: 500px; overflow-y: scroll; margin-top: 40px; margin-bottom: 70px; padding-left: 10px; padding-right: 10px;"></div> 

   <div style="margin: 20px;">
      <%-- 차트그리기 --%>
      <figure class="highcharts-figure">
          <div id="chart_container"></div>
      </figure> 
   </div>
</div>