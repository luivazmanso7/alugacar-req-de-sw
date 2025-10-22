package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.util.List;
import java.util.Optional;

public interface LocacaoRepositorio {
	void salvar(Locacao locacao);

    Optional<Locacao> buscarPorCodigoLocacao(String codigo);

    List<Locacao> listarLocacoes();
}
