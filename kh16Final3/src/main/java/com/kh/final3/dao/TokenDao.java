package com.kh.final3.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.TokenDto;



@Repository
public class TokenDao {
@Autowired
private SqlSession sqlSession;

public void insert(TokenDto tokenDto) {
	sqlSession.insert("Token.insert", tokenDto);
}

//public AccountTokenDto selectOne(Long accountTokenNo) {//지금 해당 없음
public TokenDto selectOne(TokenDto tokenDto) {
	return sqlSession.selectOne("Token.detail", tokenDto);
}

public boolean delete(Long tokenNo) {
	return sqlSession.delete("Token.delete", tokenNo) > 0;
}
public boolean deleteByTarget(String targetId) {
	return sqlSession.delete("Token.deleteByTarget", targetId) > 0;
}
	
}
