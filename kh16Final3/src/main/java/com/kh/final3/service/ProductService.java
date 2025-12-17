package com.kh.final3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.kh.final3.dao.ProductDao;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.ProductListVO;

@Service
public class ProductService {

	@Autowired
	private ProductDao productDao;

	@Transactional
	public ProductDto create(ProductDto productDto, Long loginMemberNo) {
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
		productDao.delete(productNo);
	}

	@Transactional
	public void edit(Long productNo, ProductDto productDto) {
		productDto.setProductNo(productNo);
		productDao.update(productDto);
	}

	@Transactional
	public void patch(Long productNo, ProductDto productDto) {
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
	public ProductListVO getAuctionPaged(int page) {
		int count = productDao.countByBidding();

		PageVO pageVO = new PageVO();
		pageVO.setPage(page);
		pageVO.setDataCount(count);

		List<ProductDto> list = productDao.selectListByBidding(pageVO);

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
	
}
