package com.kh.final3.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dto.BoardDto;
import com.kh.final3.service.BoardService;
import com.kh.final3.vo.TokenVO;

@CrossOrigin
@RestController
@RequestMapping("/board")
public class BoardRestController {
    
	@Autowired
	private BoardService boardService;
	
	@PostMapping("/")
	public void insert(@RequestBody BoardDto boardDto,
								@RequestPart List<MultipartFile> attachments,
								@RequestAttribute TokenVO tokenVO) {
		String loginLevel = tokenVO.getLoginLevel();
		long memberNo = tokenVO.getMemberNo();
		boardService.insert(boardDto, attachments, loginLevel, memberNo);
	}
	
	@GetMapping("/")
	public List<BoardDto> list() {
		return boardService.selectNoticeList(); 
	}
	
	@GetMapping("/{boardNo}")
	public BoardDto detail(@PathVariable long boardNo) {
		BoardDto boardDto = boardService.selectOne(boardNo);
		return boardDto; 
	}
	
	@DeleteMapping("/{boardNo}")
	public void delete(@PathVariable long boardNo,
								@RequestAttribute TokenVO tokenVO
			) {
		long memberNo = tokenVO.getMemberNo(); 
	    String loginLevel = tokenVO.getLoginLevel();
		boardService.delete(boardNo, loginLevel, memberNo);
	}
	
	@PatchMapping("/{boardNo}")
	public void edit(@PathVariable long boardNo,
								@RequestBody BoardDto boardDto,
								@RequestAttribute TokenVO tokenVO
								) {
        boardDto.setBoardNo(boardNo);
        long memberNo = tokenVO.getMemberNo(); 
	    String loginLevel = tokenVO.getLoginLevel();
		boardService.update(boardDto, memberNo, loginLevel); 
	}
}