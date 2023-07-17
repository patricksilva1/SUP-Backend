package br.com.banco.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.exceptions.TransferenciaException;
import br.com.banco.repositories.ContaRepository;
import br.com.banco.repositories.TransferenciaRepository;

@RunWith(MockitoJUnitRunner.class)
public class TransferenciaServiceImplTest {

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private TransferenciaServiceImpl transferenciaService;

    @Test
    public void testGetAllTransferencias() throws TransferenciaException {
        // Arrange
        List<Transferencia> expectedTransferencias = new ArrayList<>();
        expectedTransferencias.add(new Transferencia());
        expectedTransferencias.add(new Transferencia());
        when(transferenciaRepository.findAll()).thenReturn(expectedTransferencias);

        // Act
        List<Transferencia> resultTransferencias = transferenciaService.getAllTransferencias();

        // Assert
        assertEquals(expectedTransferencias, resultTransferencias);
    }

    @Test
    public void testGetTransferenciasPorConta() {
        // Arrange
        Long numeroConta = 123456L;
        List<Transferencia> expectedTransferencias = new ArrayList<>();
        expectedTransferencias.add(new Transferencia());
        expectedTransferencias.add(new Transferencia());
        when(transferenciaRepository.findByContaNumeroConta(numeroConta)).thenReturn(expectedTransferencias);

        // Act
        List<Transferencia> resultTransferencias = transferenciaService.getTransferenciasPorConta(numeroConta);

        // Assert
        assertEquals(expectedTransferencias, resultTransferencias);
    }

    @Test
    public void testGetTransferenciasPorPeriodo() {
        // Arrange
        ZonedDateTime dataInicio = ZonedDateTime.now();
        ZonedDateTime dataFim = ZonedDateTime.now().plusDays(7);
        List<Transferencia> expectedTransferencias = new ArrayList<>();
        expectedTransferencias.add(new Transferencia());
        expectedTransferencias.add(new Transferencia());
        when(transferenciaRepository.findByDataTransferenciaBetween(dataInicio, dataFim)).thenReturn(expectedTransferencias);

        // Act
        List<Transferencia> resultTransferencias = transferenciaService.getTransferenciasPorPeriodo(dataInicio, dataFim);

        // Assert
        assertEquals(expectedTransferencias, resultTransferencias);
    }

    @Test
    public void testGetTransferenciasPorOperador() {
        // Arrange
        String nomeOperador = "John Doe";
        List<Transferencia> expectedTransferencias = new ArrayList<>();
        expectedTransferencias.add(new Transferencia());
        expectedTransferencias.add(new Transferencia());
        when(transferenciaRepository.findByNomeOperadorTransacao(nomeOperador)).thenReturn(expectedTransferencias);

        // Act
        List<Transferencia> resultTransferencias = transferenciaService.getTransferenciasPorOperador(nomeOperador);

        // Assert
        assertEquals(expectedTransferencias, resultTransferencias);
    }

    @Test
    public void testGetTransferenciasPaginadas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transferencia> expectedPage = new PageImpl<>(new ArrayList<>());
        when(transferenciaRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Transferencia> resultPage = transferenciaService.getTransferenciasPaginadas(pageable);

        // Assert
        assertEquals(expectedPage, resultPage);
    }
    
 // *******
    @Test
    public void testSacar() {
        // Arrange
        Long contaId = 1L;
        double valorSaque = 100.0;
        Conta conta = new Conta();
        conta.setId(contaId);
        conta.setSaldo(500.0);
        when(contaRepository.findById(contaId)).thenReturn(java.util.Optional.of(conta));

        // Act
        transferenciaService.sacar(contaId, valorSaque);

        // Assert
        assertEquals(400.0, conta.getSaldo(), 0.0);
    }

    @Test
    public void testGetTransferenciasPorPeriodoEOperador() {
        // Arrange
        ZonedDateTime dataInicio = ZonedDateTime.now().minusDays(7);
        ZonedDateTime dataFim = ZonedDateTime.now();
        String nomeOperador = "John Doe";
        List<Transferencia> expectedTransferencias = new ArrayList<>();
        expectedTransferencias.add(new Transferencia());
        expectedTransferencias.add(new Transferencia());
        when(transferenciaRepository.findByDataInicioAndDataFimAndNomeOperador(dataInicio, dataFim, nomeOperador)).thenReturn(expectedTransferencias);

        // Act
        List<Transferencia> resultTransferencias = transferenciaService.getTransferenciasPorPeriodoEOperador(dataInicio, dataFim, nomeOperador);

        // Assert
        assertEquals(expectedTransferencias, resultTransferencias);
    }

    @Test
    public void testSacar_InsufficientBalance() {
        // Arrange
        Long contaId = 1L;
        double valorSaque = 1000.0;
        Conta conta = new Conta();
        conta.setId(contaId);
        conta.setSaldo(500.0);
        when(contaRepository.findById(contaId)).thenReturn(java.util.Optional.of(conta));

        // Act
        transferenciaService.sacar(contaId, valorSaque);
    }
    
    /////////////
    @Test
    public void testListaVazia() {
        List<String> lista = new ArrayList<>();

        assertTrue(lista.isEmpty());
        assertEquals(0, lista.size());
    }

    @Test
    public void testStringNulaOuVazia() {
        String texto = null;

        assertNull(texto);
        assertTrue(texto == null || texto.isEmpty());
    }

    @Test
    public void testListaVaziaAposRemocao() {
        List<String> lista = new ArrayList<>();
        lista.add("Elemento 1");
        lista.add("Elemento 2");

        // Remover os elementos da lista
        lista.clear();

        assertTrue(lista.isEmpty());
    }

    
}
