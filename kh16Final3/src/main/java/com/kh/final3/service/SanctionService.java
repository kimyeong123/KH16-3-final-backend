package com.kh.final3.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.SanctionDao;
import com.kh.final3.dto.SanctionDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.vo.PageVO; // 페이징 처리를 위해 PageVO import

@Service
public class SanctionService {

    @Autowired
    private SanctionDao sanctionDao;
    
    @Autowired
    private MessageService messageService; 
    
    @Autowired
    private MemberDao memberDao;

    /**
     * 1. 제재 등록 (관리자 기능)
     * 제재 DB 기록, 회원 상태 업데이트 (ROLE='SUSPENDED'), 알림 전송을 트랜잭션으로 처리합니다.
     */
    @Transactional
    public boolean imposeSanction(long memberNo, String type, int durationDay, String reason) {
        
        // 1. 제재 시작 시간과 종료 시간 계산
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Timestamp endTime = null;
        
        if (durationDay > 0) {
            long durationMillis = TimeUnit.DAYS.toMillis(durationDay);
            endTime = new Timestamp(startTime.getTime() + durationMillis);
        }
        
        // 2. SanctionDto 준비 및 시퀀스 발급 및 DB 등록
        long sanctionNo = sanctionDao.sequence();
        SanctionDto sanctionDto = SanctionDto.builder()
                .sanctionNo(sanctionNo)
                .memberNo(memberNo)
                .type(type)
                .durationDay(durationDay)
                .startTime(startTime)
                .endTime(endTime)
                .reason(reason)
                .status("Y") // 활성 상태로 등록
                .build();

        boolean sanctionInsertSuccess = sanctionDao.insert(sanctionDto);
        
        if (!sanctionInsertSuccess) {
            throw new TargetNotfoundException("제재 정보 등록 실패: Sanction DB INSERT 오류"); 
        }

//        // 3. 회원 테이블의 ROLE 컬럼을 'SUSPENDED'로 업데이트
        memberDao.updateMemberStatus(memberNo, "SUSPENDED");
        
        // 4. MessageService를 이용해 제재 알림 전송
        String endDateString = (endTime != null) ? endTime.toString() : "영구 정지";
        String alertContent = String.format("[운영알림] 귀하의 계정이 활동 정지 처리되었습니다.\n사유: %s\n종료 예정일: %s",
                                            type, reason, endDateString);
        
        messageService.sendNotification(memberNo, alertContent, null, null);
        
        return true;
    }
    
    /**
     * 2. 제재 해제 처리 (관리자 기능)
     * 제재 기록 상태 'N' 업데이트, 회원 상태 'DEFAULT' 업데이트를 트랜잭션으로 처리합니다.
     */
    @Transactional
    public boolean releaseSanction(long sanctionNo) {
        
        // 1. 해제할 제재 기록 상세 정보 조회 (회원 번호를 얻기 위해)
         SanctionDto detail = sanctionDao.selectOne(sanctionNo);
         if (detail == null) {
             throw new TargetNotfoundException("해당 제재 기록을 찾을 수 없습니다.");
         }
        
        // 2. 해당 제재 기록의 상태를 'N'으로 업데이트
        SanctionDto updateDto = SanctionDto.builder()
                                    .sanctionNo(sanctionNo)
                                    .status("N")
                                    .build();
        boolean updateSuccess = sanctionDao.updateStatus(updateDto);
        
        if (!updateSuccess) {
            throw new TargetNotfoundException("제재 해제 상태 업데이트 실패");
        }
        
        // 3.  회원 상태를 'DEFAULT'로 업데이트 (ROLE 변경)
        memberDao.updateMemberStatus(detail.getMemberNo(), "DEFAULT");
        
        String alertContent = "[운영알림] 귀하의 활동 정지 처분이 해제되었습니다. 이제 정상적으로 서비스를 이용하실 수 있습니다.";
        messageService.sendNotification(detail.getMemberNo(), alertContent, null, null);
        
        return true;
    }
    
    // ----------------------------------------------------
    // 3. 제재 기록 조회 및 페이징 기능 (관리자 및 회원용)
    // ----------------------------------------------------

    /**
     * 3-1. 특정 회원의 유효한(기간이 지나지 않은) 제재 목록 조회 (회원용)
     */
    public List<SanctionDto> getActiveSanctions(long memberNo) {
        return sanctionDao.selectActiveSanctions(memberNo);
    }
    
    /**
     * 3-2. 제재 기록 상세 조회 (관리자용)
     */
    public SanctionDto getSanctionDetail(long sanctionNo) {
        return sanctionDao.selectOne(sanctionNo);
    }
    
    /**
     * 3-3. 전체 제재 기록 목록 조회 (페이지네이션) (관리자용)
     */
    public PageVO<SanctionDto> getSanctionHistoryWithPaging(PageVO<SanctionDto> pageVO) {
        
        // 1. 전체 개수 조회 및 PageVO에 설정
        int count = sanctionDao.count();
        pageVO.setDataCount(count);

        // 2. DAO를 통해 페이징된 목록 조회
        List<SanctionDto> list = sanctionDao.selectHistoryWithPaging(pageVO.getBegin(), pageVO.getEnd());
        
        // 3. PageVO에 목록 설정
        pageVO.setList(list);
   
        return pageVO;
    }
    
    /**
     * 3-4. 제재 기록 삭제 (관리자용)
     */
    @Transactional
    public boolean deleteSanction(long sanctionNo) {
        return sanctionDao.delete(sanctionNo);
    }
}