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
public class QnaService { // QnaService 새로 생성
	
	@Autowired
	private BoardDao boardDao;
	
    @Autowired
    private MemberDao memberDao; 
    
//    @Autowired
//    private AttachmentService attachmentService;

    /**
     * 1. 문의 등록 (QNA)
     * - 관리자는 문의 게시판에 글을 등록할 수 없도록 권한 체크
     */
	@Transactional
	public BoardDto insert(BoardDto boardDto, List<MultipartFile> attachments, String loginLevel, long memberNo) {
	    
	    // 1. 문의 등록 권한 체크: 관리자(admin) 차단
	    if (loginLevel.equals("admin")) {
	    	throw new UnauthorizationException("관리자는 문의 게시판에 글을 등록할 수 없습니다.");
	    }
        
	    // 2. 시퀀스 번호 발급 및 DTO 설정
	    long boardNo = boardDao.sequence();
        boardDto.setBoardNo(boardNo);
        boardDto.setWriterNo(memberNo); 
        boardDto.setType("QNA"); 
	    
	    boardDao.insert(boardDto);
	    
	    return boardDto;
	}
    
    /**
     * 2. 문의 목록 조회 (QNA 전용)
     */
    public List<BoardDto> selectList() {
        // DAO의 listByType을 사용하여 QNA 타입만 조회
	    List<BoardDto> list = boardDao.selectListByType("QNA"); 
	    
	    // 작성자 닉네임 조합 로직은 기존과 동일
	    for (BoardDto boardDto : list) {
	        String writerNickname = memberDao.findNicknameByMemberNo(boardDto.getWriterNo()); 
	        boardDto.setWriterNickname(writerNickname);
	    }
	    
	    return list;
    }
    
    /**
     * 3. 상세 조회는 BoardService와 동일하게 BoardDao.selectOne을 사용하므로, 
     * 여기서는 별도의 상세 조회 로직을 정의하지 않고 필요할 경우 BoardService를 사용합니다.
     * (혹은 QnaService에만 필요한 로직이 있다면 여기서 구현합니다.)
     */

	/**
	 * 4. 문의 삭제
	 */
	@Transactional
	public void delete(long boardNo, String loginLevel, long memberNo) {

		// 1. 글 존재 유무 확인 및 원본 DTO 조회
		BoardDto originDto = boardDao.selectOne(boardNo);
		if (originDto == null)
			throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

		// 2. 권한 체크 (본인 또는 관리자만 삭제 가능)
		// DTO 필드명: writerNo 사용
		if (loginLevel.equals("admin")) {
			// 통과
		} else if (originDto.getWriterNo() == memberNo) { // DTO 필드명: writerNo 사용
			// 통과
		} else {
			throw new UnauthorizationException("해당 게시글의 삭제 권한이 없습니다.");
		}

		// 3. 첨부파일 정보 및 실제 파일 삭제
		// attachmentService.deleteFilesByParentPk(boardNo);
		
		// 4. DAO를 통한 삭제
		boardDao.delete(boardNo);
	}

}