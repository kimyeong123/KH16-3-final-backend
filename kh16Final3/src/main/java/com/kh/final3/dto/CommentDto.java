package com.kh.final3.dto;

import java.util.Date; // Date 타입을 Timestamp로 변경하는 것이 더 정확할 수 있습니다.

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentDto {
	
	// 댓글 정보 (DTO 필드명을 DB 컬럼명에 가깝게 변경)
	private Long commentNo; 			// comment_no (PK)
	private Long boardNo; 			    // commentBoardNo -> boardNo (FK)
	private Long writerNo; 			    // commentWriter -> writerNo (FK)
	private String content; 		    // commentContent -> content
	private Date createdTime; 		    // commentTime -> createdTime
    private Date editTime; 		        // commentEditDate -> editTime
	private String status; 		        // commentStatus -> status ('Y'/'N')
	
    private String writerNickname;      // 작성자 닉네임 (DB 컬럼 아님)
}