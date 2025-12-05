package com.kh.final3.dao;

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
	
	public void insert(MemberDto memberDto) {
		sqlSession.insert("member.insert", memberDto);
	}
	public MemberDto selectOneByMemberId(String memberId) {
		return sqlSession.selectOne("member.detailByMemberId", memberId);			
	}
	public MemberDto selectOneByMemberNickname(String memberNickname)
	{
		return sqlSession.selectOne("member.detailByMemberNickname", memberNickname);	
	}
	public List<MemberDto> selectList(String column, String keyword) {
		Map<String, Object> params = new HashMap<>();
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("member.search", params);
	}
}
