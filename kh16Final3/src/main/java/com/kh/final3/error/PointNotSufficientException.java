package com.kh.final3.error;

public class PointNotSufficientException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public PointNotSufficientException() {
		super();
	}
	
	public PointNotSufficientException(String message) {
		super(message);
	}

}
