package br.com.banco.exceptions;

public class InvalidPageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public InvalidPageException(String message, Throwable cause) {
        super(message, cause);
    }
}