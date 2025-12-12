package com.kh.final3.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.MessageDao;
import com.kh.final3.dto.MessageDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.vo.PageVO;

@Service
public class MessageService {

	@Autowired
	private MessageDao messageDao;

	private static final long SYSTEM_SENDER_NO = 0L; // 시스템 발신자 번호 (0번)

	// ----------------------------------------------------
	// 1. 메시지 전송 (INSERT) 기능
	// ----------------------------------------------------

	/**
	 * 1-1. 일반 쪽지 전송 (type: MESSAGE 등 DTO에 설정되어 들어옴)
	 */
	@Transactional
	public boolean sendMessage(MessageDto messageDto) {

		// 1. 시퀀스 번호 발급
        long messageNo = messageDao.sequence();
        messageDto.setMessageNo(messageNo);

		// 2. 쪽지 등록 (DAO의 insert 호출)
		boolean insertResult = messageDao.insert(messageDto);

		return insertResult;
	}
	
	/**
	 * 1-2. 상품 문의/답변 전송 (type: INQUIRY)
	 */
	@Transactional
	public boolean sendQnaToSeller(long senderNo, long receiverNo, String content, long productNo) { 
	    
	    // 1. MessageDto 생성
	    MessageDto messageDto = MessageDto.builder()
	        .senderNo(senderNo)
	        .receiverNo(receiverNo)
	        .content(content)
	        .type("INQUIRY")         
	        .productNo(productNo)
	        .build();

	    // 2. 시퀀스 번호 발급
	    long messageNo = messageDao.sequence();
	    messageDto.setMessageNo(messageNo);

	    // 3. 쪽지(문의) 등록
	    boolean insertResult = messageDao.insert(messageDto);

	    if (!insertResult) {
	        throw new TargetNotfoundException("판매자 문의 메시지 등록 실패"); // 등록 실패 시 롤백
	    }

	    return true;
	}

	/**
	 * 1-3. 시스템 알림 전송 (type: ALERT)
	 * (제재, 주문, 공지 등 모든 시스템 알림을 이 메서드로 처리합니다.)
	 */
	@Transactional
	public void sendNotification(long receiverNo, String content, String url) {

		// 1. MessageDto 생성 (발신자: 시스템, 타입: ALERT)
		MessageDto messageDto = MessageDto.builder()
				.receiverNo(receiverNo)
				.senderNo(SYSTEM_SENDER_NO)
				.content(content)
				.type("ALERT")
				.url(url)
				.build();

		// 2. 시퀀스 번호 발급
		long messageNo = messageDao.sequence();
		messageDto.setMessageNo(messageNo);

		// 3. 쪽지(알림) 등록
		boolean insertResult = messageDao.insert(messageDto);

		// 등록 실패 시 예외 처리 (트랜잭션 롤백 유도)
		if (!insertResult) {
			throw new TargetNotfoundException("시스템 알림 메시지 등록 실패");
		}
	}

    // ----------------------------------------------------
	// 2. 메시지 조회 (SELECT) 기능
	// ----------------------------------------------------

	/**
	 * 2-1. 쪽지 상세 조회 및 읽음 처리 (트랜잭션 포함)
	 */
	@Transactional
	public MessageDto getMessageDetailAndRead(Integer messageNo) {

		// 1. 쪽지 상세 정보 조회
		MessageDto detail = messageDao.selectOne(messageNo);

		// 2. 미확인 상태일 경우만 읽음 처리 업데이트
		if (detail != null && detail.getIsRead().equals("N")) {
			messageDao.updateReadTime(messageNo);

			// 업데이트 후 DTO 상태도 'Y'로 변경하여 컨트롤러에 반환
			detail.setIsRead("Y");
		}
		return detail;
	}

	/**
	 * 2-2. 미확인 알림 개수 조회
	 */
	public int countUnreadAlerts(long memberNo) {
		return messageDao.countUnreadAlerts(memberNo);
	}

	/**
	 * 2-3. 수신함 목록 조회 (일반)
	 */
	public List<MessageDto> getReceivedList(long memberNo) {
		return messageDao.selectReceivedList(memberNo);
	}

	/**
	 * 2-4. 발신함 목록 조회 (일반)
	 */
	public List<MessageDto> getSentList(long memberNo) {
		return messageDao.selectSentList(memberNo);
	}

	/**
	 * 2-5. 타입별 수신함 목록 조회 (필터링)
	 */
	public List<MessageDto> getReceivedListByTypes(Map<String, Object> paramMap) {
		return messageDao.selectReceivedListByTypes(paramMap);
	}

	/**
	 * 2-6. 전체 미확인 쪽지 개수 조회
	 * (FaBell 아이콘에 표시될, 모든 타입의 미확인 메시지 개수를 계산)
	 */
	public int countUnreadAll(long memberNo) {
	    return messageDao.countUnreadAll(memberNo);
	}
    
	// ----------------------------------------------------
	// 3. 페이지네이션 (Paging) 기능
	// ----------------------------------------------------

	/**
	 * 3-1. 발신함 목록 조회 (페이지네이션)
	 */
	public PageVO<MessageDto> getSentListByPaging(PageVO<MessageDto> pageVO, long memberNo) {

		// 1. 전체 개수 조회 및 PageVO 설정
		long count = messageDao.countSent(memberNo);
		pageVO.setDataCount((int) count);

		// 2. DAO 호출 파라미터 준비
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("memberNo", memberNo);
		paramMap.put("begin", pageVO.getBegin());
		paramMap.put("end", pageVO.getEnd());

		// 3. 목록 조회 및 PageVO에 설정
		List<MessageDto> list = messageDao.selectSentListByPaging(paramMap);
		pageVO.setList(list);

		return pageVO;
	}

	/**
	 * 3-2. 수신함 목록 조회 (페이지네이션)
	 */
	public PageVO<MessageDto> getReceivedListByPaging(PageVO<MessageDto> pageVO, long memberNo) {

		// 1. 전체 개수 조회 및 PageVO 설정
		long count = messageDao.countReceived(memberNo);
		pageVO.setDataCount((int) count);

		// 2. DAO 호출 파라미터 준비
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("memberNo", memberNo);
		paramMap.put("begin", pageVO.getBegin());
		paramMap.put("end", pageVO.getEnd());

		// 3. 목록 조회 및 PageVO에 설정
		List<MessageDto> list = messageDao.selectReceivedListByPaging(paramMap);
		pageVO.setList(list);

		return pageVO;
	}
    
	// ----------------------------------------------------
	// 4. 메시지 삭제 (DELETE/UPDATE) 기능
	// ----------------------------------------------------

	/**
	 * 4-1. 수신자 삭제 처리 (실제 DB 삭제 대신 플래그 업데이트)
	 */
	public boolean deleteMessageByReceiver(Integer messageNo) {
		return messageDao.updateReceiverDelete(messageNo);
	}

	/**
	 * 4-2. 발신자 삭제 처리 (실제 DB 삭제 대신 플래그 업데이트)
	 */
	public boolean deleteMessageBySender(Integer messageNo) {
		return messageDao.updateSenderDelete(messageNo);
	}
}