package com.kh.final3.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.CommentDto;

@Repository
public class CommentDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public long sequence() {
		return sqlSession.selectOne("comment.sequence");
	}
	
	public boolean insert(CommentDto commentDto) {
		return sqlSession.insert("comment.insert", commentDto) > 0;
	}
	
//    특정 게시글의 댓글 목록을 조회하는 메서드
	public List<CommentDto> selectList(long boardNo) {
	    return sqlSession.selectList("comment.list", boardNo); 
	}
	
//    특정 댓글의 상세 정보를 조회하는 메서드
	public CommentDto selectOne(long commentNo) {
		return sqlSession.selectOne("comment.detail", commentNo);
	}
	
	public boolean update(CommentDto commentDto) {
		return sqlSession.update("comment.update", commentDto) > 0;
	}
	
     // DB에 남겨두기위해  댓글을 소프트 삭제하는 메서드
	public boolean softDelete(long commentNo) {
		return sqlSession.update("comment.softDelete", commentNo) > 0; // 매퍼 ID는 softDelete 유지
	}
}