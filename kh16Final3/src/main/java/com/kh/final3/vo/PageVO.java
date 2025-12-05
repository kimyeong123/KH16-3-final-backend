package com.kh.final3.vo;



import lombok.Data;


	@Data
	public class PageVO {
		//필드에 페이징에 필요한 데이터들을 배치
		private int page = 1;//현재 페이지 번호
		private int size = 10;//한 페이지에 표시할 데이터 수
		private String column, keyword;//검색항목, 검색어
		private int dataCount;//총 데이터 수
		private int blockSize = 10;//표시할 블록 개수(10개)
		
		//계산이 가능하도록 Getter 메소드 추가 생성
		public boolean isSearch() {
			return column != null && keyword != null;
		}
		public boolean isList() {
			return column == null || keyword == null;
		}
		public String getSearchParams() {//목록 or 검색 여부에 따라 주소에 추가될 파라미터를 반환
			if(isSearch()) {
				return "size="+size+"&column="+column+"&keyword="+keyword;
			}
			else {
				return "size="+size;
			}
		}
		public int getBlockStart() {//블록 시작번호
			return (page-1) / blockSize * blockSize + 1;
		}
		public int getBlockFinish() {//블록 종료번호
			int number = (page-1) / blockSize * blockSize + blockSize;
			return Math.min(getTotalPage(), number);
		}
		public int getTotalPage() {//총 페이지 수
			return (dataCount - 1) / size + 1;
		}
		
		public int getBegin() {
			return page * size - (size-1);
		}
		public int getEnd() {
			return page * size;
		}
		//꼭 필요하지 않더라도 가독성을 올릴 수 있는 메소드들을 추가
		public boolean isFirstBlock() {
			return getBlockStart() == 1;
		}
		public int getPrevPage() {
			return getBlockStart() - 1;
		}
		public boolean isLastBlock() {
			return getBlockFinish() == getTotalPage();
		}
		public int getNextPage() {
			return getBlockFinish() + 1;
		}
}
