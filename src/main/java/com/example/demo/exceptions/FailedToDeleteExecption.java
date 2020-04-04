package com.example.demo.exceptions;

public class FailedToDeleteExecption extends HazelCastModuleException {

	public FailedToDeleteExecption(String message, String errorCode, Exception e) {
		super(message, errorCode, e);
		
	}

	public FailedToDeleteExecption(String message, String errorCode) {
		super(message, errorCode);
	}

}
