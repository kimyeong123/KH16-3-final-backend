package com.kh.final3.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.PointHistoryDao;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.MemberService;
import com.kh.final3.service.TokenService;
import com.kh.final3.vo.TokenVO;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.PointChargeHistoryVO;
import com.kh.final3.vo.member.MemberBidHistoryVO;
import com.kh.final3.vo.member.MemberListVO;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

	@Autowired
	private TokenService tokenService;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private MemberService memberService;
	@Autowired
	private PointHistoryDao pointHistoryDao;

	private void adminOnly(String bearerToken) {
		TokenVO tokenVO = tokenService.parse(bearerToken);

		if (tokenVO == null || !"ADMIN".equalsIgnoreCase(tokenVO.getLoginLevel())) {
			throw new UnauthorizationException("admin only");
		}
	}

	@GetMapping("/members/detail/{memberNo}")
	public MemberDto adminDetail(@PathVariable Long memberNo, @RequestHeader("Authorization") String bearerToken) {
		adminOnly(bearerToken);

		MemberDto dto = memberDao.selectOneByMemberNo(memberNo);
		if (dto == null)
			throw new TargetNotfoundException("대상 회원이 없습니다.");
		return dto;
	}

	@GetMapping("/members/{memberNo}/bid/history")
	public List<MemberBidHistoryVO> memberBidHistory(@RequestHeader("Authorization") String bearerToken,
			@PathVariable long memberNo) {
		adminOnly(bearerToken);
		return pointHistoryDao.listMemberBidHistory(memberNo);
	}

	@GetMapping("/members/{memberNo}/point/charge")
	public List<PointChargeHistoryVO> memberPointChargeHistory(@RequestHeader("Authorization") String bearerToken,
			@PathVariable long memberNo) {
		adminOnly(bearerToken);
		return pointHistoryDao.listChargeHistoryByMember(memberNo);
	}

	@GetMapping("/members")
	public PageVO<MemberListVO> memberList(@RequestHeader("Authorization") String bearerToken,
			@ModelAttribute PageVO<MemberListVO> vo) {
		adminOnly(bearerToken);
		
		return memberService.getMemberList(vo);
	}

	@GetMapping("/members/list")
	public List<MemberListVO> legacyList(@RequestHeader("Authorization") String bearerToken,
			@RequestParam(required = false) String type, @RequestParam(required = false) String keyword) {
		adminOnly(bearerToken);

		PageVO<MemberListVO> vo = new PageVO<>();
		vo.setPage(1);
		vo.setSize(5);
		vo.setType(type);
		vo.setKeyword(keyword);

		return memberService.getMemberList(vo).getList();
	}

}
