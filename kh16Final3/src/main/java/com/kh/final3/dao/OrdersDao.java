package com.kh.final3.dao;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.domain.enums.OrderStatus;
import com.kh.final3.dto.OrdersDto;
import com.kh.final3.vo.OrderShippingRequestVO;
import com.kh.final3.vo.OrderTrackingUpdateVO;

@Repository
public class OrdersDao {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "orders.";

    public long sequence() {
        return sqlSession.selectOne(NAMESPACE + "sequence");
    }

    public int insert(OrdersDto ordersDto) {
        return sqlSession.insert(NAMESPACE + "insert", ordersDto);
    }

    // 단건 조회
    public OrdersDto selectOne(long orderNo) {
        return sqlSession.selectOne(NAMESPACE + "selectOne", orderNo);
    }
    
    // 수정 용 조회(lock)
    public OrdersDto selectOneForUpdate(long orderNo) {
        return sqlSession.selectOne(NAMESPACE + "selectOneForUpdate", orderNo);
    }
    
    // 구매자 기준 주문 목록 조회
    public List<OrdersDto> selectByBuyer(long buyerNo) {
        return sqlSession.selectList(NAMESPACE + "selectByBuyer", buyerNo);
    }

    // 판매자 기준 주문 목록 조회
    public List<OrdersDto> selectBySeller(long sellerNo) {
        return sqlSession.selectList(NAMESPACE + "selectBySeller", sellerNo);
    }

    // 송장 번호 업데이트 + 상태 변경
    public int updateTrackingInfo(OrderTrackingUpdateVO trackingUpdateVO) {
        return sqlSession.update(NAMESPACE + "updateTrackingInfo", trackingUpdateVO);
    }

    // 상태 업데이트
    public int updateStatus(long orderNo, OrderStatus changeStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        params.put("changeStatus", changeStatus);
        return sqlSession.update(NAMESPACE + "updateStatus", params);
    }

    // COMPLETED로 완료 처리
    public int completeOrder(long orderNo) {
        return sqlSession.update(NAMESPACE + "completeOrder", orderNo);
    }
    
    // 수령자 정보 입력(업데이트)
    public int updateShippingAddress(OrderShippingRequestVO shippingRequestVO) {
    	return sqlSession.update(NAMESPACE + "updateShippingAddress", shippingRequestVO);
    }
    
    // 정산 대상 찾기(pk 리스트 반환)
    public List<Long> findSettlementTargets(int days){
    	return sqlSession.selectList(NAMESPACE + "findSettlementTargets", days);
    }
    
}
