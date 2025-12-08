package com.kh.final3.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentDto {
	
	// 댓글 정보
	private Long commentNo; 			// 댓글 번호 (PK)
	private Long commentBoardNo; 		// 댓글이 달린 게시글 번호 (FK to BOARD)
	private Long commentWriter; 		// 댓글 작성자 ID (FK to MEMBER)
	private String commentContent; 		// 댓글 내용
	private Date commentTime; 			// 댓글 작성 시각
    private Date commentEditDate;       // 댓글 수정 시각 (추가됨)
	private String commentStatus;       // 댓글 상태 ('y', 'n') (추가됨)
	
    private String writerNickname;      // 작성자 닉네임
}