package com.kh.final3.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.PaymentDto;

@Repository
public class PaymentDao {

    @Autowired
    private SqlSession sqlSession;

    public int sequence() {
        return sqlSession.selectOne("payment.sequence");
    }

    public void insert(PaymentDto paymentDto) {
        sqlSession.insert("payment.add", paymentDto);
    }

    public List<PaymentDto> selectList() {
        return sqlSession.selectList("payment.list");
    }

    public PaymentDto selectOne(int paymentNo) {
        return sqlSession.selectOne("payment.detail", paymentNo);
    }

    public List<PaymentDto> selectListByMember(int memberNo) {
        return sqlSession.selectList("payment.listByMember", memberNo);
    }

    public boolean delete(int paymentNo) {
        return sqlSession.delete("payment.delete", paymentNo) > 0;
    }
}
