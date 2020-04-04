package com.example.demo.exceptions;

public class FailedToStoreException extends HazelCastModuleException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FailedToStoreException(String message, String errorCode, Exception e) {
		super(message, errorCode, e);
	}

	public FailedToStoreException(String message, String errorCode) {
		super(message, errorCode);
	}

}
