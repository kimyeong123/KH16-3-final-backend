package com.kh.final3.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.PointWithdrawDto;
import com.kh.final3.vo.PageVO;

@Repository
public class WithdrawDao {

    private static final String NAMESPACE = "withdraw.";

    @Autowired
    private SqlSession sqlSession;

    // 환전 요청 생성
    public long insert(PointWithdrawDto dto) {
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

    // 상태별 개수
    public int countByStatus(String status) {
        return sqlSession.selectOne(NAMESPACE + "countByStatus", status);
    }

    // 상태별 페이징 목록
    public List<PointWithdrawDto> listByStatusPaging(PageVO<?> vo, String status) {
        Map<String, Object> param = new HashMap<>();
        param.put("vo", vo);
        param.put("status", status);
        return sqlSession.selectList(NAMESPACE + "listByStatusPaging", param);
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
