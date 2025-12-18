package com.kh.final3.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dto.BoardDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.service.BoardService;
import com.kh.final3.service.QnaService;
import com.kh.final3.vo.PageVO; // HEAD 버전에서 추가된 import
import com.kh.final3.vo.TokenVO;

import jakarta.servlet.http.HttpServletRequest;

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
	@PostMapping("/write")
	public BoardDto insert(@RequestPart BoardDto boardDto,
			@RequestPart(required = false) List<MultipartFile> attachments, @RequestAttribute TokenVO tokenVO)
			throws IllegalStateException, IOException {
		long memberNo = tokenVO.getMemberNo();
		String loginLevel = tokenVO.getLoginLevel();

		if (memberNo == 0) {
			throw new UnauthorizationException("로그인 후 문의 작성이 가능합니다.");
		}

		return qnaService.insert(boardDto, attachments, loginLevel, memberNo);
	}

	/**
	 * 2. 문의 목록 조회
	 */
	@GetMapping("/list")
	public PageVO list(@ModelAttribute PageVO pageVO, @RequestAttribute(required = false) TokenVO tokenVO) {

		if (tokenVO != null) {
			pageVO.setLoginNo(tokenVO.getMemberNo());
			pageVO.setLoginLevel(tokenVO.getLoginLevel());
		} 

		return qnaService.selectList(pageVO);
	}

	/**
	 * 3. 문의 상세 조회 (GET /qna/{boardNo})
	 */
	@GetMapping("/detail/{boardNo}")
	public BoardDto detail(@PathVariable long boardNo, HttpServletRequest request // // 인터셉터와 객체 공유를 위해 request 직접 사용
	) {
		// // 인터셉터에서 "tokenVO"라는 이름으로 저장한 데이터를 추출
		TokenVO tokenVO = (TokenVO) request.getAttribute("tokenVO");

		BoardDto boardDto = boardService.selectOne(boardNo);
		if (boardDto == null)
			throw new TargetNotfoundException("존재하지 않는 문의글입니다.");

		// // 자유게시판(FAQ 등)은 권한 체크 없이 반환
		if ("FREE".equals(boardDto.getType()))
			return boardDto;

		// // QNA 문의글일 경우 권한 검사
		if ("QNA".equals(boardDto.getType())) {
			if (tokenVO == null) {
				throw new UnauthorizationException("로그인 후 확인 가능합니다.");
			}

			long loginMemberNo = tokenVO.getMemberNo();
			long writerNo = boardDto.getWriterNo();
			String loginLevel = tokenVO.getLoginLevel();

			// // 관리자 등급 확인 (Jotai와 DB 명칭 모두 고려)
			boolean isAdmin = "ADMIN".equals(loginLevel) || "관리자".equals(loginLevel);

			// // 관리자도 아니고 작성자도 아니면 차단
			if (!isAdmin && loginMemberNo != writerNo) {
				throw new UnauthorizationException("본인의 문의글만 확인할 수 있습니다.");
			}
		}
		return boardDto;
	}

	/**
	 * 4. 문의 삭제 (DELETE /rest/qna/{boardNo})
	 */
	@DeleteMapping("/{boardNo}")
	public ResponseEntity<?> delete(@PathVariable long boardNo, @RequestAttribute TokenVO tokenVO) {
		long memberNo = tokenVO.getMemberNo();
		String loginLevel = tokenVO.getLoginLevel();

		qnaService.delete(boardNo, loginLevel, memberNo);

		return ResponseEntity.ok().build();
	}

}