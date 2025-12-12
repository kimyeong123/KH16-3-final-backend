package com.kh.final3.dao;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.MemberDto;

@Repository
public class MemberDao {

	@Autowired
    private SqlSession sqlSession;
	
	@Autowired
	private static final String NAMESPACE = "member."; 
	
	public void insert(MemberDto memberDto) {
		sqlSession.insert(NAMESPACE + "insert", memberDto);
	}
	
	public MemberDto selectOneByMemberId(String id) {
		return sqlSession.selectOne(NAMESPACE + "detailByMemberId", id);			
	}
	
	public MemberDto selectOneByMemberNickname(String nickname){
		return sqlSession.selectOne(NAMESPACE + "detailByMemberNickname", nickname);	
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
	
	//중복 가입 제거
	public MemberDto selectOneByNameBirthContact(String name, LocalDate birth, String contact) {
	    Map<String, Object> param = new HashMap<>();
	    param.put("name", name);
	    param.put("birth", birth);
	    param.put("contact", contact);
	    return sqlSession.selectOne(NAMESPACE + "selectOneByNameBirthContact", param);
	}
	
	// 회원 포인트 차감
	public int deductMemberPoint(long memberNo, long amount) {
		return sqlSession.update(NAMESPACE + "deductMemberPoint");
	}

	// 회원 포인트 증가
	public int addMemberPoint(long memberNo, long amount) {
		return sqlSession.update(NAMESPACE + "addMemberPoint");
	}
	
	// 회원 포인트 조회
	public long findMemberPoint(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + "findMemberPoint", memberNo);
	}
	
}
