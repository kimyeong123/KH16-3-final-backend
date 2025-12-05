package com.kh.final3.error;

public class UnauthorizationException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public UnauthorizationException() {
		super();
	}
	public UnauthorizationException(String message) {
		super(message);
	}
}
