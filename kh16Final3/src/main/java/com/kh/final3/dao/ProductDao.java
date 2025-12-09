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

    public int sequence() {
        return sqlSession.selectOne("product.sequence");
    }

    public void insert(ProductDto productDto) {
        sqlSession.insert("product.add", productDto);
    }

    public List<ProductDto> selectList() {
        return sqlSession.selectList("product.list");
    }

    public ProductDto selectOne(Long productNo) {
        return sqlSession.selectOne("product.detail", productNo);
    }

    public boolean delete(Long productNo) {
        return sqlSession.delete("product.delete", productNo) > 0;
    }

    public boolean update(ProductDto productDto) {
        return sqlSession.update("product.update", productDto) > 0;
    }

    public boolean updateUnit(ProductDto productDto) {
        return sqlSession.update("product.updateUnit", productDto) > 0;
    }

    public int count() {
        return sqlSession.selectOne("product.countByPaging");
    }

    public List<ProductDto> selectList(PageVO pageVO) {
        Map<String, Integer> params = new HashMap<>();
        params.put("begin", pageVO.getBegin());
        params.put("end", pageVO.getEnd());
        return sqlSession.selectList("product.listByPaging", params);
    }

    public boolean updateOnBid(Long productNo, Long bidMemberNo, Long bidAmount) {
        Map<String, Object> param = new HashMap<>();
        param.put("productNo", productNo);
        param.put("bidMemberNo", bidMemberNo);
        param.put("bidAmount", bidAmount);
        return sqlSession.update("product.updateOnBid", param) > 0;
    }

    public boolean closeAuction(Long productNo) {
        return sqlSession.update("product.closeAuction", productNo) > 0;
    }
}
