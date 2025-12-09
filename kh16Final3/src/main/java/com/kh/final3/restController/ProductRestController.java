package com.kh.final3.restController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.dao.ProductDao;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.error.TargetNotfoundException;
import com.kh.final3.vo.PageVO;
import com.kh.final3.vo.ProductListVO;

@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductRestController {
	@Autowired
	private ProductDao productDao;
	
	@PostMapping("/")
	public void insert(
			@RequestBody ProductDto productDto
			) {
			int productNo=productDao.sequence();
			productDto.setProductNo(productNo);
			productDao.insert(productDto);
	}
	
	@GetMapping("/")
	public List<ProductDto>list(){
		return productDao.selectList();
	}
	@GetMapping("/{productNo}")
		public ProductDto detail(@PathVariable int productNo) {
		ProductDto productDto=productDao.selectOne(productNo);
		if(productDto==null) throw new TargetNotfoundException("존재하지 않는 상품 입니다");
		return productDto;
	}
	
	@DeleteMapping("/{productNo}")
	public void delete(@PathVariable int productNo) {
			ProductDto productDto=productDao.selectOne(productNo);
			if(productDto==null) throw new TargetNotfoundException("존재하지 않는 상품입니다");
			productDao.delete(productNo);
	}
	@PutMapping("/{productNo}")
	public  void edit(@PathVariable int productNo,
										@RequestBody ProductDto productDto) {
		ProductDto originDto=productDao.selectOne(productNo);
		if(originDto == null) throw new TargetNotfoundException();
		originDto.setProductName(productDto.getProductName());
		originDto.setProductDescription(productDto.getProductDescription());
		originDto.setProductInstantPrice(productDto.getProductInstantPrice());
		originDto.setProductEndDate(productDto.getProductEndDate());
		originDto.setProductStatus(productDto.getProductStatus());
		
		productDao.update(originDto);
	
	}
			
	@PatchMapping("/{productNo}")
	public void update(@PathVariable int productNo,
			@RequestBody ProductDto productDto) {
		ProductDto originDto=productDao.selectOne(productNo);
		if(originDto==null) throw new TargetNotfoundException();
		
		productDto.setProductNo(productNo);
		productDao.updateUnit(productDto);
		
		}
	@GetMapping("/page/{page}")
	public ProductListVO listByPaging(@PathVariable int page) {
		PageVO pageVO=new PageVO();
		pageVO.setPage(page);
		pageVO.setDataCount(productDao.count());
		List<ProductDto>list=productDao.selectList(pageVO);
		
		return ProductListVO.builder()
				.page(pageVO.getPage())
				.size(pageVO.getSize())
				.count(pageVO.getDataCount())
				.begin(pageVO.getBegin())
				.end(pageVO.getEnd())
				.last(pageVO.getPage()>=pageVO.getTotalPage())
				.list(list)
				.build();
		
		
		
	}
	
}
