package com.kh.final3.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.MemberDto;
import com.kh.final3.vo.member.MemberComplexSearchVO;



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
	public MemberDto selectOne(String memberId) {
		return sqlSession.selectOne("member.detail", memberId);
	}
	public List<MemberDto> selectList(String column, String keyword) {
		Map<String, Object> params = new HashMap<>();
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("member.search", params);
	}
	//
	public String findNicknameByMemberNo(long memberNo) {
	    return sqlSession.selectOne("member.findNicknameByMemberNo", memberNo);
	}
	
	//중복 가입 제거
	public MemberDto selectOneByNameBirthContact(String name, LocalDate birth, String contact) {
	    Map<String, Object> param = new HashMap<>();
	    param.put("name", name);
	    param.put("birth", birth);
	    param.put("contact", contact);
	    return sqlSession.selectOne("member.selectOneByNameBirthContact", param);
	}

	public List<MemberDto> selectList(MemberComplexSearchVO vo) {

		return sqlSession.selectList("member.complexSearch", vo);
	}

	public int deleteMember(Long memberNo) {
	    return sqlSession.delete("member.deleteMember", memberNo);
	}
	
	public boolean updateMemberStatus(long memberNo, String status) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("memberNo", memberNo);
	    params.put("status", status);
	    
	    return sqlSession.update("member.updateMemberStatus", params) > 0; 
	}
		
    // 비밀번호 확인
    public String findPasswordByMemberNo(Long memberNo) {
        return sqlSession.selectOne("member.findPasswordByMemberNo", memberNo);
    }
    // 회원번호로 회원 조회
    public MemberDto selectOneByMemberNo(Long memberNo) {
        return sqlSession.selectOne("member.selectOneByMemberNo", memberNo);
    }

    // 회원정보 수정
    public int updateMember(MemberDto member) {
        return sqlSession.update("member.updateMember", member);
    }

}
