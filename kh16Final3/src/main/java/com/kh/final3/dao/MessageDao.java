package com.kh.final3.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.MessageDto;

@Repository
public class MessageDao {

	@Autowired
	private SqlSession sqlSession;

	private static final String NAMESPACE = "message";

	// 1. 시퀀스 번호 발급
	public int sequence() {
		return sqlSession.selectOne(NAMESPACE + ".sequence");
	}

	// 쪽지/알림 등록 (제재 알림 포함)
	public boolean insert(MessageDto messageDto) {
		return sqlSession.insert(NAMESPACE + ".insert", messageDto) > 0;
	}

	// 3. 발신함 목록 조회
	public List<MessageDto> selectSentList(long memberNo) {
		return sqlSession.selectList(NAMESPACE + ".selectSentList", memberNo);
	}

	// 4. 수신함 목록 조회
	public List<MessageDto> selectReceivedList(long memberNo) {
		return sqlSession.selectList(NAMESPACE + ".selectReceivedList", memberNo);
	}

	// 5. 쪽지 상세 조회
	public MessageDto selectOne(int messageNo) {
		return sqlSession.selectOne(NAMESPACE + ".selectOne", messageNo);
	}

	// 6. 쪽지 읽음 처리 (DB 컬럼명: is_read, read_time 사용)
	public boolean updateReadTime(int messageNo) {
		return sqlSession.update(NAMESPACE + ".updateReadTime", messageNo) > 0;
	}

	// 7. 발신자 삭제 처리 (DB 컬럼명: sender_deleted 사용)
	public boolean updateSenderDelete(int messageNo) {
		return sqlSession.update(NAMESPACE + ".updateSenderDelete", messageNo) > 0;
	}

	// 8. 수신자 삭제 처리 (DB 컬럼명: receiver_deleted 사용)
	public boolean updateReceiverDelete(int messageNo) {
		return sqlSession.update(NAMESPACE + ".updateReceiverDelete", messageNo) > 0;
	}

	// 9. 미확인 시스템 알림 개수 조회 (DB 컬럼명: receiver_no, is_read, type 사용)
	public int countUnreadAlerts(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + ".countUnreadAlerts", memberNo);
	}
	
	// 9-2 . 전체 미확인 개수 조회
	public int countUnreadAll(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + ".countUnreadAll", memberNo);
	}

	// 10. 특정 타입 목록의 쪽지를 조회하는 메서드
	public List<MessageDto> selectReceivedListByTypes(Map<String, Object> paramMap) {
		return sqlSession.selectList(NAMESPACE + ".selectReceivedListByTypes", paramMap);
	}

	// 11. 보낸 쪽지함 개수 조회
	public long countSent(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + ".countSent", memberNo);
	}

	// 12. 받은 쪽지함 개수 조회
	public long countReceived(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + ".countReceived", memberNo);
	}

	// 13. 보낸 쪽지함 페이징 목록 조회
	public List<MessageDto> selectSentListByPaging(Map<String, Object> paramMap) {
		return sqlSession.selectList(NAMESPACE + ".selectSentListByPaging", paramMap);
	}

	// 14. 받은 쪽지함 페이징 목록 조회
	public List<MessageDto> selectReceivedListByPaging(Map<String, Object> paramMap) {
		return sqlSession.selectList(NAMESPACE + ".selectReceivedListByPaging", paramMap);
	}

	public void insert(long memberNo, String title, String content) {
		// TODO Auto-generated method stub

	}

}