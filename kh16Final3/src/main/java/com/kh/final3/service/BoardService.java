package com.kh.final3.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dao.AttachmentDao;
import com.kh.final3.dao.BoardDao;
import com.kh.final3.dao.MemberDao;
import com.kh.final3.dto.AttachmentDto;
import com.kh.final3.dto.BoardDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.vo.PageVO;

@Service
public class BoardService {

	@Autowired
	private BoardDao boardDao;

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private AttachmentDao attachmentDao;

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

		if (attachments != null && !attachments.isEmpty()) {
			attachmentService.save(boardNo, attachments, "BOARD");
		}

		return boardDto;
	}

	/**
	 * 2. 공지 목록 조회
	 */
	public PageVO<BoardDto> selectNoticeList(int page, int size) {

		PageVO<BoardDto> pageVO = new PageVO<>();
		pageVO.setPage(page);
		pageVO.setSize(size);

		Map<String, Object> countParams = new HashMap<>();
	    countParams.put("type", "NOTICE");

	    int count = boardDao.selectCountByType(countParams);
	    
	    // 3. PageVO에 총 개수 설정 및 계산
	    pageVO.setDataCount(count);

	    // 4. 목록 조회를 위한 파라미터 추가
	    countParams.put("begin", pageVO.getBegin());
	    countParams.put("end", pageVO.getEnd());

	    // 5. 페이징된 목록 조회
	    List<BoardDto> list = boardDao.selectListByPaging(countParams);

	    // 6. 작성자 닉네임 조합
	    for (BoardDto boardDto : list) {
	        String writerNickname = memberDao.findNicknameByMemberNo(boardDto.getWriterNo());
	        boardDto.setWriterNickname(writerNickname);
	    }

	    pageVO.setList(list);
	    return pageVO;
	}

	/**
	 * 3. 상세 조회 - 조회수 증가, 게시글 조회, 작성자 정보 조합을 하나의 트랜잭션으로 처리합니다.
	 */
	public BoardDto selectOne(long boardNo) {
		BoardDto boardDto = boardDao.selectOne(boardNo);

		if (boardDto != null) {
			// 닉네임 조회 및 세팅
			if (boardDto.getWriterNo() > 0) {
				String writerNickname = memberDao.findNicknameByMemberNo(boardDto.getWriterNo());
				boardDto.setWriterNickname(writerNickname);
			}

			List<AttachmentDto> list = attachmentDao.selectListByParent("BOARD", (int) boardNo);
			boardDto.setAttachmentList(list);
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
	public void update(BoardDto boardDto, long memberNo, String loginLevel, List<MultipartFile> attach, List<Integer> deleteList) {

	    long boardNo = boardDto.getBoardNo();

	    // 1. 글 존재 유무 확인
	    BoardDto originDto = boardDao.selectOne(boardNo);
	    if (originDto == null)
	        throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

	    // 2. 권한 체크
	    if (!loginLevel.equals("ADMIN") && originDto.getWriterNo() != memberNo) {
	        throw new UnauthorizationException("해당 게시글의 수정 권한이 없습니다.");
	    }
	    
	    if (deleteList != null && !deleteList.isEmpty()) {
	        for (int no : deleteList) {
	            attachmentService.delete(no);
	        }
	    }

	    // 3. 게시물 본문 수정 (제목, 내용 등)
	    boardDao.update(boardDto);

	    // 4. 첨부 파일 처리 로직 (이미 구현된 AttachmentService 활용)
	    if (attach != null && !attach.isEmpty()) {
	        // AttachmentService의 save 메서드가 (부모번호, 파일리스트, 카테고리)를 받음
	        // 카테고리는 AttachmentService 내 정의에 따라 "BOARD"로 전달
	        attachmentService.save(boardNo, attach, "BOARD");
	    }
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