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

@Repository
public class ProductDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "product."; 
	
	public long sequence() {
		return sqlSession.selectOne(NAMESPACE + "sequence");
	}
	
	public int insert(ProductDto productDto) {
		return sqlSession.insert(NAMESPACE + "add", productDto);
	}
	
	public List<ProductDto>selectList(){
		return sqlSession.selectList(NAMESPACE + "list");
	}
	
	public ProductDto selectOne(long productNo) {
		return sqlSession.selectOne(NAMESPACE + "detail", productNo);
	}
	
	public int delete(long productNo) {
		return sqlSession.delete(NAMESPACE + "delete", productNo);
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
		return sqlSession.selectOne(NAMESPACE + "selectOneForUpdate", productNo);
	}
	
	public List<Long> findExpiredProductNos(){
		return sqlSession.selectList(NAMESPACE + "findExpiredProductNos");
	}
	
	public long findSellerNoByProductNo(long productNo) {
		return sqlSession.selectOne(NAMESPACE + "findSellerNoByProductNo");
	}

    /** 상품 정보 수정 */
    public boolean update(ProductDto productDto) {
        return sqlSession.update(NAMESPACE + "update", productDto) > 0;
    }

    /** 상품 단위 가격 수정 */
    public boolean updateUnit(ProductDto productDto) {
        return sqlSession.update(NAMESPACE + "updateUnit", productDto) > 0;
    }

    /** 페이지네이션을 위한 상품 전체 개수 조회 */
    public int count() {
        return sqlSession.selectOne(NAMESPACE + "countByPaging");
    }

    /** 페이지네이션 목록 조회 */
    public List<ProductDto> selectList(PageVO pageVO) {
        Map<String, Integer> params = new HashMap<>();
        params.put("begin", pageVO.getBegin());
        params.put("end", pageVO.getEnd());
        return sqlSession.selectList("product.listByPaging", params);
    }

    /** 입찰 발생 시: 현재가, 입찰자, 상태 갱신 */
    public boolean updateOnBid(Long productNo, Long bidMemberNo, Long bidAmount) {
        Map<String, Object> param = new HashMap<>();
        param.put("productNo", productNo);
        param.put("bidMemberNo", bidMemberNo);
        param.put("bidAmount", bidAmount);
        return sqlSession.update("product.updateOnBid", param) > 0;
    }

    /** 경매 종료/낙찰 확정 */
    public boolean closeAuction(Long productNo) {
        return sqlSession.update("product.closeAuction", productNo) > 0;
    }
}
