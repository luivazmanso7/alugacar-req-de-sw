package dev.sauloaraujo.sgb.aplicacao.locacao.operacao;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.operacao.DevolucaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Faturamento;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ProcessarDevolucaoCommand;

@Service
public class LocacaoServicoAplicacao {
    
    private final LocacaoRepositorioAplicacao repositorioLeitura;
    private final DevolucaoServico devolucaoServico; // Serviço de Domínio

    // Injeção via construtor
    public LocacaoServicoAplicacao(
            LocacaoRepositorioAplicacao repositorioLeitura,
            DevolucaoServico devolucaoServico) {
        this.repositorioLeitura = notNull(repositorioLeitura, "Repositório de leitura não pode ser nulo");
        this.devolucaoServico = notNull(devolucaoServico, "Serviço de devolução não pode ser nulo");
    }

    // --- Métodos de Leitura (Já existentes) ---
    public List<LocacaoResumo> pesquisarResumos() {
        return repositorioLeitura.pesquisarResumos();
    }
    
    public Optional<LocacaoResumo> buscarPorCodigo(String codigo) {
        return repositorioLeitura.buscarPorCodigo(codigo);
    }
    
    public List<LocacaoResumo> listarAtivas() {
        return repositorioLeitura.listarAtivas();
    }
    
    public List<LocacaoResumo> listarPorCliente(String cpfOuCnpj) {
        return repositorioLeitura.listarPorCliente(cpfOuCnpj);
    }

    // --- NOVO: Método de Escrita (Processar Devolução) ---
    
    /**
     * Processa a devolução de uma locação.
     * Delega a regra de negócio para o domínio e garante a transação.
     */
    @Transactional
    public Faturamento processarDevolucao(ProcessarDevolucaoCommand comando) {
        // O serviço de domínio (DevolucaoServico) executa a lógica e salva as entidades.
        // O @Transactional aqui garante que, se algo falhar, nada é salvo.
        return devolucaoServico.processar(comando);
    }
}