package br.com.banco.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.banco.entities.Conta;
import br.com.banco.enums.Operation;
import br.com.banco.services.ContaService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ACCOUNTS", description = "Endpoints Management.")
@CrossOrigin(origins = "*", maxAge = 3600)

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {

    private final ContaService contaService;

    @Autowired
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    // Criar Conta
    @PostMapping
    public ResponseEntity<Map<String, Object>> criarConta(@RequestBody Map<String, Object> requestBody) {
        String nome = (String) requestBody.get("nome");
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("O parâmetro 'nome' é obrigatório");
        }

        Conta novaConta = contaService.criarConta(nome);
        Map<String, Object> response = new HashMap<>();
        response.put("id", novaConta.getId());
        response.put("nome", novaConta.getNome());
        response.put("dataDeCriacao", novaConta.getDataDeCriacao());
        response.put("saldo", novaConta.getSaldo());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Depositar valor por id.
    @PostMapping("/{id}/depositar")
    public ResponseEntity<Void> depositar(@PathVariable Long id, @RequestParam double valor) {
        if(id != null) {
        	contaService.depositar(id, valor);
        }
        return ResponseEntity.ok().build();
    }

    // Sacar por id.
    @PostMapping("/{id}/sacar")
    public ResponseEntity<Void> sacar(@PathVariable Long id, @RequestParam double valor) {
        contaService.sacar(id, valor);
        return ResponseEntity.ok().build();
    }

    // Achar por Id
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obterContaPorId(@PathVariable Long id) {
		Conta conta = contaService.obterContaPorId(id);

		Map<String, Object> response = new HashMap<>();
		response.put("id", conta.getId());
		response.put("nome", conta.getNome());
		response.put("saldo", conta.getSaldo());

		return (id !=null) ? ResponseEntity.ok().body(response) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

    // TODO: CORRIGIR O TIPO para automatico.
    // TRANSFERENCIA
    @PostMapping("/{origem}/transferir/{destino}")
    public ResponseEntity<Void> transferir(
            @PathVariable("origem") Long idContaOrigem,
            @PathVariable("destino") Long idContaDestino,
            @RequestParam double valor,
            @RequestParam Operation tipo) {
        contaService.transferir(idContaOrigem, idContaDestino, valor, tipo);
        return ResponseEntity.ok().build();
    }

    // Atualizar saldo sem salvar nas transferencias
    // TODO: SALVAR NAS TRANSFERENCIAS COMO "SISTEMA"
    @PutMapping("/{id}/saldo")
    public ResponseEntity<Void> atualizarSaldo(@PathVariable Long id, @RequestParam double valor) {
        contaService.depositar(id, valor);
        return ResponseEntity.ok().build();
    }
}