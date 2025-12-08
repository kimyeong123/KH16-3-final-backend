package com.kh.final3.dto;

import java.sql.Timestamp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class BoardDto {
	private Long boardNo;
    private String boardTitle;
    private Long boardWriter; //memberNo
    private String boardContent;
    private Timestamp boardWtime, boardEtime;
    private Integer boardRead;
    private String boardType;
    
    private String writerNickname;
}
