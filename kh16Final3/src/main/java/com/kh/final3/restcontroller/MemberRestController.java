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
import com.kh.final3.dao.PointHistoryDao;
import com.kh.final3.dao.TokenDao;
import com.kh.final3.dao.WithdrawDao;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.dto.PointWithdrawDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.MemberService;
import com.kh.final3.service.ProductService;
import com.kh.final3.service.TokenService;
import com.kh.final3.service.WithdrawService;
import com.kh.final3.vo.PointChargeHistoryVO;
import com.kh.final3.vo.PurchaseListVO;
import com.kh.final3.vo.TokenVO;
import com.kh.final3.vo.member.MemberBidHistoryVO;
import com.kh.final3.vo.member.MemberChangePwVO;
import com.kh.final3.vo.member.MemberComplexSearchVO;
import com.kh.final3.vo.member.MemberFindIdVO;
import com.kh.final3.vo.member.MemberLoginResponseVO;
import com.kh.final3.vo.member.MemberRefreshVO;
import com.kh.final3.vo.member.MemberRequestVO;
import com.kh.final3.vo.member.MemberResetPwVO;
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
	@Autowired
	private PointHistoryDao pointHistoryDao;
	@Autowired
	private ProductService productService;
	@Autowired
	private WithdrawService withdrawService;
	@Autowired 
	private WithdrawDao withdrawDao;

	// 회원가입
	@PostMapping("/register")
	public String register(@Valid @RequestBody MemberDto memberDto) {
		memberService.add(memberDto);
		return "회원가입 성공";
	}

	@GetMapping("/memberId/{id}")
	public boolean checkId(@PathVariable String id) {
		return memberService.checkId(id);
	}

	@GetMapping("/memberNickname/{nickname}")
	public boolean checkNickname(@PathVariable String nickname) {
		return memberService.checkNickname(nickname);
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
		MemberDto findDto = memberDao.selectOneByMemberId(memberDto.getId());
		if (findDto == null) {// 아이디 없음
			throw new TargetNotfoundException("로그인 정보 오류");
		}
		// boolean valid =
		// findDto.getAccountPw().equals(accountDto.getAccountPw());//암호화 전에 쓰던 코드
		boolean valid = passwordEncoder.matches(memberDto.getPw(), findDto.getPw());
		if (valid == false) {// 비밀번호 불일치
			throw new TargetNotfoundException("로그인 정보 오류");
		}

		// 로그인 성공
		return MemberLoginResponseVO.builder().loginNo(findDto.getMemberNo()).loginId(findDto.getId())// 아이디
				.loginLevel(findDto.getRole())// 등급
				.nickname(findDto.getNickname()).email(findDto.getEmail()).post(findDto.getPost())
				.address1(findDto.getAddress1()).address2(findDto.getAddress2()).point(findDto.getPoint())
				.contact(findDto.getContact()).createdTime(findDto.getCreatedTime())
				.accessToken(tokenService.generateAccessToken(findDto))// 액세스토큰
				.refreshToken(tokenService.generateRefreshToken(findDto))// 갱신토큰
				.build();
	}

	// 아이디 찾기
	@PostMapping("/find-id")
	public ResponseEntity<String> findId(@RequestBody MemberFindIdVO vo) {
		try {
			memberService.sendMemberIdByEmail(vo.getEmail());
			return ResponseEntity.ok("입력하신 이메일로 아이디를 전송했습니다.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("아이디 찾기 처리 	 오류가 발생했습니다.");
		}
	}

	// 비밀번호 재설정 (아이디 + 이메일 확인 후 임시 비밀번호 발급/메일 발송)
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody MemberResetPwVO vo) {
		try {
			memberService.resetPasswordByIdAndEmail(vo.getId(), vo.getEmail());
			return ResponseEntity.ok("임시 비밀번호를 이메일로 발송했습니다.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 재설정 처리 중 오류가 발생했습니다.");
		}
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
	//일반회원이 본인상세
	@GetMapping("/mypage")
	public MemberDto mypage(@RequestHeader("Authorization") String bearerToken) {
	    TokenVO tokenVO = tokenService.parse(bearerToken);
	    Long memberNo = tokenVO.getMemberNo();

	    MemberDto dto = memberDao.selectOneByMemberNo(memberNo);
	    if (dto == null) throw new TargetNotfoundException("대상 회원이 없습니다.");
	    return dto;
	}	
	// 관리자가 회원상세	
	@GetMapping("/admin-detail/{memberNo}")
	public MemberDto adminDetail(@PathVariable Long memberNo, @RequestHeader("Authorization") String bearerToken) {

		TokenVO tokenVO = tokenService.parse(bearerToken);

		if (!"ADMIN".equals(tokenVO.getLoginLevel())) {
			throw new UnauthorizationException("관리자만 조회 가능합니다.");
		}

		MemberDto dto = memberDao.selectOneByMemberNo(memberNo);

		if (dto == null)
			throw new TargetNotfoundException("대상 회원이 없습니다.");
		return dto;
	}

	// 회원탈퇴
	@DeleteMapping("/{memberNo}")
	public ResponseEntity<String> deleteMember(@PathVariable Long memberNo,
			@RequestHeader("Authorization") String bearerToken, @RequestBody MemberRequestVO requestVO) {

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
	@DeleteMapping("/admin/{memberNo}")
	public ResponseEntity<String> deleteByAdmin(
	    @PathVariable Long memberNo,
	    @RequestHeader("Authorization") String bearerToken
	) {
	    TokenVO tokenVO = tokenService.parse(bearerToken);

	    if (!"ADMIN".equals(tokenVO.getLoginLevel())) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음");
	    }

	    boolean result = memberService.deleteMember(memberNo);
	    if (result) {
	        return ResponseEntity.ok("관리자 삭제 완료");
	    }
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 삭제 실패");
	}



	// 회원 수정
	@PutMapping("/{memberNo}")
	public ResponseEntity<String> updateMember(@PathVariable Long memberNo,
			@RequestHeader("Authorization") String bearerToken, @RequestBody MemberUpdateVO vo) {

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
	public ResponseEntity<TokenVO> checkToken(
			@RequestHeader(value = "Authorization", required = false) String bearerToken) {

		// 1. 토큰 존재 여부 확인 및 "Bearer " 접두사 검증
		if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
			// 토큰이 없으면 권한 없음 처리 (401 Unauthorized)
			throw new UnauthorizationException("토큰이 존재하지 않습니다.");
		}

		try {
			// 2. 토큰 파싱 및 유효성 검사
			// tokenService.parse 내부에서 서명, 만료 시간 등이 검사됩니다. (실패 시 예외 발생)
			TokenVO tokenVO = tokenService.parse(bearerToken);

			// 3. 유효한 토큰 정보 반환 (프론트엔드가 loginLevel을 가져감)
			return ResponseEntity.ok(tokenVO);

		} catch (Exception e) {
			// 토큰 파싱/검증 실패 (예: 만료, 변조) 시 권한 없음 처리 (401 Unauthorized)
			throw new UnauthorizationException("유효하지 않거나 만료된 토큰입니다.");
		}
	}

	// 비밀번호 변경
	@PutMapping("/changePassword/{memberNo}")
	public ResponseEntity<String> changePassword(@PathVariable Long memberNo,
			@RequestHeader("Authorization") String bearerToken, @RequestBody MemberChangePwVO changePwVO) {
		// 1. 토큰에서 로그인된 사용자 정보 추출
		TokenVO tokenVO = tokenService.parse(bearerToken);
		// 2. 본인 계정인지 확인
		if (!tokenVO.getMemberNo().equals(memberNo)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 계정만 비밀번호를 변경할 수 있습니다.");
		}
		// 3. 기존 비밀번호 확인 (현재 비번으로 체크)
		MemberRequestVO checkVO = MemberRequestVO.builder().memberNo(memberNo).pw(changePwVO.getCurrentPw()) //
				.build();
		boolean passwordOk = memberService.checkPassword(checkVO);
		if (!passwordOk) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("현재 비밀번호가 올바르지 않습니다.");
		}
		// 4. 새 비밀번호가 현재 비밀번호와 같은지 검사
		if (changePwVO.getCurrentPw().equals(changePwVO.getNewPw())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("새 비밀번호는 현재 비밀번호와 다르게 설정해야 합니다.");
		}
		// 5. 새 비밀번호 암호화
		String encryptedPassword = passwordEncoder.encode(changePwVO.getNewPw());
		// 6. 비밀번호 업데이트
		boolean result = memberService.updatePassword(memberNo, encryptedPassword);
		if (result) {
			return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 실패");
		}
	}


	//포인트 충전 내역
	@GetMapping("/point/charged-history")
	public List<PointChargeHistoryVO> myChargeHistory(
	        @RequestHeader("Authorization") String bearerToken
	) {
	    TokenVO tokenVO = tokenService.parse(bearerToken);
	    long memberNo = tokenVO.getMemberNo();
	    return pointHistoryDao.listChargeHistoryByMember(memberNo);
	}
	//입찰 내역
	@GetMapping("/bid/history")
	public List<MemberBidHistoryVO> memberBidHistory(
	        @RequestHeader("Authorization") String bearerToken
	) {
	    TokenVO tokenVO = tokenService.parse(bearerToken);
	    return pointHistoryDao.listMemberBidHistory(tokenVO.getMemberNo());
	}
	@GetMapping("/win-products/history")
	public List<PurchaseListVO> myWinProductList(
	        @RequestHeader("Authorization") String bearerToken
	) {
	    TokenVO tokenVO = tokenService.parse(bearerToken);
	    return productService.getPurchaseList(tokenVO.getMemberNo());
	}


    // 환전 요청
    @PostMapping("/withdraw")
    public long requestWithdraw(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody PointWithdrawDto dto
    ) {
        TokenVO tokenVO = tokenService.parse(bearerToken);
        long memberNo = tokenVO.getMemberNo();

        // body의 memberNo는 신뢰하지 않고 토큰 기준으로 덮어쓰기
        dto.setMemberNo(memberNo);

        return withdrawService.request(dto);
    }

    // 내 환전 내역 조회
    @GetMapping("/withdraw/history")
    public List<PointWithdrawDto> myWithdrawHistory(
            @RequestHeader("Authorization") String bearerToken
    ) {
        TokenVO tokenVO = tokenService.parse(bearerToken);
        return withdrawDao.listByMember(tokenVO.getMemberNo());
    }
    @GetMapping("/point/balance")
    public long pointBalance(@RequestHeader("Authorization") String bearerToken) {
        TokenVO tokenVO = tokenService.parse(bearerToken);
        return pointHistoryDao.calculateMemberBalance(tokenVO.getMemberNo());
    }


}
