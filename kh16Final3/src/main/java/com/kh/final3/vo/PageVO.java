package com.kh.final3.vo;

import java.util.List; // List를 사용하기 위해 import 추가
import lombok.Data;

// 1. 클래스 선언부에 제네릭 타입 <T>를 추가합니다.
@Data
public class PageVO<T> {

	// 필드에 페이징에 필요한 데이터들을 배치
	private Integer page = 1; // 현재 페이지 번호
	private Integer size = 3; // 한 페이지에 표시할 데이터 수
	private String column, keyword;// 검색항목, 검색어
	private Integer dataCount; // 총 데이터 수
	private Integer blockSize = 10;// 표시할 블록 개수(10개)

	private String type;
	private long loginNo;
	private String loginLevel;
	private String role; 

	// 2. 조회된 데이터를 담을 필드를 추가합니다.
	private List<T> list; // 조회된 데이터 리스트 (BoardDto 등)

	// 계산이 가능하도록 Getter 메소드 추가 생성

	public boolean isSearch() {
		// 기존 검색(column)
		boolean columnSearch = column != null && keyword != null && !keyword.trim().isEmpty();

		// 관리자 검색(type)
		boolean typeSearch = type != null && keyword != null && !keyword.trim().isEmpty();

		return columnSearch || typeSearch;
	}

	public boolean isList() {
		return !isSearch();
	}

	public String getSearchParams() {
		if (type != null && keyword != null && !keyword.trim().isEmpty()) {
			return "size=" + size + "&type=" + type + "&keyword=" + keyword;
		} else if (column != null && keyword != null && !keyword.trim().isEmpty()) {
			return "size=" + size + "&column=" + column + "&keyword=" + keyword;
		} else {
			return "size=" + size;
		}
	}

	public Integer getBlockStart() {// 블록 시작번호
		return (page - 1) / blockSize * blockSize + 1;
	}

	public Integer getBlockFinish() {// 블록 종료번호
		int number = (page - 1) / blockSize * blockSize + blockSize;
		// 총 페이지 수를 넘지 않도록 Math.min을 사용
		return Math.min(getTotalPage(), number);
	}

	public Integer getTotalPage() {// 총 페이지 수
		// dataCount가 0일 때를 대비해 null 체크 및 1 이상인지 확인하는 로직 추가 가능
		if (dataCount == null || dataCount == 0)
			return 1;
		return (dataCount - 1) / size + 1;
	}

	public Integer getBegin() {
		return page * size - (size - 1); // Oracle ROWNUM 기반 (1부터 시작)
	}

	public Integer getEnd() {
		return page * size;
	}

	// 꼭 필요하지 않더라도 가독성을 올릴 수 있는 메소드들을 추가
	public boolean isFirstBlock() {
		return getBlockStart() == 1;
	}

	public Integer getPrevPage() {
		return getBlockStart() - 1;
	}

	public boolean isLastBlock() {
		return getBlockFinish() == getTotalPage();
	}

	public Integer getNextPage() {
		return getBlockFinish() + 1;
	}

}