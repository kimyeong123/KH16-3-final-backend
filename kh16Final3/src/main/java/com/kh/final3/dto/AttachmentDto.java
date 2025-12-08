package com.kh.final3.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttachmentDto {
 private Long attachmentNo;          // PK
 private String attachmentType;     // img, mp4 등 
 private String attachmentPath;     // 저장 경로
 private String attachmentCategory; // 'product', 'music' 등
 private String attachmentParent;   // 부모 PK (product_no, member_id 등 문자열로) 상품번호? 느낌
 private String attachmentOriginalName; // 사용자가 업로드한 원본 이름
 private String attachmentStoredName;   //  저장된 파일명
 private Integer attachmentSize;        // 파일 크기
 private LocalDateTime attachmentTime; // 저장 시간
}
