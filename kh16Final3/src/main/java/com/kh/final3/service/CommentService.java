package com.kh.final3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.BoardDao;
import com.kh.final3.dao.CommentDao;
import com.kh.final3.dao.MemberDao;
import com.kh.final3.dto.BoardDto;
import com.kh.final3.dto.CommentDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private MemberDao memberDao; 
    @Autowired
    private BoardDao boardDao;
    @Autowired
    private MessageService messageService;
    /**
     * 1. 댓글 등록
     * - 시퀀스 발급, DTO 설정, DB 삽입을 처리합니다.
     */
    @Transactional
    public CommentDto insert(CommentDto commentDto, long memberNo, String loginLevel, Long productNo) { 

        long commentNo = commentDao.sequence();
        commentDto.setCommentNo(commentNo);
        commentDto.setWriterNo(memberNo); 
        commentDto.setStatus("N"); 
        
        boolean success = commentDao.insert(commentDto);
        if (!success) {
            throw new TargetNotfoundException("댓글 등록에 실패했습니다."); 
        }
        
        // QNA 답변 알림 로직
        long parentBoardNo = commentDto.getBoardNo(); // QNA 글 번호
        BoardDto parentBoard = boardDao.selectOne(parentBoardNo);
        
        // 조건 검사: 'QNA 타입'이며 '관리자'가 작성한 댓글(답변)인 경우
        if (parentBoard != null && parentBoard.getType().equals("QNA") && loginLevel.equals("ADMIN")) {
            
            long qnaWriterNo = parentBoard.getWriterNo(); // QNA 작성자
            
            // 5-3. 메시지 발송
            messageService.sendNotification(
                qnaWriterNo,
                "작성하신 문의에 답변이 등록되었습니다.",
                "/qna/detail/" + parentBoardNo, // 알림 클릭 시 이동할 URL
                productNo
            );
        }
        
        return commentDto;
    }

    /**
     * 2. 댓글 목록 조회
     */
    public List<CommentDto> selectList(long boardNo) {
        // 1. DAO를 통해 댓글 목록 조회
        List<CommentDto> list = commentDao.selectList(boardNo);
        
        // 2. 목록을 순회하며 작성자 정보 조합
        for (CommentDto commentDto : list) {
            // DTO 필드명: writerNo 사용
            String writerNickname = memberDao.findNicknameByMemberNo(commentDto.getWriterNo()); // getCommentWriter -> getWriterNo로 변경
            commentDto.setWriterNickname(writerNickname);

        }
        
        return list;
    }

    /**
     * 3. 댓글 수정 (PATCH)
     */
    @Transactional
    public void update(CommentDto commentDto, long memberNo, String loginLevel) {
        
        long commentNo = commentDto.getCommentNo();
        
        // 1. 댓글 존재 유무 확인 및 원본 DTO 조회
        CommentDto originDto = commentDao.selectOne(commentNo);
        if (originDto == null) throw new TargetNotfoundException("존재하지 않는 댓글입니다.");
        
        // 2. 권한 체크 (본인 또는 관리자만 수정 가능)
        // DTO 필드명: writerNo 사용
        // a. 관리자 권한
        if (loginLevel.equals("ADMIN")) {
            // 통과
        }
        // b. 작성자 본인 확인
        else if (originDto.getWriterNo() == memberNo) { // getCommentWriter -> getWriterNo로 변경
            // 통과
        }
        // c. 권한 없음
        else {
            throw new UnauthorizationException("해당 댓글의 수정 권한이 없습니다.");
        }
        
        // 3. DAO를 통한 수정
        commentDao.update(commentDto);
    }

    /**
     * 4. 댓글 삭제 (소프트 삭제)
     */
    @Transactional
    public void softDelete(long commentNo, long memberNo, String loginLevel) {
        
        // 1. 댓글 존재 유무 확인 및 원본 DTO 조회
        CommentDto originDto = commentDao.selectOne(commentNo);
        if (originDto == null) throw new TargetNotfoundException("존재하지 않는 댓글입니다.");
        
        // 2. 권한 체크 (본인 또는 관리자만 삭제 가능)
        // a. 관리자 권한
        if (loginLevel.equals("ADMIN")) {
            // 통과
        }
        // b. 작성자 본인 확인
        else if (originDto.getWriterNo() == memberNo) { // getCommentWriter -> getWriterNo로 변경
            // 통과
        }
        // c. 권한 없음
        else {
            throw new UnauthorizationException("해당 댓글의 삭제 권한이 없습니다.");
        }
        
        // 3. DAO를 통한 소프트 삭제 (상태 변경)
        commentDao.softDelete(commentNo);
    }
}