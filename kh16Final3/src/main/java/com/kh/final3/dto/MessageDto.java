package com.kh.final3.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageDto {
	private Integer messageNo; // 쪽지 번호 (PK)
	private Long senderNo; // 발신자 회원 번호 (FK)  <-- messageSender -> senderNo로 변경
	private Long receiverNo; // 수신자 회원 번호 (FK) <-- messageReceiver -> receiverNo로 변경
	private String content; // 쪽지 내용           <-- messageContent -> content로 변경
	private String type; // 쪽지 유형             <-- messageType -> type으로 변경
	
	private Timestamp sentTime; // 발신 시각      <-- messageSentTime -> sentTime으로 변경
	private Timestamp readTime; // 수신 확인 시각 <-- messageReadTime -> readTime으로 변경
	
	private String isRead; // 수신 확인 여부 ('Y', 'N') <-- messageIsRead -> isRead로 변경
	private String senderDeleted; // 발신자 삭제 여부 ('Y', 'N')
	private String receiverDeleted; // 수신자 삭제 여부 ('Y', 'N')
	
	private String url; // 알림 클릭시 이동경로

	
	// 1. 판매자 문의 시 사용할 상품 번호 (DB 컬럼 아님, 로직 처리용)
	private Integer productNo;
	
	// 2. 목록/상세 조회 시 필요한 발신자/수신자 닉네임
	// DTO 필드명을 변경해도 이 필드들은 그대로 유지합니다.
	private String senderNickname; 
	private String receiverNickname; 
}