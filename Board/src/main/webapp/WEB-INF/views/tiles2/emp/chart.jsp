<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% String ctxPath = request.getContextPath(); %>
<script src="<%=ctxPath%>/resources/Highcharts-11.0.1/code/highcharts.js"></script>
<script src="<%=ctxPath%>/resources/Highcharts-11.0.1/code/modules/exporting.js"></script>
<script src="<%=ctxPath%>/resources/Highcharts-11.0.1/code/modules/export-data.js"></script>
<script src="<%=ctxPath%>/resources/Highcharts-11.0.1/code/modules/accessibility.js"></script>
<script src="<%=ctxPath%>/resources/Highcharts-11.0.1/code/modules/series-label.js"></script>

<script src="<%=ctxPath%>/resources/Highcharts-11.0.1/code/modules/data.js"></script>
<script src="<%=ctxPath%>/resources/Highcharts-11.0.1/code/modules/drilldown.js"></script>

<style type="text/css">
.highcharts-figure,
.highcharts-data-table table {
    min-width: 320px;
    max-width: 800px;
    margin: 1em auto;
}

.highcharts-data-table table {
    font-family: Verdana, sans-serif;
    border-collapse: collapse;
    border: 1px solid #ebebeb;
    margin: 10px auto;
    text-align: center;
    width: 100%;
    max-width: 500px;
}

.highcharts-data-table caption {
    padding: 1em 0;
    font-size: 1.2em;
    color: #555;
}

.highcharts-data-table th {
    font-weight: 600;
    padding: 0.5em;
}

.highcharts-data-table td,
.highcharts-data-table th,
.highcharts-data-table caption {
    padding: 0.5em;
}

.highcharts-data-table thead tr,
.highcharts-data-table tr:nth-child(even) {
    background: #f8f8f8;
}

.highcharts-data-table tr:hover {
    background: #f1f7ff;
}

input[type="number"] {
    min-width: 50px;
}
div#table_container table {width: 100%}
   div#table_container th, div#table_container td {border: solid 1px gray; text-align: center;} 
   div#table_container th {background-color: #595959; color: white;} 


</style>

<script type="text/javascript">
		$(document).ready(function(){
				$("select#searchType").change(function(){
					func_choice($(this).val());
					// $(this).val() 은 "" 또는 "deptname" 또는 "gender" 또는 "genderHireYear" 또는 "deptnameGender" 이다.
					
				});
				
		}); // END OF $(DOCUMENT).READY(FUNCTION()
			
		function func_choice(searchTypeVal){
			switch (searchTypeVal) {
			case "": // 아무것도 선택를 안한 경우
				$("div#chart_container").empty();
				$("div#table_container").empty();
				$("div.highcharts-data-table").empty();
				break;
			
			case "deptname": // 부서별 인원통계를 선택한 경우
				$.ajax({ 
					url:"<%=ctxPath%>/chart/employeeCntByDeptname.action",	
					dataType:"json",
					success:function(json){
							/*
							console.log(JSON.stringify(json));
							[{"department_name":"Shipping","cnt":"45","percent":"40.54"},
							{"department_name":"Sales","cnt":"34","percent":"30.63"},
							{"department_name":"IT","cnt":"9","percent":"8.11"},
							{"department_name":"Purchasing","cnt":"6","percent":"5.41"},
							{"department_name":"Finance","cnt":"6","percent":"5.41"},
							{"department_name":"Executive","cnt":"3","percent":"2.7"},
							{"department_name":"Marketing","cnt":"2","percent":"1.8"},
							{"department_name":"Accounting","cnt":"2","percent":"1.8"},
							{"department_name":"Administration","cnt":"1","percent":"0.9"},
							{"department_name":"부서없음","cnt":"1","percent":"0.9"},
							{"department_name":"Public Relations","cnt":"1","percent":"0.9"},
							{"department_name":"Human Resources","cnt":"1","percent":"0.9"}]
							*/
						let resultArr = []; 	
						for ( let i = 0 ; i<json.length ; i++  ){
							let obj ;
							
							if ( i == 0 ){
								obj = {name:json[i].department_name
										, y:Number(json[i].percent) , sliced : true , selected : true };
							}
							else{
								obj = {name:json[i].department_name
										, y:Number(json[i].percent)  } ;
							}
							resultArr.push(obj); 
							
						}	
				////////////////////////////////////////////////////////////////////////////////////////	
						chartSetting(' 우리 회사 부서별 통계 ' , resultArr , '인원비율' ) ;
				////////////////////////////////////////////////////////////////////////////////////////	
						var html =  "<table>";
                        html += "<tr>" +
                                  "<th>부서명</th>" +
                                  "<th>인원수</th>" +
                                  "<th>퍼센티지</th>" +
                                "</tr>";
                         
                         $.each(json , function(index,item){
                        	 html += "<tr>"
                        	 			+"<td>"+item.department_name+"</td>"	
                        				+"<td>"+item.cnt+"</td>"	
                        				+"<td>"+Number(item.percent)+"</td>"	
                                   	 +"</tr>"
                         });
                         $("div#table_container").html(html) ;
                                
					
					},
					error: function(request, status, error){
		                  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
					}
					
				});
				break;

			
				
			case "gender":  // 성별 인원통계를 선택한 경우 
				$.ajax({ 
					url:"<%=ctxPath%>/chart/employeeCntByGender.action",	
					dataType:"json",
					success:function(json){
						let resultArr = []; 	
						for ( let i = 0 ; i<json.length ; i++  ){
							let obj ;
							
							if ( i == 0 ){
								obj = {name:json[i].gender
										, y:Number(json[i].percent) , sliced : true , selected : true };
							}
							else{
								obj = {name:json[i].gender
										, y:Number(json[i].percent)  } ;
							}
							resultArr.push(obj); 
							
						}	
				////////////////////////////////////////////////////////////////////////////////////////	
						chartSetting(' 우리 부서 성별 통계 ' , resultArr , '인원수' ) ;
				////////////////////////////////////////////////////////////////////////////////////////	
						var html =  "<table>";
                        html += "<tr>" +
                                  "<th>성별</th>" +
                                  "<th>인원수</th>" +
                                  "<th>퍼센티지</th>" +
                                "</tr>";
                         
                         $.each(json , function(index,item){
                        	 html += "<tr>"
                        	 			+"<td>"+item.gender+"</td>"	
                        				+"<td>"+item.cnt+"</td>"	
                        				+"<td>"+Number(item.percent)+"</td>"	
                                   	 +"</tr>"
                         });
                         $("div#table_container").html(html) ;
                                
					
					},
					error: function(request, status, error){
		                  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
					}
					
				});
				break;
			case "genderHireYear": // 성별 입사년도별 통계 선택한 경우
				$.ajax({ 
					url:"<%=ctxPath%>/chart/employeeCntByGenderHireYear.action",	
					dataType:"json",
					success:function(json){
						// console.log(JSON.stringify(json)); 
						let resultArr = [];
						for ( let i=0 ; i< json.length ; i++ ){
							let hireYear_arr = [] ;
							hireYear_arr.push(Number(json[i].Y2001));
							hireYear_arr.push(Number(json[i].Y2002));
							hireYear_arr.push(Number(json[i].Y2003));
							hireYear_arr.push(Number(json[i].Y2004));
							hireYear_arr.push(Number(json[i].Y2005));
							hireYear_arr.push(Number(json[i].Y2006));
							hireYear_arr.push(Number(json[i].Y2007));
							hireYear_arr.push(Number(json[i].Y2008));
							
							let obj = {name: json[i].gender , data: hireYear_arr};
							resultArr.push(obj); // 배열속에 객체를 넣기 
							
						}
						///////////////////////////////////////////////////////////////////////////////////////
						Highcharts.chart('chart_container', {

						    title: {
						        text: '2001년~2008년 우리회사 연도별 성별 입사 인원수'
						    },

						    subtitle: {
						        text: 'Source: <a href="http://localhost:9090/board/emp/empList.action" target="_blank">HR.employees</a>'
						    },

						    yAxis: {
						        title: {
						            text: '입사인원수'
						        }
						    },

						    xAxis: {
						        accessibility: {
						            rangeDescription: '범위 : 2001 to 2008 '
						        }
						    },

						    legend: {
						        layout: 'vertical',
						        align: 'right',
						        verticalAlign: 'middle'
						    },

						    plotOptions: {
						        series: {
						            label: {
						                connectorAllowed: false
						            },
						            pointStart: 2010
						        }
						    },

						    series: resultArr ,
						    responsive: {
			                    rules: [{
			                        condition: {
			                            maxWidth: 500
			                        },
			                        chartOptions: {
			                            legend: {
			                                layout: 'horizontal',
			                                align: 'center',
			                                verticalAlign: 'bottom'
			                            }
			                        }
			                    }]
			                }
						///////////////////////////////////////////////////////////////////////////////////////
						});
						var html =  "<table>";
                        html += "<tr>" +
                                  "<th>성별</th>" +
                                  "<th>2001년</th>" +
                                  "<th>2002년</th>" +
                                  "<th>2003년</th>" +
                                  "<th>2004년</th>" +
                                  "<th>2005년</th>" +
                                  "<th>2006년</th>" +
                                  "<th>2007년</th>" +
                                  "<th>2008년</th>" +
                                "</tr>";
               
                    $.each(json, function(index, item){
                       html += "<tr>" +
                                   "<td>"+ item.gender +"</td>" +
                                   "<td>"+ item.Y2001 +"</td>" +
                                   "<td>"+ item.Y2002 +"</td>" +
                                   "<td>"+ item.Y2003 +"</td>" +
                                   "<td>"+ item.Y2004 +"</td>" +
                                   "<td>"+ item.Y2005 +"</td>" +
                                   "<td>"+ item.Y2006 +"</td>" +
                                   "<td>"+ item.Y2007 +"</td>" +
                                   "<td>"+ item.Y2008 +"</td>" +
                               "</tr>";
                    });        
                            
                    html += "</table>";
                    
                    $("div#table_container").html(html);
					},
					error: function(request, status, error){
		                  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
					}
			
				});
			
			
			
				break;
			
			
			
			
			
			case "deptnameGender": // 부서별 성별 인원통계 선택한 경우
				
				$.ajax({
					url:"<%=ctxPath%>/chart/employeeCntByDeptname.action",	
					dataType:"json",
					success:function(json){
						let deptnameArr = []; // 부서명별 인원수 퍼센티지 객체 배열
						$.each(json, function(index, item){
							deptnameArr.push({name: item.department_name ,
			                    y: Number(item.percent),
			                    drilldown: item.department_name});
						});
						
						let genderArr = []; // 특정 부서명에 근무하는 직원들의 성별 인원수 퍼센티지 객체 배열 
						
						
						$.each(json , function(index,item){
							$.ajax({
								url:"<%=ctxPath%>/chart/genderCntSpecialDeptname.action",
								data: {"deptname" : item.department_name }
							});
							
						});
						
						////////////////////////////////////////////////////////////////////////////////////////////////////////
						// Create the chart
						Highcharts.chart('chart_container', {
						    chart: {
						        type: 'column'
						    },
						    title: {
						        align: 'left',
						        text: '부서별 남녀 비율'
						    },
						    subtitle: {
						        align: 'left',
						        text: 'Source: <a href="http://localhost:9090/board/emp/empList.action" target="_blank">HR.employees</a>'
						    },
						    accessibility: {
						        announceNewData: {
						            enabled: true
						        }
						    },
						    xAxis: {
						        type: 'category'
						    },
						    yAxis: {
						        title: {
						            text: '구성비율'
						        }

						    },
						    legend: {
						        enabled: false
						    },
						    plotOptions: {
						        series: {
						            borderWidth: 0,
						            dataLabels: {
						                enabled: true,
						                format: '{point.y:.1f}%'
						            }
						        }
						    },

						    tooltip: {
						        headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
						        pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
						    },

						    series: [
						        {
						            name: '부서명',
						            colorByPoint: true,
						            data:  deptnameArr 
						         // **** 위에서 구한 값을 대입시킴. 부서명별 인원수 퍼센티지 객체 배열  ****
						        }
						    ],
						    drilldown: {
						        breadcrumbs: {
						            position: {
						                align: 'right'
						            }
						        },
						        series:  genderArr  // **** 위에서 구한 값을 대입시킴. 특정 부서명에 근무하는 직원들의 성별 인원수 퍼센티지 객체 배열  ****
						    }
						});
						///////////////////////////////////////////////////////////////////////////////////////////////////
					},
					error: function(request, status, error){
		                  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
					}
				
				});
				
				break;
			
			default:
				break;
			}
		}// END OF 	function func_choice(searchTypeVal){

		function chartSetting(set_title , resultArr , set_name ){	
				Highcharts.chart('chart_container', {
				    chart: {
				        plotBackgroundColor: null,
				        plotBorderWidth: null,
				        plotShadow: false,
				        type: 'pie'
				    },
				    title: {
				        text: set_title ,
				        align: 'left'
				    },
				    tooltip: {
				        pointFormat: '{series.name}: <b>{point.percentage:.2f}%</b>'
				    },
				    accessibility: {
				        point: {
				            valueSuffix: '%'
				        }
				    },
				    plotOptions: {
				        pie: {
				            allowPointSelect: true,
				            cursor: 'pointer',
				            dataLabels: {
				                enabled: true,
				                format: '<b>{point.name}</b>: {point.percentage:.2f} %'
				            }
				        }
				    },
				    series: [{
				        name: set_name ,
				        colorByPoint: true,
				        data: resultArr
				    }]
				});  
		}
			
			
		
		
	

</script>

<div style="display: flex;">   
<div style="width: 80%; min-height: 1100px; margin:auto; ">

   <h2 style="margin: 50px 0;">HR 사원 통계정보(차트)</h2>
   
   <form name="searchFrm" style="margin: 20px 0 50px 0; ">
      <select name="searchType" id="searchType" style="height: 30px;">
         <option value="">통계선택하세요</option>
         <option value="deptname">부서별 인원통계</option>
         <option value="gender">성별 인원통계</option>
         <option value="genderHireYear">성별 입사년도별 통계</option>
         <option value="deptnameGender">부서별 성별 인원통계</option>
      </select>
   </form>
   
   <div id="chart_container"></div>
   <div id="table_container" style="margin: 40px 0 0 0;"></div>

</div>
</div>

<!-- <figure class="highcharts-figure">
  <div id="container"></div>
  <p class="highcharts-description">
    Pie charts are very popular for showing a compact overview of a
    composition or comparison. While they can be harder to read than
    column charts, they remain a popular choice for small datasets.
  </p>
</figure> -->