package com.kh.final3.service;

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
	 * 1-1. 일반 쪽지 전송
	 */
	@Transactional
	public boolean sendMessage(MessageDto messageDto) {
	    
	    // 1. 닉네임으로 수신자의 MEMBER_NO를 조회
	    Long receiverMemberNo = messageDao.findMemberNoByNickname(messageDto.getReceiverNickname()); 

	    if (receiverMemberNo == null) {
	        // 수신자 닉네임을 찾지 못했으므로 쪽지 전송 불가
	        throw new TargetNotfoundException("수신자 닉네임에 해당하는 회원을 찾을 수 없습니다.");
	    }
	    
	    // 3. DTO에 수신자 번호 설정
	    messageDto.setReceiverNo(receiverMemberNo); 
	    
	    // 4. 시퀀스 번호 발급
	    long messageNo = messageDao.sequence();
	    messageDto.setMessageNo(messageNo);

	    // 5. 쪽지 등록 (RECEIVER_NO가 채워진 상태로 DAO 호출)
	    boolean insertResult = messageDao.insertMessage(messageDto);

	    return insertResult;
	}
	
	/**
	 * 1-2. 상품 문의/답변 전송
	 */
	@Transactional
	public boolean sendQnaToSeller(long senderNo, long receiverNo, String content, long productNo) {	
	    MessageDto messageDto = MessageDto.builder()
	        .senderNo(senderNo)
	        .receiverNo(receiverNo)
	        .content(content)
	        .type("SELLER_QNA")         
	        .productNo(productNo)
	        .build();

	    // 2. 시퀀스 번호 발급
	    long messageNo = messageDao.sequence();
	    messageDto.setMessageNo(messageNo);

	    boolean insertResult = messageDao.insertMessage(messageDto);

	    if (!insertResult) {
	        throw new TargetNotfoundException("판매자 문의 메시지 등록 실패");
	    }

	    return true;
	}

	/**
	 * 1-3. 시스템 알림 전송 (type: SYSTEM_ALERT)
	 */
	@Transactional
	public void sendNotification(long receiverNo, String content, String url, Long productNo) {
		MessageDto messageDto = MessageDto.builder()
				.receiverNo(receiverNo)
				.senderNo(SYSTEM_SENDER_NO)
				.content(content)
				.type("SYSTEM_ALERT") //
				.url(url)
				.productNo(productNo)
				.build();

		// 2. 시퀀스 번호 발급
		long messageNo = messageDao.sequence();
		messageDto.setMessageNo(messageNo);

		boolean insertResult = messageDao.insertMessage(messageDto);

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
	public MessageDto getMessageDetailAndRead(long messageNo, long memberNo) {
	    
	    // 1. 상세 조회
	    MessageDto detail = messageDao.selectOne(messageNo); 

	    // 쪽지 번호 자체가 없거나, Mapper의 LEFT JOIN이 실패한 경우
	    if (detail == null) {
	        return null;
	    }
	    
	    // 2. 권한 체크 (NULL-Safe 로직만 사용)
	    // DTO 필드가 Long 타입임을 전제로, NULL 체크 후 longValue() 비교
	    boolean isSender = (detail.getSenderNo() != null && detail.getSenderNo().longValue() == memberNo);
	    boolean isReceiver = (detail.getReceiverNo() != null && detail.getReceiverNo().longValue() == memberNo);

	    if (!isSender && !isReceiver) {
	        return null;
	    }
	    
	    // 3. 읽음 처리: 쪽지가 미확인(N) 상태이고, 현재 사용자가 '수신자'일 경우에만 처리
	    if (detail.getIsRead().equals("N") && isReceiver) {
	        messageDao.updateReadTime(messageNo); 
	        detail.setIsRead("Y"); 
	    }
	    
	    // 발신자/수신자 권한을 가진 사용자에게만 detail 반환
	    return detail;
	}
	
	//모두 읽기
	@Transactional
	public void readAllMessages(long receiverNo) {
	    messageDao.updateAllReadTime(receiverNo);
	}

	/**
	 * 2-2. 미확인 알림 개수 조회 (헤더 알림 아이콘용)
	 */
	public int countUnreadAlerts(long memberNo) {
		Long countResult = messageDao.countUnreadAlerts(memberNo);
		return countResult != null ? countResult.intValue() : 0;
	}
    
    /**
     * 2-3. 헤더 드롭다운용 미확인 쪽지 목록 조회 (최신 5개 등)
     */
	public List<MessageDto> getUnreadListForHeader(long memberNo) {
	    return messageDao.selectUnreadList(memberNo);
	}

	/**
	 * 2-4. 수신함 목록 조회 (페이지네이션 + 필터링 + 검색)
	 */
	@Transactional
	public PageVO<MessageDto> getReceivedListByPaging(PageVO<MessageDto> pageVO, Map<String, Object> paramMap, long memberNo) {

	    // 1. DAO 호출 파라미터 준비: 회원 번호와 페이징 정보 추가
	    paramMap.put("memberNo", memberNo);
	    // PageVO의 현재 페이지 정보를 Map에 추가 (DAO는 Map만 받기 때문)
	    paramMap.put("page", pageVO.getPage()); 
	    paramMap.put("size", pageVO.getSize()); 
	    
	    // 2. 검색 및 필터링된 전체 개수 조회
	    long count = messageDao.countReceived(paramMap); 
	    pageVO.setDataCount((int) count);

	    // 3. 목록 조회를 위한 페이징 정보 계산 및 Map에 추가
	    paramMap.put("begin", pageVO.getBegin());
	    paramMap.put("end", pageVO.getEnd());

	    // 4. 목록 조회 및 PageVO에 설정
	    List<MessageDto> list = messageDao.selectReceivedListByPaging(paramMap);
	    pageVO.setList(list);

	    return pageVO;
	}


	/**
	 * 2-5. 발신함 목록 조회 (페이지네이션 + 필터링 + 검색)
	 */
	@Transactional
	public PageVO<MessageDto> getSentListByPaging(PageVO<MessageDto> pageVO, Map<String, Object> paramMap, long memberNo) { 

	    // 1. DAO 호출 파라미터 준비: 회원 번호와 페이징 정보 추가
	    paramMap.put("memberNo", memberNo);
	    // PageVO의 현재 페이지 정보를 Map에 추가
	    paramMap.put("page", pageVO.getPage()); 
	    paramMap.put("size", pageVO.getSize()); 
	    
	    // 2. 검색 및 필터링된 전체 개수 조회
	    long count = messageDao.countSent(paramMap); 
	    pageVO.setDataCount((int) count);

	    // 3. 목록 조회를 위한 페이징 정보 계산 및 Map에 추가
	    // PageVO의 계산 로직을 통해 begin/end가 설정되었다고 가정
	    paramMap.put("begin", pageVO.getBegin());
	    paramMap.put("end", pageVO.getEnd());

	    // 4. 목록 조회 및 PageVO에 설정
	    List<MessageDto> list = messageDao.selectSentListByPaging(paramMap);
	    pageVO.setList(list);

	    return pageVO;
	}
	
	// ----------------------------------------------------
	// 3. 메시지 삭제 (DELETE/UPDATE) 기능
	// ----------------------------------------------------

	/**
	 * 3-1. 수신자 삭제 처리 (실제 DB 삭제 대신 플래그 업데이트)
	 */
	public boolean deleteMessageByReceiver(long messageNo) {
		return messageDao.updateReceiverDelete(messageNo);
	}

	/**
	 * 3-2. 발신자 삭제 처리 (실제 DB 삭제 대신 플래그 업데이트)
	 */
	public boolean deleteMessageBySender(long messageNo) {
		return messageDao.updateSenderDelete(messageNo);
	}
}