package com.kh.final3.restcontroller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ByteArrayResource> download(@PathVariable int attachmentNo) throws IOException {
        AttachmentDto dto = attachmentService.get(attachmentNo);
        ByteArrayResource resource = attachmentService.load(attachmentNo);

        String contentType = dto.getMediaType() != null ? dto.getMediaType() : "application/octet-stream";
        String encodedName = URLEncoder.encode(dto.getOriginalName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedName + "\"")
                .contentLength(dto.getFileSize())
                .body(resource);
    }
}
