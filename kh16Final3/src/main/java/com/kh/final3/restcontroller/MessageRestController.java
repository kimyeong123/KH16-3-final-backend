package com.kh.final3.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/rest/message")
public class MessageRestController {
	
	@Autowired
	private MessageService messageService;
	
	/**
	 * 1. 쪽지 전송 (POST /rest/message)
	 */
	@PostMapping
	public ResponseEntity<String> sendMessage(
											@RequestBody MessageDto messageDto,
											@RequestAttribute("memberNo") long memberNo){
		
		// 발신자 번호 설정
		messageDto.setSenderNo(memberNo);
		
		// Service 호출 (Service 반환 타입이 void라고 가정하며, 실패 시 예외 발생)
		messageService.sendMessage(messageDto);
		
		// 예외 없이 성공
		return ResponseEntity.ok("쪽지 전송 완료");
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
			// 필터가 없을 경우 전체 수신함 목록 조회
			list = messageService.getReceivedList(memberNo);
		}
		else {
			// 필터가 있을 경우 타입별 목록 조회
			Map<String, Object> param = new HashMap<>();
			param.put("memberNo", memberNo);
			param.put("typeList", types);
			
			list = messageService.getReceivedListByTypes(param);
		}
		
		return ResponseEntity.ok(list);
	}
	
	/**
	 * 4. 수신함에서 쪽지 삭제 (POST /rest/message/delete/receiver/{messageNo})
	 */
	@PostMapping("delete/receiver/{messageNo}")
	public ResponseEntity<String> deleteMessageForReceiver(@PathVariable Integer messageNo) {
		
		// Service 호출 (Service 반환 타입이 void라고 가정하며, 실패 시 예외 발생)
		messageService.deleteMessageByReceiver(messageNo);
		
		// 예외 없이 성공
		return ResponseEntity.ok("수신함 쪽지 삭제 성공");
	}
	
	// --- 페이지네이션 및 상세 조회 기능 ---
	
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
	    @RequestAttribute("memberNo") long currentMemberNo // 현재 로그인한 회원 번호 (보안 체크용)
	) {
	  // 1. 서비스 호출: 상세 조회 및 읽음 처리 트랜잭션 실행
	  MessageDto detail = messageService.getMessageDetailAndRead(messageNo);

	  // 2. 보안 체크 (예외 처리)
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