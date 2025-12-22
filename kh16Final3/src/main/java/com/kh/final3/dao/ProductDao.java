package com.kh.final3.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.domain.enums.ProductStatus;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.vo.AuctionEndRequestVO;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.PurchaseListVO;

@Repository
public class ProductDao {

	@Autowired
	private SqlSession sqlSession;

	private static final String NAMESPACE = "product.";

	public long sequence() { return sqlSession.selectOne(NAMESPACE + "sequence"); }
	public int insert(ProductDto productDto) { return sqlSession.insert(NAMESPACE + "add", productDto); }
	public List<ProductDto> selectList() { return sqlSession.selectList(NAMESPACE + "list"); }
	public ProductDto selectOne(long productNo) { return sqlSession.selectOne(NAMESPACE + "detail", productNo); }
	public int delete(long productNo) { return sqlSession.delete(NAMESPACE + "delete", productNo); }

	public int updateStatus(long productNo, ProductStatus changeStatus) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("changeStatus", changeStatus.getStatus());
		return sqlSession.update(NAMESPACE + "updateStatus", params);
	}

	public int updateProductOnAuctionEnd(AuctionEndRequestVO endRequestVO) {
		return sqlSession.update(NAMESPACE + "updateProductOnAuctionEnd", endRequestVO);
	}

	public ProductDto selectOneForUpdate(long productNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		return sqlSession.selectOne(NAMESPACE + "selectOneForUpdate", params);
	}

	public List<Long> findExpiredProductNos() { return sqlSession.selectList(NAMESPACE + "findExpiredProductNos"); }
	public List<Long> findStartableProductNos(){ return sqlSession.selectList(NAMESPACE + "findStartableProductNos"); }
	public Long findSellerByRegProductNo(long productNo) { return sqlSession.selectOne(NAMESPACE + "findSellerNoByProductNo", productNo); }
	public Long findSellerNoByProductNo(long productNo) { return sqlSession.selectOne(NAMESPACE + "findSellerNoByProductNo", productNo); }
	
	public boolean update(ProductDto productDto) { return sqlSession.update(NAMESPACE + "update", productDto) > 0; }
	
	public int updateCurrentPrice(long productNo, long currentPrice) {
		Map<String, Long> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("currentPrice", currentPrice);
		return sqlSession.update(NAMESPACE + "updateCurrentPrice", params);
	}

	public boolean updateUnit(ProductDto productDto) { return sqlSession.update(NAMESPACE + "updateUnit", productDto) > 0; }
	public int count() { return sqlSession.selectOne(NAMESPACE + "countByPaging"); }

	public List<ProductDto> selectList(PageVO pageVO) {
		Map<String, Integer> params = new HashMap<>();
		params.put("begin", pageVO.getBegin());
		params.put("end", pageVO.getEnd());
		return sqlSession.selectList(NAMESPACE + "listByPaging", params);
	}

	public int countBySeller(long sellerNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("sellerNo", sellerNo);
		return sqlSession.selectOne(NAMESPACE + "countBySeller", params);
	}

	public List<ProductDto> selectListBySeller(PageVO pageVO, long sellerNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("begin", pageVO.getBegin());
		params.put("end", pageVO.getEnd());
		params.put("sellerNo", sellerNo);
		return sqlSession.selectList(NAMESPACE + "listBySellerPaging", params);
	}

	public int countByBidding() { return sqlSession.selectOne(NAMESPACE + "countByBidding"); }

	public List<ProductDto> selectListByBidding(PageVO pageVO) {
		Map<String, Integer> params = new HashMap<>();
		params.put("begin", pageVO.getBegin());
		params.put("end", pageVO.getEnd());
		return sqlSession.selectList(NAMESPACE + "listByBiddingPaging", params);
	}

	public boolean updateOnBid(Long productNo, Long bidMemberNo, Long bidAmount) {
		Map<String, Object> param = new HashMap<>();
		param.put("productNo", productNo);
		param.put("bidMemberNo", bidMemberNo);
		param.put("bidAmount", bidAmount);
		return sqlSession.update(NAMESPACE + "updateOnBid", param) > 0;
	}

	public boolean closeAuction(Long productNo) {
		return sqlSession.update(NAMESPACE + "closeAuction", productNo) > 0;
	}
	
	// ========================================================
	// 🔥🔥🔥 [최종 수정] 복잡한 파라미터 다 없애고 PageVO 하나만 받습니다! 🔥🔥🔥
	// ========================================================
	
	// 1. 검색 조건에 맞는 개수 조회 (vo 통째로 넘김 -> XML에서 #{q}, #{category} 등 꺼내 씀)
	public int countAuction(PageVO vo) {
	    return sqlSession.selectOne(NAMESPACE + "countAuction", vo);
	}

	// 2. 리스트 조회 (vo 통째로 넘김 -> XML에서 #{begin}, #{end}, #{sort} 등 꺼내 씀)
	public List<ProductDto> selectAuctionListByPaging(PageVO vo) {
	    return sqlSession.selectList(NAMESPACE + "listAuctionByPaging", vo);
	}

    // 자식 테이블 삭제 메소드들
    public int deleteEscrow(long productNo) { return sqlSession.delete(NAMESPACE + "deleteEscrow", productNo); }
    public int deleteBid(long productNo) { return sqlSession.delete(NAMESPACE + "deleteBid", productNo); }
    public int deletePointHistory(long productNo) { return sqlSession.delete(NAMESPACE + "deletePointHistory", productNo); }
    public int deleteReview(long productNo) { return sqlSession.delete(NAMESPACE + "deleteReview", productNo); }
    public int deleteMessage(long productNo) { return sqlSession.delete(NAMESPACE + "deleteMessage", productNo); }
    public int deleteOrders(long productNo) { return sqlSession.delete(NAMESPACE + "deleteOrders", productNo); }
	
	public List<PurchaseListVO> selectPurchaseList(long memberNo) {
		return sqlSession.selectList(NAMESPACE + "selectPurchaseList", memberNo);
	}
	
	public List<ProductDto> selectClosingSoon() {
	    return sqlSession.selectList("product.selectClosingSoon");
	}
}