package com.spring.board.model;

import java.util.Calendar;

import lombok.Data;
@Data
public class MemberVO {

	// insert 용도
	private String userid;       // 회원아이디
	private String pwd;          // 비밀번호 (SHA-256 암호화 대상) 한번 암호화를 시키면 복호화가 불가능하다. 오직 회원만 알고 있음
	private String name;         // 회원명
	private String email;        // 이메일 (AES-256 암호화/복호화 대상) 복호화가 가능하다.
	private String mobile;       // 연락처 (AES-256 암호화/복호화 대상) 복호화가 가능하다.
	private String postcode;     // 우편번호
	private String address;      // 주소
	private String detailaddress;//상세 주소
	private String extraaddress; // 참고항목
	private String gender;       // 성별 남자:1  / 여자:2
	private String birthday;     // 생년월일 
	private int coin;  			 // 코인액 (비축) 프로젝트 때는 비축이 아니라 결제할 때마다 금액이 빠져나가게 : coin 은 + - 되어야 할 경우가 생기기 때문에 int 로 생성한다. 
	private int point; 	  		 // 포인트 (마일리지) :point 은 + - 되어야 할 경우가 생기기 때문에 int 로 생성한다.
	private String registerday;  // 가입일자 
	private String lastpwdchangedate; // 마지막으로 암호를 변경한 날짜 (비밀번호 변경 권유하기 위해) 
	private int status; 		 // 회원탈퇴유무   1: 사용가능(가입중) / 0:사용불능(탈퇴) 
	private int idle;  			 // 휴면유무      0 : 활동중  /  1 : 휴면중 
								 // 마지막으로 로그인한 날짜시간이 현재시각으로부터 1년이 지났으면 휴면으로 지정
	
	private int pwdchangegap;          // select 용. 지금으로 부터 마지막으로 암호를 변경한지가 몇개월인지 알려주는 개월수(3개월 동안 암호를 변경 안 했을시 암호를 변경하라는 메시지를 보여주기 위함)  
	private int lastlogingap;          // select 용. 지금으로 부터 마지막으로 로그인한지가 몇개월인지 알려주는 개월수(12개월 동안 로그인을 안 했을 경우 해당 로그인 계정을 비활성화 시키려고 함) 
	
	//==============================================================================================================//

	// select 용도
	private boolean requirePwdChange = false;
	//  마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 true
	//  마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지나지 않았으면 false 
	
	// #138. 먼저 답변글쓰기는 일반회원은 불가하고 직원(관리파트)들만 답변글쓰기가 가능하도록 하기 위해서 
	//       먼저 오라클에서 tbl_member 테이블에  gradelevel 이라는 컬럼을 추가해야 한다.
	private int gradelevel ;


	// ==== select 용. 지금으로 부터 마지막으로 암호를 변경하지가 몇개월인지 알려주는 개월수 ==== //
	public int getPwdchangegap() {
		return pwdchangegap;
	}
	public void setPwdchangegap(int pwdchangegap) {
		this.pwdchangegap = pwdchangegap;
	}
	 // ==== select 용. 지금으로 부터 마지막으로 로그인한지가 몇개월인지 알려주는 개월수 ==== //
	public int getLastlogingap() {
		return lastlogingap;
	}
	public void setLastlogingap(int lastlogingap) {
		this.lastlogingap = lastlogingap;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public int getGradelevel() {
		return gradelevel;
	}
	public void setGradelevel(int gradelevel) {
		this.gradelevel = gradelevel;
	}
	
	public int getAge() {
		
		int age = 0;
		
		// 현재 날짜와 시간을 얻어온다.
		Calendar currentDate = Calendar.getInstance();
		int currentYear = currentDate.get(Calendar.YEAR);
		
		age = currentYear - Integer.parseInt(birthday.substring(0, 4)) + 1;
	
		return age;
	}
	
	
}
