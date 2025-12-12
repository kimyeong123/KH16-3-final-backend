package com.kh.final3.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// PATCHMapping은 현재 제거된 상태로 가정합니다.
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dto.BoardDto;
import com.kh.final3.error.TargetNotfoundException; // 현재 미사용
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.BoardService; 
import com.kh.final3.service.QnaService;
import com.kh.final3.vo.PageVO; // HEAD 버전에서 추가된 import
import com.kh.final3.vo.TokenVO; 

@CrossOrigin
@RestController
@RequestMapping("/qna")
public class QnaRestController {
	
	@Autowired
	private QnaService qnaService;
    
    // 상세 조회는 BoardService의 공통 로직을 사용 (조회수 증가 기능 포함)
    @Autowired
    private BoardService boardService; 
	
	/**
	 * 1. 문의 등록 (POST /qna)
	 */
	@PostMapping("/list")
	public BoardDto insert(
            @RequestPart BoardDto boardDto,
            @RequestPart(required = false) List<MultipartFile> attachments,
			@RequestAttribute TokenVO tokenVO
	) throws IllegalStateException, IOException {
        
		long memberNo = tokenVO.getMemberNo();
		String loginLevel = tokenVO.getLoginLevel();
        
		if (memberNo == 0) { 
	        throw new UnauthorizationException("로그인 후 문의 작성이 가능합니다.");
		}
        
		return qnaService.insert(boardDto, attachments, loginLevel, memberNo); 
	}	
	
	/**
	 * 2. 문의 목록 조회 (GET /rest/qna) - 페이지네이션 지원 버전 채택
	 */
	@GetMapping("/")
	public ResponseEntity<PageVO<BoardDto>> list(PageVO<BoardDto> pageVO) {
        // PageVO는 클라이언트의 URL 쿼리 파라미터 (page, size, column, keyword)에 자동 바인딩됩니다.
        PageVO<BoardDto> resultVO = qnaService.selectList(pageVO);
        
        // 결과 VO와 HTTP 200 OK 응답을 반환
		return ResponseEntity.ok(resultVO); 
	}
    
    /**
	 * 3. 문의 상세 조회 (GET /rest/qna/{boardNo})
	 */
	@GetMapping("/{boardNo}")
	public BoardDto detail(@PathVariable long boardNo) {
        return boardService.selectOne(boardNo);
	}
	
    /**
	 * 4. 문의 삭제 (DELETE /rest/qna/{boardNo})
	 */
	@DeleteMapping("/{boardNo}")
	public ResponseEntity<?> delete(
				@PathVariable long boardNo,
				@RequestAttribute TokenVO tokenVO
			) {
		long memberNo = tokenVO.getMemberNo();
		String loginLevel = tokenVO.getLoginLevel();
        
		qnaService.delete(boardNo, loginLevel, memberNo);
        
        return ResponseEntity.ok().build(); 
	}
	
}