package com.kh.final3.error;

public class InsufficientBidAmountException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InsufficientBidAmountException() {
		super();
	}
	
	public InsufficientBidAmountException(String message) {
		super(message);
	}
}
