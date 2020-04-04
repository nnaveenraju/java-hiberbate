package com.example.demo.exceptions;

public class HazelCastModuleException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	private String errorCode;

	public HazelCastModuleException(String message, String errorCode) {
		super(message);
		this.message = message;
		this.errorCode = errorCode;
	}

	public HazelCastModuleException(String message, String errorCode, Exception e) {
		super(message, e);
		this.message = message;
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return "Error Message " + this.message + "\n" + "Error Code " + this.errorCode;
	}

}
