package br.com.banco.exceptions;

public class ContaNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ContaNotFoundException(String message) {
		super(message);
	}

	public ContaNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}