package com.kh.final3.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.web.bind.annotation.*;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.PointHistoryDao;
import com.kh.final3.dao.WithdrawDao;
import com.kh.final3.dto.MemberDto;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.dto.PointWithdrawDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.MemberService;
import com.kh.final3.service.OrderService;
import com.kh.final3.service.TokenService;
import com.kh.final3.service.WithdrawService;
import com.kh.final3.vo.TokenVO;
import com.kh.final3.vo.WithdrawRejectRequestVO;
import com.kh.final3.vo.AdminOrderListVO;
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
	@Autowired
	private WithdrawDao withdrawDao;
	@Autowired
	private WithdrawService withdrawService;
	@Autowired
	private OrderService orderService;


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
	// 환전 요청 목록 (페이지네이션)
	@GetMapping("/withdraw")
	public PageVO<PointWithdrawDto> withdrawList(
	        @RequestHeader("Authorization") String bearerToken,
	        @ModelAttribute PageVO<PointWithdrawDto> vo,
	        @RequestParam(required = false, defaultValue = "REQUEST") String status
	) {
	    adminOnly(bearerToken);

	    if (vo.getSize() == 0) vo.setSize(10);

	    int count = withdrawDao.countByStatus(status);
	    vo.setDataCount(count);

	    List<PointWithdrawDto> list = withdrawDao.listByStatusPaging(vo, status);
	    vo.setList(list);

	    return vo;
	}



	// 환전 승인
	@PostMapping("/withdraw/{withdrawNo}/approve")
	public void withdrawApprove(
	        @RequestHeader("Authorization") String bearerToken,
	        @PathVariable long withdrawNo
	) {
	    adminOnly(bearerToken);
	    TokenVO tokenVO = tokenService.parse(bearerToken);
	    withdrawService.approve(withdrawNo, tokenVO.getMemberNo());
	}

	// 환전 반려
	@PostMapping("/withdraw/{withdrawNo}/reject")
	public void withdrawReject(
	        @RequestHeader("Authorization") String bearerToken,
	        @PathVariable long withdrawNo,
	        @RequestBody WithdrawRejectRequestVO req
	) {
	    adminOnly(bearerToken);
	    TokenVO tokenVO = tokenService.parse(bearerToken);
	    withdrawService.reject(
	        withdrawNo,
	        tokenVO.getMemberNo(),
	        req.getRejectReason()
	    );
	}

	// 주문 처리
	@GetMapping("/orders")
	public PageVO<AdminOrderListVO> adminOrders(PageVO<AdminOrderListVO> pageVO) {
	    return orderService.getAdminOrderList(pageVO);
	}
	

}
