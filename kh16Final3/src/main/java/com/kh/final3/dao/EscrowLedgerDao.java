package com.kh.final3.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.EscrowLedgerDto;
import com.kh.final3.domain.enums.EscrowStatus; 

@Repository
public class EscrowLedgerDao {

    @Autowired
    private SqlSession sqlSession;
    
    private static final String NAMESPACE = "escrowLedger."; 
    
    public long sequence() {
        return sqlSession.selectOne(NAMESPACE + "sequence");
    }
    
    public int insert(EscrowLedgerDto escrowDto) {
        return sqlSession.insert(NAMESPACE + "insert", escrowDto);
    }
    
    // HELD, SETTLED와 같이 단일 건만 존재해야 하는 경우 사용.
    // 결과가 2건 이상이면 TooManyResultsException을 발생시켜 오류를 유도.
    public EscrowLedgerDto findDtoByProductNoAndStatus(long productNo, EscrowStatus targetStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("productNo", productNo);
        params.put("targetStatus", targetStatus.name()); 
        
        return sqlSession.selectOne(NAMESPACE + "findDtoByProductNoAndStatus", params);
    }

    // RELEASED, CANCELLED와 같이 다행 건이 존재할 수 있는 경우 사용.
    public List<EscrowLedgerDto> findListByProductNoAndStatus(long productNo, EscrowStatus targetStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("productNo", productNo);
        params.put("targetStatus", targetStatus.name()); 
        
        return sqlSession.selectList(NAMESPACE + "findDtoByProductNoAndStatus", params);
    }
    
    // 특정 상품의 특정 상태 pk 찾기 (단일 건만 존재하는 상태에만 사용)
    public Long findEscrowNoByProductNoAndStatus(long productNo, EscrowStatus targetStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("productNo", productNo);
        params.put("targetStatus", targetStatus.name());
        
        return sqlSession.selectOne(NAMESPACE + "findEscrowNoByProductNoAndStatus", params);
    }
   
    // 입찰 번호로 Dto 찾기
    public EscrowLedgerDto findDtoByBidNo(long bidNo) {
    	return sqlSession.selectOne(NAMESPACE + "findDtoByBidNo", bidNo);
    }
    
    // 입찰 번호로 pk 찾기
    public Long findEscrowNoByBidNo(long bidNo) {
    	return sqlSession.selectOne(NAMESPACE + "findEscrowNoByBidNo", bidNo);
    }
    
    // 특정 입찰자의 상품별 에스크로 내역 목록 조회
    public List<EscrowLedgerDto> findListByBidderAndProduct(long bidderNo, long productNo) {
        Map<String, Long> params = new HashMap<>();
        params.put("bidderNo", bidderNo);
        params.put("productNo", productNo);
        
        return sqlSession.selectList(NAMESPACE + "selectListByBidderAndProduct", params);
    }

    // Status 변경 처리 (RELEASED, SETTLED, CANCELLED 등으로 변경)
    public int updateStatusByEscrowNo(long escrowLedgerNo, EscrowStatus changeStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("escrowLedgerNo", escrowLedgerNo);
        params.put("changeStatus", changeStatus.name()); 
        
        return sqlSession.update(NAMESPACE + "updateStatusByEscrowNo", params);
    }
    
}