package com.kh.final3.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.SanctionDto;

@Repository
public class SanctionDao {

	@Autowired
	private SqlSession sqlSession;
	
	// 1. 시퀀스 번호 발급
	public long sequence() {
		return sqlSession.selectOne("sanction.sequence");
	}
	
	// 2. 제재 기록 등록 (INSERT)
	public boolean insert(SanctionDto sanctionDto) {
		return sqlSession.insert("sanction.insert", sanctionDto) > 0;
	}
	
	// 3. 제재 기록 상세 조회 (PK: sanctionNo)
	public SanctionDto selectOne(long sanctionNo) {
		return sqlSession.selectOne("sanction.selectOne", sanctionNo);
	}
	
	// 4. 특정 회원의 유효한 제재 목록 조회 (selectActiveSanctions로 변경)
	public List<SanctionDto> selectActiveSanctions(long memberNo) {
		return sqlSession.selectList("sanction.selectValidSanctionsByMember", memberNo);
	}
	
	// 5. 제재 상태 변경 (STATUS: 'Y' or 'N')
	public boolean updateStatus(SanctionDto sanctionDto) {
		return sqlSession.update("sanction.updateStatus", sanctionDto) > 0;
	}
	
	// 6. 제재 기록 삭제 (DELETE)
	public boolean delete(long sanctionNo) {
		return sqlSession.delete("sanction.delete", sanctionNo) > 0;
	}
	
	// --- 관리자 페이지 - 전체 제재 기록 목록 기능 ---

	// 7. 전체 제재 기록 개수 조회 (페이지네이션용)
	public int count() {
		return sqlSession.selectOne("sanction.count");
	}
	
	// 8. 전체 제재 기록 목록 조회 (페이지네이션 적용)
	public List<SanctionDto> selectHistoryWithPaging(int begin, int end) {
		Map<String, Object> params = new HashMap<>();
		params.put("begin", begin);
		params.put("end", end);
		
		return sqlSession.selectList("sanction.selectHistoryWithPaging", params);
	}
}