package com.spring.employees.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spring.board.model.MemberVO;
import com.spring.employees.service.InterEmpService;

@Controller
public class EmpController {

		@Autowired
		private InterEmpService service ;
	
		// === #175. 다중 체크박스를 사용시 sql문에서 in 절을 처리하는 예제 === //
		@RequestMapping(value="/emp/empList.action")
		public String empmanager_employeeInfoView(HttpServletRequest request) {
			// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 
			List<String> depIdList = service.depIdList(); 
			
			String str_DeptId = request.getParameter("str_DeptId");
			// System.out.println("=== 확인용 str_DeptId  : " + str_DeptId);
			// === 확인용 str_DeptId  : null
			// 뷰단에서 체크 되어진 값을 유지시키기 위한 것이다. 
			request.setAttribute("str_DeptId", str_DeptId);
			
			String gender = request.getParameter("gender");
			// System.out.println("=== 확인용 gender  : " + gender);
			// === 확인용 gender  : null
			
			Map<String, Object> paraMap = new HashMap<>();
			
			if (  str_DeptId != null && str_DeptId !="") {
				String[] arr_DeptId = str_DeptId.split("\\,");	
				paraMap.put("arr_DeptId", arr_DeptId) ;
			}
			
			if ( gender != null && gender !="") {
				paraMap.put("gender", gender );
				// 뷰단에서 체크되어진 값을 유지시키기 위한 것이다.
				request.setAttribute("gender", gender);
			}
			
			List<Map<String,String>> empList = service.empList(paraMap);
			
			request.setAttribute("depIdList", depIdList);
			request.setAttribute("empList", empList);
			
			return "emp/empList.tiles2";
		}
		
	
		// === #176. 
		@RequestMapping(value="/excel/downloadExcelFile.action")
		public String downloadExcelFile(HttpServletRequest request , Model model) {
			String str_DeptId = request.getParameter("str_DeptId");
			String gender = request.getParameter("gender");
			
			System.out.println(str_DeptId); 
			
			Map<String, Object> paraMap = new HashMap<>();
			
			if (  str_DeptId != null && str_DeptId !="") {
				String[] arr_DeptId = str_DeptId.split("\\,");	
				paraMap.put("arr_DeptId", arr_DeptId) ;
			}
			
			if ( gender != null && gender !="") {
				paraMap.put("gender", gender );
				// 뷰단에서 체크되어진 값을 유지시키기 위한 것이다.
			}
			List<Map<String,String>> empList = service.empList(paraMap);
			// === 조회 결과물인 empList 를 가지고 엑셀 시트 생성하기 === 
			// 시트를 생성하고 , 행을 생성하고 , 셀을 생성하고 ,셀안에 내용을 넣어주면 된다. 
		    
		     /* 
		      	<!-- 아파치 POI(Apache POI)는 아파치 소프트웨어 재단에서 만든 라이브러리로서 마이크로소프트 오피스파일 포맷을 순수 자바 언어로서 읽고 쓰는 기능을 제공한다. 
		           주로 워드, 엑셀, 파워포인트와 파일을 지원하며 최근의 오피스 포맷인 Office Open XML File Formats(OOXML, 즉 xml 기반의 *.docx, *.xlsx, *.pptx 등) 이나 아웃룩, 비지오, 퍼블리셔 등으로 지원 파일 포맷을 늘려가고 있다. 
		          poi-ooxml은 excel 2007이후 버전이며 이때 schemas도 같이 porting을 해줘야 xlsx 파일을 읽어들인다. 
		          poi는 excel 2007포함한 이전 버전이다.  -->
		     */ 
			
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			
			// 시트 생성
			SXSSFSheet sheet =  workbook.createSheet("HR사원정보");
			
			// 시트 열 너비 설정
			sheet.setColumnWidth(0, 2000);
		    sheet.setColumnWidth(1, 4000);
		    sheet.setColumnWidth(2, 2000);
		    sheet.setColumnWidth(3, 4000);
		    sheet.setColumnWidth(4, 3000);
		    sheet.setColumnWidth(5, 2000);
		    sheet.setColumnWidth(6, 1500);
		    sheet.setColumnWidth(7, 1500);
			
		    // 행의 위치를 나타내는 변수 
		    int rowLocation = 0 ;
		    
					////////////////////////////////////////////////////////////////////////////////////////
					// CellStyle 정렬하기(Alignment)
					// CellStyle 객체를 생성하여 Alignment 세팅하는 메소드를 호출해서 인자값을 넣어준다.
					// 아래는 HorizontalAlignment(가로)와 VerticalAlignment(세로)를 모두 가운데 정렬 시켰다.
					CellStyle mergeRowStyle = workbook.createCellStyle();
					mergeRowStyle.setAlignment(HorizontalAlignment.CENTER);
					mergeRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
					// import org.apache.poi.ss.usermodel.VerticalAlignment 으로 해야함.
					
					CellStyle headerStyle = workbook.createCellStyle();
					headerStyle.setAlignment(HorizontalAlignment.CENTER);
					headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
					
					
					// CellStyle 배경색(ForegroundColor)만들기
					// setFillForegroundColor 메소드에 IndexedColors Enum인자를 사용한다.
					// setFillPattern은 해당 색을 어떤 패턴으로 입힐지를 정한다.
					mergeRowStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());  // IndexedColors.DARK_BLUE.getIndex() 는 색상(남색)의 인덱스값을 리턴시켜준다. 
					mergeRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					
					headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex()); // IndexedColors.LIGHT_YELLOW.getIndex() 는 연한노랑의 인덱스값을 리턴시켜준다. 
					headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					
					
					// CellStyle 천단위 쉼표, 금액
					CellStyle moneyStyle = workbook.createCellStyle();
					moneyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
					
					
					// Cell 폰트(Font) 설정하기
					// 폰트 적용을 위해 POI 라이브러리의 Font 객체를 생성해준다.
					// 해당 객체의 세터를 사용해 폰트를 설정해준다. 대표적으로 글씨체, 크기, 색상, 굵기만 설정한다.
					// 이후 CellStyle의 setFont 메소드를 사용해 인자로 폰트를 넣어준다.
					Font mergeRowFont = workbook.createFont(); // import org.apache.poi.ss.usermodel.Font; 으로 한다.
					mergeRowFont.setFontName("나눔고딕");
					mergeRowFont.setFontHeight((short)500);
					mergeRowFont.setColor(IndexedColors.WHITE.getIndex());
					mergeRowFont.setBold(true);
					
					mergeRowStyle.setFont(mergeRowFont);
					
					
					// CellStyle 테두리 Border
					// 테두리는 각 셀마다 상하좌우 모두 설정해준다.
					// setBorderTop, Bottom, Left, Right 메소드와 인자로 POI라이브러리의 BorderStyle 인자를 넣어서 적용한다.
					headerStyle.setBorderTop(BorderStyle.THICK);
					headerStyle.setBorderBottom(BorderStyle.THICK);
					headerStyle.setBorderLeft(BorderStyle.THIN);
					headerStyle.setBorderRight(BorderStyle.THIN);
					
					
					// Cell Merge 셀 병합시키기
					/* 셀병합은 시트의 addMergeRegion 메소드에 CellRangeAddress 객체를 인자로 하여 병합시킨다.
					CellRangeAddress 생성자의 인자로(시작 행, 끝 행, 시작 열, 끝 열) 순서대로 넣어서 병합시킬 범위를 정한다. 배열처럼 시작은 0부터이다.  
					*/
					
					// 병합할 행 만들기 
					Row mergeRow = sheet.createRow(rowLocation); // 엑셀에서 행의 시작은 0부터 시작한다. 
					
					// 병합할 행에 "우리회사 사원정보" 로  셀을 만들어 셀에 스타일 주기
					for ( int i = 0 ; i < 8 ; i++ ) {
						Cell cell = mergeRow.createCell(i);
						cell.setCellStyle(mergeRowStyle);
						cell.setCellValue("우리회사 사원정보");
					}
					
					// 셀을 병합하기
					sheet.addMergedRegion(new CellRangeAddress(rowLocation ,  rowLocation , 0  , 7 )); // 시작 행, 끝 행, 시작 열, 끝 열
					/////////////////////////////////////////////////
					
					// 헤더 행 생성
					Row headerRow = sheet.createRow(++rowLocation); //  엑셀에서 행의 시작은 0부터 시작한다.
																		 	// 전위 연산자로 나가야 한다.
					
					
					// 해당 행의 첫번째 열 셀 생성
			        Cell headerCell = headerRow.createCell(0); // 엑셀에서 열의 시작은 0 부터 시작한다.
			        headerCell.setCellValue("부서번호");
			        headerCell.setCellStyle(headerStyle);
			        
			        // 해당 행의 두번째 열 셀 생성
			        headerCell = headerRow.createCell(1);
			        headerCell.setCellValue("부서명");
			        headerCell.setCellStyle(headerStyle);
			        
			        // 해당 행의 세번째 열 셀 생성
			        headerCell = headerRow.createCell(2);
			        headerCell.setCellValue("사원번호");
			        headerCell.setCellStyle(headerStyle);
			        
			        // 해당 행의 네번째 열 셀 생성
			        headerCell = headerRow.createCell(3);
			        headerCell.setCellValue("사원명");
			        headerCell.setCellStyle(headerStyle);
			        
			        // 해당 행의 다섯번째 열 셀 생성
			        headerCell = headerRow.createCell(4);
			        headerCell.setCellValue("입사일자");
			        headerCell.setCellStyle(headerStyle);
			        
			        // 해당 행의 여섯번째 열 셀 생성
			        headerCell = headerRow.createCell(5);
			        headerCell.setCellValue("월급");
			        headerCell.setCellStyle(headerStyle);
			        
			        // 해당 행의 일곱번째 열 셀 생성
			        headerCell = headerRow.createCell(6);
			        headerCell.setCellValue("성별");
			        headerCell.setCellStyle(headerStyle);
			        
			        // 해당 행의 여덟번째 열 셀 생성
			        headerCell = headerRow.createCell(7);
			        headerCell.setCellValue("나이");
			        headerCell.setCellStyle(headerStyle);
					
			        // === HR 사원정보 내용에 해당하는 행 및 셀 생성하기 === //
			        Row bodyRow = null;
			        Cell bodyCell = null;
			        
			        for(int i=0; i<empList.size(); i++) {
			           
			           Map<String, String> empMap = empList.get(i);
			           
			           // 행생성
			           bodyRow = sheet.createRow(i + (rowLocation+1));
			           
			           // 데이터 부서번호 표시
			           bodyCell = bodyRow.createCell(0);
			           bodyCell.setCellValue(empMap.get("department_id")); 
			           
			           // 데이터 부서명 표시
			           bodyCell = bodyRow.createCell(1);
			           bodyCell.setCellValue(empMap.get("department_name")); 
			                      
			           // 데이터 사원번호 표시
			           bodyCell = bodyRow.createCell(2);
			           bodyCell.setCellValue(empMap.get("employee_id")); 
			           
			           // 데이터 사원명 표시
			           bodyCell = bodyRow.createCell(3);
			           bodyCell.setCellValue(empMap.get("fullname")); 
			           
			           // 데이터 입사일자 표시
			           bodyCell = bodyRow.createCell(4);
			           bodyCell.setCellValue(empMap.get("hire_date")); 
			           
			           // 데이터 월급 표시
			           bodyCell = bodyRow.createCell(5);
			           bodyCell.setCellValue(Integer.parseInt(empMap.get("monthsal")));
			           bodyCell.setCellStyle(moneyStyle); // 천단위 쉼표, 금액
			           
			           // 데이터 성별 표시
			           bodyCell = bodyRow.createCell(6);
			           bodyCell.setCellValue(empMap.get("gender")); 
			           
			           // 데이터 나이 표시
			           bodyCell = bodyRow.createCell(7);
			           bodyCell.setCellValue(Integer.parseInt(empMap.get("age"))); 
			           
			        }// end of for------------------------------
			        
			        model.addAttribute("locale" , Locale.KOREA);
			        model.addAttribute("workbook" , workbook) ;
			        model.addAttribute("workbookName" , "HR사원정보") ;
			        
					return "excelDownloadView" ;
					//   "excelDownloadView" 은 
				    //  /webapp/WEB-INF/spring/appServlet/servlet-context.xml 파일에서
				    //  기술된 bean 의 id 값이다.
		}
		
		
		// === #176. Excel 파일을 업로드 하면 엑셀데이터를 데이터베이스 테이블에 insert 하는 예제 ===
		@ResponseBody
		@RequestMapping(value="/excel/uploadExcelFile.action" , method= {RequestMethod.POST})
		public String uploadExcelFile (MultipartHttpServletRequest mtp_request) {
			MultipartFile mtp_excel_file =  mtp_request.getFile("excel_file");
			JSONObject jsonObj = new JSONObject() ;
			try {
			if ( mtp_excel_file != null ) {
				// === MultipartFile 을 File 로 변환하기 시작 === //
				// WAS 의 webapp 의 절대경로를 알아와야 한다. 
				HttpSession session = mtp_request.getSession();
				String root = session.getServletContext().getRealPath("/");
				String path = root + "resources" + File.separator + "files" ;
				
				File excel_file = new File(path + File.separator + mtp_excel_file.getOriginalFilename());
					mtp_excel_file.transferTo(excel_file);
					// === MultipartFile 을 File 로 변환하기 끝=== //
				// System.out.println(mtp_excel_file.getOriginalFilename());
				//System.out.println("=== 확인용 excel_file :  === " + excel_file);
				// System.out.println("=== 확인용 mtp_excel_file :  === " + mtp_excel_file);
				
					OPCPackage opcPackage = OPCPackage.open(excel_file);
		            /* 아파치 POI(Apache POI)는 아파치 소프트웨어 재단에서 만든 라이브러리로서 마이크로소프트 오피스파일 포맷을 순수 자바 언어로서 읽고 쓰는 기능을 제공한다. 
		                  주로 워드, 엑셀, 파워포인트와 파일을 지원하며 최근의 오피스 포맷인 Office Open XML File Formats(OOXML, 즉 xml 기반의 *.docx, *.xlsx, *.pptx 등) 이나 아웃룩, 비지오, 퍼블리셔 등으로 지원 파일 포맷을 늘려가고 있다. 
		            */		
			
					XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
					// 첫번째 시트 불러오기
		            XSSFSheet sheet = workbook.getSheetAt(0);
		            List<Map<String, String>> paraMapList = new ArrayList<>();
		            
		            for ( int i = 1 ; i< sheet.getLastRowNum()+1 ; i++) {
		            	Map<String, String> paraMap = new HashMap<>();
		            	XSSFRow row = sheet.getRow(i);
		            	
		            	// 행이 존재하지 않으면 건너뛴다 . 
		            	if ( row == null ) {
		            		continue ; 
		            	}
		            	// 행의 1번째 열(사원번호)
		                XSSFCell cell = row.getCell(0);
		                
		                if(cell != null) {
		                   paraMap.put("EMPLOYEE_ID", String.valueOf(cellReader(cell)));
		                }
		                
		                // 행의 2번째 열(이름)
		                cell = row.getCell(1);
		                
		                if(cell != null) {
		                   paraMap.put("FIRST_NAME", String.valueOf(cellReader(cell)));
		                }
		                
		                // 행의 3번째 열(성)
		                cell = row.getCell(2);
		                
		                if(cell != null) {
		                   paraMap.put("LAST_NAME", String.valueOf(cellReader(cell)));
		                }
		                
		                // 행의 4번째 열(이메일)
		                cell = row.getCell(3);
		                
		                if(cell != null) {
		                   paraMap.put("EMAIL", String.valueOf(cellReader(cell)));
		                }               
		                
		                // 행의 5번째 열(연락처)
		                cell = row.getCell(4);
		                
		                if(cell != null) {
		                   paraMap.put("PHONE_NUMBER", String.valueOf(cellReader(cell)));
		                }               
		                
		                // 행의 6번째 열(입사일자)
		                cell = row.getCell(5);
		                
		                if(cell != null) {
		                   paraMap.put("HIRE_DATE", String.valueOf(cellReader(cell)));
		                }               
		                
		                // 행의 7번째 열(직종ID)
		                cell = row.getCell(6);
		                
		                if(cell != null) {
		                   paraMap.put("JOB_ID", String.valueOf(cellReader(cell)));
		                }
		                
		                // 행의 8번째 열(기본급여)
		                cell = row.getCell(7);
		                
		                if(cell != null) {
		                   paraMap.put("SALARY", String.valueOf(cellReader(cell)));
		                }
		                
		                // 행의 9번째 열(커미션퍼센티지)
		                cell = row.getCell(8);
		                
		                if(cell != null) {
		                   paraMap.put("COMMISSION_PCT", String.valueOf(cellReader(cell)));
		                }
		                
		                // 행의 10번째 열(직속상관사원번호)
		                cell = row.getCell(9);
		                
		                if(cell != null) {
		                   paraMap.put("MANAGER_ID", String.valueOf(cellReader(cell)));
		                }
		                
		                // 행의 11번째 열(부서번호)
		                cell = row.getCell(10);
		                
		                if(cell != null) {
		                   paraMap.put("DEPARTMENT_ID", String.valueOf(cellReader(cell)));
		                }
		                
		                // 행의 12번째 열(주민번호)
		                cell = row.getCell(11);
		                
		                if(cell != null) {
		                   paraMap.put("JUBUN", String.valueOf(cellReader(cell)));
		                }
		                
		                paraMapList.add(paraMap);
		            
		            }// end of for ----------------------------------------------------------
		            workbook.close();
		            
		            int insert_count = service.addEmpList(paraMapList);
		            
		            
		            
		            if(insert_count == paraMapList.size()) {
		               jsonObj.put("result", 1);
		            }
		            else {
		               jsonObj.put("result", 0);
		            }
		            excel_file.delete();// 업로드 된 파일 삭제하기 
		            
				}
				
				else {
					 jsonObj.put("result", 0);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				 jsonObj.put("result", 0);
			}
			
			return jsonObj.toString();
		}
	
		@SuppressWarnings("incomplete-switch")
		private static String cellReader(XSSFCell cell) {
		      String value = "";
		      CellType ct = cell.getCellType();
		      if(ct != null) {
		         switch(cell.getCellType()) {
		            case FORMULA:
		               value = cell.getCellFormula()+"";
		               break;
		            case NUMERIC:
		                value = cell.getNumericCellValue()+"";
		                break;
		            case STRING:
		                value = cell.getStringCellValue()+"";
		                break;
		            case BOOLEAN:
		                value = cell.getBooleanCellValue()+"";
		                break;
		            case ERROR:
		                value = cell.getErrorCellValue()+"";
		                break;
		         }
		      }
		      return value; 
		   }
		
		
		
		// === #177. 차트( 그래프 ) 를 보여주는 예제 ( view 단 )
		@RequestMapping(value="/emp/chart.action" ,method= {RequestMethod.GET})
		public String empmanager_chart(HttpServletRequest request){
			
			return "emp/chart.tiles2" ;
		}
		
		// 
		// === #178. 차트그리기 ( Ajax ) 부서명 별 인원수 및 퍼센티지 가져오기 ===
		@ResponseBody
		@RequestMapping(value="/chart/employeeCntByDeptname.action" ,  produces="text/plain;charset=UTF-8")
		public String employeeCntByDeptname(HttpServletRequest request) {
			List<Map<String,String>> mapList = service.employeeCntByDeptname();
			/*
			 * JSONArray jsonArr = new JSONArray(); for ( Map<String,String> map : mapList)
			 * { JSONObject jsonObj = new JSONObject(); jsonObj.put("department_name",
			 * map.get("department_name")); jsonObj.put("cnt", map.get("cnt"));
			 * jsonObj.put("percent", map.get("percent"));
			 * 
			 * jsonArr.put(jsonObj); }
				return jsonArr.toString();
			 */
			
			JsonArray jsonArr = new JsonArray() ;
			if ( mapList != null && mapList.size() > 0 ) {
				 for ( Map<String,String> map : mapList) {
					 JsonObject json = new JsonObject() ;
					 json.addProperty("department_name", map.get("department_name"));
					 json.addProperty("cnt", map.get("cnt"));
					 json.addProperty("percent", map.get("percent"));
					 
					 jsonArr.add(json);
				 } // end of for 문 
			}
			
			return new Gson().toJson(jsonArr);
		}
		
		// === #179. 
		@ResponseBody
		@RequestMapping(value="/chart/employeeCntByGender.action" ,  produces="text/plain;charset=UTF-8")
		public String employeeCntByGender(HttpServletRequest request) {
			List<Map<String,String>> mapList = service.employeeCntByGender();
			/*
			 * JSONArray jsonArr = new JSONArray(); for ( Map<String,String> map : mapList)
			 * { JSONObject jsonObj = new JSONObject(); jsonObj.put("department_name",
			 * map.get("department_name")); jsonObj.put("cnt", map.get("cnt"));
			 * jsonObj.put("percent", map.get("percent"));
			 * 
			 * jsonArr.put(jsonObj); }
				return jsonArr.toString();
			 */
			
			JsonArray jsonArr = new JsonArray() ;
			if ( mapList != null && mapList.size() > 0 ) {
				 for ( Map<String,String> map : mapList) {
					 JsonObject json = new JsonObject() ;
					 json.addProperty("gender", map.get("gender"));
					 json.addProperty("cnt", map.get("cnt"));
					 json.addProperty("percent", map.get("percent"));
					 
					 jsonArr.add(json);
				 } // end of for 문 
			}
			
			return new Gson().toJson(jsonArr);
		}
		
		
		// === #180. 차트그리기(Ajax) 성별별 입사년도별 인원수 가져오기 === // 
		@ResponseBody
		@RequestMapping(value="/chart/employeeCntByGenderHireYear.action" ,  produces="text/plain;charset=UTF-8")
		public String employeeCntByGenderHireYear(HttpServletRequest request) {
			List<Map<String,String>> mapList = service.employeeCntByGenderHireYear();
			
			Gson gson = new Gson();
			String result = gson.toJson(mapList);
//			MemberVO member = new MemberVO();
//			String memberInfo = gson.toJson(member);
//			
//			
//			MemberVO member2  = gson.fromJson(memberInfo, MemberVO.class);
//			JsonArray jsonArray = (JsonArray) gson.toJsonTree(mapList);
			
//			JsonArray jsonArr = new JsonArray() ;
//			if ( mapList != null && mapList.size() > 0 ) {
//				 for ( Map<String,String> map : mapList) {
//					 
//					 JsonObject json = new JsonObject() ;
//					 json.addProperty("gender", map.get("gender"));
//					 json.addProperty("Y2001", map.get("Y2001"));
//					 json.addProperty("Y2002", map.get("Y2002"));
//					 json.addProperty("Y2003", map.get("Y2003"));
//					 json.addProperty("Y2004", map.get("Y2004"));
//					 json.addProperty("Y2005", map.get("Y2005"));
//					 json.addProperty("Y2006", map.get("Y2006"));
//					 json.addProperty("Y2007", map.get("Y2007"));
//					 json.addProperty("Y2008", map.get("Y2008"));
//					 
//					 jsonArr.add(json);
//				 } // end of for 문 
//			}
			
			return result; // new Gson().toJson(jsonArr);
		}
		
			// === 주소를 위도/경도로 변환하기 === // 
		   @RequestMapping(value="/emp/geocoder.action", produces="text/plain;charset=UTF-8") 
		   public String geocoding(HttpServletRequest request) {
		            
		      return "emp/geocoder.tiles2";
		   }
		   
		   // === #180. 차트 그리기 => 특정 부서명에 근무하는 직원들의 성별 인원수 및 퍼센티지 가져오기 === 
		   @ResponseBody
		   @GetMapping(value="/chart/genderCntSpecialDeptname.action", produces="text/plain;charset=UTF-8" )
		   public String genderCntSpecialDeptname(@RequestParam String deptname) {
			   List<Map<String,String>> List = service.genderCntSpecialDeptname(deptname);
			   Gson gson = new Gson();
			   String result = gson.toJson(List);
			   
			   return result ;
		   }
		   
		   // === #181.  
		   
		// === #200. 기상청 공공데이터(오픈데이터)를 가져와서 날씨정보 보여주기 === //
		   @RequestMapping(value="/opendata/weatherXML.action", method = {RequestMethod.GET})
		   public String weatherXML() {
		      return "opendata/weatherXML";
		      //   /Board/src/main/webapp/WEB-INF/views/opendata/weatherXML.jsp 파일을 생성한다. 
		   }
		   
				//////////////////////////////////////////////////////
				@ResponseBody
				@RequestMapping(value="/opendata/weatherXMLtoJSON.action", method= {RequestMethod.POST}, produces="text/plain;charset=UTF-8") 
				public String weatherXMLtoJSON(HttpServletRequest request) { 
				
				String str_jsonObjArr = request.getParameter("str_jsonObjArr");
				// System.out.println(str_jsonObjArr);
				
				/*  확인용
				//  [{"locationName":"속초","ta":"2.4"},{"locationName":"북춘천","ta":"-2.3"},{"locationName":"철원","ta":"-2.0"},{"locationName":"동두천","ta":"-0.7"},{"locationName":"파주","ta":"-1.2"},{"locationName":"대관령","ta":"-3.0"},{"locationName":"춘천","ta":"-1.6"},{"locationName":"백령도","ta":"1.1"},{"locationName":"북강릉","ta":"3.4"},{"locationName":"강릉","ta":"4.3"},{"locationName":"동해","ta":"4.1"},{"locationName":"서울","ta":"-0.3"},{"locationName":"인천","ta":"-0.2"},{"locationName":"원주","ta":"-2.2"},{"locationName":"울릉도","ta":"4.2"},{"locationName":"수원","ta":"1.4"},{"locationName":"영월","ta":"-4.5"},{"locationName":"충주","ta":"-3.0"},{"locationName":"서산","ta":"2.5"},{"locationName":"울진","ta":"3.9"},{"locationName":"청주","ta":"-0.7"},{"locationName":"대전","ta":"2.9"},{"locationName":"추풍령","ta":"3.2"},{"locationName":"안동","ta":"-2.3"},{"locationName":"상주","ta":"1.5"},{"locationName":"포항","ta":"4.7"},{"locationName":"군산","ta":"2.6"},{"locationName":"대구","ta":"1.6"},{"locationName":"전주","ta":"5.7"},{"locationName":"울산","ta":"4.0"},{"locationName":"창원","ta":"4.4"},{"locationName":"광주","ta":"2.8"},{"locationName":"부산","ta":"4.3"},{"locationName":"통영","ta":"5.7"},{"locationName":"목포","ta":"4.5"},{"locationName":"여수","ta":"5.6"},{"locationName":"흑산도","ta":"7.8"},{"locationName":"완도","ta":"7.3"},{"locationName":"고창","ta":"3.5"},{"locationName":"순천","ta":"5.3"},{"locationName":"홍성","ta":"1.7"},{"locationName":"제주","ta":"8.5"},{"locationName":"고산","ta":"9.1"},{"locationName":"성산","ta":"7.5"},{"locationName":"서귀포","ta":"8.8"},{"locationName":"진주","ta":"3.5"},{"locationName":"강화","ta":"-0.9"},{"locationName":"양평","ta":"-1.3"},{"locationName":"이천","ta":"-2.0"},{"locationName":"인제","ta":"-0.5"},{"locationName":"홍천","ta":"-2.6"},{"locationName":"태백","ta":"-1.5"},{"locationName":"정선군","ta":"-1.9"},{"locationName":"제천","ta":"-4.4"},{"locationName":"보은","ta":"-1.2"},{"locationName":"천안","ta":"-1.0"},{"locationName":"보령","ta":"3.6"},{"locationName":"부여","ta":"0.6"},{"locationName":"금산","ta":"3.7"},{"locationName":"세종","ta":"-0.8"},{"locationName":"부안","ta":"5.8"},{"locationName":"임실","ta":"1.6"},{"locationName":"정읍","ta":"5.8"},{"locationName":"남원","ta":"0.1"},{"locationName":"장수","ta":"1.4"},{"locationName":"고창군","ta":"4.3"},{"locationName":"영광군","ta":"4.2"},{"locationName":"김해시","ta":"4.1"},{"locationName":"순창군","ta":"1.0"},{"locationName":"북창원","ta":"5.9"},{"locationName":"양산시","ta":"3.9"},{"locationName":"보성군","ta":"5.1"},{"locationName":"강진군","ta":"4.4"},{"locationName":"장흥","ta":"4.9"},{"locationName":"해남","ta":"6.2"},{"locationName":"고흥","ta":"5.4"},{"locationName":"의령군","ta":"5.7"},{"locationName":"함양군","ta":"4.9"},{"locationName":"광양시","ta":"5.4"},{"locationName":"진도군","ta":"7.0"},{"locationName":"봉화","ta":"-3.3"},{"locationName":"영주","ta":"-4.3"},{"locationName":"문경","ta":"-3.1"},{"locationName":"청송군","ta":"0.5"},{"locationName":"영덕","ta":"4.7"},{"locationName":"의성","ta":"-0.6"},{"locationName":"구미","ta":"2.3"},{"locationName":"영천","ta":"3.4"},{"locationName":"경주시","ta":"4.5"},{"locationName":"거창","ta":"0.8"},{"locationName":"합천","ta":"0.7"},{"locationName":"밀양","ta":"1.1"},{"locationName":"산청","ta":"4.2"},{"locationName":"거제","ta":"5.8"},{"locationName":"남해","ta":"6.2"}]  
				*/
				//   return str_jsonObjArr;  -- 지역 96개 모두 차트에 그리기에는 너무 많으므로 아래처럼 작업을 하여 지역을  21개(String[] locationArr 임)로 줄여서 나타내기로 하겠다.
				
				str_jsonObjArr = str_jsonObjArr.substring(1, str_jsonObjArr.length()-1);
				
				String[] arr_str_jsonObjArr = str_jsonObjArr.split("\\},");
				
				for(int i=0; i<arr_str_jsonObjArr.length; i++) {
				arr_str_jsonObjArr[i] += "}";
				}
				
				/*   
				for(String jsonObj : arr_str_jsonObjArr) {
				System.out.println(jsonObj);
				}
				*/
				
				
				// {"locationName":"속초","ta":"15.7"}
				// {"locationName":"북춘천","ta":"24.9"}
				// {"locationName":"철원","ta":"23.8"}
				// {"locationName":"동두천","ta":"26.3"}
				// {"locationName":"파주","ta":"25.5"}
				// {"locationName":"대관령","ta":"10.8"}
				// {"locationName":"춘천","ta":"26.7"}
				// {"locationName":"백령도","ta":"13.8"}
				// ........ 등등  
				// {"locationName":"밀양","ta":"24.7"}
				// {"locationName":"산청","ta":"24.2"}
				// {"locationName":"거제","ta":"21.0"}
				// {"locationName":"남해","ta":"22.7"}}
				
				
				String[] locationArr = {"서울","인천","수원","춘천","강릉","청주","홍성","대전","안동","포항","대구","전주","울산","부산","창원","여수","광주","목포","제주","울릉도","백령도"};
				String result = "[";
				
				for(String jsonObj : arr_str_jsonObjArr) {
				
				for(int i=0; i<locationArr.length; i++) {
				if( jsonObj.indexOf(locationArr[i]) >= 0 && jsonObj.indexOf("북") == -1 && jsonObj.indexOf("서청주") == -1 ) { // 북춘천,춘천,북강릉,강릉,북창원,창원이 있으므로  "북" 이 있는 것은 제외하도록 한다. 또한 서청주(예)도 제외하도록 한다.
				result += jsonObj+",";  // [{"locationName":"춘천","ta":"26.7"},{"locationName":"백령도","ta":"13.8"}, ..... {"locationName":"제주","ta":"18.9"}, 
				break;
				}
				}
				}// end of for------------------------------
				
				result = result.substring(0, result.length()-1);  // [{"locationName":"춘천","ta":"26.7"},{"locationName":"백령도","ta":"13.8"}, ..... {"locationName":"제주","ta":"18.9"}
				result = result + "]";                            // [{"locationName":"춘천","ta":"26.7"},{"locationName":"백령도","ta":"13.8"}, ..... {"locationName":"제주","ta":"18.9"}]
				
				// System.out.println(result);
				/*  확인용
				// [{"locationName":"춘천","ta":"26.7"},{"locationName":"백령도","ta":"13.8"},{"locationName":"강릉","ta":"18.4"},{"locationName":"서울","ta":"27.7"},{"locationName":"인천","ta":"23.8"},{"locationName":"울릉도","ta":"19.2"},{"locationName":"수원","ta":"26.5"},{"locationName":"청주","ta":"26.8"},{"locationName":"대전","ta":"26.4"},{"locationName":"안동","ta":"24.3"},{"locationName":"포항","ta":"19.4"},{"locationName":"대구","ta":"22.7"},{"locationName":"전주","ta":"26.3"},{"locationName":"울산","ta":"20.7"},{"locationName":"창원","ta":"21.9"},{"locationName":"광주","ta":"25.6"},{"locationName":"부산","ta":"22.0"},{"locationName":"목포","ta":"23.2"},{"locationName":"여수","ta":"23.0"},{"locationName":"홍성","ta":"25.0"},{"locationName":"제주","ta":"18.9"}]
				
				*/
				return result;   
				}
}
