package com.kh.final3.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

	private void requireOwner(long productNo, String authorization) {
		TokenVO tokenVO = requireToken(authorization);

		Long loginMemberNo = tokenVO.getMemberNo();
		if (loginMemberNo == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰에 memberNo가 없습니다");
		}

		long sellerNo = productService.getSellerNo(productNo);
		if (sellerNo == 0L || sellerNo != loginMemberNo.longValue()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다");
		}
	}

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

	@GetMapping("/my/page/{page}")
	public ProductListVO myListByPaging(
		@PathVariable int page,
		@RequestHeader(value="Authorization", required=false) String authorization
	) {
		TokenVO tokenVO = requireToken(authorization);

		Long loginMemberNo = tokenVO.getMemberNo();
		if (loginMemberNo == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰에 memberNo가 없습니다");
		}

		return productService.getMyPaged(page, loginMemberNo);
	}

	@GetMapping("/auction/page/{page}")
	public ProductListVO auctionListByPaging(@PathVariable int page) {
		return productService.getAuctionPaged(page);
	}

	@GetMapping("/{productNo}")
	public ProductDto detail(@PathVariable long productNo) {
		return productService.get(productNo);
	}

	@DeleteMapping("/{productNo}")
	public void delete(
		@PathVariable long productNo,
		@RequestHeader(value="Authorization", required=false) String authorization
	) {
		requireOwner(productNo, authorization);
		attachmentService.deleteByParent(CAT_PRODUCT, (int)productNo);
		productService.delete(productNo);
	}

	@PutMapping("/{productNo}")
	public void edit(
		@PathVariable long productNo,
		@RequestBody ProductDto productDto,
		@RequestHeader(value="Authorization", required=false) String authorization
	) {
		requireOwner(productNo, authorization);
		productService.edit(productNo, productDto);
	}

	@PatchMapping("/{productNo}")
	public void update(
		@PathVariable long productNo,
		@RequestBody ProductDto productDto,
		@RequestHeader(value="Authorization", required=false) String authorization
	) {
		requireOwner(productNo, authorization);
		productService.patch(productNo, productDto);
	}

	@GetMapping("/{productNo}/attachments")
	public List<AttachmentDto> attachmentList(@PathVariable long productNo) {
		return attachmentService.listByParent(CAT_PRODUCT, (int)productNo);
	}

	@PostMapping(value="/{productNo}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<AttachmentDto> addAttachments(
		@PathVariable long productNo,
		@RequestPart("files") List<MultipartFile> files,
		@RequestHeader(value="Authorization", required=false) String authorization
	) throws Exception {
		requireOwner(productNo, authorization);

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

	@PutMapping(value="/{productNo}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<AttachmentDto> replaceAttachments(
		@PathVariable long productNo,
		@RequestPart("files") List<MultipartFile> files,
		@RequestHeader(value="Authorization", required=false) String authorization
	) throws Exception {
		requireOwner(productNo, authorization);

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

	@DeleteMapping("/{productNo}/attachments/{attachmentNo}")
	public void deleteAttachment(
		@PathVariable long productNo,
		@PathVariable int attachmentNo,
		@RequestHeader(value="Authorization", required=false) String authorization
	) {
		requireOwner(productNo, authorization);
		attachmentService.delete(attachmentNo);
	}
}
