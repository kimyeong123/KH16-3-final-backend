package com.kh.final3.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // 💡 ResponseEntity 사용을 위해 추가
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping; // 💡 DELETE 사용
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// PATCHMapping이 제거되었음
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dto.BoardDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.BoardService; // 상세 조회를 위한 BoardService 주입 (공통 로직)
import com.kh.final3.service.QnaService;
import com.kh.final3.vo.TokenVO; 

@CrossOrigin
@RestController
@RequestMapping("/rest/qna")
public class QnaRestController {
	
	@Autowired
	private QnaService qnaService;
    
    // 💡 상세 조회는 BoardService의 공통 로직을 사용 (조회수 증가 기능 포함)
    @Autowired
    private BoardService boardService; 
	
	/**
	 * 1. 문의 등록 (POST /rest/qna)
	 */
	@PostMapping
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
	 * 2. 문의 목록 조회 (GET /rest/qna)
	 */
	@GetMapping
	public List<BoardDto> list() {
		return qnaService.selectQnaList(); 
	}
    
    /**
	 * 3. 문의 상세 조회
	 */
	@GetMapping("/{boardNo}")
	public BoardDto detail(@PathVariable long boardNo) {
        // 상세 조회는 공지사항과 동일한 BoardService의 로직을 사용합니다.
        return boardService.selectOne(boardNo);
	}
	
    /**
	 * 4. 문의 삭제 
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