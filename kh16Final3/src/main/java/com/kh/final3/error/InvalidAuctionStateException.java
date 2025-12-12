package com.kh.final3.error;

public class InvalidAuctionStateException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidAuctionStateException() {
		super();
	}
	
	public InvalidAuctionStateException(String message) {
		super(message);
	}
	
}
