package br.com.banco.services;

import java.time.LocalDateTime;
import java.util.List;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;

public interface ContaService {
	Conta criarConta(String nome);

	void depositar(Long idConta, double valor);

	void sacar(Long idConta, double valor);

	void transferir(Long idContaOrigem, Long idContaDestino, double valor, Operation tipo);

	public Conta obterContaPorId(Long id);

	public List<Transferencia> buscarTransacoesPorPeriodoENome(LocalDateTime dataInicio, LocalDateTime dataFim, String nome);

	public double calcularSaldoTotalPorNome(String nome);
	
	public double calcularSaldoPeriodoPorNome(LocalDateTime dataInicio, LocalDateTime dataFim, String nome);
}