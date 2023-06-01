package com.spring.board.common;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.stereotype.Component;

//=== #185. Spring Scheduler(스프링스케줄러5) === //
//=== Spring Scheduler(스프링스케줄러)를 사용한 email 발송하기 === 
//=== GoogleMail 을 사용할 수 있도록 Google email 계정 및 암호 입력하기 ===
@Component
public class GoogleMail {
	// ==== 먼저 오라클에서 tbl_reservation 테이블을 생성해야 한다. ====
    // ==== Spring Scheduler(스프링 스케줄러)를 사용한 email 발송하기 예제 ==== //
    public void sendmail_Reservation(String recipient, String emailContents) throws Exception {
           
           // 1. 정보를 담기 위한 객체
           Properties prop = new Properties(); 
           
           // 2. SMTP 서버의 계정 설정
              //    Google Gmail 과 연결할 경우 Gmail 의 email 주소를 지정 
           prop.put("mail.smtp.user", "javareactclass123@gmail.com");
               
           
           // 3. SMTP 서버 정보 설정
           //    Google Gmail 인 경우  smtp.gmail.com
           prop.put("mail.smtp.host", "smtp.gmail.com");
           
           prop.put("mail.smtp.port", "465");
           prop.put("mail.smtp.starttls.enable", "true");
           prop.put("mail.smtp.auth", "true");
           prop.put("mail.smtp.debug", "true");
           prop.put("mail.smtp.socketFactory.port", "465");
           prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
           prop.put("mail.smtp.socketFactory.fallback", "false");
           
           prop.put("mail.smtp.ssl.enable", "true");
           prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
             
           
           Authenticator smtpAuth = new MySMTPAuthenticator();
           Session ses = Session.getInstance(prop, smtpAuth);
              
           // 메일을 전송할 때 상세한 상황을 콘솔에 출력한다.
           ses.setDebug(true);
                   
           // 메일의 내용을 담기 위한 객체생성
           MimeMessage msg = new MimeMessage(ses);

           // 제목 설정
           String subject = "localhost:9090/board/ 방문 예약일자를 알려드립니다.";
           msg.setSubject(subject);
                   
           // 보내는 사람의 메일주소
           String sender = "javareactclass123@gmail.com";
           Address fromAddr = new InternetAddress(sender);
           msg.setFrom(fromAddr);
                   
           // 받는 사람의 메일주소
           Address toAddr = new InternetAddress(recipient);
           msg.addRecipient(Message.RecipientType.TO, toAddr);
                   
           // 메시지 본문의 내용과 형식, 캐릭터 셋 설정
           msg.setContent("<div style='font-size:14pt;'>"+emailContents+"</div>", "text/html; charset=UTF-8");
                   
           // 메일 발송하기
           Transport.send(msg);
           
        }// end of sendmail_Reservation(String recipient, String emailContents)------------------- 
	public void sendmail(String recipient, String certificationCode) throws Exception {
		// 1. 정보를 담기 위한 객체
        Properties prop = new Properties(); 
        
        // 2. SMTP(Simple Mail Transfer Protocoal) 서버의 계정 설정
        //    Google Gmail 과 연결할 경우 Gmail 의 email 주소를 지정 
        prop.put("mail.smtp.user", "javareactclass123@gmail.com");
		
        // 3. SMTP 서버 정보 설정
        //    Google Gmail 인 경우  smtp.gmail.com
        prop.put("mail.smtp.host", "smtp.gmail.com");
             
        
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.debug", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", "false");
        
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
          
        
        Authenticator smtpAuth = new MySMTPAuthenticator();
        Session ses = Session.getInstance(prop, smtpAuth);
           
        // 메일을 전송할 때 상세한 상황을 콘솔에 출력한다.
        ses.setDebug(true);
                
        // 메일의 내용을 담기 위한 객체생성
        MimeMessage msg = new MimeMessage(ses);

        // 제목 설정
        String subject = "localhost:9090/MyMVC/index.up 회원님의 비밀번호를 찾기위한 인증코드 발송";
        msg.setSubject(subject);
                
        // 보내는 사람의 메일주소
        String sender = "javareactclass123@gmail.com";
        Address fromAddr = new InternetAddress(sender);
        msg.setFrom(fromAddr);
                
        // 받는 사람의 메일주소
        Address toAddr = new InternetAddress(recipient);
        msg.addRecipient(Message.RecipientType.TO, toAddr);
                
        // 메시지 본문의 내용과 형식, 캐릭터 셋 설정
        msg.setContent("발송된 인증코드 : <span style='font-size:14pt; color:red;'>"+certificationCode+"</span>", "text/html;charset=UTF-8");
                
        // 메일 발송하기
        Transport.send(msg);
		
	}// END OF 	PUBLIC VOID SENDMAIL(STRING EMAIL, STRING CERTIFICATIONCODE) {

	public void sendmail_OrderFinish(String recipient, String name, String emailContents)   throws Exception {
		// 1. 정보를 담기 위한 객체
        Properties prop = new Properties(); 
        
        // 2. SMTP(Simple Mail Transfer Protocoal) 서버의 계정 설정
        //    Google Gmail 과 연결할 경우 Gmail 의 email 주소를 지정 
        prop.put("mail.smtp.user", "javareactclass123@gmail.com");
		
        // 3. SMTP 서버 정보 설정
        //    Google Gmail 인 경우  smtp.gmail.com
        prop.put("mail.smtp.host", "smtp.gmail.com");
             
        
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.debug", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", "false");
        
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
          
        
        Authenticator smtpAuth = new MySMTPAuthenticator();
        Session ses = Session.getInstance(prop, smtpAuth);
           
        // 메일을 전송할 때 상세한 상황을 콘솔에 출력한다.
        ses.setDebug(true);
                
        // 메일의 내용을 담기 위한 객체생성
        MimeMessage msg = new MimeMessage(ses);

        // 제목 설정
        String subject = "localhost:9090/MyMVC/mallHome1.up "+name+" 회원님의 주문이 성공했습니다";
        msg.setSubject(subject);
                
        // 보내는 사람의 메일주소
        String sender = "javareactclass123@gmail.com";
        Address fromAddr = new InternetAddress(sender);
        msg.setFrom(fromAddr);
                
        // 받는 사람의 메일주소
        Address toAddr = new InternetAddress(recipient);
        msg.addRecipient(Message.RecipientType.TO, toAddr);
                
        // 메시지 본문의 내용과 형식, 캐릭터 셋 설정
        msg.setContent("<div stlye='font-size:14pt color:red;'>"+emailContents+"</div>", "text/html;charset=UTF-8");
                
        // 메일 발송하기
        Transport.send(msg);
			
		
	}

	// === 다중 첨부 파일이 있는 메일 보내기 === // 
	public void sendmail_withFile(Map<String, Object> paraMap) throws Exception {
		// 1. 정보를 담기 위한 객체
        Properties prop = new Properties(); 
        
        // 2. SMTP(Simple Mail Transfer Protocoal) 서버의 계정 설정
        //    Google Gmail 과 연결할 경우 Gmail 의 email 주소를 지정 
        prop.put("mail.smtp.user", "javareactclass123@gmail.com");
		
        // 3. SMTP 서버 정보 설정
        //    Google Gmail 인 경우  smtp.gmail.com
        prop.put("mail.smtp.host", "smtp.gmail.com");
             
        
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.debug", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", "false");
        
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
          
        
        Authenticator smtpAuth = new MySMTPAuthenticator();
        Session ses = Session.getInstance(prop, smtpAuth);
           
        // 메일을 전송할 때 상세한 상황을 콘솔에 출력한다.
        ses.setDebug(true);
                
        // 메일의 내용을 담기 위한 객체생성
        MimeMessage msg = new MimeMessage(ses);

        // 제목 설정
        msg.setSubject((String)paraMap.get("subject"));
                
        // 보내는 사람의 메일주소
        String sender = "javareactclass123@gmail.com";
        Address fromAddr = new InternetAddress(sender);
        msg.setFrom(fromAddr);
                
        // 받는 사람의 메일주소
        Address toAddr = new InternetAddress((String)paraMap.get("recipient_email"));
        msg.addRecipient(Message.RecipientType.TO, toAddr);
                
        // 메시지 본문의 내용과 형식, 캐릭터 셋 설정
        if ( paraMap.get("arr_attachFilename") == null ) {
        	msg.setContent((String)paraMap.get("content"), "text/html;charset=UTF-8");
        }
        
        else {// 첨부파일이 있으면 내용 및 파일을 전송한다.
            // 메일에 첨부파일을 전송하기 위해서는 MIME를 확장한 "MIME Multi-part"라 불리는 규격에 의하여 실현되고 있다.
            // JavaMail 에서 MIME Multi-part을 실현을 위해서 "javax.mail.internet.MimeMultipart" 와 "javax.mail.internet.MimeBodyPart" 클래스를 사용한다.
        	
        	MimeMultipart mParts = new MimeMultipart();       // 메일 내용 및 첨부파일을 모두 담아놓을 MimeMultipart 객체를 생성
            MimeBodyPart mText_BodyPart = new MimeBodyPart(); // 메일 내용용 MimeBodyPart 객체 생성
            
            // 메일 본문의 내용과 형식, 캐릭터 셋 설정
            mText_BodyPart.setText((String)paraMap.get("content"), "UTF-8", "html");
            mParts.addBodyPart(mText_BodyPart); // MimeMultipart 에 MimeBodyPart mText_BodyPart 를 추가함.
            
            if(paraMap.get("arr_attachFilename") != null) {
                String[] arr_attachFilename = (String[]) paraMap.get("arr_attachFilename");
                          
                for(String attachFilename : arr_attachFilename) {
                                   
                   MimeBodyPart mFile_BodyPart = new MimeBodyPart(); // 첨부파일용 MimeBodyPart 객체 생성
                   
                   // 다음은 탐색기에서 C:\NCS\workspace(spring)\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\email_attach_file 에 생성된 파일을 불러온다. 
                   String fileName = (String)paraMap.get("path")+File.separator+attachFilename;
                   File attachFile = new File(fileName); 
                                    
                    // 첨부파일 이름(한글 깨짐현상 발생하므로 꼭 인코딩한 후 setFileName 을 해야만 한다)
                    String attachFile_name = attachFile.getName();
                    mFile_BodyPart.setFileName(MimeUtility.encodeText(attachFilename, "UTF-8", "B")); // B 는 Base64 로써, Base64 Encoding 은 Binary Data를 Text로 변경하는 Encoding 을 방법중 하나로서 64진법을 사용한다. 
                    
                    FileDataSource filedataSource = new FileDataSource(attachFile); // 생성자 파라미터로 File 객체가 들어온다.  
                    DataHandler dataHandler = new DataHandler(filedataSource);
                    mFile_BodyPart.setDataHandler(dataHandler);
                    
                    // 첨부파일의 mimeType 지정하기
                    // mimeType 종류를 알려주는 사이트 ==> https://developer.mozilla.org/ko/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
                    Path path = Paths.get(attachFile.getCanonicalPath());
                    String mimeType = Files.probeContentType(path);
                    mFile_BodyPart.setHeader("Content-Type", mimeType);
                    
                    // 확장자 떼고, Description 지정
                    mFile_BodyPart.setDescription(attachFile_name.split("\\.")[0], "UTF-8");
                    
                    // MimeMultipart 에 첨부파일용으로 생성한 MimeBodyPart mFile_BodyPart 를 추가함.
                      mParts.addBodyPart(mFile_BodyPart);  
                      
                      msg.setContent(mParts, "text/html; charset=UTF-8"); // 메일에 MimeMultipart를 설정
                    
                }// end of for----------------------------------------------------
             } // end of if(paraMap.get("arr_newFilename") != null)----------------
        }
        // 메일 발송하기
        Transport.send(msg);
	}

	
	
	
	
	
	
}
