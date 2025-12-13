package dev.sauloaraujo.sgb.apresentacao.locacao.operacao;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.LocacaoResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.LocacaoServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/locacoes")
@Tag(name = "Locações Admin", description = "Operações de consulta de locações para administradores")
public class AdminLocacaoController {
	
	private final LocacaoServicoAplicacao locacaoServico;

	public AdminLocacaoController(LocacaoServicoAplicacao locacaoServico) {
		this.locacaoServico = locacaoServico;
	}

	@GetMapping("/em-andamento")
	@Operation(summary = "Listar locações em andamento", description = "Retorna todas as locações em andamento (status EM_ANDAMENTO) para administradores")
	public ResponseEntity<List<LocacaoResumo>> listarEmAndamento() {
		var locacoes = locacaoServico.listarEmAndamento();
		return ResponseEntity.ok(locacoes);
	}
}

