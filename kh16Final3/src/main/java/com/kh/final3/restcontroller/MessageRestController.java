package com.kh.final3.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // Spring의 RequestBody로 명시적 import
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.dto.MessageDto;
import com.kh.final3.service.MessageService;
import com.kh.final3.vo.PageVO; // HEAD 버전에 있던 PageVO import
import com.kh.final3.vo.TokenVO; // 토큰 관련 VO가 있다면 import (현재 코드에는 없지만 예상하여 추가)

@RestController
@RequestMapping("/rest/message")
public class MessageRestController {
	@Autowired
	private MessageService messageService;
	
	/**
	 * 1. 쪽지 전송 (POST /rest/message)
	 */
	@PostMapping // 💡 RESTful 원칙에 따라 "/" 제거. 기본 경로 사용
	public ResponseEntity<String> sendMessage(
											@RequestBody MessageDto messageDto, // 💡 Spring의 @RequestBody 사용
											@RequestAttribute("memberNo") long memberNo){
		
		// 발신자 번호 설정
		messageDto.setSenderNo(memberNo);
		
		boolean success = messageService.sendMessage(messageDto);
		
		if(success) {
			return ResponseEntity.ok("전송 완료");
		} 
		else {
			return ResponseEntity.internalServerError().body("전송 실패");
		}
	}
	
	/**
	 * 2. 미확인 알림 개수 조회 (GET /rest/message/unread/count)
	 */
	@GetMapping("/unread/count")
	public ResponseEntity<Map<String, Object>> getUnreadAlertCount(@RequestAttribute("memberNo") long memberNo) {
		
		int count = messageService.countUnreadAlerts(memberNo);
		
		Map<String, Object> response = new HashMap<>();
        response.put("memberNo", memberNo);
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
	}
	
	/**
	 * 3. 수신함 목록 조회 (필터링 지원) (GET /rest/message/received?types=...)
	 */
	@GetMapping("/received")
	public ResponseEntity<List<MessageDto>> getReceivedMessagesByFilter(
						@RequestParam(required = false) List<String> types,
						@RequestAttribute("memberNo") long memberNo) {
		
		List<MessageDto> list;
		
		if(types == null || types.isEmpty()) {
			list = messageService.getReceivedList(memberNo);
		}
		else {
			Map<String, Object> param = new HashMap<>();
			param.put("memberNo", memberNo);
			param.put("typeList", types);
			
			list = messageService.getReceivedListByTypes(param);
		}
		
		return ResponseEntity.ok(list);
	}
	
	/**
	 * 4. 수신함에서 쪽지 삭제 (POST /rest/message/delete/receiver/{messageNo})
	 * 참고: PATCH 또는 DELETE를 사용하는 것이 RESTful에 더 적합함.
	 */
	@PostMapping("delete/receiver/{messageNo}")
	public ResponseEntity<String> deleteMessageForReceiver(@PathVariable Integer messageNo) {
		
		boolean success = messageService.deleteMessageByReceiver(messageNo);
		
		if(success) {
			return ResponseEntity.ok("삭제 성공");
		} 
		else {
			return ResponseEntity.internalServerError().body("삭제 실패");
		}
	}
	
	// --- 페이지네이션 및 상세 조회 기능 추가 (HEAD 버전 기능) ---
	
	/**
	 * 5. 수신함 목록 조회 (페이지네이션) (GET /rest/message/received/page)
	 */
	@GetMapping("/received/page")
    public ResponseEntity<PageVO<MessageDto>> getReceivedListByPaging(
            PageVO<MessageDto> pageVO, 
            @RequestAttribute("memberNo") long memberNo
    ) {
        PageVO<MessageDto> resultVO = messageService.getReceivedListByPaging(pageVO, memberNo);
        
        return ResponseEntity.ok(resultVO);
    }
	
	/**
	 * 6. 발신함 목록 조회 (페이지네이션) (GET /rest/message/sent/page)
	 */
	@GetMapping("/sent/page")
    public ResponseEntity<PageVO<MessageDto>> getSentListByPaging(
            PageVO<MessageDto> pageVO, 
            @RequestAttribute("memberNo") long memberNo
    ) {
        PageVO<MessageDto> resultVO = messageService.getSentListByPaging(pageVO, memberNo);
        
        return ResponseEntity.ok(resultVO);
    }
	
	/**
	 * 7. 상세 조회 및 읽음 처리 (GET /rest/message/{messageNo})
	 */
	@GetMapping("/{messageNo}")
	public ResponseEntity<MessageDto> getMessageDetail(
			@PathVariable Integer messageNo,
	        @RequestAttribute("memberNo") long currentMemberNo // 쪽지 수신자 확인용 (보안 강화)
	) {
	    // 1. 서비스 호출: 상세 조회 및 읽음 처리 트랜잭션 실행
	    MessageDto detail = messageService.getMessageDetailAndRead(messageNo);

	    // 2. 보안 체크 (선택 사항): 해당 쪽지의 수신자/발신자가 맞는지 확인
	    if (detail == null || (detail.getReceiverNo() != currentMemberNo && detail.getSenderNo() != currentMemberNo)) {
	         return ResponseEntity.notFound().build();
	         // 또는 권한 없음 예외 처리 (throw new UnauthorizationException("권한 없음");)
	    }

	    return ResponseEntity.ok(detail);
	}
	
}