package br.com.banco.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.banco.entities.Transferencia;

public interface TransferenciaRepository
		extends JpaRepository<Transferencia, Long>, JpaSpecificationExecutor<Transferencia> {

//	List<Transferencia> findAll(Specification<Transferencia> spec);

	@Query("SELECT t FROM Transferencia t WHERE t.conta.id = :numeroConta")
	List<Transferencia> findByContaNumeroConta(Long numeroConta);

	List<Transferencia> findByDataTransferenciaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

	List<Transferencia> findByNomeOperadorTransacao(String nomeOperador);

//	List<Transferencia> findByDataTransferenciaBetweenAndNomeOperadorTransacao(LocalDateTime dataInicio, LocalDateTime dataFim, String nomeOperador);

//	List<Transferencia> findByDataTransferenciaBetweenAndNomeOperadorTransacaoAndDataTransferenciaGreaterThanEqual(LocalDateTime dataInicio, LocalDateTime dataFim, String nomeOperador, LocalDateTime dataTransferenciaInicio);
//	List<Transferencia> findByDataTransferenciaAfterAndNomeOperadorTransacao(LocalDateTime dataInicio, String nomeOperador);


	List<Transferencia> findByContaId(Long contaId);

	Page<Transferencia> findAll(Pageable pageable);

	
	@Query("SELECT t FROM Transferencia t WHERE t.dataTransferencia >= :dataInicio AND t.nomeOperadorTransacao = :nomeOperador")
	List<Transferencia> findByDataInicioAndNomeOperador(@Param("dataInicio") LocalDateTime dataInicio, @Param("nomeOperador") String nomeOperador);
	
}
