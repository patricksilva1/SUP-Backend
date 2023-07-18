package br.com.banco.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;

public interface ContaService {
	Conta criarConta(String nome);

	void depositar(Long idConta, double valor);

	void sacar(Long idConta, double valor);

	void transferir(Long idContaOrigem, Long idContaDestino, double valor, Operation tipo);

	public Conta obterContaPorId(Long id);

	public List<Transferencia> buscarTransacoesPorPeriodoENome(ZonedDateTime dataInicio, ZonedDateTime dataFim,	String nome);

	public double calcularSaldoTotalPorNome(String nome);

	public double calcularSaldoPeriodoPorNome(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nome);

	Conta obterContaPorNome(String nome);

	public List<Transferencia> buscarTransacoesPorNome(String nome);

	public Map<String, Object> createErrorResponse(String errorMessage);

	public boolean hasContaByName(String nome);

	public boolean hasConta(Long id);
	
	public void validarParametros(Long idContaOrigem, Long idContaDestino, double valor, Operation tipo);
	
	public Map<String, ZonedDateTime> encontrarPrimeiraEUltimaDataPorNomeOperador(String nomeOperador);
}