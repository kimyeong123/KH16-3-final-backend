package com.kh.final3.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryDto {
    private Long categoryCode;
    private Long parentCode; // 상위 카테고리 코드 (최상위면 NULL 가능)
    private String name;
    private Integer depth;
}