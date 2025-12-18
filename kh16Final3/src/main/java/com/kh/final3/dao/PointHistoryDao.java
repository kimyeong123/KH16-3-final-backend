package com.kh.final3.dao;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.PointHistoryDto;
import com.kh.final3.vo.PointChargeHistoryVO;

@Repository
public class PointHistoryDao {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "pointHistory."; 
    
    public long sequence() {
        return sqlSession.selectOne(NAMESPACE + "sequence");
    }

    public int insert(PointHistoryDto pointHistoryDto) {
        return sqlSession.insert(NAMESPACE + "insert", pointHistoryDto);
    }

    public PointHistoryDto selectOne(int pointHistoryNo) {
        return sqlSession.selectOne(NAMESPACE + "detail", pointHistoryNo);
    }

    public List<PointHistoryDto> selectListByMember(int memberNo) {
        return sqlSession.selectList(NAMESPACE + "listByMember", memberNo);
    }
    
    public long calculateMemberBalance(int memberNo) {
    	return sqlSession.selectOne(NAMESPACE + "calculateMemberBalance", memberNo);
    }
    
    public int insertSettlementHistory(PointHistoryDto pointHistoryDto) {
    	return sqlSession.insert(NAMESPACE + "insertSettlementHistory", pointHistoryDto);
    }

    public int insertCharge(PointHistoryDto dto) {
        return sqlSession.insert(NAMESPACE + "insertCharge", dto);
    }
    public List<PointChargeHistoryVO> listChargeHistoryByMember(long memberNo) {
        return sqlSession.selectList(NAMESPACE + "listChargeHistoryByMember", memberNo);
    }
    	
}
