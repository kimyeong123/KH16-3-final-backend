package com.kh.final3.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.MessageDao;
import com.kh.final3.dto.MessageDto;

@Service
public class MessageService {
	
	@Autowired
	private MessageDao messageDao;
	
	//쪽지 전송
	@Transactional
	public boolean sendMessage(MessageDto messageDto) {
		
		// 1. 시퀀스 번호 발급
        int messageNo = messageDao.sequence();
        messageDto.setMessageNo(messageNo);

        // 2. 쪽지 등록 (DAO의 insert 호출)
        boolean insertResult = messageDao.insert(messageDto);

        return insertResult;
	}
	
	// 수신함 목록 조회
	public List<MessageDto> getReceivedList(long memberNo) {
        return messageDao.selectReceivedList(memberNo);
    }
	
	// 발신함 목록 조회
	public List<MessageDto> getSentList(long memberNo) {
		return messageDao.selectSentList(memberNo);
	}
	
	// 쪽지 상세 조회 및 읽음 처리
	@Transactional
	public MessageDto getMessageDetailAndRead(Integer messageNo) {
		
		// 1. 쪽지 상세 정보 조회
        MessageDto detail = messageDao.selectOne(messageNo);
        
        // 2. 미확인 상태일 경우만 읽음 처리 업데이트 (수정)
        if (detail != null && detail.getIsRead().equals("N")) {
            messageDao.updateReadTime(messageNo);
            
            // 업데이트 후 DTO 상태도 'Y'로 변경하여 컨트롤러에 반환 (선택 사항)
            detail.setIsRead("Y"); 
        }
        return detail;
    }
	
	// 수신자 삭제 처리
	public boolean deleteMessageByReceiver(Integer messageNo) {
		return messageDao.updateReceiverDelete(messageNo);
	}
	
	// 발신자 삭제 처리
	public boolean deleteMessageBySender(Integer messageNo) {
        return messageDao.updateSenderDelete(messageNo);
    }
	
	// 미확인 알림 개수 조회
	public int countUnreadAlerts(long memberNo) {
		return messageDao.countUnreadAlerts(memberNo);
	}
	
	// 타입별 수신함 목록 조회
	public List<MessageDto> getReceivedListByTypes(Map<String, Object> paramMap) {
		return messageDao.selectReceivedListByTypes(paramMap);
	}

	public void sendNotification(long receiverNo, String content, String url) {
		
		
		long systemSenderNo = 0;
		
		// 1. MessageDto 생성
        MessageDto messageDto = MessageDto.builder()
                .receiverNo(receiverNo)
                .senderNo(systemSenderNo)
                .content(content)
                // MessageDto에 URL 필드가 있다면 추가 (없다면 필드를 추가해야 함)
                // .url(url) 
                .build();
        
        // 2. 시퀀스 번호 발급
        int messageNo = messageDao.sequence();
        messageDto.setMessageNo(messageNo);

        // 3. 쪽지(알림) 등록
        boolean insertResult = messageDao.insert(messageDto);
        
        // 등록 실패 시 예외 처리 (선택 사항)
        if (!insertResult) {
            // throw new RuntimeException("알림 메시지 등록 실패");
		
        }
	}
}
	
	
	