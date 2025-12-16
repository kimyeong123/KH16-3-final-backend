package com.kh.final3.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dto.BoardDto;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.BoardService;
import com.kh.final3.vo.TokenVO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/board")
public class BoardRestController {
    
	@Autowired
	private BoardService boardService;
	
	@PostMapping("/write")
	public void insert(@ModelAttribute BoardDto boardDto,
			@RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
								@RequestAttribute TokenVO tokenVO) {
		String loginLevel = tokenVO.getLoginLevel();
		long memberNo = tokenVO.getMemberNo();
		boardService.insert(boardDto, attachments, loginLevel, memberNo);
	}
	
	@GetMapping("/list")
	public List<BoardDto> list() {
		return boardService.selectNoticeList(); 
	}
	
	@GetMapping("/{boardNo}")
	public BoardDto detail(@PathVariable long boardNo,
										HttpServletRequest request, 
										HttpServletResponse response) {
		
		Cookie[] cookies = request.getCookies();
	    Cookie readCookie = null;
	    
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (cookie.getName().equals("readBoard")) {
	                readCookie = cookie;
	                break;
	            }
	        }
	    }
	    
	    // 2. 조회수 증가 처리
	    if (readCookie != null) {
	        // 이미 쿠키가 존재함: 해당 게시물 번호가 포함되어 있는지 확인
	        String cookieValue = readCookie.getValue();
	        if (!cookieValue.contains("[" + boardNo + "]")) {
	            // 포함되어 있지 않으면: 조회수 증가 & 쿠키 업데이트
	            boardService.updateReadCount(boardNo); // 분리된 메서드 호출
	            
	            readCookie.setValue(cookieValue + "[" + boardNo + "]");
	            readCookie.setPath("/board");
	            readCookie.setMaxAge(60 * 5); 
	            response.addCookie(readCookie);
	        }
	        // 포함되어 있으면: 아무것도 하지 않음 (중복 조회 방지)
	    } else {
	        // 쿠키가 없음: 최초 조회이므로 조회수 증가 & 신규 쿠키 생성
	        boardService.updateReadCount(boardNo); // 분리된 메서드 호출
	        
	        Cookie newCookie = new Cookie("readBoard", "[" + boardNo + "]");
	        newCookie.setPath("/board");
	        newCookie.setMaxAge(60 * 5); 
	        response.addCookie(newCookie);
	    }
		BoardDto boardDto = boardService.selectOne(boardNo);
		return boardDto; 
	}
	
	@DeleteMapping("/{boardNo}")
	public void delete(@PathVariable long boardNo,
				@RequestAttribute(required = false) TokenVO tokenVO
			) {
		
		if (tokenVO == null) {
	        throw new UnauthorizationException("로그인이 필요합니다."); 
	    }
		
		long memberNo = tokenVO.getMemberNo(); 
	    String loginLevel = tokenVO.getLoginLevel();
		boardService.delete(boardNo, loginLevel, memberNo);
	}
	
	@PatchMapping("/edit/{boardNo}")
	public void edit(@PathVariable long boardNo,
								@RequestPart BoardDto boardDto,
								@RequestAttribute TokenVO tokenVO,
								@RequestPart(required = false) List<MultipartFile> attach) {
        boardDto.setBoardNo(boardNo);
        long memberNo = tokenVO.getMemberNo(); 
	    String loginLevel = tokenVO.getLoginLevel();
		boardService.update(boardDto, memberNo, loginLevel, attach); 
	}
}