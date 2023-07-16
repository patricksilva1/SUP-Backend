package br.com.banco.exceptions;

public class DataInvalidaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public DataInvalidaException(String message) {
        super(message);
    }
}