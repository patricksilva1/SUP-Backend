package br.com.banco.repositories;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.banco.entities.Transferencia;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long>, JpaSpecificationExecutor<Transferencia> {

	Page<Transferencia> findAll(Pageable pageable);
	
	List<Transferencia> findByContaId(Long contaId);

	List<Transferencia> findByDataTransferenciaBetween(ZonedDateTime dataInicio, ZonedDateTime dataFim);

	@Query("SELECT t FROM Transferencia t WHERE t.conta.id = :numeroConta")
	List<Transferencia> findByContaNumeroConta(Long numeroConta);

	@Query("SELECT t FROM Transferencia t WHERE LOWER(t.nomeOperadorTransacao) LIKE LOWER(CONCAT('%', :nomeOperador, '%'))")
	List<Transferencia> findByNomeOperadorTransacao(@Param("nomeOperador") String nomeOperador);

	@Query("SELECT t FROM Transferencia t WHERE t.dataTransferencia BETWEEN :dataInicio AND :dataFim AND LOWER(t.nomeOperadorTransacao) LIKE LOWER(CONCAT('%', :nomeOperador, '%'))")
	List<Transferencia> findByDataInicioAndDataFimAndNomeOperador(@Param("dataInicio") ZonedDateTime dataInicio, @Param("dataFim") ZonedDateTime dataFim, @Param("nomeOperador") String nomeOperador);

	@Query("SELECT t FROM Transferencia t WHERE t.dataTransferencia >= :dataInicio AND t.dataTransferencia <= :dataFim AND t.conta.nome LIKE %:nome%")
	List<Transferencia> buscarPorPeriodoENome(@Param("dataInicio") ZonedDateTime dataInicio, @Param("dataFim") ZonedDateTime dataFim, @Param("nome") String nome);

	@Query("SELECT t FROM Transferencia t WHERE t.conta.nome LIKE %:nome%")
	List<Transferencia> buscarPorNome(@Param("nome") String nome);
}