package com.kh.final3.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.domain.enums.ProductStatus;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.vo.AuctionEndRequestVO;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.ProductListVO;

@Repository
public class ProductDao {

	@Autowired
	private SqlSession sqlSession;

	private static final String NAMESPACE = "product.";

	public long sequence() {
		Number n = sqlSession.selectOne(NAMESPACE + "sequence");
		return n.longValue();
	}

	public int insert(ProductDto productDto) {
		return sqlSession.insert(NAMESPACE + "add", productDto);
	}

	public List<ProductDto> selectList() {
		return sqlSession.selectList(NAMESPACE + "list");
	}

	public ProductDto selectOne(long productNo) {
		return sqlSession.selectOne(NAMESPACE + "detail", productNo);
	}

	public int delete(long productNo) {
		return sqlSession.delete(NAMESPACE + "delete", productNo);
	}

	public boolean update(ProductDto productDto) {
		return sqlSession.update(NAMESPACE + "update", productDto) > 0;
	}

	public boolean updateUnit(ProductDto productDto) {
		return sqlSession.update(NAMESPACE + "updateUnit", productDto) > 0;
	}

	public int updateStatus(long productNo, ProductStatus changeStatus) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		params.put("changeStatus", changeStatus.getStatus());
		return sqlSession.update(NAMESPACE + "updateStatus", params);
	}

	public int updateEndedAuction(AuctionEndRequestVO endRequestVO) {
		return sqlSession.update(NAMESPACE + "updateEndedAuction", endRequestVO);
	}

	public ProductDto selectOneForUpdate(long productNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		return sqlSession.selectOne(NAMESPACE + "selectOneForUpdate", params);
	}

	public List<Long> findExpiredProductNos() {
		return sqlSession.selectList(NAMESPACE + "findExpiredProductNos");
	}

  public List<Long> findStartableProductNos(){
		return sqlSession.selectList(NAMESPACE + "findStartableProductNos");
	}
  
	public long findSellerNoByProductNo(long productNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("productNo", productNo);
		Number n = sqlSession.selectOne(NAMESPACE + "findSellerNoByProductNo", params);
		return n == null ? 0L : n.longValue();
	}
  
//   /** 상품 정보 수정 */
//    public boolean update(ProductDto productDto) {
//        return sqlSession.update(NAMESPACE + "update", productDto) > 0;
//    }
//
//    /** 상품 단위 가격 수정 */
//    public boolean updateUnit(ProductDto productDto) {
//        return sqlSession.update(NAMESPACE + "updateUnit", productDto) > 0;
//    }

	public int count() {
		return sqlSession.selectOne(NAMESPACE + "countByPaging");
	}

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

	public int countByBidding() {
		return sqlSession.selectOne(NAMESPACE + "countByBidding");
	}

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
	
	// ProductDao.java (추가)
	public int countAuction() {
	    return sqlSession.selectOne("product.countAuction");
	}

	public List<ProductDto> selectAuctionListByPaging(PageVO pageVO) {
	    Map<String, Integer> params = new HashMap<>();
	    params.put("begin", pageVO.getBegin());
	    params.put("end", pageVO.getEnd());
	    return sqlSession.selectList("product.listAuctionByPaging", params);
	}
}
