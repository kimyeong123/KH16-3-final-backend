package com.kh.final3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
    /**
     * 1. 게시글 등록
     * - 시퀀스 발급, DTO 설정, 게시판 타입별 권한 체크, DB 삽입을 처리합니다.
     */
	@Transactional
	public BoardDto insert(BoardDto boardDto, String loginLevel, long memberNo) {
	    
	    // 1. 시퀀스 번호 발급 및 DTO에 설정 (DAO에서 중복 호출 방지)
	    long boardNo = boardDao.sequence();
        boardDto.setBoardNo(boardNo);
		
		// 2. 권한 체크 로직
		// 공지사항 (NOTICE) 체크: 관리자(ADMIN)만 등록 가능
	    if (boardDto.getBoardType().equals("NOTICE") && !loginLevel.equals("admin")) {
	        throw new UnauthorizationException("공지사항 등록 권한이 없습니다.");
	    }
	    
	    // 문의 게시판 (QNA) 체크: 일반 회원만 등록 가능 (관리자 차단)
	    if (boardDto.getBoardType().equals("QNA") && loginLevel.equals("admin")) {
	    	throw new UnauthorizationException("관리자는 문의 게시판에 글을 등록할 수 없습니다.");
	    }
	    
	    boardDto.setBoardWriter(memberNo);
	    
	    boardDao.insert(boardDto);
	    
	    return boardDto;
	}
    
    /**
     * 2. 게시글 목록 조회
     * - 순수 BOARD 목록을 조회한 후, 각 게시글의 작성자 닉네임 정보를 조합
     */
    public List<BoardDto> selectList() {
	    List<BoardDto> list = boardDao.selectList(); 
	    
	    for (BoardDto boardDto : list) {
	        String writerNickname = memberDao.findNicknameByMemberNo(boardDto.getBoardWriter()); 
            
	        boardDto.setWriterNickname(writerNickname);
	    }
	    
	    return list;
    }
    
    /**
     * 3. 게시글 상세 조회
     * - 조회수 증가, 게시글 조회, 작성자 정보 조합을 하나의 트랜잭션으로 처리합니다.
     */
	@Transactional
	public BoardDto selectOne(long boardNo) {
	    
        // 1. 조회수 증가
	    boolean isSuccess = boardDao.updateBoardRead(boardNo);
        // 조회수 증가에 실패하면 게시글이 없는 것으로 간주 가능
        if (!isSuccess) throw new TargetNotfoundException("존재하지 않는 게시글입니다.");
	    
	    // 2. 게시글 정보 조회
	    BoardDto boardDto = boardDao.selectOne(boardNo);
	    
	    // 3. 작성자 정보 조합  
	    if (boardDto != null) {
            // boardWriter(member_no)를 이용해 Member 테이블에서 닉네임 조회
	        String writerNickname = memberDao.findNicknameByMemberNo(boardDto.getBoardWriter());
	        boardDto.setWriterNickname(writerNickname);
	    }
	    
	    return boardDto;
	}
    
    /**
     * 4. 게시글 수정 (PATCH)
     * - 게시글 존재 유무 및 수정 권한을 체크한 후, DTO의 Null 값을 확인하며 업데이트합니다.
     */
    @Transactional
    public void update(BoardDto boardDto, long memberNo, String loginLevel) {
        
        long boardNo = boardDto.getBoardNo();
        
        // 1. 글 존재 유무 확인
        BoardDto originDto = boardDao.selectOne(boardNo);
        if (originDto == null) throw new TargetNotfoundException("존재하지 않는 게시글입니다.");
        
        // 2. 권한 체크 (본인 또는 관리자만 수정 가능)
        // a. 관리자는 모든 글 수정 가능
        if (loginLevel.equals("admin")) {
            // 통과
        } 
        // b. 작성자는 본인 글만 수정 가
        else if (originDto.getBoardWriter() == memberNo) {
            // 통과
        }
        // c. 권한 없는 사용자
        else {
            throw new UnauthorizationException("해당 게시글의 수정 권한이 없습니다.");
        }
        
        // 3. DAO를 통한 수정
        // DAO의 update 쿼리는 <if test>로 Null 체크를 처리하므로, 그대로 DTO를 전달
        boardDao.update(boardDto);
    }
    
    /**
     * 5. 게시글 삭제
     * - 게시글 존재 유무 및 삭제 권한을 체크한 후, DB에서 삭제합니다.
     */
    @Transactional
	public void delete(long boardNo, long memberNo, String loginLevel) {
		
        // 1. 글 존재 유무 확인 및 원본 DTO 조회 (조회수 증가 방지를 위해 selectOne 대신 DAO 메서드를 직접 호출할 수도 있음)
        BoardDto originDto = boardDao.selectOne(boardNo);
        if (originDto == null) throw new TargetNotfoundException("존재하지 않는 게시글입니다.");
        
        // 2. 권한 체크 (본인 또는 관리자만 삭제 가능)
        // a. 관리자는 모든 글 삭제 가능
        if (loginLevel.equals("admin")) {
            // 통과
        } 
        // b. 작성자는 본인 글만 삭제 가능
        else if (originDto.getBoardWriter() == memberNo) {
            // 통과
        }
        // c. 권한 없는 사용자
        else {
            throw new UnauthorizationException("해당 게시글의 삭제 권한이 없습니다.");
        }
        
        // 3. DAO를 통한 삭제
		boardDao.delete(boardNo);
	}
}