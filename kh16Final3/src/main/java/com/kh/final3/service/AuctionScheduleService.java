/*
 * package com.kh.final3.service;
 * 
 * import java.util.ArrayList; import java.util.List;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.stereotype.Service;
 * 
 * import com.kh.final3.dao.ProductDao;
 * 
 * import lombok.extern.slf4j.Slf4j;
 * 
 * @Slf4j
 * 
 * @Service public class AuctionScheduleService {
 * 
 * @Autowired private ProductDao productDao;
 * 
 * @Autowired private AuctionService auctionService;
 * 
 * // 트랜젝션을 붙이지 않는 이유는 내부의 반복문 때문(내부사용 메소드에 transaction 걸어서 상품단위로 트랜젝션 일어나게 유도)
 * public void processExpiredAuctions() { List<Long> expiredProductNos =
 * productDao.findExpiredProductNos();
 * 
 * List<Long> failedNos = new ArrayList<>();
 * 
 * for (Long productNo : expiredProductNos) { try {
 * auctionService.handleSingleAuctionEnd(productNo); // endAuction or
 * noBidAuction } catch (Exception e) { failedNos.add(productNo); // 실패한 번호 기록 }
 * }
 * 
 * // 실패한 상품 재시도 1회 for (Long failedNo : failedNos) { try {
 * auctionService.handleSingleAuctionEnd(failedNo); } catch (Exception e) {
 * log.warn("경매 종료 처리 실패 (두 번 실패): productNo={}", failedNo, e); } } }
 * 
 * 
 * public void processStartableAuctions() { List<Long> startableProductNos =
 * productDao.findStartableProductNos();
 * 
 * List<Long> failedNos = new ArrayList<>();
 * 
 * for (Long productNo : startableProductNos) { try {
 * auctionService.handleSingleAuctionStart(productNo); } catch (Exception e) {
 * failedNos.add(productNo); } }
 * 
 * 
 * // 실패 재시도 1회 for (Long failedNo : failedNos) { try {
 * auctionService.handleSingleAuctionStart(failedNo); } catch (Exception e) {
 * log.warn("경매 시작 처리 실패 (두 번 실패): productNo={}", failedNo, e); } } }
 * 
 * }
 */
