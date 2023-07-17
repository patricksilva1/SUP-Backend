package br.com.banco.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;
import br.com.banco.exceptions.ContaException;
import br.com.banco.exceptions.ContaNotFoundException;
import br.com.banco.exceptions.SaldoInsuficienteException;
import br.com.banco.repositories.ContaRepository;
import br.com.banco.repositories.TransferenciaRepository;
import br.com.banco.services.ContaServiceImpl;

public class ContaServiceImplTest {

	private ContaRepository contaRepository;
	private TransferenciaRepository transferenciaRepository;
	private ContaServiceImpl contaService;

	@Before
	public void setUp() {
		contaRepository = mock(ContaRepository.class);
		transferenciaRepository = mock(TransferenciaRepository.class);
		contaService = new ContaServiceImpl(contaRepository, transferenciaRepository);
	}

	@Test
	public void testCriarConta() {
		String nome = "John Doe";
		Conta conta = new Conta(nome);
		when(contaRepository.save(any(Conta.class))).thenReturn(conta);

		Conta novaConta = contaService.criarConta(nome);

		assertEquals(nome, novaConta.getNome());
		assertEquals(0.0, novaConta.getSaldo(), 0.001);
	}

	@Test(expected = ContaException.class)
	public void testCriarContaErro() {
		String nome = "John Doe";
		when(contaRepository.save(any(Conta.class))).thenThrow(new RuntimeException());

		contaService.criarConta(nome);
	}

	@Test
	public void testDeposit() throws ContaNotFoundException {
		Long idConta = 1L;
		double valor = 100.0;
		Conta conta = new Conta("John Doe");
		conta.setSaldo(200.0);
		when(contaRepository.findById(idConta)).thenReturn(java.util.Optional.of(conta));

		contaService.depositar(idConta, valor);

		assertEquals(300.0, conta.getSaldo(), 0.001);
	}

	@Test(expected = ContaNotFoundException.class)
    public void testDepositContaNaoEncontrada() throws ContaNotFoundException {
        when(contaRepository.findById(any(Long.class))).thenReturn(java.util.Optional.empty());

        contaService.depositar(1L, 100.0);
    }

	@Test
	public void testWithdraw() throws ContaNotFoundException, SaldoInsuficienteException {
		Long idConta = 1L;
		double valor = 100.0;
		Conta conta = new Conta("John Doe");
		conta.setSaldo(200.0);
		when(contaRepository.findById(idConta)).thenReturn(java.util.Optional.of(conta));

		contaService.sacar(idConta, valor);

		assertEquals(100.0, conta.getSaldo(), 0.001);
	}

	@Test(expected = ContaNotFoundException.class)
    public void testWithdrawContaNaoEncontrada() throws ContaNotFoundException, SaldoInsuficienteException {
        when(contaRepository.findById(any(Long.class))).thenReturn(java.util.Optional.empty());

        contaService.sacar(1L, 100.0);
    }

	@Test
	public void testObterContaPorId() throws ContaNotFoundException {
		Long idConta = 1L;
		Conta conta = new Conta("John Doe");
		when(contaRepository.findById(idConta)).thenReturn(java.util.Optional.of(conta));

		Conta contaObtida = contaService.obterContaPorId(idConta);

		assertEquals(conta, contaObtida);
	}

	@Test(expected = ContaNotFoundException.class)
    public void testObterContaPorIdNaoEncontrada() throws ContaNotFoundException {
        when(contaRepository.findById(any(Long.class))).thenReturn(java.util.Optional.empty());

        contaService.obterContaPorId(1L);
    }

	@Test
	public void testObterContaPorNome() {
		String nome = "John Doe";
		Conta conta = new Conta(nome);
		when(contaRepository.findByNomeIgnoreCaseLike(nome)).thenReturn(conta);

		Conta contaObtida = contaService.obterContaPorNome(nome);

		assertEquals(conta, contaObtida);
	}

	@Test
	public void testTransferir() throws ContaNotFoundException, SaldoInsuficienteException {
		Long idContaOrigem = 1L;
		Long idContaDestino = 2L;
		double valor = 100.0;
		Conta contaOrigem = new Conta("John Doe");
		contaOrigem.setSaldo(200.0);
		Conta contaDestino = new Conta("Jane Smith");
		when(contaRepository.findById(idContaOrigem)).thenReturn(java.util.Optional.of(contaOrigem));
		when(contaRepository.findById(idContaDestino)).thenReturn(java.util.Optional.of(contaDestino));

		contaService.transferir(idContaOrigem, idContaDestino, valor, Operation.TRANSFERENCIA);

		assertEquals(100.0, contaOrigem.getSaldo(), 0.001);
		assertEquals(100.0, contaDestino.getSaldo(), 0.001);
	}

	@Test(expected = ContaNotFoundException.class)
    public void testTransferirContaOrigemNaoEncontrada() throws ContaNotFoundException, SaldoInsuficienteException {
        when(contaRepository.findById(any(Long.class))).thenReturn(java.util.Optional.empty());

        contaService.transferir(1L, 2L, 100.0, Operation.TRANSFERENCIA);
    }

	@Test(expected = ContaNotFoundException.class)
	public void testTransferirContaDestinoNaoEncontrada() throws ContaNotFoundException, SaldoInsuficienteException {
		Conta contaOrigem = new Conta("John Doe");
		when(contaRepository.findById(1L)).thenReturn(java.util.Optional.of(contaOrigem));
		when(contaRepository.findById(2L)).thenReturn(java.util.Optional.empty());

		contaService.transferir(1L, 2L, 100.0, Operation.TRANSFERENCIA);
	}

	@Test
	public void testBuscarTransacoesPorPeriodoENome() {
		ZonedDateTime dataInicio = ZonedDateTime.parse("2023-07-01T00:00:00Z");
		ZonedDateTime dataFim = ZonedDateTime.parse("2023-07-31T23:59:59Z");
		String nome = "John Doe";
		List<Transferencia> transacoes = new ArrayList<>();
		when(transferenciaRepository.buscarPorPeriodoENome(dataInicio, dataFim, nome)).thenReturn(transacoes);

		List<Transferencia> transacoesObtidas = contaService.buscarTransacoesPorPeriodoENome(dataInicio, dataFim, nome);

		assertEquals(transacoes, transacoesObtidas);
	}

	@Test
	public void testBuscarTransacoesPorNome() {
		String nome = "John Doe";
		List<Transferencia> transacoes = new ArrayList<>();
		when(transferenciaRepository.buscarPorNome(nome)).thenReturn(transacoes);

		List<Transferencia> transacoesObtidas = contaService.buscarTransacoesPorNome(nome);

		assertEquals(transacoes, transacoesObtidas);
	}

	@Test
	public void testCalcularSaldoTotalPorNome() throws ContaNotFoundException {
		String nome = "John Doe";
		Conta conta = new Conta(nome);
		conta.setSaldo(500.0);
		when(contaRepository.findByNomeIgnoreCaseLike(nome)).thenReturn(conta);

		double saldoTotal = contaService.calcularSaldoTotalPorNome(nome);

		assertEquals(500.0, saldoTotal, 0.001);
	}

	@Test
	public void testCalcularSaldoTotalPorNomeContaNaoEncontrada() throws ContaNotFoundException {
		String nome = "John Doe";
		when(contaRepository.findByNomeIgnoreCaseLike(nome)).thenReturn(null);

		double saldoTotal = contaService.calcularSaldoTotalPorNome(nome);

		assertEquals(0.0, saldoTotal, 0.001);
	}

	@Test
	public void testCalcularSaldoTotalPorNomeContaNotFoundException() throws ContaNotFoundException {
		String nome = "John Doe";
		when(contaRepository.findByNomeIgnoreCaseLike(nome))
				.thenThrow(new ContaNotFoundException("Conta não encontrada."));

		double saldoTotal = contaService.calcularSaldoTotalPorNome(nome);

		assertEquals(0.0, saldoTotal, 0.001);
	}

	@Test
	public void testCalcularSaldoPeriodoPorNomeDataInicioNulaDataFimNula() {
		String nome = "John Doe";
		List<Transferencia> transacoes = new ArrayList<>();
		Transferencia transferencia1 = new Transferencia();
		transferencia1.setTipo(Operation.DEPOSITO);
		transferencia1.setValor(100.0);
		Transferencia transferencia2 = new Transferencia();
		transferencia2.setTipo(Operation.SAQUE);
		transferencia2.setValor(50.0);
		transacoes.add(transferencia1);
		transacoes.add(transferencia2);
		when(transferenciaRepository.buscarPorNome(nome)).thenReturn(transacoes);

		double saldoPeriodo = contaService.calcularSaldoPeriodoPorNome(null, null, nome);

		assertEquals(50.0, saldoPeriodo, 0.001);
	}

	@Test
	public void testCreateErrorResponse() {
		String errorMessage = "Error message";
		String expectedError = "Error message";
		Map<String, Object> errorResponse = contaService.createErrorResponse(errorMessage);

		assertEquals(expectedError, errorResponse.get("error"));
	}

	@Test
	public void testHasContaByNameTrue() {
		String nome = "John Doe";
		Conta conta = new Conta(nome);
		when(contaRepository.findByNomeIgnoreCaseLike(nome)).thenReturn(conta);

		boolean hasConta = contaService.hasContaByName(nome);

		assertTrue(hasConta);
	}

	@Test
	public void testHasContaByNameFalse() {
		String nome = "John Doe";
		when(contaRepository.findByNomeIgnoreCaseLike(nome)).thenReturn(null);

		boolean hasConta = contaService.hasContaByName(nome);

		assertFalse(hasConta);
	}

	@Test
	public void testHasContaTrue() {
		Long id = 1L;
		when(contaRepository.existsById(id)).thenReturn(true);

		boolean hasConta = contaService.hasConta(id);

		assertTrue(hasConta);
	}

	@Test
	public void testHasContaFalse() {
		Long id = 1L;
		when(contaRepository.existsById(id)).thenReturn(false);

		boolean hasConta = contaService.hasConta(id);

		assertFalse(hasConta);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidarParametrosIdContaOrigemNulo() {
		contaService.validarParametros(null, 2L, 100.0, Operation.TRANSFERENCIA);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidarParametrosIdContaDestinoNulo() {
		contaService.validarParametros(1L, null, 100.0, Operation.TRANSFERENCIA);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidarParametrosValorInvalido() {
		contaService.validarParametros(1L, 2L, -100.0, Operation.TRANSFERENCIA);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidarParametrosTipoNulo() {
		contaService.validarParametros(1L, 2L, 100.0, null);
	}
}
