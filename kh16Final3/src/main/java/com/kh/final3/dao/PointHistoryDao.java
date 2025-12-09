package com.kh.final3.dao;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.PointHistoryDto;

@Repository
public class PointHistoryDao {

    @Autowired
    private SqlSession sqlSession;

    public int sequence() {
        return sqlSession.selectOne("pointHistory.sequence");
    }

    public void insert(PointHistoryDto pointHistoryDto) {
        sqlSession.insert("pointHistory.insert", pointHistoryDto);
    }

    public PointHistoryDto selectOne(int pointHistoryNo) {
        return sqlSession.selectOne("pointHistory.detail", pointHistoryNo);
    }

    public List<PointHistoryDto> selectListByMember(int memberNo) {
        return sqlSession.selectList("pointHistory.listByMember", memberNo);
    }
}
