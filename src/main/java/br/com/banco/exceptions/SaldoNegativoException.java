package br.com.banco.exceptions;

public class SaldoNegativoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public SaldoNegativoException(String message) {
        super(message);
    }
}