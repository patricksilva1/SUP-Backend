package br.com.banco.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="CONTAS", description="Endpoints Management.")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/account")
public class ContaController {

	@Operation(summary = "Checa o status do Controller.", description = "Nos ajuda a Realizar a checagem do Controller.")
	@GetMapping(value = "/status")
	public String statusService(@Value("${local.server.port}") String port) {

		return String.format("Running at %s", port);
	}
}