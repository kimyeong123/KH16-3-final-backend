package com.kh.final3.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dao.BoardDao;
import com.kh.final3.dao.MemberDao;
import com.kh.final3.dto.BoardDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;
import com.kh.final3.vo.PageVO;

@Service
public class QnaService {
    
    @Autowired
    private BoardDao boardDao;
    
    @Autowired
    private MemberDao memberDao; 
    
    @Autowired
    private AttachmentService attachmentService;

    /**
     * 1. 문의 등록
     */
    @Transactional
    public BoardDto insert(BoardDto boardDto, List<MultipartFile> attachments, String loginLevel, long memberNo) {
        if ("ADMIN".equals(loginLevel)) {
            throw new UnauthorizationException("관리자는 문의 게시판에 글을 등록할 수 없습니다.");
        }
        
        long boardNo = boardDao.sequence();
        boardDto.setBoardNo(boardNo);
        boardDto.setWriterNo(memberNo); 
        boardDto.setType("QNA"); 
        
        boardDao.insert(boardDto);
        return boardDto;
    }
    
    /**
     * 2. 문의 목록 조회 (전체/FAQ용)
     */
    public PageVO<BoardDto> selectList(PageVO<BoardDto> pageVO, String type) { 
        // 1. 파라미터 맵 구성
        Map<String, Object> params = new HashMap<>();
        params.put("type", "QNA");
        
        // 2. 전체 개수 조회 (에러 방지를 위해 Map 전달)
        int count = boardDao.selectCountByType(params); 
        pageVO.setDataCount(count); 
        
        // 3. 페이징 정보 추가
        params.put("begin", pageVO.getBegin()); 
        params.put("end", pageVO.getEnd());
        
        // 4. 목록 조회
        List<BoardDto> list = boardDao.selectListByPaging(params); 
        
        // 5. 닉네임 세팅
        for (BoardDto boardDto : list) {
            boardDto.setWriterNickname(memberDao.findNicknameByMemberNo(boardDto.getWriterNo()));
        }
        
        pageVO.setList(list);
        return pageVO; 
    }

    /**
     * 3. 내 문의 목록 조회 (필터링 버전)
     */
    public PageVO<BoardDto> selectMyList(PageVO<BoardDto> pageVO, long memberNo) {
        // 1. 파라미터 맵 구성 (writerNo 포함)
        Map<String, Object> params = new HashMap<>();
        params.put("type", "QNA");
        params.put("writerNo", memberNo); // 👈 이 값이 있으면 매퍼에서 AND writer_no = ... 가 붙음
        
        // 2. 내 글 개수 조회 (동일한 Map 사용)
        int count = boardDao.selectCountByType(params); 
        pageVO.setDataCount(count); 
        
        // 3. 페이징 정보 추가
        params.put("begin", pageVO.getBegin()); 
        params.put("end", pageVO.getEnd());
        
        // 4. 목록 조회 (동일한 DAO 메서드 사용)
        List<BoardDto> list = boardDao.selectListByPaging(params); 
        
        // 5. 닉네임 세팅 (내 닉네임으로 일괄 세팅)
        String myNickname = memberDao.findNicknameByMemberNo(memberNo);
        for (BoardDto boardDto : list) {
            boardDto.setWriterNickname(myNickname);
        }
        
        pageVO.setList(list);
        return pageVO; 
    }

    /**
     * 4. 문의 삭제
     */
    @Transactional
    public void delete(long boardNo, String loginLevel, long memberNo) {
        BoardDto originDto = boardDao.selectOne(boardNo);
        if (originDto == null) throw new TargetNotfoundException("존재하지 않는 게시글입니다.");

        if (!"ADMIN".equals(loginLevel) && originDto.getWriterNo() != memberNo) {
            throw new UnauthorizationException("해당 게시글의 삭제 권한이 없습니다.");
        }

        boardDao.delete(boardNo);
    }
}