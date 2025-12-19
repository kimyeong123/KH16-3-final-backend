package com.kh.final3.dao;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.MemberDto;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.member.MemberComplexSearchVO;
import com.kh.final3.vo.member.MemberListVO;

@Repository
public class MemberDao {

	@Autowired
	private SqlSession sqlSession;

	private static final String NAMESPACE = "member.";

	public void insert(MemberDto memberDto) {
		sqlSession.insert(NAMESPACE + "insert", memberDto);
	}

	public MemberDto selectOne(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + "detail", memberNo);
	}

	public MemberDto selectOneByMemberId(String id) {
		return sqlSession.selectOne(NAMESPACE + "detailById", id);
	}

	public MemberDto selectOneByNickname(String nickname) {
		return sqlSession.selectOne(NAMESPACE + "detailByNickname", nickname);
	}

	public List<MemberDto> selectList(String column, String keyword) {
		Map<String, Object> params = new HashMap<>();
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList(NAMESPACE + "search", params);
	}

	public String findNicknameByMemberNo(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + "findNicknameByMemberNo", memberNo);
	}

	// 아이디
	public MemberDto selectOneByEmail(String email) {
		return sqlSession.selectOne("member.selectOneByEmail", email);
	}

	// 비밀번호 찾기
	public MemberDto selectOneByIdAndEmail(String memberId, String email) {
		Map<String, Object> param = new HashMap<>();
		param.put("id", memberId);
		param.put("email", email);
		return sqlSession.selectOne("member.selectOneByIdAndEmail", param);
	}

	// 중복 가입 제거
	public MemberDto selectOneByNameBirthContact(String name, LocalDate birth, String contact) {
		Map<String, Object> param = new HashMap<>();
		param.put("name", name);
		param.put("birth", birth);
		param.put("contact", contact);
		return sqlSession.selectOne(NAMESPACE + "selectOneByNameBirthContact", param);
	}

	// 회원 포인트 차감
	public int deductMemberPoint(long memberNo, long amount) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberNo", memberNo);
		params.put("amount", amount);
		return sqlSession.update(NAMESPACE + "deductMemberPoint", params);
	}

	// 회원 포인트 증가
	public int addMemberPoint(long memberNo, long amount) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberNo", memberNo);
		params.put("amount", amount);
		return sqlSession.update(NAMESPACE + "addMemberPoint", params);
	}

	// 회원 포인트 조회
	public long findMemberPoint(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + "findMemberPoint", memberNo);
	}

	public List<MemberDto> selectList(MemberComplexSearchVO vo) {
		return sqlSession.selectList(NAMESPACE + "complexSearch", vo);
	}

	public int deleteMember(Long memberNo) {
		return sqlSession.delete(NAMESPACE + "deleteMember", memberNo);
	}

	public boolean updateMemberStatus(long memberNo, String status) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberNo", memberNo);
		params.put("status", status);

		return sqlSession.update(NAMESPACE + "updateMemberStatus", params) > 0;
	}

	// 비밀번호 확인
	public String findPasswordByMemberNo(Long memberNo) {
		return sqlSession.selectOne(NAMESPACE + "findPasswordByMemberNo", memberNo);
	}

	// 회원번호로 회원 조회
	public MemberDto selectOneByMemberNo(Long memberNo) {
		return sqlSession.selectOne(NAMESPACE + "selectOneByMemberNo", memberNo);
	}

	// 회원정보 수정
	public int updateMember(MemberDto memberDto) {
		return sqlSession.update(NAMESPACE + "updateMember", memberDto);
	}

	public int updatePassword(@Param("memberNo") Long memberNo, @Param("memberPw") String memberPw) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberNo", memberNo);
		params.put("memberPw", memberPw);
		return sqlSession.update("member.updatePassword", params);
	}

	// 관리자 회원 목록 조회(페이징)
	public List<MemberListVO> selectMemberList(PageVO<?> vo) {
		return sqlSession.selectList(NAMESPACE + "selectMemberList", vo);
	}

	// 관리자 회원 목록 검색(페이징)
	public List<MemberListVO> selectMemberListSearch(PageVO<?> vo) {
		return sqlSession.selectList(NAMESPACE + "selectMemberListSearch", vo);
	}

	// 전체 개수
	public int countMemberList() {
		return sqlSession.selectOne(NAMESPACE + "countMemberList");
	}

	// 검색 개수
	public int countMemberListSearch(PageVO<?> vo) {
		return sqlSession.selectOne(NAMESPACE + "countMemberListSearch", vo);
	}

	// 포인트 증가
	public int increasePoint(Long memberNo, Long amount) {
		Map<String, Object> param = new HashMap<>();
		param.put("memberNo", memberNo);
		param.put("amount", amount);

		return sqlSession.update(NAMESPACE + "increasePoint", param);
	}

}
