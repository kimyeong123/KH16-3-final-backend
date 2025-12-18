package com.kh.final3.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.AttachmentDto;

@Repository
public class AttachmentDao {

    @Autowired
    private SqlSession sqlSession;

    // 1. 시퀀스 번호 생성
    public int sequence() {
        return sqlSession.selectOne("attachment.sequence");
    }

    // 2. 파일 정보 등록
    public void insert(AttachmentDto dto) {
        sqlSession.insert("attachment.add", dto);
    }

    // 3. 단일 조회 (상세)
    public AttachmentDto selectOne(int attachmentNo) {
        return sqlSession.selectOne("attachment.detail", attachmentNo);
    }

    // 4. 부모글에 달린 파일 목록 조회 (리스트)
    public List<AttachmentDto> selectListByParent(String category, int parentPkNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("category", category);
        param.put("parentPkNo", parentPkNo);
        return sqlSession.selectList("attachment.listByParent", param);
    }

    // 5. 파일 삭제 (단일)
    public boolean delete(int attachmentNo) {
        return sqlSession.delete("attachment.delete", attachmentNo) > 0;
    }

    // 6. 게시글 삭제 시 관련 파일 일괄 삭제
    public boolean deleteByParent(String category, int parentPkNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("category", category);
        param.put("parentPkNo", parentPkNo);
        return sqlSession.delete("attachment.deleteByParent", param) > 0;
    }
}