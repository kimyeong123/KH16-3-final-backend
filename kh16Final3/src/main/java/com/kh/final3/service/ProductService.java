package com.kh.final3.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.ProductDao;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.ProductListVO;
import com.kh.final3.vo.PurchaseListVO; // [추가] 구매내역 VO

@Service
public class ProductService {

	@Autowired
	private ProductDao productDao;

	// [수정] 상품 등록 (날짜 유효성 검사 추가됨)
	@Transactional
	public ProductDto create(ProductDto productDto, Long loginMemberNo) {
		// 날짜 유효성 검사 로직
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime start = productDto.getStartTime();
		LocalDateTime end = productDto.getEndTime();

		// 1. 시작 시간이 현재 시간보다 너무 과거인 경우 (네트워크 지연 감안 1분 여유)
		if (start.isBefore(now.minusMinutes(1))) {
			throw new RuntimeException("시작 시간은 과거일 수 없습니다.");
		}

		// 2. 마감 시간이 시작 시간보다 빠른 경우
		if (end.isBefore(start)) {
			throw new RuntimeException("마감 시간은 시작 시간보다 이후여야 합니다.");
		}
		
		// 기존 등록 로직 유지
		long productNo = productDao.sequence();
		productDto.setProductNo(productNo);
		productDto.setSellerNo(loginMemberNo);
		productDao.insert(productDto);
		return productDto;
	}

	@Transactional(readOnly = true)
	public ProductDto get(Long productNo) {
		return productDao.selectOne(productNo);
	}

	@Transactional(readOnly = true)
	public long getSellerNo(long productNo) {
		return productDao.findSellerNoByProductNo(productNo);
	}

	@Transactional
	public void delete(Long productNo) {
        ProductDto product = productDao.selectOne(productNo);
        if (product == null) throw new RuntimeException("존재하지 않는 상품입니다.");

        String status = product.getStatus();
        if ("BIDDING".equals(status) || "ENDED".equals(status) || "CLOSED".equals(status)) {
            throw new RuntimeException("입찰이 진행 중이거나 종료된 경매는 삭제할 수 없습니다.");
        }
        
        productDao.deleteEscrow(productNo);
        productDao.deleteBid(productNo);           
        productDao.deletePointHistory(productNo);  
        productDao.deleteReview(productNo);
        productDao.deleteMessage(productNo);
        productDao.deleteOrders(productNo);

		productDao.delete(productNo);
	}

	@Transactional
	public void edit(Long productNo, ProductDto productDto) {
        ProductDto origin = productDao.selectOne(productNo);
        if (origin == null) throw new RuntimeException("존재하지 않는 상품입니다.");

        if ("BIDDING".equals(origin.getStatus())) {
            throw new RuntimeException("입찰이 진행 중인 상품은 정보를 수정할 수 없습니다.");
        }

		productDto.setProductNo(productNo);
		productDao.update(productDto);
	}

	@Transactional
	public void patch(Long productNo, ProductDto productDto) {
        ProductDto origin = productDao.selectOne(productNo);
        if (origin == null) throw new RuntimeException("존재하지 않는 상품입니다.");

        if ("BIDDING".equals(origin.getStatus())) {
            throw new RuntimeException("입찰이 진행 중인 상품은 정보를 수정할 수 없습니다.");
        }

		productDto.setProductNo(productNo);
		productDao.updateUnit(productDto);
	}

	@Transactional(readOnly = true)
	public ProductListVO getMyPaged(int page, long sellerNo) {
		int count = productDao.countBySeller(sellerNo);

		PageVO pageVO = new PageVO();
		pageVO.setPage(page);
		pageVO.setDataCount(count);

		List<ProductDto> list = productDao.selectListBySeller(pageVO, sellerNo);

		boolean last = pageVO.getPage() >= pageVO.getTotalPage();

		return ProductListVO.builder()
				.page(pageVO.getPage())
				.count(count)
				.size(pageVO.getSize())
				.begin(pageVO.getBegin())
				.end(pageVO.getEnd())
				.last(last)
				.list(list)
				.build();
	}

	@Transactional(readOnly = true)
	public ProductListVO getAuctionPaged(int page, String q, Long category, String sort, Integer minPrice, Integer maxPrice) {
		int count = productDao.countAuction(q, category, minPrice, maxPrice);

		PageVO pageVO = new PageVO();
		pageVO.setPage(page);
		pageVO.setDataCount(count);

		List<ProductDto> list = productDao.selectAuctionListByPaging(pageVO, q, category, sort, minPrice, maxPrice);

		boolean last = pageVO.getPage() >= pageVO.getTotalPage();

		return ProductListVO.builder()
				.page(pageVO.getPage())
				.count(count)
				.size(pageVO.getSize())
				.begin(pageVO.getBegin())
				.end(pageVO.getEnd())
				.last(last)
				.list(list)
				.build();
	}
	
	// ========================================================
	// [추가됨] 내 구매/입찰 내역 조회 (React 구매관리 페이지용)
	// ========================================================
	@Transactional(readOnly = true)
	public List<PurchaseListVO> getPurchaseList(long memberNo) {
		return productDao.selectPurchaseList(memberNo);
	}
}