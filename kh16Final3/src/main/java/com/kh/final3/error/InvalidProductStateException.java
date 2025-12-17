package com.kh.final3.error;

public class InvalidProductStateException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidProductStateException() {
		super();
	}
	
	public InvalidProductStateException(String message) {
		super(message);
	}
	
}
