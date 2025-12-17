package com.kh.final3.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.BoardDto;

@Repository
public class BoardDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 1. 시퀀스 번호 발급
	public long sequence() {
		return sqlSession.selectOne("board.sequence");
	}
	
	// 2. 등록 (Service에서 번호가 설정된 DTO를 받아 DB에 삽입)
	public boolean insert(BoardDto boardDto) {
		return sqlSession.insert("board.insert", boardDto) > 0;
	}
	
	// 3. 전체 목록 조회
	public List<BoardDto> selectList() {
	    return sqlSession.selectList("board.list"); 
	}
	
	// 4. 단일 게시글 조회
	public BoardDto selectOne(long boardNo) {
		return sqlSession.selectOne("board.detail", boardNo); 
	}
    
    // 5. 조회수 증가 (Service에서 트랜잭션 걸고 호출)
    public boolean updateBoardRead(long boardNo) {
        return sqlSession.update("board.updateBoardRead", boardNo) > 0;
    }
	
	// 6. 수정
	public boolean update(BoardDto boardDto) {
		return sqlSession.update("board.update", boardDto) > 0;
	}
	
	// 7. 삭제
	public boolean delete(long boardNo) {
		return sqlSession.delete("board.delete", boardNo) > 0;
	}

	// 8. 타입별 조회
	public List<BoardDto> selectListByType(String type) {
		return sqlSession.selectList("board.listByType", type);
	}
	
	// 9. 전체 개수 조회 (Service에서 type을 "NOTICE"로 전달 가정)
	public int selectCountByType(Map<String, Object> params) {
	    return sqlSession.selectOne("board.selectCount", params);
	}

	// 10. 페이징된 목록 조회 
		public List<BoardDto> selectListByPaging(Map<String, Object> params) {
		    return sqlSession.selectList("board.selectListByPaging", params);
		}

	
}