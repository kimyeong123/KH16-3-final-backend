package com.kh.final3.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.kh.final3.dto.BoardDto;
import com.kh.final3.service.BoardService; 
import com.kh.final3.error.TargetNotfoundException; 

@CrossOrigin
@RestController
@RequestMapping("/board")
public class BoardRestController {
    
	@Autowired
	private BoardService boardService; 
	
	@PostMapping("/")
	public void insert(@RequestBody BoardDto boardDto) {
//		String loginLevel = tokenVO.getLoginLevel();
//		boardService.insert(boardDto, loginLevel);
	}
	
	@GetMapping("/")
	public List<BoardDto> list() {
		return boardService.selectList(); 
	}
	
	@GetMapping("/{boardNo}")
	public BoardDto detail(@PathVariable long boardNo) {
		BoardDto boardDto = boardService.selectOne(boardNo);
		return boardDto; 
	}
	
	@DeleteMapping("/{boardNo}")
	public void delete(@PathVariable long boardNo
//								@RequestAttribute TokenVO tokenVO
			) {
//		long loginMemberNo = tokenVO.getMemberNo(); 
//	    String loginLevel = tokenVO.getLoginLevel();
//		boardService.delete(boardNo, loginMemberNo, loginLevel);
	}
	
	@PatchMapping("/{boardNo}")
	public void edit(@PathVariable long boardNo,
								@RequestBody BoardDto boardDto
//								@RequestAttribute TokenVO tokenVO
								) {
        boardDto.setBoardNo(boardNo);
//     long loginMemberNo = tokenVO.getMemberNo(); 
//	    String loginLevel = tokenVO.getLoginLevel();
//		boardService.update(boardDto, loginMemberNo, loginLevel); 
	}
}