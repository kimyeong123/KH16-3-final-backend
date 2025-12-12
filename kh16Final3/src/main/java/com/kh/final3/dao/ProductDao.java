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
	
	public int update(ProductDto productDto) {
		return sqlSession.update(NAMESPACE + "update", productDto);
	}
	
	public int updateUnit(ProductDto productDto) {
		return sqlSession.update(NAMESPACE + "updateUnit", productDto);
	}
	
	public int count() {
		return sqlSession.selectOne(NAMESPACE + "countByPaging");
		
	}
	public List<ProductDto>selectList(PageVO pageVO){
		Map<String,Integer>params=new HashMap<>();
		params.put("begin", pageVO.getBegin());
		params.put("end", pageVO.getEnd());
		return sqlSession.selectList(NAMESPACE + "listByPaging", params);
		
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
	
}
