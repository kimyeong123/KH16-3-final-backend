package com.kh.final3.restcontroller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.dto.AttachmentDto;
import com.kh.final3.service.AttachmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attachment")
@CrossOrigin
@RequiredArgsConstructor
public class AttachmentRestController {

    private final AttachmentService attachmentService;

    @GetMapping("/{attachmentNo}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable long attachmentNo) {
        try {
            AttachmentDto dto = attachmentService.get(attachmentNo);
            ByteArrayResource resource = attachmentService.load(attachmentNo);

            String contentType = dto.getMediaType() != null ? dto.getMediaType() : "application/octet-stream";
            String encodedName = URLEncoder.encode(dto.getOriginalName(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedName + "\"")
                    .contentLength(dto.getFileSize())
                    .body(resource);
        } catch (Exception e) {
            // [수정] 파일이 없거나 에러 발생 시 500 대신 404 반환
            return ResponseEntity.notFound().build();
        }
    }
}