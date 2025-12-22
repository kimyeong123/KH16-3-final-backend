package com.kh.final3.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.ProductDao;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.ProductListVO;
import com.kh.final3.vo.PurchaseListVO;

@Service
public class ProductService {

	@Autowired
	private ProductDao productDao;

	@Transactional
	public ProductDto create(ProductDto productDto, Long loginMemberNo) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime start = productDto.getStartTime();
		LocalDateTime end = productDto.getEndTime();

		if (start.isBefore(now.minusMinutes(1))) {
			throw new RuntimeException("시작 시간은 과거일 수 없습니다.");
		}
		if (end.isBefore(start)) {
			throw new RuntimeException("마감 시간은 시작 시간보다 이후여야 합니다.");
		}
		
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

	//20개씩받기
	@Transactional(readOnly = true)
	public Map<String, Object> getAuctionList(PageVO vo) {
		// 1. 검색 조건 + 카테고리 + 가격 필터에 맞는 전체 개수 조회
		int count = productDao.countAuction(vo);

		// 2. PageVO에 개수 세팅 (여기서 전체 페이지 수 계산됨)
		vo.setDataCount(count);

		// 3. 실제 리스트 조회 (limit, offset 등이 PageVO 안의 size=30에 맞춰 계산됨)
		List<ProductDto> list = productDao.selectAuctionListByPaging(vo);

		// 4. 결과 맵핑 (Controller가 Map을 원함)
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("count", count);
		map.put("page", vo.getPage());
		map.put("size", vo.getSize()); // 30개 요청했으면 30이 나감
		map.put("totalPage", vo.getTotalPage());
		
		boolean last = vo.getPage() >= vo.getTotalPage();
		map.put("last", last);

		return map;
	}
	
	@Transactional(readOnly = true)
	public List<PurchaseListVO> getPurchaseList(long memberNo) {
		return productDao.selectPurchaseList(memberNo);
	}
	
	@Transactional(readOnly = true)
	public List<ProductDto> getClosingSoon() {
	    return productDao.selectClosingSoon();
	}
}