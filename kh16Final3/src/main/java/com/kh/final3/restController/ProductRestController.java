package com.kh.final3.restController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.dao.ProductDao;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.error.TargetNotfoundException;

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
			long productNo=productDao.sequence();
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
			
	}
	@PutMapping("/{productNo}")
	public  void edit(@PathVariable int productNo,
										@RequestBody ProductDto prdouctDto) {
		ProductDto originDto=productDao.selectOne(productNo);
		if(originDto == null) throw new TargetNotfoundException();

	
	}
			
	
	
}
