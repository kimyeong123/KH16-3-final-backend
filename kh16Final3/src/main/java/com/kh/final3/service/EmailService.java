package com.kh.final3.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.final3.dao.CertDao;
import com.kh.final3.dto.CertDto;
import com.kh.final3.dto.MemberDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender sender;
	@Autowired
	private CertDao certDao;
	
	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		sender.send(message);
	}
	  @Transactional
	public void sendCertNumber(String email) {
		//랜덤번호 생성
		Random r = new Random();
		int number = r.nextInt(1000000);
		DecimalFormat df = new DecimalFormat("000000");
		String certNumber = df.format(number);
		
		//메세지 생성 및 전송
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("안녕하세요, 비드하우스 고객님.\r\n"
				+ "\r\n"
				+ "계정 보호를 위해 이메일 인증이 필요하여 아래와 같이 인증번호를 발송해드립니다.\r\n"
                );
		message.setText("인증번호는 ["+certNumber+"] 입니다");
		sender.send(message);
		
		//인증번호를 DB에 저장하는 코드
		CertDto certDto = certDao.selectOne(email);
		if(certDto == null) {//인증메일을 보낸 기록이 없다면 ----> insert
			certDao.insert(CertDto.builder()
						.certEmail(email).certNumber(certNumber)
					.build());
			System.out.println(">>> DB insert 실행: " + email + ", " + certNumber);
		}
		else {//인증메일을 보낸 기록이 있다면 ----> update
			certDao.update(CertDto.builder()
						.certEmail(email).certNumber(certNumber)
					.build());
			System.out.println(">>> DB update 실행: " + email + ", " + certNumber);
		}
	}
	
	public void sendWelcomeMail(MemberDto memberDto) throws MessagingException, IOException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
		
		helper.setTo(memberDto.getMemberEmail());
		helper.setSubject("[비드하우스] 가입을 진심으로 환영합니다!");

		//이메일 본문 생성
		ClassPathResource resource = new ClassPathResource("templates/welcome2.html");
		File target = resource.getFile();
		
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(target));
		while(true) {
			String line = reader.readLine();
			if(line == null) break;
			buffer.append(line);
		}
		reader.close();
		
		//helper.setText(buffer.toString(), true);//기존 코드(그대로 전송)
		
		//(+추가) 불러온 HTML 템플릿에서 특정 태그를 찾아 내용을 변경 후 전송
		//jQuery였다면...   $("#target").text("???")  ,  $("#link").attr("href", "주소")
		
		Document document = Jsoup.parse(buffer.toString());//String을 HTML로 해석
		Element targetId = document.selectFirst("#target");//id=target인 대상을 탐색
		Element targetLink = document.selectFirst("#link");//id=link인 대상을 탐색
		targetId.text(memberDto.getMemberNickname());//textContent변경
		
		//targetLink.attr("href", "http://localhost:8080");//attribute 변경
		//(+추가) 현재 접속중인 홈페이지의 주소에 기반해서 링크의 이동 경로를 설정
		String url = ServletUriComponentsBuilder
				.fromCurrentContextPath()//http://localhost:8080
				.path("/")//홈페이지
				//.path("/board/list")//게시판 
				.build().toUriString();
		targetLink.attr("href", url);//attribute 변경
		
		helper.setText(document.toString(), true);//HTML로 해석된 내용을 본문으로 설정
		
		sender.send(message);
	}

}
