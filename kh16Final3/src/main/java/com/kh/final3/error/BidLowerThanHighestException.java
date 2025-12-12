package com.kh.final3.error;

public class BidLowerThanHighestException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public BidLowerThanHighestException() {
		super();
	}
	
	public BidLowerThanHighestException(String message) {
		super(message);
	}
}
