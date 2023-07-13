package br.com.banco.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.banco.entities.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

}