//package com.kh.final3.restcontroller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestAttribute;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.kh.final3.dto.CommentDto;
//import com.kh.final3.error.UnauthorizationException;
//import com.kh.final3.service.CommentService;
//import com.kh.final3.vo.TokenVO; 
//
//@CrossOrigin
//@RestController
//@RequestMapping("/rest/comment") // REST API 관례에 따라 경로를 /rest/comment로 설정
//public class CommentRestController {
//    
//	@Autowired
//	private CommentService commentService; 
//	
//	@Autowired
//	private TokenVO tokenVO;
//	
//	/**
//	 * 1. 댓글 등록 (POST)
//	 */
//	@PostMapping("/")
//	public CommentDto insert(@RequestBody CommentDto commentDto,
//			@RequestAttribute TokenVO tokenVO
//			) {
//        
//		long memberNo = tokenVO.getMemberNo();
//        
//		if (memberNo == 0) { 
//	        throw new UnauthorizationException("로그인 후 댓글 작성이 가능합니다.");
//		}
//		return commentService.insert(commentDto, memberNo);
//	}	
//	
//	/**
//	 * 2. 댓글 목록 조회 (GET)
//	 * - 특정 게시글의 모든 댓글 조회
//	 */
//	@GetMapping("/{boardNo}")
//	public List<CommentDto> list(@PathVariable long boardNo) {
//		return commentService.selectList(boardNo); 
//	}
//	
//	/**
//	 * 3. 댓글 수정 (PATCH)
//	 */
//	@PatchMapping("/{commentNo}")
//	public ResponseEntity<?> edit(
//				@PathVariable long commentNo,
//				@RequestBody CommentDto commentDto,
//				@RequestAttribute TokenVO tokenVO
//				) {
//        
//        commentDto.setCommentNo(commentNo); 
//        long memberNo = tokenVO.getMemberNo();
//        String loginLevel = tokenVO.getLoginLevel();
////        
//		commentService.update(commentDto, memberNo, loginLevel); 
//        
//        return ResponseEntity.ok().build();
//	}
//	
//	/**
//	 * 4. 댓글 삭제 (DELETE)
//	 * - softDelete 처리 (실제 DB에서 삭제하는 대신 상태만 변경)
//	 */
//	@DeleteMapping("/{commentNo}")
//	public ResponseEntity<?> delete(
//				@PathVariable long commentNo,
//				@RequestAttribute TokenVO tokenVO
//			) {
//        
//		long memberNo = tokenVO.getMemberNo();
//		String loginLevel = tokenVO.getLoginLevel();
//        
//		commentService.softDelete(commentNo, memberNo, loginLevel);
//        
//        return ResponseEntity.ok().build(); // 200 OK 반환
//	}
//    
//}