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
	public Conta obterContaPorId(Long id);

	List<Transferencia> getAllTransferencias();

	public void sacar(Long idConta, double valor);

	public boolean isValidDateFormat(String date);

	Transferencia criarTransferencia(Transferencia transferencia);

	List<Transferencia> getTransferenciasPorConta(Long numeroConta);

	Page<Transferencia> getTransferenciasPaginadas(Pageable pageable);

	List<Transferencia> getTransferenciasPorOperador(String nomeOperador);

	Transferencia atualizarTransferencia(Long id, Transferencia transferencia);

	public Map<String, String> getPrimeiraEUltimaDataPorNomeOperador(String nomeOperador);

	List<Transferencia> getTransferenciasPorPeriodo(ZonedDateTime dataInicioCompleta, ZonedDateTime dataFimCompleta);

	List<Transferencia> getTransferenciasPorPeriodoEOperador(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nomeOperador);
}