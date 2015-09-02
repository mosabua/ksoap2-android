package org.ksoap2.transport;

public class NtlmAuthenticationException extends Exception {
	private static final long serialVersionUID = 1L;

	public NtlmAuthenticationException() {
		super();
	}
	
	public NtlmAuthenticationException(String message) {
		super(message);
	}
	
	public NtlmAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
