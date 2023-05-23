<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <% String ctxPath = request.getContextPath(); %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 


<style type="text/css">

    span.move {cursor: pointer; color: navy;}
    .moveColor {color: #660029; font-weight: bold; background-color: #ffffe6;}
    
    td.comment {text-align: center;}
    
    a {text-decoration: none !important;}

</style>   
    
<script type="text/javascript">
  $(document).ready(function(){
	 //  goReadComment();
     // console.log('${gobackURL}');
    goViewComment (1);
    
  });// end of $(document).ready(function(){})------------------------
  
  
  // Function Declaration
  
	  // == 댓글쓰기 == 
	  function goAddWrite (){
			const commentContent = $('input#commentContent').val().trim();			  
		  	if ( commentContent == "" ){
		  		alert("댓글 내용을 입력하세요");
		  		return ; //  종료 
		  	}
		  	if ( $('input#attach').val() == "" ) {
		  		// 첨부파일이 없는 댓글 쓰기인 경우
		  		goAddWrite_noattach();
		  	}
		  	else{
		  		// 첨부파일이 있는 댓글 쓰기인 경우 
		  		goAddWrite_withattach();
		  	}
	  }
  	 
  		
  		
	  // 파일첨부가 없는 댓글쓰기 
  	  function goAddWrite_noattach (){
	  		<%--
	  		http://localhost:9090/board/list.action?currentShowPageNo=5&searchType=name&searchWord=이순신  에서
	  		currentShowPageNo=5&searchType=name&searchWord=이순신 을 "query string" 이라고 부른다.
	  		
	  		
	  		
	        // 보내야할 데이터를 선정하는 또 다른 방법
	       // jQuery에서 사용하는 것으로써,
	       // form태그의 선택자.serialize(); 을 해주면 form 태그내의 모든 값들을 name값을 키값으로 만들어서 보내준다. 
	       const queryString = $("form[name='addWriteFrm']").serialize();
	    --%>	  		  
	      const queryString = $("form[name='addWriteFrm']").serialize();
  		  
	      $.ajax({
  			  url:"<%= request.getContextPath()%>/addComment.action" ,
/*   			  data:{"fk_userid":'$("input#fk_userid").val()',
  				  			"name":'$("input#name").val()',
  				  			"content":'$("input#commentContent".val())',
  				  			"parentSeq":'$("input#parentSeq").val()'
  				  				}, */
  			  data: queryString ,
  			  type:"post",
  			  dataType:"json",
  			  success:function(json){
  				  // console.log( "~~ 확인용 : "+ JSON.stringify(json));
  				  if ( json.n == 0){
  					  alert(json.name+'님의 포인트는 300점을 초과할 수 없으므로, 댓글쓰기가 불가합니다');
  				  }
  				  else{
  					  // goReadComment(); // 페이징 처리 안한 댓글 읽어오기 
  					  									
  					goViewComment(1); // 페이징 처리 한 댓글 읽어오기
  				  }
  				  $("input#commentContent").val("");
  				  
  				  
  			  },
  			 error: function(request, status, error){
  	            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
  	         }
  			  
  		  });  
	    
  		  
  	  }// end of function goAddWrite_noattach ();'
  
  
 	 // ==== #169. 파일첨부가 있는 댓글쓰기 ==== // 
  		function goAddWrite_withattach() {
  			<%-- === ajax로 파일을 업로드할때 가장 널리 사용하는 방법 ==> ajaxForm === //
  		         === 우선 ajaxForm 을 사용하기 위해서는 jquery.form.min.js 이 있어야 하며
  		             /WEB-INF/tiles/layout/layout-tiles1.jsp 와 
  		             /WEB-INF/tiles/layout/layout-tiles2.jsp 와 
  		             /WEB-INF/tiles/layout/layout-tiles3.jsp 와 
  		             /WEB-INF/tiles/layout/layout-tiles4.jsp 에 기술해 두었다. 
  		     --%>
  			const queryString = $("form[name='addWriteFrm']").serialize();
  		  	 // console.log(queryString);
  	      $("form[name='addWriteFrm']").ajaxForm({
  	  		 url:"<%= request.getContextPath()%>/addComment_withAttach.action",
  	  		 data:queryString,
  	  		 type:"post",
  	  		 enctype:"multipart/form-data",
  	  		 dataType:"json",
  	  		 success:function(json){
  	  			 console.log("~~ 확인용 : " + JSON.stringify(json));
  	  			 // ~~ 확인용 : {"name":"서영학","n":1}
  	  			 // 또는 
  	  			 // ~~ 확인용 : {"name":"서영학","n":0} 
  	  			 
  	  			 if(json.n == 0) {
  	  				 alert(json.name + "님의 포인트는 300점을 초과할 수 없으므로 댓글쓰기가 불가합니다.");
  	  			 }
  	  			 else {
  	  			   // goReadComment();  // 페이징 처리 안한 댓글 읽어오기
  	  				  goViewComment(1); // 페이징 처리 한 댓글 읽어오기
  	  			 }
  	  			 
  	  			 $("input#commentContent").val("");
  	  			 $("input#attach").val("");
  	  		 },
  	  		 error: function(request, status, error){
  				alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
  		     }
  	      });
  		  
  			$("form[name='addWriteFrm']").submit();
  		  	
  	  	} // end of function goAddWrite_withattach() {
  
  
  // === 페이징 처리 안한 댓글 읽어오기  === //
  function goReadComment (){
  		  	$.ajax({
  		  		url: "<%= request.getContextPath()%>/readComment.action" ,
  		  		data:{"parentSeq":"${bdvo.seq}"},
  		  		dataType:"json",
  		  		success:function(json){
  		  			// console.log("~~ 확인용 ~~ :  " + JSON.stringify(json));
  		  			let html = "" ; 
  		  			if ( json.length > 0 ){
  		  				$.each( json , function(index, item ){
  		  						html += "<tr>" + 
  		  									"<td>"+(index+1)+"</td>" + 
  		  									"<td>" + item.content+ "</td>" + 
  		  									"<td></td>" + 
  		  									"<td></td>" + 
  		  									"<td>"+item.name+"</td>" + 
  		  									"<td>"+item.regdate+"</td>" + 
  		  									"</tr>"
  		  				});
  		  			}
  		  			else {
		  		  			html += "<tr>" + 
											"<td colspan='6' class='text-center'> 댓글이 없습니다. </td>" + 
											"</tr>"
  		  			}
  		  			$("tbody#commentDisplay").html(html); 
  		  		},
  	 			 error: function(request, status, error){
  	  	            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
  	  	         }
  		  	}) ; 		  
  		  
  	  }
  
  // === #127. Ajax로 불러온 댓글내용을  페이징 처리 하기  === //
   function goViewComment (currentShowPageNo){
		  	$.ajax({
  		  		url: "<%= request.getContextPath()%>/commentList.action" ,
  		  		data:{"parentSeq":"${bdvo.seq}" ,
  		  			    "currentShowPageNo":currentShowPageNo},
  		  		dataType:"json",
  		  		success:function(json){
  		  			// console.log("~~ 확인용 ~~ :  " + JSON.stringify(json));
  		  			let html = "" ; 
  		  			if ( json.length > 0 ){
  		  				$.each( json , function(index, item ){
  		  						html += "<tr>" + 
  		  									"<td>"+(index+1)+"</td>" + 
  		  									"<td>" + item.content+ "</td>" + 
  		  									<%-- === 첨부파일의 기능이 추가된 경우 시작 --%>
  		  									
  		  									"<td>"+item.orgFilename+"</td>" + 
  		  									"<td>"+item.fileSize+"</td>" + 
  		  									
  		  									<%-- === 첨부파일의 기능이 추가된 경우 끝 --%>
  		  									
  		  									"<td>"+item.name+"</td>" + 
  		  									"<td>"+item.regdate+"</td>" + 
  		  									"</tr>"
  		  				});
  		  			}
  		  			else {
		  		  			html += "<tr>" + 
											"<td colspan='6' class='text-center'> 댓글이 없습니다. </td>" + 
											"</tr>"
  		  			}
  		  			$("tbody#commentDisplay").html(html); 
  		  			
  		  			// == 페이지바 함수 호출 === // 
  		  			makeCommentPageBar(currentShowPageNo);
  		  			
  		  		},
  	 			 error: function(request, status, error){
  	  	            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
  	  	         }
  		  	}) ; 		  
  		  
  	  }
  
  // ==== 댓글내용 페이지바 Ajax로 만들기 ==== //
  function makeCommentPageBar(currentShowPageNo){
	  $.ajax({
			url: "<%= request.getContextPath()%>/getCommentTotalPage.action" ,
		  		data:{"parentSeq":"${bdvo.seq}" ,
		  			    "sizePerPage":"5"},
		  		dataType:"json",
		  		success:function(json){
		  			// console.log("~~ 확인용 ~~ :  " + JSON.stringify(json));
		  			// ~~ 확인용 ~~ :  {"totalPage":3}
		  			
		  			if ( json.totalPage > 0 ){
		  				// 댓글이 있는 경우 
		  				const totalPage = json.totalPage ;
		  				const blockSize = 10 ; 
		  				// blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
		  	            /*
		  	                             1 2 3 4 5 6 7 8 9 10  [다음][마지막]           -- 1개블럭
		  	               [맨처음][이전]  11 12 13 14 15 16 17 18 19 20  [다음][마지막]   -- 1개블럭
		  	               [맨처음][이전]  21 22 23
		  	            */
		  				let loop = 1;
		  	            /*
		  	                loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		  	             */
		  	             if ( typeof  currentShowPageNo == "string"){
		  	            	currentShowPageNo = Number(currentShowPageNo);
		  	             }
		  	    	      // *** !! 다음은 currentShowPageNo 를 얻어와서 pageNo 를 구하는 공식이다. !! *** //
		  	              let pageNo = Math.floor( (currentShowPageNo - 1)/blockSize ) * blockSize + 1;
		  	              /*
		  	                currentShowPageNo 가 3페이지 이라면 pageNo 는 1 이 되어야 한다.
		  	                ((3 - 1)/10) * 10 + 1;
		  	                ( 2/10 ) * 10 + 1;
		  	                ( 0.2 ) * 10 + 1;
		  	                Math.floor( 0.2 ) * 10 + 1;  // 소수부가 있을시 Math.floor(0.2) 은 0.2 보다 작은 최대의 정수인 0을 나타낸다.
		  	                0 * 10 + 1 
		  	                1
		  	                
		  	                currentShowPageNo 가 11페이지 이라면 pageNo 는 11 이 되어야 한다.
		  	                ((11 - 1)/10) * 10 + 1;
		  	                ( 10/10 ) * 10 + 1;
		  	                ( 1 ) * 10 + 1;
		  	                Math.floor( 1 ) * 10 + 1;  // 소수부가 없을시 Math.floor(1) 은 그대로 1 이다.
		  	                1 * 10 + 1
		  	                11
		  	                
		  	                currentShowPageNo 가 20페이지 이라면 pageNo 는 11 이 되어야 한다.
		  	                ((20 - 1)/10) * 10 + 1;
		  	                ( 19/10 ) * 10 + 1;
		  	                ( 1.9 ) * 10 + 1;
		  	                Math.floor( 1.9 ) * 10 + 1;  // 소수부가 있을시 Math.floor(1.9) 은 1.9 보다 작은 최대의 정수인 1을 나타낸다.
		  	                1 * 10 + 1
		  	                11
		  	             
		  	               /* 
		  	                1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은 1 이다.
		  	                11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.
		  	                21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
		  	                
		  	                currentShowPageNo         pageNo
		  	               ----------------------------------
		  	                     1                      1 = Math.floor((1 - 1)/10) * 10 + 1
		  	                     2                      1 = Math.floor((2 - 1)/10) * 10 + 1
		  	                     3                      1 = Math.floor((3 - 1)/10) * 10 + 1
		  	                     4                      1
		  	                     5                      1
		  	                     6                      1
		  	                     7                      1 
		  	                     8                      1
		  	                     9                      1
		  	                     10                     1 = Math.floor((10 - 1)/10) * 10 + 1
		  	                    
		  	                     11                    11 = Math.floor((11 - 1)/10) * 10 + 1
		  	                     12                    11 = Math.floor((12 - 1)/10) * 10 + 1
		  	                     13                    11 = Math.floor((13 - 1)/10) * 10 + 1
		  	                     14                    11
		  	                     15                    11
		  	                     16                    11
		  	                     17                    11
		  	                     18                    11 
		  	                     19                    11 
		  	                     20                    11 = Math.floor((20 - 1)/10) * 10 + 1
		  	                     
		  	                     21                    21 = Math.floor((21 - 1)/10) * 10 + 1
		  	                     22                    21 = Math.floor((22 - 1)/10) * 10 + 1
		  	                     23                    21 = Math.floor((23 - 1)/10) * 10 + 1
		  	                     ..                    ..
		  	                     29                    21
		  	                     30                    21 = Math.floor((30 - 1)/10) * 10 + 1
		  	                     
		  	            */
		  	            
		  	            let pageBarHTML = "<ul style='list-style: none;'>";
		  	            
		  	            // === [맨처음][이전] 만들기 === //
		  	            if(pageNo != 1) {
		  	               pageBarHTML += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='javascript:goViewComment(\"1\")'>[맨처음]</a></li>";
		  	               pageBarHTML += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='javascript:goViewComment(\""+(pageNo-1)+"\")'>[이전]</a></li>";
		  	            }
		  	            
		  	            while( !(loop > blockSize || pageNo > totalPage) ) {
		  	               
		  	               if(pageNo == currentShowPageNo) {
		  	                  pageBarHTML += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";  
		  	               }
		  	               else {
		  	                  pageBarHTML += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='javascript:goViewComment(\""+pageNo+"\")'>"+pageNo+"</a></li>"; 
		  	               }
		  	               
		  	               loop++;
		  	               pageNo++;
		  	               
		  	            }// end of while-----------------------
		  	            
		  	            
		  	            // === [다음][마지막] 만들기 === //
		  	            if( pageNo <= totalPage ) {
		  	               pageBarHTML += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='javascript:goViewComment(\""+pageNo+"\")'>[다음]</a></li>";
		  	               pageBarHTML += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='javascript:goViewComment(\""+totalPage+"\")'>[마지막]</a></li>"; 
		  	            }
		  	             
		  	            pageBarHTML += "</ul>";
		  	             
		  	            $("div#pageBar").html(pageBarHTML);
		  			}// end of if 
		  			
		  			
		  		},
	 			 error: function(request, status, error){
	  	            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
	  	         }
	  });
	  
	  
	  
	  
  }
  
  
</script>

<div style="display: flex;">
<div style="margin: auto; padding-left: 3%;">

   <h2 style="margin-bottom: 30px;">글내용보기</h2>

    <c:if test="${not empty requestScope.bdvo }">
       <table style="width: 1024px" class="table table-bordered table-dark">
          <tr>
              <th style="width: 15%">글번호</th>
              <td>${bdvo.seq }</td>
          </tr>
          <tr>
              <th>성명</th>
              <td>${bdvo.name}</td>
          </tr>
          <tr>
              <th>제목</th>
              <td>${bdvo.subject }</td>
          </tr>
          <tr>
              <th>내용</th>
              <td>
                <p style="word-break: break-all;"> ${bdvo.content }</p>
                <%-- 
                  style="word-break: break-all; 은 공백없는 긴영문일 경우 width 크기를 뚫고 나오는 것을 막는 것임. 
                       그런데 style="word-break: break-all; 나 style="word-wrap: break-word; 은
                       테이블태그의 <td>태그에는 안되고 <p> 나 <div> 태그안에서 적용되어지므로 <td>태그에서 적용하려면
                  <table>태그속에 style="word-wrap: break-word; table-layout: fixed;" 을 주면 된다.
             --%>
              </td>
          </tr>
          <tr>
              <th>조회수</th>
              <td>${bdvo.readCount }</td>
          </tr>
          <tr>
              <th>날짜</th>
              <td>${bdvo.regDate }</td>
          </tr>
          
          <%-- === #162. 첨부파일 이름 및 파일크기를 보여주고 첨부파일을 다운로드 되도록 만들기 === --%>
         <tr>
            <th>첨부파일</th>
           <td>
            		<c:if test="${sessionScope.loginuser != null}">
            			<a href="<%=ctxPath%>/download.action?seq=${bdvo.seq}">${bdvo.orgFilename}</a>
            		</c:if>
            		
            		<c:if test="${sessionScope.loginuser == null}">
            			${bdvo.orgFilename}
            		</c:if>
            	</td>
         </tr>
         <tr>
            <th>파일크기(bytes)</th>
              <td>
            		<fmt:formatNumber value="${bdvo.fileSize}" pattern="#,###" />
            </td>
         </tr>
          
       </table>
       <br/>
       
      <%--  <c:set var="v_gobackURL" value='${ fn:replace(gobackURL , "&"  , " " ) }' /> --%>
       
       <%-- 글조회수 1증가를 위해서  view.action 대신에 view_2.action 으로 바꾼다. --%>
       <div style="margin-bottom: 1%;">이전글제목&nbsp;&nbsp;<span class="move" onclick="javascript:location.href='<%=ctxPath%>/view_2.action?seq=${bdvo.previousseq}&searchType=${paraMap.searchType}&searchWord=${paraMap.searchWord}&gobackURL=${v_gobackURL}'">${bdvo.previoussubject}</span></div>
       <div style="margin-bottom: 1%;">다음글제목&nbsp;&nbsp;<span class="move" onclick="javascript:location.href='<%=ctxPath%>/view_2.action?seq=${bdvo.nextseq}&searchType=${paraMap.searchType}&searchWord=${paraMap.searchWord}&gobackURL=${v_gobackURL}'">${bdvo.nextsubject }</span></div>
       <br/> 
              
       <button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%=ctxPath%>/list.action'">전체목록보기</button>
       
       <%-- === #126. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
                                      사용자가 목록보기 버튼을 클릭했을 때 돌아갈 페이지를 알려주기 위해
                                      현재 페이지 주소를 뷰단으로 넘겨준다. === --%>
       <button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%=ctxPath%>${gobackURL}'">검색된결과목록보기</button>
       
       <button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%=ctxPath%>/edit.action?seq=${bdvo.seq}'">글수정하기</button>
       <button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%=ctxPath%>/del.action?seq=${bdvo.seq}'">글삭제하기</button>
       
       <%-- === #141. 어떤글에 대한 답변글쓰기는 로그인 되어진 회원의 gradelevel 컬럼의 값이 10인 직원들만 답변글쓰기가 가능하다.  === --%>
     
       <c:if test="${sessionScope.loginuser.gradelevel == 10 }">
       		 <span style="display:none;">groupno  :  ${bdvo.groupno} </span>
       		 <span style="display:none;">depthno  :  ${bdvo.depthno }</span>
       		 
             <button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%=ctxPath%>/add.action?fk_seq=${bdvo.seq}&groupno=${bdvo.groupno }&depthno=${bdvo.depthno}&subject=${bdvo.subject}'">답변 글쓰기</button>
       </c:if>
       
       <%-- === #83. 댓글쓰기 폼 추가 === --%>
       <c:if test="${not empty sessionScope.loginuser}">
	      <h3 style="margin-top : 50px;" >댓글쓰기 </h3>
	       <form name="addWriteFrm" id="addWriteFrm" style="margin-top: 20px;" enctype="multipart/form-data">
	             <table class="table" style="width: 1024px">
	               <tr style="height: 30px;">
	                  <th width="10%">성명</th>
	                  <td>
	                     	<input type="hidden" name="fk_userid" id="fk_userid" value="${sessionScope.loginuser.userid }" }/>
	                     	<input type="text" name="name" id="name" value="${sessionScope.loginuser.name}"} />
	                     	
	                  </td>
	               </tr>
	               <tr style="height: 30px;">
	                  <th>댓글내용</th>
	                  <td>
	                     <input type="text" name="content" id="commentContent" size="100" />
	                     <!--  댓글에 달리는 원게시물 글번호 ( 즉 , 댓글의 부모 글 번호-->
	                     <input type="hidden" name="parentSeq"  value="${bdvo.seq}"/>
	                  </td>
	               </tr>
	               
	               <tr style="height: 30px;">
	                  <th>파일첨부</th>
	                  <td>
	                      <input type="file" name="attach" id="attach" />
	                  </td>
	               </tr>
	               
	               <tr>
	                  <th colspan="2">
	                     <button type="button" class="btn btn-success btn-sm mr-3" onclick="goAddWrite()">댓글쓰기 확인</button>
	                     <button type="reset" class="btn btn-success btn-sm">댓글쓰기 취소</button>
	                  </th>
	               </tr>
	           </table>         
	          </form>
       </c:if>
       
       <%-- === #94. 댓글 내용 보여주기 === --%>
       <h3 style="margin-top: 50px;">댓글내용</h3>
      <table class="table" style="width: 1024px; margin-top: 2%; margin-bottom: 3%;">
         <thead>
         <tr>
             <th style="width: 6%;  text-align: center;">번호</th>
             <th style="width: 25%;  text-align: center;">내용</th>
            <%-- === #162.  첨부파일 이름 및 파일크기를 보여주고 첨부파일을 다운로드 되로록 만들기  --%>
            <th style="width: 15%">첨부파일</th>
            	
            
            <th style="width: 8%">bytes</th>
            
    
            	
            	
            <%-- 첨부파일 있는 경우 끝 --%>
            
            <th style="width: 8%; text-align: center;">작성자</th>
            <th style="width: 12%; text-align: center;">작성일자</th>
         </tr>
         </thead>
         <tbody id="commentDisplay"></tbody>
      </table>
      
      <%-- ==== #136. 댓글 페이지바 ==== --%>
       <div style="display: flex; margin-bottom: 50px;">
          <div id="pageBar" style="margin: auto; text-align: center;"></div>
       </div>
       
    </c:if>

   <c:if test="${empty requestScope.bdvo }">
       <div style="padding: 50px 0; font-size: 16pt; color: red;" >존재하지 않습니다</div>
    </c:if>
    
</div>
</div>    