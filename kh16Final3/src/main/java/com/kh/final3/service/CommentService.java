package com.kh.final3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.CommentDao;
import com.kh.final3.dao.MemberDao;
import com.kh.final3.dto.CommentDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.error.UnauthorizationException;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private MemberDao memberDao; 

    /**
     * 1. 댓글 등록
     * - 시퀀스 발급, DTO 설정, DB 삽입을 처리합니다.
     */
    @Transactional
    public CommentDto insert(CommentDto commentDto, long memberNo) {

        // 1. 시퀀스 번호 발급 및 DTO에 설정
        long commentNo = commentDao.sequence();
        commentDto.setCommentNo(commentNo);
        
        // 2. 작성자 ID 설정 (DTO 필드명: writerNo 사용)
        commentDto.setWriterNo(memberNo); // setCommentWriter -> setWriterNo로 변경
        
        // 3. 댓글 상태 및 기타 초기값 설정 (DTO 필드명: status 사용)
        commentDto.setStatus("N"); // setCommentStatus -> setStatus로 변경. DB CHECK에 따라 'N'으로 설정.
        
        // 4. DAO를 통해 DB에 등록
        boolean success = commentDao.insert(commentDto);
        if (!success) {
            // 등록 실패 시 예외 처리 필요
        }
        
        // 5. 등록된 댓글 정보 반환 (닉네임 조합)
        String writerNickname = memberDao.findNicknameByMemberNo(memberNo);
        commentDto.setWriterNickname(writerNickname);

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
        if (loginLevel.equals("admin")) {
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
            throw new UnauthorizationException("해당 댓글의 삭제 권한이 없습니다.");
        }
        
        // 3. DAO를 통한 소프트 삭제 (상태 변경)
        commentDao.softDelete(commentNo);
    }
}