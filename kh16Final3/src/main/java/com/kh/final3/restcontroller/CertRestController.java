package com.kh.final3.restcontroller;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.dao.CertDao;
import com.kh.final3.dto.CertDto;
import com.kh.final3.service.EmailService;
import com.kh.final3.vo.CertResultVO;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/cert")
public class CertRestController {
	@Autowired
	private EmailService emailService;
	@Autowired
	private CertDao certDao;
	
	//표준 주소 체계를 따르지 않고 편의성을 고려하여 설계
	// - post 방식을 적극 사용 (body를 이용하여 많은 데이터를 JSON으로 전송)
	@PostMapping("/send")
	public void send(@Valid @RequestBody CertDto certDto) {
		 System.out.println(">>> 컨트롤러 입수 이메일: [" + certDto.getCertEmail() + "]");
		emailService.sendCertNumber(certDto.getCertEmail());
	}
	@PostMapping("/check")	
	public CertResultVO check(@Valid @RequestBody CertDto certDto) {
		//[1] 이메일로 인증정보를 조회
		CertDto findDto = certDao.selectOne(certDto.getCertEmail());
		if(findDto == null) 
			return CertResultVO.builder()
						.result(false)//실패했어
						.message("인증 내역이 존재하지 않습니다")
					.build();
		
		//[2] 유효시간 검사
		LocalDateTime current = LocalDateTime.now();
		LocalDateTime sent = findDto.getCertTime().toLocalDateTime();
		Duration duration = Duration.between(sent, current);
		//if(duration.toMinutes() > 10) //10분 초과! (10분 59초 초과)
		if(duration.toSeconds() > 600) //10분 초과! (10분 0초 초과)
			return CertResultVO.builder()
							.result(false)//실패했어!
							.message("시간 내에 인증을 완료하지 못했습니다")
						.build();
		
		//[3] 인증번호 검사
		//boolean isValid = certDto.getCertNumber() == findDto.getCertNumber();
		boolean isValid = certDto.getCertNumber().equals(findDto.getCertNumber());
		if(isValid == false) //인증번호가 틀렸대!
			return CertResultVO.builder()
						.result(false)//실패했어!
						.message("인증 번호가 일치하지 않습니다")
					.build();
		
		//다 통과했으면 인증 완료! (+인증내역 삭제)
		certDao.delete(certDto.getCertEmail());
		return CertResultVO.builder()
						.result(true)//성공!	
						.message("인증이 완료되었습니다")
					.build();
	}
}