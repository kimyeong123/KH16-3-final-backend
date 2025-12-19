package com.kh.final3.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.PointWithdrawDto;

@Repository
public class WithdrawDao {

    private static final String NAMESPACE = "withdraw.";

    @Autowired
    private SqlSession sqlSession;

    // 환전 요청 생성
    public long insert(PointWithdrawDto dto) {
        // PK 생성은 시퀀스 nextval을 별도로 뽑아 세팅하거나,
        // insert에서 seq를 쓰고 selectKey로 받아와도 됨.
        sqlSession.insert(NAMESPACE + "insert", dto);
        return dto.getWithdrawNo(); // selectKey 사용 시 들어옴
    }

    // 단건 조회
    public PointWithdrawDto selectOne(long withdrawNo) {
        return sqlSession.selectOne(NAMESPACE + "selectOne", withdrawNo);
    }

    // 관리자: 상태별 목록
    public List<PointWithdrawDto> listByStatus(String status) {
        return sqlSession.selectList(NAMESPACE + "listByStatus", status);
    }

    // 마이페이지: 내 환전 내역
    public List<PointWithdrawDto> listByMember(long memberNo) {
        return sqlSession.selectList(NAMESPACE + "listByMember", memberNo);
    }

    public int approve(PointWithdrawDto dto) {
        return sqlSession.update(NAMESPACE + "approve", dto);
    }

    public int reject(PointWithdrawDto dto) {
        return sqlSession.update(NAMESPACE + "reject", dto);
    }

}
