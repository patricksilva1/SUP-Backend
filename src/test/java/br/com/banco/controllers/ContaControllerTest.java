package br.com.banco.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.banco.entities.Conta;
import br.com.banco.services.ContaService;

public class ContaControllerTest {

	@Mock
	private ContaService contaService;

	private ContaController contaController;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		contaController = new ContaController(contaService);
	}

	@Test
	public void testCriarConta() {
		String nome = "John Doe";
		Conta conta = new Conta(nome);
		conta.setId(1L);
		conta.setSaldo(0.0);
		when(contaService.hasContaByName(nome)).thenReturn(false);
		when(contaService.criarConta(nome)).thenReturn(conta);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("nome", nome);

		ResponseEntity<Map<String, Object>> response = contaController.criarConta(requestBody);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(conta.getId(), response.getBody().get("id"));
		assertEquals(conta.getNome(), response.getBody().get("nome"));
		assertEquals(conta.getDataDeCriacao(), response.getBody().get("dataDeCriacao"));
		assertEquals(conta.getSaldo(), response.getBody().get("saldo"));
	}

	@Test
	public void testCriarContaIllegalArgumentException() {
		String nome = "John Doe";
		when(contaService.hasContaByName(nome)).thenReturn(false);
		when(contaService.criarConta(nome)).thenThrow(new IllegalArgumentException("Erro ao criar conta"));

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("nome", nome);

		ResponseEntity<Map<String, Object>> response = contaController.criarConta(requestBody);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testCriarContaException() {
		String nome = "John Doe";
		when(contaService.hasContaByName(nome)).thenReturn(false);
		when(contaService.criarConta(nome)).thenThrow(new RuntimeException("Erro interno ao criar conta"));

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("nome", nome);

		ResponseEntity<Map<String, Object>> response = contaController.criarConta(requestBody);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	public void testDepositar() {
		Long id = 1L;
		double valor = 100.0;
		when(contaService.hasConta(id)).thenReturn(true);

		ResponseEntity<Void> response = contaController.depositar(id, valor);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testDepositarIdNulo() {
		ResponseEntity<Void> response = contaController.depositar(null, 100.0);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testDepositarContaNaoEncontrada() {
		Long id = 1L;
		when(contaService.hasConta(id)).thenReturn(false);

		ResponseEntity<Void> response = contaController.depositar(id, 100.0);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testSacar() {
		Long id = 1L;
		double valor = 100.0;
		when(contaService.hasConta(id)).thenReturn(true);

		ResponseEntity<Void> response = contaController.sacar(id, valor);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testSacarContaNaoEncontrada() {
		Long id = 1L;
		when(contaService.hasConta(id)).thenReturn(false);

		ResponseEntity<Void> response = contaController.sacar(id, 100.0);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testObterContaPorId() {
		Long id = 1L;
		Conta conta = new Conta("John Doe");
		conta.setId(id);
		conta.setSaldo(500.0);
		when(contaService.hasConta(id)).thenReturn(true);
		when(contaService.obterContaPorId(id)).thenReturn(conta);

		ResponseEntity<Map<String, Object>> response = contaController.obterContaPorId(id);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(id, response.getBody().get("id"));
		assertEquals(conta.getNome(), response.getBody().get("nome"));
		assertEquals(conta.getSaldo(), response.getBody().get("saldo"));
	}

	@Test
	public void testObterContaPorIdNulo() {
		ResponseEntity<Map<String, Object>> response = contaController.obterContaPorId(null);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testObterContaPorIdNaoEncontrada() {
		Long id = 1L;
		when(contaService.hasConta(id)).thenReturn(false);

		ResponseEntity<Map<String, Object>> response = contaController.obterContaPorId(id);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testObterContaPorIdContaNula() {
		Long id = 1L;
		when(contaService.hasConta(id)).thenReturn(true);
		when(contaService.obterContaPorId(id)).thenReturn(null);

		ResponseEntity<Map<String, Object>> response = contaController.obterContaPorId(id);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	public void testTransferir() {
		Long idContaOrigem = 1L;
		Long idContaDestino = 2L;
		double valor = 100.0;
		br.com.banco.enums.Operation tipo = br.com.banco.enums.Operation.TRANSFERENCIA;
		when(contaService.hasConta(idContaOrigem)).thenReturn(true);
		when(contaService.hasConta(idContaDestino)).thenReturn(true);

		ResponseEntity<Void> response = contaController.transferir(idContaOrigem, idContaDestino, valor, tipo);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testAtualizarSaldo() {
		Long id = 1L;
		double valor = 100.0;
		when(contaService.hasConta(id)).thenReturn(true);

		ResponseEntity<Void> response = contaController.atualizarSaldo(id, valor);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testAtualizarSaldoIdNulo() {
		ResponseEntity<Void> response = contaController.atualizarSaldo(null, 100.0);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testAtualizarSaldoContaNaoEncontrada() {
		Long id = 1L;
		when(contaService.hasConta(id)).thenReturn(false);

		ResponseEntity<Void> response = contaController.atualizarSaldo(id, 100.0);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

}