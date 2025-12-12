package com.kh.final3.dto;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private Long attachmentNo;
    private String mediaType;
    private String path;
    private String category;
    private Long parentPkNo;
    private String originalName;
    private String storedName;
    private Long fileSize;      
    private LocalDateTime createdTime;
}
