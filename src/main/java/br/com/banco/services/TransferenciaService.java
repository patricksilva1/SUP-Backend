package br.com.banco.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;

@Service
@EnableJpaRepositories
public interface TransferenciaService {
	List<Transferencia> getAllTransferencias();

	List<Transferencia> getTransferenciasPorConta(Long numeroConta);

	Page<Transferencia> getTransferenciasPaginadas(Pageable pageable);

	List<Transferencia> getTransferenciasPorPeriodo(ZonedDateTime dataInicioCompleta, ZonedDateTime dataFimCompleta);

	List<Transferencia> getTransferenciasPorOperador(String nomeOperador);

	List<Transferencia> getTransferenciasPorPeriodoEOperador(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nomeOperador);

	Transferencia criarTransferencia(Transferencia transferencia);

	Transferencia atualizarTransferencia(Long id, Transferencia transferencia);
	
	public void sacar(Long idConta, double valor);
	
	public Conta obterContaPorId(Long id);

	public boolean isValidDateFormat(String date);

	public Map<String, String> getPrimeiraEUltimaDataPorNomeOperador(String nomeOperador);
	
//	public Map<String, String> encontrarPrimeiraEUltimaDataPorNomeOperador(String nomeOperador);
}