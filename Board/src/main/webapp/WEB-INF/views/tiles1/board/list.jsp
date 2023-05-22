<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%
   String ctxPath = request.getContextPath();
%>   

<style type="text/css">

   th {background-color: #DDD}

    .subjectStyle {font-weight: bold;
                color: navy;
                cursor: pointer;}

    a {text-decoration: none !important;}
    
</style>

<script type="text/javascript">

  $(document).ready(function(){
	  	
	  	$("span.subject").bind("mouseover" , function(e){
     				$(e.target).addClass('subjectStyle');
     	});
		
     	$("span.subject").bind("mouseout" , function(e){
     				$(e.target).removeClass('subjectStyle');		
     	});
     	
     	$("input#searchWord").bind('keyup' , function(e){
     		if ( e.keyCode == 13 ){
     			goSearch();
     		}
     	})
     	
     	// 검색시 검색조건 및 검색어 유지시키기
     	if ( '${paraMap.searchWord}' != '' && '${paraMap.searchType}' != '' ){
	     	$('input#searchWord').val('${paraMap.searchWord}');
    	 	$('select#searchType').val('${paraMap.searchType}');	
     	}
     	  <%-- === #107. 검색어 입력시 자동글 완성하기 2 === --%>
     	$("div#displayList").hide();
     	
     	$("input#searchWord").keyup(function(){
     		const wordLength = $(this).val().trim().length;     		
     		// 검색어에서 공백을 제거한 길이을 알아온다.
     		if ( wordLength == 0 ){
     			$("div#displayList").hide();
     			// 검색어가 공백이거나 검색어 입력 후 백스페이스키를 눌러서 검색어를 모두 지우면 검색된 내용이
     			// 안 나오도록 해야한다. 
     		}
     		else{
     			
     			$.ajax({
     				url:"<%= ctxPath%>/wordSearchShow.action",
     				type:"get",
     				data:{"searchType":$("select#searchType").val() ,
     						 "searchWord":$("input#searchWord").val()},
					dataType: "json" ,
					success: function(json){
							// console.log(JSON.stringify(json));  [{"input":"java 가 쉽나요? JVM 이 뭔가요?"}] 
							// [{"input":"손주선"}] 
							<%--  === #112. 검색어 입력시 자동글 완성하기 7  === --%>
							if ( json.length > 0 ){
								// 검색된 데이터가 있는 경우 
								let html = "";
								$.each ( json , function(index, item ){
									const word = item.input ;
									const idx = word.toLowerCase().indexOf($("input#searchWord").val().toLowerCase())
									
									const len = $("input#searchWord").val().length;
									// 검색어(java) 의 길이 len 은 4가 된다. 
									
								//	console.log( " ~~~~~~~~~~~~ 시작 ~~~~~~~~~~~"); 
								//	console.log (word.substring(0,idx)) ;  // 검색어 앞까지의 글자 ==> 
								//	console.log(word.substring(idx,idx+len)); // 검색어 만 빼온 것
								//	console.log(word.substring(idx+len));  // 검색어 이후 나머지
								//	console.log( " ~~~~~~~~~~~~ 끝  ~~~~~~~~~~~"); 
								 	const result = word.substring(0,idx)+ "<span style='color:blue;'>" + word.substring(idx,idx+len) + "</span>" +word.substring(idx+len) ;
								 	
								 	html += "<span class='result' style='cursor:pointer'>"+result + "</span><br>" ;
								}); // end of $.each -------------------------------------------------------------------
								const input_width = $('input#searchWord').css("width"); // 검색어 input 태그 width 알아오기 
								$("div#displayList").css("width",input_width) ; // 검색결과 div 의 width 크기를 검색어 입력 input 태그의 width 와 일치시키기  
								
								
								$("div#displayList").html(html);
								$("div#displayList").show();
								
							}
					},
					error: function(request, status, error){
		                  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
		             }
					
     			});
     		}
     		
     	}); // end of  $("input#searchWord").keyup(function(){
     	<%--  === #113. 검색어 입력시 자동글 완성하기 8  === --%>
     	$(document).on('click', 'span.result' , function(){
     			const text = $(this).text(); // $(this).text() 은 되지만, $(this).val() 은 적용되지 않는다.  
     			$("input#searchWord").val(text); // 텍스트 박스에 검색된 결과의 문자열을 넣어준다
     			goSearch();
     	});
     	
     	
  });// end of $(document).ready(function(){})-------------------------------
  
  // Function Declaration
    
		function goView(seq){
	  		<%--   location.href="<%=ctxPath%>/view.action?seq="+seq; --%>
	 		// === #124. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
	  	    //           사용자가 목록보기 버튼을 클릭했을 때 돌아갈 페이지를 알려주기 위해
	  	    //           현재 페이지 주소를 뷰단으로 넘겨준다. 
	  	    const gobackURL = "${gobackURL}";
	  	    
	  	    // alert(gobackURL);
	  	    // /list.action?searchType=subject&searchWord=%EC%9D%B4%EC%88%9C%EC%8B%A0&currentShowPageNo=3
	  	    		
	 		const searchType = $("select#searchType").val();
	 		const searchWord = $("input#searchWord").val();
	 		
	 		
	  	  	location.href="<%=ctxPath %>/view.action?seq="+seq+"&searchType="+searchType+"&searchWord="+searchWord+"&gobackURL="+gobackURL;
	  	   
				  	  
  		}
  		
  		function goSearch(){
  			const frm = document.searchFrm	;
  			frm.method = "get";
  			frm.action="<%=ctxPath %>/list.action" ;
  			frm.submit();
  		}
  
</script>

<div style="display: flex;">
<div style="margin: auto; padding-left: 3%;">

   <h2 style="margin-bottom: 30px;">글목록</h2>
   
   <table style="width: 1024px" class="table table-bordered">
      <thead>
       <tr>
          <th style="width: 70px;  text-align: center;">글번호</th>
         <th style="width: 360px; text-align: center;">제목</th>
         <th style="width: 70px;  text-align: center;">성명</th>
         <th style="width: 150px; text-align: center;">날짜</th>
         <th style="width: 70px;  text-align: center;">조회수</th>
       </tr>
      </thead>
      
      <tbody>
      		<c:if test="${empty requestScope.boardList }">
	      			<tr>
	      				<td colspan="5"> 게시글 데이터가 없습니다.</td>
	      			</tr>
      		</c:if>
      		<c:if test="${not empty requestScope.boardList }">
	      		<c:forEach var="bvo" items="${requestScope.boardList }">
	      			<tr>
	      				<td align="center">${bvo.seq}</td>
	      				<%-- 댓글쓰기 및 답변형 및 파일첨부가 없는  게시판 시작   --%>
	      				<%-- 
			      				<td><span class="subject" onclick="goView('${bvo.seq}')">
			      				<c:if test="${bvo.depthno > 0 }">
			      					<span style="color:red; font-style:italic; margin-left: ${bvo.depthno * 20 }px ;">└Reply</span>
			      				</c:if>
			      				${bvo.subject }</span>
			      				<!--  왜 not eq 는 작동하지 않는 걸까? NOT EQ 는 무엇인지 자세히 파악해보자
			      						not eq 라는 말은 존재하지 않으며, ne 또는 != 를 사용해야 한다.  -->
			      				<c:if test="${bvo.commentCount != 0}">
									<span style="color:red; ">${bvo.commentCount }</span>
								</c:if>
															
			      				</td> 
	      				--%>
	      				<%-- 댓글쓰기 및 답변형 및 파일첨부가 없는  게시판 끝   --%>
	      				
	      				<!--  === 댓글쓰기 및 답변형 및 파일첨부가 있는 게시판 시작 -->
		      		
	      				
	      			
	      				
		      				<td><span class="subject" onclick="goView('${bvo.seq}')">
		      				<%-- 답변글이 있는 경우  --%>
		      				<c:if test="${bvo.depthno > 0 }">
		      					<span style="color:red; font-style:italic; margin-left: ${bvo.depthno * 20 }px ;">└Reply</span>
		      				</c:if>
		      				${bvo.subject }</span>
		      				<!--  왜 not eq 는 작동하지 않는 걸까? NOT EQ 는 무엇인지 자세히 파악해보자
		      						not eq 라는 말은 존재하지 않으며, ne 또는 != 를 사용해야 한다.  -->
		      				<%--  댓글이 있는 경우  --%>
		      				<c:if test="${bvo.commentCount != 0}">
								<span style="color:red; ">${bvo.commentCount }</span>
							</c:if>
							
							<%--  첨부파일이 있는 경우 --%>
							<c:if test="${not empty bvo.fileName}">
								&nbsp; <img src="<%=ctxPath%>/resources/images/disk.gif"  />
							</c:if>
		      				
		      				</td>
	      				
	      				<%--  첨부파일이 있는 경우 끝 --%>
	      				<!--  === 댓글쓰기 및 답변형 및 파일첨부가 있는 게시판 끝 -->
	      				
	      				<td align="center">${bvo.name }</td>
	      				<td align="center">${bvo.regDate }</td>
	      				<td align="center">${bvo.readCount }</td>
	      			</tr>
	      		</c:forEach>
      		</c:if>   
      </tbody>
   </table>

    <%-- === #122. 페이지바 보여주기 === --%>  
	<div align="center" style="border: solid 0px gray; width: 70%; margin: 20px auto; ">
       		${requestScope.pageBar}
    </div>
 		
 
    <%-- === #101. 글검색 폼 추가하기 : 글제목, 글쓴이로 검색을 하도록 한다. === --%>
	    <form name="searchFrm" style="margin-top: 20px;">
	      <select name="searchType" id="searchType" style="height: 26px;">
	         <option value="subject">글제목</option>
	         <option value="name">글쓴이</option>
	      </select>
	      <input type="text" name="searchWord" id="searchWord" size="40" autocomplete="off" /> 
	      <input type="text" style="display: none;"/> <%-- form 태그내에 input 태그가 오로지 1개 뿐일경우에는 엔터를 했을 경우 검색이 되어지므로 이것을 방지하고자 만든것이다. --%> 
	      <button type="button" class="btn btn-secondary btn-sm" onclick="goSearch()">검색</button>
	   </form>
   
   <%-- === #106. 검색어 입력시 자동글 완성하기 1 === --%>
      <div id="displayList" style="border:solid 1px gray; border-top:0px; height:100px; margin-left:75px; margin-top:-1px; overflow:auto;">
      
   </div>
   
   
   
</div>
</div>
  