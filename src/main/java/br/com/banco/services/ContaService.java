package br.com.banco.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;

public interface ContaService {
	Conta criarConta(String nome);

	public boolean hasConta(Long id);

	Conta obterContaPorNome(String nome);

	public Conta obterContaPorId(Long id);

	void sacar(Long idConta, double valor);

	void depositar(Long idConta, double valor);

	public boolean hasContaByName(String nome);

	public double calcularSaldoTotalPorNome(String nome);

	public List<Transferencia> buscarTransacoesPorNome(String nome);

	public Map<String, Object> createErrorResponse(String errorMessage);

	void transferir(Long idContaOrigem, Long idContaDestino, double valor, Operation tipo);

	public void validarParametros(Long idContaOrigem, Long idContaDestino, double valor, Operation tipo);

	public double calcularSaldoPeriodoPorNome(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nome);

	public List<Transferencia> buscarTransacoesPorPeriodoENome(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nome);
}