package com.kh.final3.vo;

import java.util.List;

import com.kh.final3.dto.ProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductListVO {
	private int page;
	private int count;

	private int size;
	private int begin,end;
	private boolean last;
	private List<ProductDto>list;
	
}
