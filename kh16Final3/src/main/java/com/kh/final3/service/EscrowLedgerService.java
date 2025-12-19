package com.kh.final3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.final3.dao.EscrowLedgerDao;
import com.kh.final3.domain.enums.EscrowStatus;
import com.kh.final3.dto.BidDto;
import com.kh.final3.dto.EscrowLedgerDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.helper.AuctionHelper;

@Service
public class EscrowLedgerService {
	
	@Autowired
	private EscrowLedgerDao escrowLedgerDao;
	
	@Autowired
	private AuctionHelper auctionHelperService;
	
	public void registerEscrowForBid(BidDto bidDto) {
		long escrowLedgerNo = escrowLedgerDao.sequence();
		EscrowLedgerDto escrowLedgerDto = 
				auctionHelperService.createEscrowDtoByBid(bidDto, escrowLedgerNo, EscrowStatus.HELD);
		
		escrowLedgerDao.insert(escrowLedgerDto);
	}
	
	public void updateEscrowForBid(long bidNo, EscrowStatus status) {
		Long escrowNo = escrowLedgerDao.findEscrowNoByBidNo(bidNo);
		if(escrowNo == null)
			throw new TargetNotfoundException("존재하지 않는 에스크로 입니다.");
		
		escrowLedgerDao.updateStatusByEscrowNo(escrowNo, status);
	}
	
	public void markEscrowAsSettled(long productNo) {
		Long escrowNo = 
				escrowLedgerDao.findEscrowNoByProductNoAndStatus(productNo, EscrowStatus.PENDING_SETTLEMENT);
		// 스케줄러 기반 자동 정산 로직이므로
		// 중복 실행 / 이미 처리된 경우는 예외 없이 return (idempotent 처리)
		// 사용자 호출 메소드에서는 예외 처리함
		if(escrowNo == null) return;
		
		escrowLedgerDao.updateStatusByEscrowNo(escrowNo, EscrowStatus.SETTLED);
	}
	
}
