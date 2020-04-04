package com.example.demo.exceptions;

public class FailedToLoadExecption extends HazelCastModuleException {

	public FailedToLoadExecption(String message, String errorCode, Exception e) {
		super(message, errorCode, e);
		
	}

	public FailedToLoadExecption(String message, String errorCode) {
		super(message, errorCode);
	}

}
