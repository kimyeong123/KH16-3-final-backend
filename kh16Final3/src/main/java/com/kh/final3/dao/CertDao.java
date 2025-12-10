package com.kh.final3.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.CertDto;


@Repository
public class CertDao {
	@Autowired
	private SqlSession sqlSession;
	
	public void insert(CertDto certDto) {
		sqlSession.insert("cert.insert", certDto);
	}
	public boolean update(CertDto certDto) {
		return sqlSession.update("cert.update", certDto) > 0;
	}
	public boolean delete(String certEmail) {
		return sqlSession.delete("cert.delete", certEmail) > 0;
	}
	public CertDto selectOne(String certEmail) {
	    CertDto param = CertDto.builder().certEmail(certEmail).build();
	    return sqlSession.selectOne("cert.detail", param);
	}

}