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
	
	public int sequence() {
		return sqlSession.selectOne("bid.sequence");
	}
	
	public void insert(BidDto bidDto) {
		sqlSession.insert("bid.add", bidDto);
	}
	
    public List<BidDto> listByProduct(int productNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("bidProduct", productNo); // XML의 #{bidProduct}와 이름 맞추기
        return sqlSession.selectList("bid.listByProduct", param);
	
}
}