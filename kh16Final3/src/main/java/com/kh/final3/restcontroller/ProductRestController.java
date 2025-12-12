package com.kh.final3.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.kh.final3.dto.AttachmentDto;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.service.AttachmentService;
import com.kh.final3.service.ProductService;
import com.kh.final3.service.TokenService;
import com.kh.final3.vo.ProductListVO;
import com.kh.final3.vo.TokenVO;

import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AttachmentService attachmentService;

    private static final String CAT_PRODUCT = "PRODUCT";

    // ===== 공통: 토큰 검사 =====
    private TokenVO requireToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        }
        try {
            return tokenService.parse(authorization);
        }
        catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
        }
    }

    // ===== 상품 등록 =====
    @PostMapping("/")
    public ProductDto insert(
        @RequestBody ProductDto productDto,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) {
        TokenVO tokenVO = requireToken(authorization);

        Long loginMemberNo = tokenVO.getMemberNo();
        if (loginMemberNo == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰에 memberNo가 없습니다");
        }

        return productService.create(productDto, loginMemberNo);
    }

    // ===== 상품 목록 =====
    @GetMapping("/")
    public List<ProductDto> list() {
        return productService.getList();
    }

    // ===== 상품 상세 =====
    @GetMapping("/{productNo}")
    public ProductDto detail(@PathVariable long productNo) {
        return productService.get(productNo);
    }

    // ===== 상품 삭제 =====
    @DeleteMapping("/{productNo}")
    public void delete(
        @PathVariable long productNo,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) {
        requireToken(authorization);

        attachmentService.deleteByParent(CAT_PRODUCT, (int)productNo);
        productService.delete(productNo);
    }

    // ===== 상품 전체 수정 =====
    @PutMapping("/{productNo}")
    public void edit(
        @PathVariable long productNo,
        @RequestBody ProductDto productDto,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) {
        requireToken(authorization);
        productService.edit(productNo, productDto);
    }

    // ===== 상품 부분 수정 =====
    @PatchMapping("/{productNo}")
    public void update(
        @PathVariable long productNo,
        @RequestBody ProductDto productDto,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) {
        requireToken(authorization);
        productService.patch(productNo, productDto);
    }

    // ===== 상품 페이징 =====
    @GetMapping("/page/{page}")
    public ProductListVO listByPaging(@PathVariable int page) {
        return productService.getPaged(page);
    }

    // 1) 첨부 목록 조회
    @GetMapping("/{productNo}/attachments")
    public List<AttachmentDto> attachmentList(@PathVariable long productNo) {
        return attachmentService.listByParent(CAT_PRODUCT, (int)productNo);
    }

    // 2) 첨부 추가
    @PostMapping(value="/{productNo}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AttachmentDto> addAttachments(
        @PathVariable long productNo,
        @RequestPart("files") List<MultipartFile> files,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) throws Exception {
        requireToken(authorization);

        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드 파일이 없습니다");
        }

        List<AttachmentDto> result = new ArrayList<>();
        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;
            result.add(attachmentService.saveProduct(f, productNo));
        }
        return result;
    }

    // 3) 첨부 교체
    @PutMapping(value="/{productNo}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AttachmentDto> replaceAttachments(
        @PathVariable long productNo,
        @RequestPart("files") List<MultipartFile> files,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) throws Exception {
        requireToken(authorization);

        attachmentService.deleteByParent(CAT_PRODUCT, (int)productNo);

        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<AttachmentDto> result = new ArrayList<>();
        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;
            result.add(attachmentService.saveProduct(f, productNo));
        }
        return result;
    }

    // 4) 첨부 단건 삭제
    @DeleteMapping("/{productNo}/attachments/{attachmentNo}")
    public void deleteAttachment(
        @PathVariable long productNo,
        @PathVariable int attachmentNo,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) {
        requireToken(authorization);
        attachmentService.delete(attachmentNo);
    }
}
