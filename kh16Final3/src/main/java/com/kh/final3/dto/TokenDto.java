package com.kh.final3.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TokenDto {

	private Long tokenNo;
	private String targetId;
	private String value;
	private LocalDateTime createdTime;
}
