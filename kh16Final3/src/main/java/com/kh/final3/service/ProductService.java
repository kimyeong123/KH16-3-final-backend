package com.kh.final3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.final3.dao.ProductDao;
import com.kh.final3.dto.ProductDto;
import com.kh.final3.vo.ProductListVO;

@Service
public class ProductService {

  @Autowired private ProductDao productDao;

  public ProductDto create(ProductDto productDto, Long loginMemberNo) {

    // 1) PK 세팅 (필수)
    long productNo = productDao.sequence();
    productDto.setProductNo(productNo);

    // 2) seller_no 세팅 (토큰에서 받은 값)
    productDto.setSellerNo(loginMemberNo);

    // 3) insert
    productDao.insert(productDto);

    return productDto; // 또는 selectOne(productNo)로 다시 조회해서 반환
  }



    /** 전체 목록 */
    @Transactional(readOnly = true)
    public List<ProductDto> getList() {
        return productDao.selectList();
    }

    /** 상세 조회 */
    @Transactional(readOnly = true)
    public ProductDto get(Long productNo) {
        return productDao.selectOne(productNo);
    }

    /** 삭제 */
    @Transactional
    public void delete(Long productNo) {
        productDao.delete(productNo);
    }

    /** 전체 수정 */
    @Transactional
    public void edit(Long productNo, ProductDto productDto) {
        productDto.setProductNo(productNo);
        productDao.update(productDto);
    }

    /** 부분 수정 */
    @Transactional
    public void patch(Long productNo, ProductDto productDto) {
        productDto.setProductNo(productNo);
        productDao.updateUnit(productDto); // 필요에 맞게 update / updateUnit 선택
    }

    /** 페이징 목록 (너가 쓰던 로직에 맞게 구현) */
    @Transactional(readOnly = true)
    public ProductListVO getPaged(int page) {
        // PageVO 만들어서 Dao 호출하는 기존 코드 넣으면 됨
        return null;
    }
}
