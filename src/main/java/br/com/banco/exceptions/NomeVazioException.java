package br.com.banco.exceptions;

public class NomeVazioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public NomeVazioException(String message) {
        super(message);
    }
}