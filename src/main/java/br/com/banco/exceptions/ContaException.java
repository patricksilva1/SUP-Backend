package br.com.banco.exceptions;

public class ContaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ContaException(String message) {
		super(message);
	}

	public ContaException(String message, Throwable cause) {
		super(message, cause);
	}
}