<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%
   String ctxPath = request.getContextPath();
%>   

<style type="text/css">
		/* -- CSS 로딩화면 구현 시작(bootstrap 에서 가져옴) -- */    
		   div.loader {
		     border: 16px solid #f3f3f3;
		     border-radius: 50%;
		     border-top: 12px dotted blue;
		     border-right: 12px dotted green; 
		     border-bottom: 12px dotted red; 
		     border-left: 12px dotted pink; 
		     width: 120px;
		     height: 120px;
		     -webkit-animation: spin 2s linear infinite;
		     animation: spin 2s linear infinite;
		   }
		   
		   @-webkit-keyframes spin {
		     0% { -webkit-transform: rotate(0deg); }
		     100% { -webkit-transform: rotate(360deg); }
		   }
		   
		   @keyframes spin {
		     0% { transform: rotate(0deg); }
		     100% { transform: rotate(360deg); }
		   }
		/* -- CSS 로딩화면 구현 끝(bootstrap 에서 가져옴) -- */   
		
		div.fileDrop{ display: inline-block; 
                 width: 100%; 
                 height: 100px;
                 overflow: auto;
                 background-color: #fff;
                 padding-left: 10px;}
                 
   		div.fileDrop > div.fileList > span.delete{display:inline-block; width: 20px; border: solid 1px gray; text-align: center;} 
   		div.fileDrop > div.fileList > span.delete:hover{background-color: #000; color: #fff; cursor: pointer;}
  	 	div.fileDrop > div.fileList > span.fileName{padding-left: 10px;}
   		div.fileDrop > div.fileList > span.fileSize{padding-right: 20px; float:right;} 
   		span.clear{clear: both;} 
</style>

<script type="text/javascript">

  $(document).ready(function(){
	  // CSS 로딩 화면 감추기 
	  $('div.loader').hide();
	  
	  <%-- === jQuery 를 사용하여 드래그앤드롭(DragAndDrop)을 통한 파일 업로드 시작 === --%>
	  		let file_arr =  []; 
	  		
	  		// == 파일 Drag & Drop 만들기 == // 
	  		$("div#fileDrop").on("dragenter" ,  function(e){ /* "dragenter" 이벤트는 드롭대상인 박스 안에 Drag 한 파일이 최초로 들어왔을 때 */ 
	  			e.preventDefault();
	  	      <%-- 
              브라우저에 어떤 파일을 drop 하면 브라우저 기본 동작이 실행된다. 
              이미지를 drop 하면 바로 이미지가 보여지게되고, 만약에 pdf 파일을 drop 하게될 경우도 각 브라우저의 pdf viewer 로 브라우저 내에서 pdf 문서를 열어 보여준다. 
              이것을 방지하기 위해 preventDefault() 를 호출한다. 
              즉, e.preventDefault(); 는 해당 이벤트 이외에 별도로 브라우저에서 발생하는 행동을 막기 위해 사용하는 것이다.
   				 --%>
              e.stopPropagation();
              <%--
              propagation 의 사전적의미는 전파, 확산이다.
              stopPropagation 은 부모태그로의 이벤트 전파를 stop 중지하라는 의미이다.
                        즉, 이벤트 버블링을 막기위해서 사용하는 것이다. 
                        사용예제 사이트 https://devjhs.tistory.com/142 을 보면 이해가 될 것이다. 
          --%>
	  		} ).on("dragover" , function(e){/* "dragover" 이벤트는 드롭대상인 박스 안에 Drag 한 파일이 머물러 있는 중일 때. 필수이벤트이다. 
		  		dragover 이벤트를 적용하지 않으면 drop 이벤트가 작동하지 않음 */
	  			e.preventDefault();
	  			e.stopPropagation();
	  			$(this).css("background-color" , "#ffd8d8");
	  		} ).on("dragleave" , function(e){	/* "dragleave" 이벤트는 Drag 한 파일이 드롭대상인 박스 밖으로 벗어났을 때  */
	  			e.preventDefault();
	  			e.stopPropagation();
	  			$(this).css("background-color" , "#fff");
	  		}).on("drop", function(e){ /* "drop" 이벤트는 드롭대상인 박스 안에서 Drag 한것을 Drop(Drag 한 파일(객체)을 놓는것) 했을 때. 필수이벤트이다. */
	  			e.preventDefault();
	  		
	  			var files = e.originalEvent.dataTransfer.files;  
	  			//  Drag & Drop 동작에서 파일 정보는 DataTransfer 라는 객체를 통해 얻어올 수 있다. 
	            //  jQuery를 이용하는 경우에는 event가 순수한 DOM 이벤트(각기 다른 웹브라우저에서 해당 웹브라우저의 객체에서 발생되는 이벤트)가 아니기 때문에,
	            //  event.originalEvent를 사용해서 순수한 원래의 DOM 이벤트 객체를 가져온다.
	            //  Drop 된 파일은 드롭이벤트가 발생한 객체(여기서는 $("div#fileDrop")임)의 dataTransfer 객체에 담겨오고, 
	            //  담겨진 dataTransfer 객체에서 files 로 접근하면 드롭된 파일의 정보를 가져오는데 그 타입은 FileList 가 되어진다. 
	            //  그러므로 for문을 사용하든지 또는 [0]을 사용하여 파일의 정보를 알아온다. 
	  			
	             // console.log( typeof files); // object
	             // console.log(files);  // FileList 
	            
	            if ( files != null && files != undefined ){
	            	// console.log(" files.length 는 =>  "+ files.length );
	            	// files.length 는 => 1 이다. 
	            	<%--
	            	for ( let i=0 ; i < files.length ; i++ ){
	            		const f = files[i];
	            		const fileName = f.name ; // 파일명 
	            		const fileSize = f.size ; // 파일 크기 
	            		// console.log(" 파일명 : " + fileName );   // 파일명 : CASE WHEN.jpg

	            		// console.log(" 파일크기 : " + fileSize ); //  파일크기 : 3015204
	            		
	            	}// end of for 문
	            	--%>
	            	
	            	let html = "" ; 
	            	const f = files[0]; // 어차피 files.length 의 값이 1 이므로 위의 for문을 사용하지 않고 files[0] 을 사용하여 1개만 가져오면 된다.
	            	let fileSize = f.size/1024/1024;  /* 파일의 크기는 MB로 나타내기 위하여 /1024/1024 하였음 */
	            	
	            	if ( fileSize >= 10) {
	            		alert("10MB 이상인 파일은 업로드할 수 없습니다.!!");
	                    $(this).css("background-color", "#fff");
	                    return;
	            	}
	            	else{
	            		file_arr.push(f);         		
			            const fileName = f.name ; // 파일명
			            fileSize = fileSize < 1 ? fileSize.toFixed(3) : fileSize.toFixed(1);
		                  // fileSize 가 1MB 보다 작으면 소수부는 반올림하여 소수점 3자리까지 나타내며, 
		                   // fileSize 가 1MB 이상이면 소수부는 반올림하여 소수점 1자리까지 나타낸다. 만약에 소수부가 없으면 소수점은 0 으로 표시한다.
		                   /* 
		                        numObj.toFixed([digits]) 의 toFixed() 메서드는 숫자를 고정 소수점 표기법(fixed-point notation)으로 표시하여 나타난 수를 문자열로 반환해준다. 
		                                        파라미터인 digits 는 소수점 뒤에 나타날 자릿수 로써, 0 이상 20 이하의 값을 사용할 수 있으며, 구현체에 따라 더 넓은 범위의 값을 지원할 수도 있다. 
		                        digits 값을 지정하지 않으면 0 을 사용한다.
		                        
		                        var numObj = 12345.6789;

		                   numObj.toFixed();       // 결과값 '12346'   : 반올림하며, 소수 부분을 남기지 않는다.
		                   numObj.toFixed(1);      // 결과값 '12345.7' : 반올림한다.
		                   numObj.toFixed(6);      // 결과값 '12345.678900': 빈 공간을 0 으로 채운다.
		                   */
		                   html += 
		                       "<div class='fileList'>" +
		                           "<span class='delete'>&times;</span>" +
		                           "<span class='fileName'>"+fileName+"</span>" +
		                           "<span class='fileSize'>"+fileSize+" MB</span>" +
		                           "<span class='clear'></span>" +
		                       "</div>";
		                       
		                   $(this).append(html);
		                   
			            
	            	}
            		
	            }// end of if 문
	  		
	  			$(this).css("background-colro" , "#fff") ; 
	  		});
	  		
	  		// === drop 되어진 파일목록 제거하기 === //
	  		$(document).on('click', 'span.delete' , function(e){
	  			let idx = $("span.delete").index($(e.target));
	  			// alert (" 인덱스 : " + idx );
	  			// console.log('배열에서 파일을 제거하기 전 => ' + file_arr) ;
	  			file_arr.splice(idx,1);
	  			// console.log(file_arr);
	  			<%-- 
                배열명.splice() : 배열의 특정 위치에 배열 요소를 추가하거나 삭제하는데 사용한다. 
                                         삭제할 경우 리턴값은 삭제한 배열 요소이다. 삭제한 요소가 없으면 빈 배열( [] )을 반환한다.
    
            배열명.splice(start, 0, element);  // 배열의 특정 위치에 배열 요소를 추가하는 경우 
                    start   - 수정할 배열 요소의 인덱스
                       0       - 요소를 추가할 경우
                       element - 배열에 추가될 요소
           
                    배열명.splice(start, deleteCount); // 배열의 특정 위치의 배열 요소를 삭제하는 경우    
                       start   - 수정할 배열 요소의 인덱스
                       deleteCount - 삭제할 요소 개수
  				  --%>
                   $(e.target).parent().remove(); 
	  			
	  		});
	  	  
	  	  // 보내기 버튼
	         $("button#btnWrite").click(function(){
	            
	         <%-- == 스마트 에디터 구현 시작 ==--%>
	            // id 가 content 인 textarea 에 에디터 대입
	         <%-- == 스마트 에디터 구현 끝 ==--%>
	         
	           // 받는 사람 유효성 검사
	               const recipient = $("input#recipient").val().trim();
	               if(recipient == "") {
	                  alert("받는 사람 이메일 주소를 입력하세요!!");
	                  return;
	               }
	               
	          // 제목 유효성 검사
	               const subject = $("input#subject").val().trim();
	               if(subject == "") {
	                  alert("이메일 제목을 입력하세요!!");
	                  return;
	               }
	                           
	           <%-- ==== 글내용 유효성 검사(스마트 에디터 사용 할 경우) 시작 ==== --%>
	               let content = $("textarea#content").val();
	               
	               // 글내용 유효성 검사하기
	               // alert(content); // content에 공백만 여러개를 입력하여 쓰기를 할 경우 알아본 것.
	               /*
	                 <p>&nbsp;</p>
	                 <p>&nbsp; &nbsp; &nbsp; &nbsp;</p>
	               */
	               
	               content = content.replace(/&nbsp;/gi, ""); 
	                                        // &nbsp; 를 "" 으로 변환한다.
	                                        /*    
	                                                대상문자열.replace(/찾을 문자열/gi, "변경할 문자열");
	                                            ==> 여기서 꼭 알아야 될 점은 나누기(/)표시안에 넣는 찾을 문자열에는 "" 는 없어야 한다는 점입니다. 
	                                                         그리고 뒤의 gi는 다음을 의미합니다.
	                                       
	                                            g : 전체 모든 문자열을 변경 global
	                                            i : 영문 대소문자를 무시, 모두 일치하는 패턴 검색 ignore
	                                      */ 
	            //alert(content);                                                             
	            /*
	            <p></p>
	            <p>                </p>
	            */                                        
	            content = content.substring(content.indexOf("<p>") + 3); //                 </p>
	            content = content.substring(0, content.indexOf("</p>"));
	            

	               
	          <%-- ==== 글내용 유효성 검사(스마트 에디터 사용 할 경우) 끝 ==== --%>
	          
	              // ajax 를 사용하여 파일 전송하는 2가지 방법
	              // 첫번째 ==> ajaxForm 을 사용하는 것 (tiles1/view.jsp 참조)
	              // 두번째 ==> FormData 를 사용하는 것 (tiles2/empList.jsp 참조)
	          
	                
	          /* 
	            FormData 객체는 ajax 로 폼 전송을 가능하게 해주는 자바스크립트 객체이다.
	                    즉, FormData란 HTML5 의 <form> 태그를 대신 할 수 있는 자바스크립트 객체로서,
	                   자바스크립트 단에서 ajax 를 사용하여 폼 데이터를 다루는 객체라고 보면 된다. 
	            FormData 객체가 필요하는 경우는 ajax로 파일을 업로드할 때 필요하다.
	          */ 
	       
	          /*
	             === FormData 의 사용방법 2가지 ===
	             <form id="myform">
	                <input type="text" id="title" name="title" />
	                <input type="file" id="imgFile" name="imgFile" />
	             </form>
	                  
	                    첫번째 방법, 폼에 작성된 전체 데이터 보내기   
	             var formData = new FormData($("form#myform").get(0));  // 폼에 작성된 모든것
	             
	                     또는
	             var formData = new FormData($("form#myform")[0]);  // 폼에 작성된 모든것
	             // jQuery선택자.get(0) 은 jQuery 선택자인 jQuery Object 를 DOM(Document Object Model) element 로 바꿔주는 것이다. 
	            // DOM element 로 바꿔주어야 순수한 javascript 문법과 명령어를 사용할 수 있게 된다. 
	          
	         // 또는
	             var formData = new FormData(document.getElementById('myform'));  // 폼에 작성된 모든것
	           
	                    두번째 방법, 폼에 작성된 것 중 필요한 것만 선택하여 데이터 보내기 
	             var formData = new FormData();
	          // formData.append("key", value값);
	             formData.append("title", $("input#title").val());
	             formData.append("imgFile", $("input#imgFile")[0].files[0]);
	         */  
	                  
	              // var formData = new FormData($("form#addFrm").get(0));  // $("form#addFrm") 폼에 작성된 모든 데이터 보내기
	              // 또는
	              var formData = new FormData($("form[name='addFrm']").get(0));  // $("form#addFrm") 폼에 작성된 모든 데이터 보내기
	              
	              if(file_arr.length > 0){// 파일 첨부가 있을 경우
	                 
	                 // 첨부한 파일의 총합의 크기가 10MB 이상이라면 메일 전송을 못하게 막는다.
	                 let sum_file_size = 0;
	                  
	                   for(let i=0; i<file_arr.length;i++){
	                     sum_file_size += file_arr[i].size;
	                   } // end of for -------------------------------------------
	                   
	                   if(sum_file_size >= 10*1024*1024){ // 첨부한 파일의 총합의 크기가 10MB 이상이라면
	                      alert("첨부한 파일의 총합의 크기가 10MB 이상이라서 파일을 업로드 할 수 없습니다.");
	                        return; // 종료
	                   }
	                   else{ // formData 속에 첨부파일을 넣어주기 
	                      file_arr.forEach(function(item){
	                         formData.append("file_arr", item); // 첨부파일을 추가하기
	                      });                            // 같은 key를 가진 값을 여러 개 넣을 수 있다.(덮어씌워지지 않고 추가가 된다.)
	                   }
	                 
	              }// end of if(file_arr.length > 0) ----------------------------
	              
	              $("div.loader").show();// CSS 로딩화면 보여주기
	                       
		          		$.ajax({
						url : "<%=ctxPath%>/emailWrite.action",
	                    type : "post",
	                    data : formData,
	                    processData:false,  // 파일 전송시 설정 
	                    contentType:false,  // 파일 전송시 설정 
	                    dataType:"json",
	                    success:function(json){
	                       if(json.result == 1) {
	                       		location.href="<%=ctxPath%>/emailWrite_done.action" 
	                       }
	                        else {
	                           alert("메일 보내기가 실패했습니다.ㅜㅜ");
	                        }
	                    },
	                    error: function(request, status, error){
	                    alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
	                    }	
					
						}); // end of ajax 	
		         	
	     	});
	  	
	  	<%-- === jQuery 를 사용하여 드래그앤드롭(DragAndDrop)을 통한 파일 업로드 끝 === --%>
		  	
	  
	      
  });// end of $(document).ready(function(){})-------------------------------

  </script>

<div style="display: flex;">
<div style="margin: auto; padding-left: 3%;">

<%-- CSS 로딩화면 구현한것--%>
    <div style="display: flex">
      <div class="loader" style="margin: auto"></div>
   	</div>
   
   	<h2 style="margin-bottom: 30px;">e메일 쓰기</h2>
     
<form name="addFrm" id="addFrm" enctype="multipart/form-data">
   
   <div class="my-3">
      <button type="button" class="btn btn-success mr-3" id="btnWrite">보내기</button>
      <button type="button" class="btn btn-danger" onclick="javascript:location.reload(true)">취&nbsp;소</button>
   </div>
   
   <table style="width: 1024px" class="table table-bordered">
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">받는사람</th>
         <td>
				<input type="email" name="recipient" id="recipient" size="100" />
         </td>
      </tr>
      
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">제목</th>
         <td>
				<input type="text" name="subject" id="subject" size="100" /> 
         </td>
      </tr>
      
      <%-- === #150. 파일첨부 타입 추가하기 ===  --%>
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">파일첨부</th>
         <td>
			<span style="font-size: 10pt;">파일을 마우스로 끌어 오세요</span>
            <div id="fileDrop" class="fileDrop border border-secondary"></div>
         </td>
      </tr>
      
      <tr>
         <th style="width: 15%; background-color: #DDDDDD;">내용</th>
         <td>
            	<textarea style="width:100%; height: 612px;" name="content" id="content"></textarea>
         </td>
      </tr>

   </table>
   	
   
   
   

   
</form>   
</div>
</div>
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    