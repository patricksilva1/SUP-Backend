package br.com.banco.repositories;

import java.time.ZonedDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.banco.entities.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
	@Query("SELECT c FROM Conta c WHERE LOWER(c.nome) LIKE LOWER(concat('%', :nome, '%'))")
	Conta findByNomeIgnoreCaseLike(@Param("nome") String nome);

	@Query("SELECT SUM(c.saldo) FROM Conta c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND c.dataDeCriacao < :dataInicio")
	Double findByNomeIgnoreCaseLike(@Param("nome") String nome, @Param("dataInicio") ZonedDateTime dataInicio);

	@Query("SELECT SUM(c.saldo) FROM Conta c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND c.dataDeCriacao >= :dataInicio AND c.dataDeCriacao <= :dataFim")
	Double findByNomeIgnoreCaseLikeAndDataDeCriacaoBetween(@Param("nome") String nome, @Param("dataInicio") ZonedDateTime dataInicio, @Param("dataFim") ZonedDateTime dataFim);
}