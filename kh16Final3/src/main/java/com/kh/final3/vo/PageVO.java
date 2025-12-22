package com.kh.final3.vo;

import java.util.List;
import lombok.Data;

@Data
public class PageVO<T> {

	// 필드에 페이징에 필요한 데이터들을 배치
	private Integer page = 1; // 현재 페이지 번호
	
	// 🔥 핵심: 기본값 10. 프론트에서 size=30 보내면 30으로 바뀜!
	private Integer size = 10; 
	
	private String column, keyword; // 기존 검색항목
	private Integer dataCount; // 총 데이터 수
	private Integer blockSize = 10; // 표시할 블록 개수

	private String type;
	private long loginNo;
	private String loginLevel;
	private String role; 

	// ==========================================
	// 👇 [여기만 추가하세요] 경매 검색용 필드들 👇
	// ==========================================
	private String q;           // 경매 검색어
	private Long category;      // 카테고리 코드
	private Integer minPrice;   // 최소 가격
	private Integer maxPrice;   // 최대 가격
	private String sort;        // 정렬 기준 (PRICE_DESC 등)
	// ==========================================

	// 2. 조회된 데이터를 담을 필드
	private List<T> list; 

	// --- 아래는 기존 메소드 그대로 유지 ---

	public boolean isSearch() {
		boolean columnSearch = column != null && keyword != null && !keyword.trim().isEmpty();
		boolean typeSearch = type != null && keyword != null && !keyword.trim().isEmpty();
		return columnSearch || typeSearch;
	}

	public boolean isList() { return !isSearch(); }

	public String getSearchParams() {
		if (type != null && keyword != null && !keyword.trim().isEmpty()) {
			return "size=" + size + "&type=" + type + "&keyword=" + keyword;
		} else if (column != null && keyword != null && !keyword.trim().isEmpty()) {
			return "size=" + size + "&column=" + column + "&keyword=" + keyword;
		} else {
			return "size=" + size;
		}
	}

	public Integer getBlockStart() { return (page - 1) / blockSize * blockSize + 1; }

	public Integer getBlockFinish() {
		int number = (page - 1) / blockSize * blockSize + blockSize;
		return Math.min(getTotalPage(), number);
	}

	public Integer getTotalPage() {
		if (dataCount == null || dataCount == 0) return 1;
		return (dataCount - 1) / size + 1;
	}

	// 오라클 ROWNUM 계산 (size가 30이면 알아서 1~30 계산됨)
	public Integer getBegin() {
		return page * size - (size - 1); 
	}

	public Integer getEnd() {
		return page * size;
	}

	public boolean isFirstBlock() { return getBlockStart() == 1; }
	public Integer getPrevPage() { return getBlockStart() - 1; }
	public boolean isLastBlock() { return getBlockFinish() == getTotalPage(); }
	public Integer getNextPage() { return getBlockFinish() + 1; }
}