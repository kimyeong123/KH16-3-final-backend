package com.kh.final3.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.dto.CommentDto;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.CommentService;
import com.kh.final3.vo.TokenVO; 

@CrossOrigin
@RestController
@RequestMapping("/comment") 
public class CommentRestController {
    
	@Autowired
	private CommentService commentService; 
	
	/**
	 * 1. 댓글 등록 (POST)
	 */
	@PostMapping("/write")
	public CommentDto insert(@RequestBody CommentDto commentDto,
			@RequestAttribute(required = false) TokenVO tokenVO
			) {
        
		if (tokenVO == null) {
	        // 토큰이 없으므로 인증이 필요한 요청임을 가정하고 예외 처리
	        throw new UnauthorizationException("유효한 인증 정보가 없습니다. 로그인 후 댓글 작성이 가능합니다.");
	        // 또는 디버깅을 위해 임시 memberNo를 넣을 수도 있습니다.
	    }
		
		long memberNo = tokenVO.getMemberNo();
		String loginLevel = tokenVO.getLoginLevel();
        
		if (memberNo == 0) { 
	        throw new UnauthorizationException("로그인 후 댓글 작성이 가능합니다.");
		}
		
		return commentService.insert(commentDto, memberNo, loginLevel, commentDto.getProductNo());
	}	
	
	/**
	 * 2. 댓글 목록 조회 (GET)
	 * - 특정 게시글의 모든 댓글 조회
	 */
	@GetMapping("/list/{boardNo}")
	public List<CommentDto> list(@PathVariable long boardNo) {
		return commentService.selectList(boardNo); 
	}
	
	/**
	 * 3. 댓글 수정 (PATCH)
	 */
	@PatchMapping("/edit/{commentNo}")
	public ResponseEntity<?> edit(
				@PathVariable long commentNo,
				@RequestBody CommentDto commentDto,
				@RequestAttribute TokenVO tokenVO
				) {
        
        commentDto.setCommentNo(commentNo); 
        long memberNo = tokenVO.getMemberNo();
        String loginLevel = tokenVO.getLoginLevel();
//        
		commentService.update(commentDto, memberNo, loginLevel); 
        
        return ResponseEntity.ok().build();
	}
	
	/**
	 * 4. 댓글 삭제 (DELETE)
	 * - softDelete 처리 (실제 DB에서 삭제하는 대신 상태만 변경)
	 */
	@DeleteMapping("/delete/{commentNo}")
	public ResponseEntity<?> delete(
				@PathVariable long commentNo,
				@RequestAttribute TokenVO tokenVO
			) {
        
		long memberNo = tokenVO.getMemberNo();
		String loginLevel = tokenVO.getLoginLevel();
        
		commentService.softDelete(commentNo, memberNo, loginLevel);
        
        return ResponseEntity.ok().build();
	}
    
}
