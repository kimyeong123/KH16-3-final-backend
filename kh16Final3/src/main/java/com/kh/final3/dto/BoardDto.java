package com.kh.final3.dto;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class BoardDto {
	private Long boardNo;
    private String title; // boardTitle -> title 로 변경
    private Long writerNo; // boardWriter -> writerNo 로 변경
    private String content; // boardContent -> content 로 변경
    
    private Timestamp writeTime, editTime; // boardWtime, boardEtime -> writeTime, editTime 로 변경
    private Integer readCount; // boardRead -> readCount 로 변경
    private String type; // boardType -> type 로 변경
    
    // DB 테이블에 없는 조인 필드는 그대로 유지
    private String writerNickname; 
    
    // 파일 목록 담을 필드 추가
    private List<AttachmentDto> attachmentList;
}