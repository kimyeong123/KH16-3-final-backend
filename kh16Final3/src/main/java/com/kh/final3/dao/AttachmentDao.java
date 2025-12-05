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


    public int sequence() {
        return sqlSession.selectOne("attachment.sequence");
    }


    public void insert(AttachmentDto dto) {
        sqlSession.insert("attachment.add", dto);
    }


    public AttachmentDto selectOne(int attachmentNo) {
        return sqlSession.selectOne("attachment.detail", attachmentNo);
    }


    public List<AttachmentDto> selectListByParent(String category, String parent) {
        Map<String, Object> param = new HashMap<>();
        param.put("attachmentCategory", category);
        param.put("attachmentParent", parent);
        return sqlSession.selectList("attachment.listByParent", param);
    }

    public boolean delete(int attachmentNo) {
        return sqlSession.delete("attachment.delete", attachmentNo) > 0;
    }


    public boolean deleteByParent(String category, String parent) {
        Map<String, Object> param = new HashMap<>();
        param.put("attachmentCategory", category);
        param.put("attachmentParent", parent);
        return sqlSession.delete("attachment.deleteByParent", param) > 0;
    }
}