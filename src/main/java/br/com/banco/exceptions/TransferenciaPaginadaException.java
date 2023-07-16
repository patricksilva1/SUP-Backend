package br.com.banco.exceptions;

public class TransferenciaPaginadaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public TransferenciaPaginadaException(String message, Throwable cause) {
        super(message, cause);
    }
}