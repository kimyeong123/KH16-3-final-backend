package com.kh.final3.dao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.BidDto;

@Repository
public class BidDao {

    @Autowired
    private SqlSession sqlSession;
    
    private static final String NAMESPACE = "bid."; 
    
    public long sequence() {
        return sqlSession.selectOne(NAMESPACE + "sequence");
    }
    
    public int insert(BidDto bidDto) {
        return sqlSession.insert(NAMESPACE + "insert", bidDto);
    }
    
    public BidDto selectOne(long bidNo) {
    	return sqlSession.selectOne(NAMESPACE + "selectOne", bidNo);
    }
    
    // 상품별 입찰 랭킹 목록 조회(금액 1순위, 입찰순서 2순위)
    public List<BidDto> findBidRankingByProduct(long productNo) { 
        return sqlSession.selectList(NAMESPACE + "selectListBidByProductNo", productNo);
    }
    
    // 상품별 최고 입찰 단건 조회
    public BidDto findHighestBid(long productNo) {
        return sqlSession.selectOne(NAMESPACE + "findHighestBidByProductNo", productNo);
    }
    
    // 상품별 총 입찰 횟수 조회
    public int countByProduct(long productNo) {
    	return sqlSession.selectOne(NAMESPACE + "countByProductNo", productNo);
    }
    
    // 최고 입찰자 회원 번호 조회
    public Long findHighestBidderNo(long productNo) { 
    	return sqlSession.selectOne(NAMESPACE + "findHighestBidderNo", productNo);
    }
    
    // 특정 상품의 입찰자 수 조회
    public int countDistinctBidder(long productNo) {
    	return sqlSession.selectOne(NAMESPACE + "countDistinctBidder", productNo);
    }
    
    // 특정 유저의 전체 입찰 내역
    public List<BidDto> findHistoryByBidder(long bidderNo) {
    	return sqlSession.selectList(NAMESPACE + "selectListByBidder", bidderNo);
    }
    
    // 특정 유저의 특정 상품에 대한 입찰 내역
    public List<BidDto> findMyBidHistoryByProduct(long bidderNo, long productNo) {
    	Map<String, Object> params = new HashMap<>();
        params.put("bidderNo", bidderNo);
        params.put("productNo", productNo);
        
        return sqlSession.selectList(NAMESPACE + "selectListMyBidHistory", params);
    }
    
    // 특정 상품의 입찰 내역 페이징
    public List<BidDto> findProductBidPaging(long productNo, int begin, int end) {
        Map<String, Object> params = new HashMap<>();
        params.put("productNo", productNo);
        params.put("begin", begin);
        params.put("end", end);
        
        return sqlSession.selectList(NAMESPACE + "selectListByProductNoPaging", params);
    }

 public boolean deleteByProductNo(long productNo) {
     // "product." 은 product-mapper.xml의 namespace입니다.
     // "deleteBidByProductNo"는 xml에 적은 id입니다. (id 확인 필수!)
     return sqlSession.delete("product.deleteBidByProductNo", productNo) > 0;
 }
   
}