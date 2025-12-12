package com.kh.final3.restcontroller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.TokenDao;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.MemberService;
import com.kh.final3.service.TokenService;
import com.kh.final3.vo.TokenVO;
import com.kh.final3.vo.member.MemberComplexSearchVO;
import com.kh.final3.vo.member.MemberLoginResponseVO;
import com.kh.final3.vo.member.MemberRefreshVO;
import com.kh.final3.vo.member.MemberRequestVO;
import com.kh.final3.vo.member.MemberUpdateVO;

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
	public boolean checkDuplicate(@RequestParam String name,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birth,
			@RequestParam String contact) {
		return memberService.selectOneByNameBirthContact(name, birth, contact) == null;
	}

	// 로그인
	// @GetMapping("/accountId/{accountId}/accountPw/{accountPw}")
	@PostMapping("/login")
	public MemberLoginResponseVO login(@RequestBody MemberDto memberDto) {
		MemberDto findDto = memberDao.selectOne(memberDto.getMemberId());
		if (findDto == null) {// 아이디 없음
			throw new TargetNotfoundException("로그인 정보 오류");
		}
		// boolean valid =
		// findDto.getAccountPw().equals(accountDto.getAccountPw());//암호화 전에 쓰던 코드
		boolean valid = passwordEncoder.matches(memberDto.getMemberPw(), findDto.getMemberPw());
		if (valid == false) {// 비밀번호 불일치
			throw new TargetNotfoundException("로그인 정보 오류");
		}

		// 로그인 성공
		return MemberLoginResponseVO.builder()
				.loginNo(findDto.getMemberNo())
				.loginId(findDto.getMemberId())// 아이디
				.loginLevel(findDto.getMemberRole())// 등급
				.nickname(findDto.getMemberNickname()).email(findDto.getMemberEmail())
				.post(findDto.getMemberPost())
				.address1(findDto.getMemberAddress1()).address2(findDto.getMemberAddress2())
				.point(findDto.getMemberPoint()).contact(findDto.getMemberContact())
				.createdTime(findDto.getMemberCreatedTime()).accessToken(tokenService.generateAccessToken(findDto))// 액세스토큰
				.refreshToken(tokenService.generateRefreshToken(findDto))// 갱신토큰
				.build();
	}

	// 토큰 갱신
	@PostMapping("/refresh")
	public MemberLoginResponseVO refresh(@RequestBody MemberRefreshVO memberRefreshVO) {
		String refreshToken = memberRefreshVO.getRefreshToken();
		if (refreshToken == null)
			throw new UnauthorizationException();

		TokenVO tokenVO = tokenService.parse(refreshToken);
		// 아이디와 토큰문자열로 발급내역을 조회하여 비교
		boolean valid = tokenService.checkRefreshToken(tokenVO, refreshToken);
		if (valid == false)
			throw new TargetNotfoundException();

		// 재생성 후 반환
		return MemberLoginResponseVO.builder().loginId(tokenVO.getLoginId()).loginLevel(tokenVO.getLoginLevel())
				.accessToken(tokenService.generateAccessToken(tokenVO))
				.refreshToken(tokenService.generateRefreshToken(tokenVO)).build();
	}

	@Autowired
	private TokenDao tokenDao;

	// 로그아웃
	@DeleteMapping("/logout")
	public void logout(@RequestHeader("Authorization") String bearerToken// 직접 받을 경우
	// @RequestAttribute TokenVO tokenVO//MemberInterceptor가 준 데이터를 받을 경우
	) {
		TokenVO tokenVO = tokenService.parse(bearerToken);
		tokenDao.deleteByTarget(tokenVO.getLoginId());
	}

	// 복합검색
	@PostMapping("/search") // @GetMapping은 JSON 수신 불가
	public List<MemberDto> search(@RequestBody MemberComplexSearchVO vo) {
		return memberDao.selectList(vo);
	}
	// 회원탈퇴
	@DeleteMapping("/{memberNo}")
	public ResponseEntity<String> deleteMember(@PathVariable Long memberNo,
			@RequestHeader("Authorization") String bearerToken,  @RequestBody MemberRequestVO requestVO) {

		// 1. 토큰에서 로그인된 사용자 정보 추출
		TokenVO tokenVO = tokenService.parse(bearerToken);

		// 2. 로그인한 사용자와 삭제 대상 비교
		if (!tokenVO.getMemberNo().equals(memberNo)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 계정만 탈퇴할 수 있습니다.");
		}
	    // 3. 비밀번호 확인
	    requestVO.setMemberNo(memberNo); // PathVariable -> VO에 세팅
	    boolean passwordOk = memberService.checkPassword(requestVO);
	    if (!passwordOk) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 올바르지 않습니다.");
	    }

		// 3. 회원 삭제 수행
		boolean result = memberService.deleteMember(memberNo);
		if (result) {
			// 4. 관련 토큰 모두 삭제
			tokenDao.deleteByTarget(tokenVO.getLoginId());

			return ResponseEntity.ok("회원이 삭제되었습니다.");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 삭제 실패");
		}
	}
	@PutMapping("/{memberNo}")
	public ResponseEntity<String> updateMember(
	        @PathVariable Long memberNo,
	        @RequestHeader("Authorization") String bearerToken,
	        @RequestBody MemberUpdateVO vo) {

	    // 1. 토큰에서 로그인된 사용자 정보 추출
	    TokenVO tokenVO = tokenService.parse(bearerToken);

	    // 2. 본인 계정인지 확인
	    if (!tokenVO.getMemberNo().equals(memberNo)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 계정만 수정할 수 있습니다.");
	    }

	    // 3. 수정 수행
	    boolean result = memberService.updateMember(memberNo, vo);

	    if (result) {
	        return ResponseEntity.ok("회원정보가 수정되었습니다.");
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원정보 수정 실패");
	    }
	}


	// 토큰 유효성 검사 및 사용자 정보 반환 (새로고침 시 상태 복구용)
	@PostMapping("/check-token") 
	public ResponseEntity<TokenVO> checkToken(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
	    
	    //  1. 토큰 존재 여부 확인 및 "Bearer " 접두사 검증
	    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
	        // 토큰이 없으면 권한 없음 처리 (401 Unauthorized)
	        throw new UnauthorizationException("토큰이 존재하지 않습니다.");
	    }
	    
	    try {
	        //  2. 토큰 파싱 및 유효성 검사
	        // tokenService.parse 내부에서 서명, 만료 시간 등이 검사됩니다. (실패 시 예외 발생)
	        TokenVO tokenVO = tokenService.parse(bearerToken);
	        
	        // 3. 유효한 토큰 정보 반환 (프론트엔드가 loginLevel을 가져감)
	        return ResponseEntity.ok(tokenVO); 
	        
	    } catch (Exception e) {
	        // 토큰 파싱/검증 실패 (예: 만료, 변조) 시 권한 없음 처리 (401 Unauthorized)
	        throw new UnauthorizationException("유효하지 않거나 만료된 토큰입니다."); 
	    }
	}

}
