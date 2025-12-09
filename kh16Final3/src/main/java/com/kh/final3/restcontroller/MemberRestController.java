package com.kh.final3.restcontroller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.TokenDao;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.MemberService;
import com.kh.final3.service.TokenService;
import com.kh.final3.vo.MemberComplexSearchVO;
import com.kh.final3.vo.MemberLoginResponseVO;
import com.kh.final3.vo.MemberRefreshVO;
import com.kh.final3.vo.TokenVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/member")
@CrossOrigin
public class MemberRestController {

    @Autowired
    private MemberService memberService;
    
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TokenService tokenService;

    // 회원가입
    @PostMapping("/register")
    public String register(@Valid @RequestBody MemberDto memberDto) {
        memberService.add(memberDto);
        return "회원가입 성공";
    }

    @GetMapping("/memberId/{memberId}")
    public boolean checkId(@PathVariable String memberId) {
        return memberService.checkId(memberId);
    }

    @GetMapping("/memberNickname/{memberNickname}")
    public boolean checkNickname(@PathVariable String memberNickname) {
        return memberService.checkNickname(memberNickname);
    }

    @GetMapping("/checkDuplicate")
    public boolean checkDuplicate(
            @RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birth,
            @RequestParam String contact) {
        return memberService.selectOneByNameBirthContact(name, birth, contact) == null;
    }
	//로그인
	//@GetMapping("/accountId/{accountId}/accountPw/{accountPw}")
	@PostMapping("/login")
	public MemberLoginResponseVO login(@RequestBody MemberDto memberDto) {
		MemberDto findDto = memberDao.selectOne(memberDto.getMemberId());
		if(findDto == null) {//아이디 없음
			throw new TargetNotfoundException("로그인 정보 오류");
		}
		//boolean valid = findDto.getAccountPw().equals(accountDto.getAccountPw());//암호화 전에 쓰던 코드
		boolean valid = passwordEncoder.matches(memberDto.getMemberPw(), findDto.getMemberPw());
		if(valid == false) {//비밀번호 불일치
			throw new TargetNotfoundException("로그인 정보 오류");
		}
		
		//로그인 성공
		return MemberLoginResponseVO.builder()
					.loginId(findDto.getMemberId())//아이디
					.loginLevel(findDto.getMemberRole())//등급
					.accessToken(tokenService.generateAccessToken(findDto))//액세스토큰
					.refreshToken(tokenService.generateRefreshToken(findDto))//갱신토큰
				.build();
	}
	//토큰 갱신
			@PostMapping("/refresh")
			public MemberLoginResponseVO refresh(
					@RequestBody MemberRefreshVO memberRefreshVO) {
				String refreshToken = memberRefreshVO.getRefreshToken();
				if(refreshToken == null) throw new UnauthorizationException();
				
				TokenVO tokenVO = tokenService.parse(refreshToken);
				//아이디와 토큰문자열로 발급내역을 조회하여 비교
				boolean valid = tokenService.checkRefreshToken(tokenVO, refreshToken);
				if(valid == false) throw new TargetNotfoundException();
				
				//재생성 후 반환
				return MemberLoginResponseVO.builder()
							.loginId(tokenVO.getLoginId())
							.loginLevel(tokenVO.getLoginLevel())
							.accessToken(tokenService.generateAccessToken(tokenVO))
							.refreshToken(tokenService.generateRefreshToken(tokenVO))
						.build();
			}
			@Autowired
			private TokenDao tokenDao;
			
			//로그아웃
			@DeleteMapping("/logout")
			public void logout(
				@RequestHeader("Authorization") String bearerToken//직접 받을 경우
				//@RequestAttribute TokenVO tokenVO//MemberInterceptor가 준 데이터를 받을 경우
			) {
				TokenVO tokenVO = tokenService.parse(bearerToken);
				tokenDao.deleteByTarget(tokenVO.getLoginId());
			}
			//복합검색
			@PostMapping("/search")//@GetMapping은 JSON 수신 불가
			public List<MemberDto> search(@RequestBody MemberComplexSearchVO vo){
				return memberDao.selectList(vo);
			}
			


}
