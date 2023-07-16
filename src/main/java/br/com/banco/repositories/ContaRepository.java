package br.com.banco.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.banco.entities.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
	@Query("SELECT c FROM Conta c WHERE LOWER(c.nome) LIKE %:nome%")
	Optional<Conta> findByNomeIgnoreCaseLike(String nome);
}