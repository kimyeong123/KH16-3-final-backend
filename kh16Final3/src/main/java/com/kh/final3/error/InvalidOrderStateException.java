package com.kh.final3.error;

public class InvalidOrderStateException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidOrderStateException() {
		super();
	}
	
	public InvalidOrderStateException(String message) {
		super(message);
	}
	
}
