package br.com.banco.exceptions;

public class DataRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public DataRequestException(String mensagem) {
        super(mensagem);
    }
}