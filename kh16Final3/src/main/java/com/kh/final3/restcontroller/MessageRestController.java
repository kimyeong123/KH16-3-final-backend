package com.kh.final3.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.dto.MessageDto;
import com.kh.final3.service.MessageService;
import com.kh.final3.vo.PageVO;
import com.kh.final3.error.TargetNotfoundException; 
import com.kh.final3.error.UnauthorizationException; 

@CrossOrigin
@RestController
@RequestMapping("/message")
public class MessageRestController {
	
	@Autowired
	private MessageService messageService;
	
	/**
	 * 1. 쪽지 전송 (POST /message)
	 */
	@PostMapping
	public ResponseEntity<String> sendMessage(
											@RequestBody MessageDto messageDto,
											@RequestAttribute("memberNo") long memberNo){
		
		// 발신자 번호 설정
		messageDto.setSenderNo(memberNo);
		
		messageService.sendMessage(messageDto); // Service 호출
		
		return ResponseEntity.ok("쪽지 전송 완료");
	}
	
	/**
	 * 2. 미확인 알림 개수 조회 (GET /message/unread/count)
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
	 * 3. 미확인 쪽지/알림 목록 조회 (헤더 드롭다운용) (GET /message/unread/list)
	 */
	@GetMapping("/unread/list")
	public ResponseEntity<List<MessageDto>> getUnreadMessagesList(
	    @RequestAttribute("memberNo") long memberNo
	) {
	    List<MessageDto> list = messageService.getUnreadListForHeader(memberNo); 
	    return ResponseEntity.ok(list);
	}
	
	/**
	 * 4. 수신함 목록 조회 (페이지네이션 및 필터링 지원)
	 * (GET /message/received/page?page=1&size=10&types=GENERAL,SYSTEM_ALERT)
	 * * 모든 수신함 조회 요청을 이 단일 엔드포인트에서 처리합니다.
	 */
	@GetMapping("/received/page")
    public ResponseEntity<PageVO<MessageDto>> getReceivedListByPagingAndFilter(
			PageVO<MessageDto> pageVO,
			@RequestParam(required = false) List<String> types,
			@RequestAttribute("memberNo") long memberNo) {
		
		// Service 호출 시 types 파라미터를 함께 전달해야 합니다.
		PageVO<MessageDto> resultVO = messageService.getReceivedListByPaging(pageVO, memberNo, types);
		
		return ResponseEntity.ok(resultVO);
	}
	
	/**
	 * 5. 발신함 목록 조회 (페이지네이션 및 필터링 지원)
	 * (GET /message/sent/page?page=1&size=10&types=...)
	 */
	@GetMapping("/sent/page")
    public ResponseEntity<PageVO<MessageDto>> getSentListByPagingAndFilter(
			PageVO<MessageDto> pageVO,
			@RequestParam(required = false) List<String> types, // 발신함에도 필터링 필요시 사용
			@RequestAttribute("memberNo") long memberNo) {
		
		// Service 호출 시 types 파라미터를 함께 전달해야 합니다.
		PageVO<MessageDto> resultVO = messageService.getSentListByPaging(pageVO, memberNo, types);
		
		return ResponseEntity.ok(resultVO);
	}

	/**
	 * 6. 수신함에서 쪽지 삭제 (POST /message/delete/receiver/{messageNo})
	 */
	@PostMapping("delete/receiver/{messageNo}")
	public ResponseEntity<String> deleteMessageForReceiver(@PathVariable Integer messageNo) {
		
		messageService.deleteMessageByReceiver(messageNo);
		
		return ResponseEntity.ok("수신함 쪽지 삭제 성공");
	}
	
	/**
	 * 7. 상세 조회 및 읽음 처리 (GET /message/{messageNo})
	 */
	@GetMapping("/{messageNo}")
	public ResponseEntity<MessageDto> getMessageDetail(
			@PathVariable Integer messageNo,
	    @RequestAttribute("memberNo") long currentMemberNo) {

	    MessageDto detail = messageService.getMessageDetailAndRead(messageNo);

	    if (detail == null) {
	        throw new TargetNotfoundException("해당 쪽지를 찾을 수 없습니다.");
	    }
	        
        // 쪽지 수신자 또는 발신자가 현재 로그인한 사용자가 아니면 권한 없음
        if (detail.getReceiverNo() != currentMemberNo && detail.getSenderNo() != currentMemberNo) {
	        throw new UnauthorizationException("해당 쪽지를 조회할 권한이 없습니다.");
        }

	    return ResponseEntity.ok(detail);
	}
	
}