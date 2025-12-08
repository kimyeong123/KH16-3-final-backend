package com.kh.final3.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageDto {
	private Integer messageNo; // 쪽지 번호 (PK)
	private Integer messageSender; // 발신자 회원 번호 (FK)
	private Integer messageReceiver; // 수신자 회원 번호 (FK)
	private String messageContent; // 쪽지 내용
	
	private Timestamp messageSentTime; // 발신 시각
	private Timestamp messageReadTime; // 수신 확인 시각
	
	private String messageIsRead; // 수신 확인 여부 ('Y', 'N')
	private String messageSenderDeleted; // 발신자 삭제 여부 ('Y', 'N')
	private String messageReceiverDeleted; // 수신자 삭제 여부 ('Y', 'N')

    
    // 1. 판매자 문의 시 사용할 상품 번호 (DB 컬럼 아님, 로직 처리용)
    private Integer productNo; 
	
	// 2. 목록/상세 조회 시 필요한 발신자/수신자 닉네임
	private String senderNickname; // 발신자 닉네임 (MEMBER 테이블 조인 필요)
	private String receiverNickname; // 수신자 닉네임 (MEMBER 테이블 조인 필요)
}