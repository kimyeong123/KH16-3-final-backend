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
import org.springframework.web.bind.annotation.RequestParam;
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
    @PostMapping("/")
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
     * 2. 문의 목록 조회 (GET /qna/list)
     * 사용자가 /qna/list로 접속할 때 에러가 나지 않도록 주소를 명시합니다.
     */
    @GetMapping("/list")
    public ResponseEntity<PageVO<BoardDto>> list(
            @RequestParam(defaultValue = "QNA") String type,
            PageVO<BoardDto> pageVO) {
        PageVO<BoardDto> resultVO = qnaService.selectList(pageVO, type); 
        return ResponseEntity.ok(resultVO); 
    }

    /**
     * 3. 문의 상세 조회 (GET /qna/{boardNo})
     * 상세 조회는 목록(/list) 뒤에 오거나 상세 경로를 따로 두는 것이 안전합니다.
     */
    @GetMapping("/detail/{boardNo}") // 경로 충돌 방지를 위해 /detail 추가 추천
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
	
	@GetMapping("/my")
	public ResponseEntity<PageVO<BoardDto>> myList(
	        PageVO<BoardDto> pageVO,
	        @RequestAttribute TokenVO tokenVO) {
	    
	    // 1. 로그인 확인
	    if (tokenVO == null) throw new UnauthorizationException();

	    // 2. 서비스 호출 (내 번호 전달)
	    // 서비스에서 boardDao.selectCountByMember 와 selectListByMember 를 호출하도록 구현
	    PageVO<BoardDto> result = qnaService.selectMyList(pageVO, tokenVO.getMemberNo());
	    
	    return ResponseEntity.ok(result);
	}
	
}