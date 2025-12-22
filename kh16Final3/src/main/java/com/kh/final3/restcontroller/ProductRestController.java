package com.kh.final3.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.kh.final3.dto.AttachmentDto;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.service.AttachmentService;
import com.kh.final3.service.ProductService;
import com.kh.final3.service.TokenService;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.ProductListVO;
import com.kh.final3.vo.PurchaseListVO; // [추가됨]
import com.kh.final3.vo.SalesListVO;
import com.kh.final3.vo.TokenVO;

import io.jsonwebtoken.ExpiredJwtException;

@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AttachmentService attachmentService;

    private static final String CAT_PRODUCT = "PRODUCT";

    private TokenVO requireToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        }
        try {
            return tokenService.parse(authorization);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
        }
    }

    private void requireOwner(long productNo, String authorization) {
        TokenVO tokenVO = requireToken(authorization);
        Long loginMemberNo = tokenVO.getMemberNo();
        long sellerNo = productService.getSellerNo(productNo);
        if (sellerNo == 0L || sellerNo != loginMemberNo.longValue()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다");
        }
    }

    // 1. 마감 임박 상품 4개
    @GetMapping("/main/closing-soon")
    public List<ProductDto> closingSoon() {
        return productService.getClosingSoon();
    }

    // 2. 구매관리
    @GetMapping("/purchase")
    public List<PurchaseListVO> purchaseList(@RequestHeader(value="Authorization", required=false) String authorization) {
        TokenVO tokenVO = requireToken(authorization);
        return productService.getPurchaseList(tokenVO.getMemberNo());
    }

    @PostMapping("/")
    public ProductDto insert(@RequestBody ProductDto productDto, @RequestHeader(value="Authorization", required=false) String authorization) {
    	if (productDto.getCurrentPrice() == null) 
    	    productDto.setCurrentPrice(productDto.getStartPrice());
    	
        TokenVO tokenVO = requireToken(authorization);
        return productService.create(productDto, tokenVO.getMemberNo());
    }

    //구매관리
    @GetMapping("/purchase")
    public PageVO<PurchaseListVO> purchaseList(
            @RequestHeader(value="Authorization", required=false) String authorization,
            PageVO<PurchaseListVO> pageVO) {

        TokenVO tokenVO = requireToken(authorization);
        pageVO.setLoginNo(tokenVO.getMemberNo());

        return productService.getPurchaseList(pageVO);
    }
    
    @GetMapping("/sales")
    public PageVO<SalesListVO> salesList(
            @RequestHeader(value="Authorization", required=false) String authorization,
            PageVO<SalesListVO> pageVO) {

   //20개씩받기
    @GetMapping("/auction/list")
    public Map<String, Object> list(@ModelAttribute PageVO vo) {
        return productService.getAuctionList(vo);
    }
    
    // (혹시 모를 구버전 호환용 - page 경로도 살려둠)
    @GetMapping("/auction/page/{page}")
    public Map<String, Object> listByPagePath(@PathVariable int page, @ModelAttribute PageVO vo) {
    	vo.setPage(page);
    	return productService.getAuctionList(vo);
    }

    @GetMapping("/{productNo}")
    public ProductDto detail(@PathVariable long productNo) {
        return productService.get(productNo);
    }

    @DeleteMapping("/{productNo}")
    public void delete(@PathVariable long productNo, @RequestHeader(value="Authorization", required=false) String authorization) {
        requireOwner(productNo, authorization);
        attachmentService.deleteByParent(CAT_PRODUCT, productNo);
        productService.delete(productNo);
    }

    @PutMapping("/{productNo}")
    public void edit(@PathVariable long productNo, @RequestBody ProductDto productDto, @RequestHeader(value="Authorization", required=false) String authorization) {
        requireOwner(productNo, authorization);
        productService.edit(productNo, productDto);
    }

    @PatchMapping("/{productNo}")
    public void update(@PathVariable long productNo, @RequestBody ProductDto productDto, @RequestHeader(value="Authorization", required=false) String authorization) {
        requireOwner(productNo, authorization);
        productService.patch(productNo, productDto);
    }

    @GetMapping("/{productNo}/attachments")
    public List<AttachmentDto> attachmentList(@PathVariable long productNo) {
        return attachmentService.listByParent(CAT_PRODUCT, productNo);
    }

    @PostMapping(value="/{productNo}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AttachmentDto> addAttachments(
        @PathVariable long productNo,
        @RequestParam("files") List<MultipartFile> files,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) throws Exception {
        requireOwner(productNo, authorization);
        List<AttachmentDto> result = new ArrayList<>();
        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;
            result.add(attachmentService.saveProduct(f, productNo));
        }
        return result;
    }

    @PutMapping(value="/{productNo}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AttachmentDto> replaceAttachments(
        @PathVariable long productNo,
        @RequestParam("files") List<MultipartFile> files,
        @RequestHeader(value="Authorization", required=false) String authorization
    ) throws Exception {
        requireOwner(productNo, authorization);
        attachmentService.deleteByParent(CAT_PRODUCT, productNo);
        List<AttachmentDto> result = new ArrayList<>();
        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;
            result.add(attachmentService.saveProduct(f, productNo));
        }
        return result;
    }
    
    @DeleteMapping("/{productNo}/attachments/{attachmentNo}")
    public void deleteAttachment(@PathVariable long productNo, @PathVariable long attachmentNo, @RequestHeader(value="Authorization", required=false) String authorization) {
        requireOwner(productNo, authorization);
        attachmentService.delete(attachmentNo);
    }
    
    @GetMapping("/{productNo}/image")
    public Long productThumbnail(@PathVariable long productNo) {
        return attachmentService.findProductThumbnailAttachmentNo(productNo);
    }
}