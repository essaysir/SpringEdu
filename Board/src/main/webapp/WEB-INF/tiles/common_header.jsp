<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.net.InetAddress" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctxPath = request.getContextPath(); %>

<style type="text/css">

	.header_width {
		width: 80px;
		border-radius: 5px;
		margin-left: 3px;
	}

	.header_hover:hover {
		cursor:pointer;
		background-color: #E3F2FD;
	}
	
	/* 프로필 드롭다운 */
	.dropdown {
	  position: relative;
	  display: inline-block;
	}
	
	.dropdown-content {
	  display: none;
	  position: absolute;
	  background-color: #f9f9f9;
	  min-width: 100px;
	  box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
	  z-index: 1;
	  border-radius: 10px; 
	}
	
	.dropdown-content a {
	  color: black;
	  padding: 12px 16px;
	  text-decoration: none;
	  display: block;
	}
	
	.dropdown-content a:hover {background-color: #ddd;}

	.dropdown:hover .dropdown-content {display: block;}
	
	.activeHeaderMenu {
		color: #086BDE !important;
		background-color: #E3F2FD;
	}
	
	.header_profile_css {
		border-radius: 50%; 
		background-color: #E3F2FD; 
		width:50px; 
		height:50px; 
		display: flex; 
		justify-content: center; 
		align-items: center;
		color:white;
		font-size: 25pt;
		font-weight: bold;
	}
	
	#msgStack {
		position: fixed;
		right: 16px;
		z-index: 1;
	}
	
	a {
		color: black;
	}
</style>

<script type="text/javascript">
// 소켓 전역변수 설정
var socket  = null;

// toast생성 및 추가
function onMessage(evt){
	var data = evt.data;
	// toast
	let toast = "<div class='toast' role='alert' aria-live='assertive' aria-atomic='true'>";
	toast += "<div class='toast-header'><i class='fas fa-bell mr-2'></i><strong class='mr-auto'>알림</strong>";
	toast += "<button type='button' class='ml-2 mb-1 close' data-dismiss='toast' aria-label='Close'>";
	toast += "<span aria-hidden='true'>&times;</span></button>";
	toast += "</div> <div class='toast-body'>" + data + "</div></div>";
	$("#msgStack").append(toast);   // msgStack div에 생성한 toast 추가
	$(".toast").toast({"animation": true, "autohide": false});
	$('.toast').toast('show');
};	

$(document).ready(function(){
	// 웹소켓 연결
	/* sock = new SockJS("/alert.on");
	socket = sock;

	// 소켓이 열렸을 때
	sock.onopen = function() {
	 	console.log('open');
	}; */
	const url = window.location.host; // 웹브라우저의 주소창의 포트까지 가져옴
    const pathname = window.location.pathname; // 최초 '/' 부터 오른쪽에 있는 모든 경로
    const appCtx = pathname.substring(0, pathname.lastIndexOf("/") ); 
    const root = url + appCtx;
	var ws = new WebSocket('ws://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/alert.on');		   
       
    socket = ws;
    
	ws.onopen = function() {
   		console.log('Info: connection opened.');
	};

	ws.onmessage = function(event) {
   		console.log('Info: connection onmessage.');

    	onMessage(event); // toast 생성
	};

	ws.onclose = function(event) {
   		console.log('Info: connection closed');
	};
    
	ws.onerror = function(err) {
   		console.log('Error:', err);
	};

	
 	// 현재 메뉴 표시하기
	const pathName = window.location.pathname;
	const ctxPath = '<%=ctxPath%>';
	let menuName = pathName.substring(ctxPath.length+1);
	
	const index = menuName.indexOf("/");
	
	menuName = menuName.substring(0,index) || menuName.substring(0,menuName.length-3);
	$('div#'+menuName).addClass('activeHeaderMenu'); // 현재 메뉴에 색 입히기
	
	$("#header_profile_bg").text("${sessionScope.loginuser.name}".substring(0,1));	
});

</script>
<div id="msgStack"></div>
<nav class="navbar navbar-expand-sm">

  <ul class="navbar-nav headerNavbar">
  	<li class="nav-item mt-1">
      	<div style="padding: 0 0 0 10%; margin-right: 100px; cursor:pointer;" class="nav-link" onClick='location.href="<%=ctxPath%>/index.on"'>
      		<img src='<%=ctxPath%>/resources/images/sist_logo.png' width="150"/>
    	</div>
    </li>
    
    <li class="nav-item"  >
    	<div id='index' class="nav-link text-dark header_hover header_width" onClick='location.href="<%=ctxPath%>/index.on"'>
    		<div class="text-center"><i class="fas fa-home fa-lg"></i></div>
    		<div style="text-align: center;">홈</div>
    	</div>
    </li>
    <li class="nav-item" >
      	<div id='mail' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/mail/receiveMailBox.on"'>
    		<div class="text-center"><i class="fas fa-envelope fa-lg"></i></div>
    		<div style="text-align: center;">메일</div>
   		</div>
    </li>
    <li class="nav-item">
    	<div id='organization' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/organization.on"'>
    		<div class="text-center"><i class="fas fa-sitemap fa-lg"></i></div>
    		<div style="text-align: center;">조직도</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='attend' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/attend/myAttend.on"'>
    		<div class="text-center"><i class="fas fa-business-time fa-lg"></i></div>
    		<div style="text-align: center;">근태관리</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='approval' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/approval/home.on"'>
    		<div class="text-center"><i class="fas fa-stamp fa-lg"></i></div>
    		<div style="text-align: center;">전자결재</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='manage' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/manage/info/viewInfo.on"'>
    		<div class="text-center"><i class="fas fa-id-card-alt fa-lg"></i></div>
    		<div style="text-align: center;">사원관리</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='schedule' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/schedule/schedule.on"'>
    		<div class="text-center"><i class="far fa-calendar-alt fa-lg"></i></div>
    		<div style="text-align: center;">일정관리</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='reservation' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/reservation/meetingRoom.on"'>
    		<div class="text-center"><i class="fas fa-bookmark fa-lg"></i></div>
    		<div style="text-align: center;">자원예약</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='notice' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/notice/list.on"'>
    		<div class="text-center"><i class="fas fa-bullhorn fa-lg"></i></div>
    		<div style="text-align: center;">공지사항</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='community' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/community/list.on"'>
    		<div class="text-center"><i class="fas fa-chalkboard-teacher fa-lg"></i></div>
    		<div style="text-align: center;">커뮤니티</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='survey' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/survey/surveyList.on"'>
    		<div class="text-center"><i class="fas fa-chart-pie fa-lg"></i></div>
    		<div style="text-align: center;">설문조사</div>
   		</div>
    </li>
    <li class="nav-item">
      	<div id='chat' class="nav-link text-dark  header_hover header_width" onClick='location.href="<%=ctxPath%>/chat.on"'>
    		<div class="text-center"><i class="fas fa-comments fa-lg"></i></div>
    		<div style="text-align: center;">채팅</div>
   		</div>
    </li>
    
    <%-- 프로필 이미지 --%>
    <li class="nav-item" style="margin-left: 12%;">
      	<div class="nav-link dropdown" onClick='location.href="#"'>
    		<div class="dropbtn">
    			
    			<c:if test="${empty sessionScope.loginuser.empimg}">
					<div class="header_profile_css" id="header_profile_bg"></div>
				</c:if>
				
				<c:if test="${not empty sessionScope.loginuser.empimg}">
					<img style="border-radius: 50%; height:50px; width: 50px;" src="<%=ctxPath %>/resources/images/profile/${sessionScope.loginuser.empimg}" />
				</c:if>
								
   			</div>
   			<div class="dropdown-content">
			    <a href="<%=ctxPath%>/manage/info/viewInfo.on">프로필</a>
			    <a href="<%= ctxPath%>/login.on">로그아웃</a>
			</div>
   		</div>
    </li>
  
  </ul>
  
</nav>
