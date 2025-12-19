package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.MemberDao;
import com.kh.final3.dao.PointHistoryDao;
import com.kh.final3.dao.WithdrawDao;
import com.kh.final3.dto.PointWithdrawDto;
import com.kh.final3.error.TargetNotfoundException;

@Service
public class WithdrawService {

    @Autowired private WithdrawDao withdrawDao;
    @Autowired private PointHistoryDao pointHistoryDao;
    @Autowired private MemberDao memberDao;

    @Transactional
    public long request(PointWithdrawDto dto) {
        // 0) member 포인트 차감 (부족하면 업데이트 0행)
        int r1 = memberDao.deductMemberPoint(dto.getMemberNo(), dto.getAmount());
        if (r1 == 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        // 1) 환전 요청 insert
        long withdrawNo = withdrawDao.insert(dto);

        // 2) 원장 기록
        pointHistoryDao.insertWithdrawDeduct(dto);

        return withdrawNo;
    }

    @Transactional
    public void approve(long withdrawNo, long adminNo) {
        PointWithdrawDto dto = new PointWithdrawDto();
        dto.setWithdrawNo(withdrawNo);
        dto.setProcessedBy(adminNo);

        int result = withdrawDao.approve(dto);
        if (result == 0) {
            throw new TargetNotfoundException("처리할 환전 요청이 없거나 이미 처리되었습니다.");
        }
    }

    @Transactional
    public void reject(long withdrawNo, long adminNo, String rejectReason) {
        PointWithdrawDto dto = withdrawDao.selectOne(withdrawNo);
        if (dto == null) throw new TargetNotfoundException("환전 요청이 존재하지 않습니다.");

        dto.setProcessedBy(adminNo);
        dto.setRejectReason(rejectReason);

        int result = withdrawDao.reject(dto);
        if (result == 0) {
            throw new TargetNotfoundException("처리할 환전 요청이 없거나 이미 처리되었습니다.");
        }

        // 1) member 포인트 복구
        memberDao.addMemberPoint(dto.getMemberNo(), dto.getAmount());

        // 2) 원장 복구 기록
        pointHistoryDao.insertWithdrawRefund(dto);
    }
}
