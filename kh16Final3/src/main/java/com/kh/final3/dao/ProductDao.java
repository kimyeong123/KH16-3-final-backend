package com.kh.final3.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.ProductDto;
import com.kh.final3.vo.PageVO;

@Repository
public class ProductDao {

    @Autowired
    private SqlSession sqlSession;

    /** 상품 번호 시퀀스 조회 */
    public int sequence() {
        return sqlSession.selectOne("product.sequence");
    }

    /** 상품 등록 */
    public void insert(ProductDto productDto) {
        sqlSession.insert("product.add", productDto);
    }

    /** 상품 목록 조회 */
    public List<ProductDto> selectList() {
        return sqlSession.selectList("product.list");
    }

    /** 상품 상세 조회 */
    public ProductDto selectOne(Long productNo) {
        return sqlSession.selectOne("product.detail", productNo);
    }

    /** 상품 삭제 */
    public boolean delete(Long productNo) {
        return sqlSession.delete("product.delete", productNo) > 0;
    }

    /** 상품 정보 수정 */
    public boolean update(ProductDto productDto) {
        return sqlSession.update("product.update", productDto) > 0;
    }

    /** 상품 단위 가격 수정 */
    public boolean updateUnit(ProductDto productDto) {
        return sqlSession.update("product.updateUnit", productDto) > 0;
    }

    /** 페이지네이션을 위한 상품 전체 개수 조회 */
    public int count() {
        return sqlSession.selectOne("product.countByPaging");
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