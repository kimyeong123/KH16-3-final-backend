package com.kh.final3.dao;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.PointHistoryDto;
import com.kh.final3.dto.PointWithdrawDto;
import com.kh.final3.vo.PointChargeHistoryVO;
import com.kh.final3.vo.member.MemberBidHistoryVO;

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

    public PointHistoryDto selectOne(long pointHistoryNo) {
        return sqlSession.selectOne(NAMESPACE + "detail", pointHistoryNo);
    }

    public List<PointHistoryDto> selectListByMember(long memberNo) {
        return sqlSession.selectList(NAMESPACE + "listByMember", memberNo);
    }
    
    public long calculateMemberBalance(long memberNo) {
    	return sqlSession.selectOne(NAMESPACE + "calculateMemberBalance", memberNo);
    }
    
    public int insertSettlementHistory(PointHistoryDto pointHistoryDto) {
    	return sqlSession.insert(NAMESPACE + "insertSettlementHistory", pointHistoryDto);
    }
    
 // PointHistoryDao.java 에 추가
    public int deleteByProductNo(long productNo) {
        return sqlSession.delete(NAMESPACE + "deleteByProductNo", productNo);
    }
    
    public int insertCharge(PointHistoryDto dto) {
        return sqlSession.insert(NAMESPACE + "insertCharge", dto);
    }
    //회원 충전내역
    public List<PointChargeHistoryVO> listChargeHistoryByMember(long memberNo) {
        return sqlSession.selectList(NAMESPACE + "listChargeHistoryByMember", memberNo);
    }
    //회원 입찰내역
    public List<MemberBidHistoryVO> listMemberBidHistory(long memberNo) {
        return sqlSession.selectList(
            NAMESPACE + "listMemberBidHistory",
            memberNo
        );
    }
    public void insertWithdrawDeduct(PointWithdrawDto dto) {
        sqlSession.insert(NAMESPACE + "insertWithdrawDeduct", dto);
    }

    public void insertWithdrawRefund(PointWithdrawDto dto) {
        sqlSession.insert(NAMESPACE + "insertWithdrawRefund", dto);
    }







    	
}
