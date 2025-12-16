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

	// ----------------------------------------------------
	// 1. 등록/조회/업데이트 기본 기능
	// ----------------------------------------------------
	
	// 1. 시퀀스 번호 발급
	public int sequence() {
		return sqlSession.selectOne(NAMESPACE + ".sequence");
	}

	// 2. 쪽지/알림 등록
	public boolean insert(MessageDto messageDto) {
		return sqlSession.insert(NAMESPACE + ".insert", messageDto) > 0;
	}

	// 3. 쪽지 상세 조회
	public MessageDto selectOne(long messageNo) {
		return sqlSession.selectOne(NAMESPACE + ".selectOne", messageNo);
	}

	// 4. 쪽지 읽음 처리 (update read_time)
	public boolean updateReadTime(long messageNo) {
		return sqlSession.update(NAMESPACE + ".updateReadTime", messageNo) > 0;
	}

	// 5. 발신자 삭제 처리 (update sender_deleted)
	public boolean updateSenderDelete(long messageNo) {
		return sqlSession.update(NAMESPACE + ".updateSenderDelete", messageNo) > 0;
	}

	// 6. 수신자 삭제 처리 (update receiver_deleted)
	public boolean updateReceiverDelete(long messageNo) {
		return sqlSession.update(NAMESPACE + ".updateReceiverDelete", messageNo) > 0;
	}
    
	// ----------------------------------------------------
	// 2. 카운트 및 미확인 목록 기능
	// ----------------------------------------------------

	// 7. 미확인 시스템 알림 개수 조회 (type='ALERT' & is_read='N')
	public Long countUnreadAlerts(long memberNo) {
		return sqlSession.selectOne(NAMESPACE + ".countUnreadAlerts", memberNo);
	}
	
	// 8. 헤더 드롭다운용 미확인 쪽지 목록 조회
	public List<MessageDto> selectUnreadList(long receiverNo) {
        return sqlSession.selectList(NAMESPACE + ".selectUnreadList", receiverNo);
    }
    
	// ----------------------------------------------------
	// 3. 페이지네이션 (카운트 및 목록 조회) 기능
	// ----------------------------------------------------
	
	/**
	 * 9. 발신함 전체 개수 조회 (필터링 포함)
	 * // @param paramMap (memberNo, types)
	 */
	public long countSent(Map<String, Object> paramMap) { // 💡 [수정] 파라미터 타입을 Map으로 변경
		return sqlSession.selectOne(NAMESPACE + ".countSent", paramMap);
	}

	/**
	 * 10. 수신함 전체 개수 조회 (필터링 포함)
	 * // @param paramMap (memberNo, types)
	 */
	public long countReceived(Map<String, Object> paramMap) { // 💡 [수정] 파라미터 타입을 Map으로 변경
		return sqlSession.selectOne(NAMESPACE + ".countReceived", paramMap);
	}

	/**
	 * 11. 발신함 페이징 목록 조회 (페이징/필터링 포함)
	 * // @param paramMap (memberNo, begin, end, types)
	 */
	public List<MessageDto> selectSentListByPaging(Map<String, Object> paramMap) {
		return sqlSession.selectList(NAMESPACE + ".selectSentListByPaging", paramMap);
	}

	/**
	 * 12. 수신함 페이징 목록 조회 (페이징/필터링 포함)
	 * // @param paramMap (memberNo, begin, end, types)
	 */
	public List<MessageDto> selectReceivedListByPaging(Map<String, Object> paramMap) {
		return sqlSession.selectList(NAMESPACE + ".selectReceivedListByPaging", paramMap);
	}
}