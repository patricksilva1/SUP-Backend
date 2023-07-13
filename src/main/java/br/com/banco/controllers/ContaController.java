package br.com.banco.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.banco.dtos.ContaDto;
import br.com.banco.entities.Conta;
import br.com.banco.services.TransferenciaService;
import br.com.banco.services.TransferenciaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ACCOUNTS", description = "Endpoints Management.")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/account")
public class ContaController {
//	@Autowired
//	private TransferenciaService transferenciaServiceImpl;

	@Operation(summary = "Checa o status do Controller.", description = "Nos ajuda a Realizar a checagem do Controller.")
	@GetMapping(value = "/status")
	public String statusService(@Value("${local.server.port}") String port) {

		return String.format("Running at %s", port);
	}

	/*
	 * api/v1/registrar - registrar api/v1/depositar/{id}/{valor} - depositar
	 * api/v1/sacar/{id}/{valor} - sacar
	 * api/v1/transferir/{idOrigem}/{valor}/{idDestino} - transferir
	 * api/v1/transacoes/{id} - getAllTransactions 
	 *  Paginado
	 */

	/*
	 * /transferencias: Retorna todas as transferências sem nenhum filtro.
		/transferencias?conta=<numero_conta>: Retorna todas as transferências relacionadas a um número de conta específico.
		/transferencias?data_inicio=<data_inicio>&data_fim=<data_fim>: Retorna todas as transferências dentro de um período de tempo especificado.
		/transferencias?operador=<nome_operador>: Retorna todas as transferências relacionadas a um operador específico.
		/transferencias?data_inicio=<data_inicio>&data_fim=<data_fim>&operador=<nome_operador>: Retorna todas as transferências com base no período de tempo e operador especificados.
	 */
	
	
	
	
	
	
//	   @PostMapping(path = "/register")
//	    public ResponseEntity<Void> registerAccount(@RequestBody ContaDto contaDto) {
//
//	        Conta conta = contaService.fromDto(contaDto);
//
//	        contaService.inserir(conta);
//
//	        return ResponseEntity.ok().build();
//	    }

//	@RequestMapping(value = "page", method = RequestMethod.GET)
//	public ResponseEntity<Page<Transferencia>> findPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
//			@RequestParam(value = "filterDataInicio", defaultValue = "") String filterDataInicio,
//			@RequestParam(value = "filterDataFim", defaultValue = "") String filterDataFim,
//			@RequestParam(value = "filterNomeOperadorTransacao", defaultValue = "") String filterNomeOperadorTransacao,
//			@RequestParam(value = "linesPerPage", defaultValue = "24") Integer linesPerPage,
//			@RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
//			@RequestParam(value = "direction", defaultValue = "ASC") String direction) {
//		Page<Transferencia> categorias = transferenciaServiceImpl.findPage(page, linesPerPage, orderBy, direction, filterDataInicio, filterDataFim, filterNomeOperadorTransacao);
//		
//		return ResponseEntity.ok().body(categorias);
//	}
}