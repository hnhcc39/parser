package com.internal.exception;

/**
 * 解析异常
 */
public class ParserException extends Exception{

	private static final long serialVersionUID = 7849065654774394609L;

	public ParserException() {
		super();
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserException(String message) {
		super(message);
	}

	public ParserException(Throwable cause) {
		super(cause);
	}
}
