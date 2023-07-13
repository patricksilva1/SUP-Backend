package br.com.banco.enums;

public enum Operation {
	DEPOSITO(1, "Deposito"), SAQUE(2, "Saque"), TRANSFERENCIA(3, "Transferencia");

	private final int id;

	private final String descricao;

	Operation(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}
}