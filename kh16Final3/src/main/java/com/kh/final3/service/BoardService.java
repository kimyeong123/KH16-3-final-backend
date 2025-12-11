package com.kh.final3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dao.BoardDao;
import com.kh.final3.dao.MemberDao;
import com.kh.final3.dto.BoardDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;

@Service
public class BoardService {

	@Autowired
	private BoardDao boardDao;

	@Autowired
	private MemberDao memberDao;
	
//	@Autowired
//    private AttachmentService attachmentService;

	/**
	 * 1. 게시글 등록
	 */
	@Transactional
	public BoardDto insert(BoardDto boardDto, List<MultipartFile> attachments, String loginLevel, long memberNo) {

		// 공지 권한 체크
		if (!loginLevel.equals("ADMIN")) {
			throw new UnauthorizationException("등록 권한이 없습니다");
		}

		// 시퀀스 번호 발급 및 DTO에 설정 (DTO 필드명: boardNo)
		long boardNo = boardDao.sequence();
		boardDto.setBoardNo(boardNo);
		boardDto.setWriterNo(memberNo);
		boardDto.setType("NOTICE");

		boardDao.insert(boardDto);
		
		if(attachments != null && !attachments.isEmpty()) {
//			attachmentService.save(boardNo, attachments, "BOARD");
		}

		return boardDto;
	}

	/**
	 * 2. 공지 목록 조회
	 */
	public List<BoardDto> selectNoticeList() {
		// DAO의 listByType을 사용하여 NOTICE 타입만 조회
		List<BoardDto> list = boardDao.selectListByType("NOTICE");

		// 작성자 닉네임 조합 로직은 기존과 동일
		for (BoardDto boardDto : list) {
			String writerNickname = memberDao.findNicknameByMemberNo(boardDto.getWriterNo());
			boardDto.setWriterNickname(writerNickname);
		}

		return list;
	}

	/**
	 * 3. 상세 조회 - 조회수 증가, 게시글 조회, 작성자 정보 조합을 하나의 트랜잭션으로 처리합니다.
	 */
	public BoardDto selectOne(long boardNo) {
	    BoardDto boardDto = boardDao.selectOne(boardNo);
	    
	    // 작성자 닉네임 조합 로직은 그대로 유지
	    if (boardDto != null && boardDto.getWriterNo() > 0) {
	        String writerNickname = memberDao.findNicknameByMemberNo(boardDto.getWriterNo());
	        boardDto.setWriterNickname(writerNickname);
	    }
	    
	    return boardDto;
	}
	
	@Transactional
	public void updateReadCount(long boardNo) {
	    boardDao.updateBoardRead(boardNo);
	}

	/**
	 * 4. 게시글 수정 (PATCH)
	 */
	@Transactional
	public void update(BoardDto boardDto, long memberNo, String loginLevel) {

		long boardNo = boardDto.getBoardNo();

		// 1. 글 존재 유무 확인
		BoardDto originDto = boardDao.selectOne(boardNo);
		if (originDto == null)
			throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

		// 2. 권한 체크 (본인 또는 관리자만 수정 가능)
		// DTO 필드명: writerNo 사용
		if (loginLevel.equals("ADMIN")) {
			// 통과
		} else if (originDto.getWriterNo() == memberNo) { // DTO 필드명: writerNo 사용
			// 통과
		} else {
			throw new UnauthorizationException("해당 게시글의 수정 권한이 없습니다.");
		}

		// 3. DAO를 통한 수정
		boardDao.update(boardDto);
	}

	/**
	 * 5. 게시글 삭제
	 */
	@Transactional
	public void delete(long boardNo, String loginLevel, long memberNo) {

		// 1. 글 존재 유무 확인 및 원본 DTO 조회
		BoardDto originDto = boardDao.selectOne(boardNo);
		if (originDto == null)
			throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

		// 2. 권한 체크 (본인 또는 관리자만 삭제 가능)
		// DTO 필드명: writerNo 사용
		if (loginLevel.equals("ADMIN")) {
			// 통과
		} else if (originDto.getWriterNo() == memberNo) { // DTO 필드명: writerNo 사용
			// 통과
		} else {
			throw new UnauthorizationException("해당 게시글의 삭제 권한이 없습니다.");
		}

		// 3. DAO를 통한 삭제
		boardDao.delete(boardNo);
	}

}