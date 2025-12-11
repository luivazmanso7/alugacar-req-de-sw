package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;

public class ReservaServico {
    private static final BigDecimal FATOR_ALTA_DEMANDA = BigDecimal.valueOf(1.25);
    private final ReservaRepositorio reservaRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final LocacaoRepositorio locacaoRepositorio;
    private final VeiculoRepositorio veiculoRepositorio;

    public ReservaServico(ReservaRepositorio reservaRepositorio, CategoriaRepositorio categoriaRepositorio,
            ClienteRepositorio clienteRepositorio, LocacaoRepositorio locacaoRepositorio,
            VeiculoRepositorio veiculoRepositorio) {
        this.reservaRepositorio = Objects.requireNonNull(reservaRepositorio,
                "Repositorio de reservas é obrigatório");
        this.categoriaRepositorio = Objects.requireNonNull(categoriaRepositorio,
                "Repositorio de categorias é obrigatório");
        this.clienteRepositorio = Objects.requireNonNull(clienteRepositorio,
                "Repositorio de clientes é obrigatório");
        this.locacaoRepositorio = Objects.requireNonNull(locacaoRepositorio,
                "Repositorio de locações é obrigatório");
        this.veiculoRepositorio = Objects.requireNonNull(veiculoRepositorio,
                "Repositorio de veículos é obrigatório");
    }

	public InformacaoReserva obterInformacoesReserva() {
		return new InformacaoReserva(true, true, true, true);
	}

	public List<StatusReserva> statusDisponiveis() {
		return List.of(StatusReserva.values());
	}

	public RequisitosCriacaoReserva requisitosCriacao() {
		return new RequisitosCriacaoReserva(true, true, true, true);
	}

	public Reserva criarReserva(String codigo, CategoriaCodigo categoriaCodigo, String cidadeRetirada,
			PeriodoLocacao periodo, Cliente cliente, String placaVeiculo) {
		Objects.requireNonNull(codigo, "O código é obrigatório");
		Objects.requireNonNull(placaVeiculo, "A placa do veículo é obrigatória");
		
		// 1. Validar que o veículo existe e está disponível
		var veiculo = veiculoRepositorio.buscarPorPlaca(placaVeiculo)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + placaVeiculo));
		
		// 2. Validar que a categoria do veículo corresponde à categoria solicitada
		if (!veiculo.getCategoria().equals(categoriaCodigo)) {
			throw new IllegalArgumentException("A categoria do veículo não corresponde à categoria solicitada");
		}
		
		// 3. Validar que o veículo está disponível (status)
		if (!veiculo.disponivel()) {
			throw new IllegalStateException("O veículo não está disponível para reserva");
		}
		
		// 4. Validar que o veículo não está reservado ou locado no período solicitado
		validarDisponibilidadeVeiculo(placaVeiculo, periodo);
		
		var categoria = obterCategoria(categoriaCodigo);
        var valorEstimado = calcularValorEstimado(categoria.getDiaria(), periodo, categoriaCodigo,
                categoria.getQuantidadeDisponivel());
        registrarClienteSeNecessario(cliente);

        var reserva = new Reserva(codigo, categoriaCodigo, cidadeRetirada, periodo, valorEstimado,
                StatusReserva.ATIVA, cliente, placaVeiculo);
        reservaRepositorio.salvar(reserva);

        return reserva;
    }

	public List<Categoria> categoriasDisponiveis() {
		return categoriaRepositorio.listarTodas();
	}

    private BigDecimal calcularValorEstimado(BigDecimal valorDiaria, PeriodoLocacao periodo,
            CategoriaCodigo categoriaCodigo, int capacidadeCategoria) {
        var base = valorDiaria.multiply(BigDecimal.valueOf(periodo.dias()));
        var ocupacao = calcularOcupacao(categoriaCodigo, periodo);
        var disponibilidade = capacidadeCategoria - ocupacao;

        if (disponibilidade <= 0) {
            throw new IllegalStateException("Não há veículos disponíveis para o período selecionado");
        }

        if (disponibilidade == 1) {
            return base.multiply(FATOR_ALTA_DEMANDA);
        }

        return base;
    }

    /**
     * Calcula a ocupação de uma categoria em um período específico.
     * Considera TANTO reservas ativas QUANTO locações ativas no período.
     * 
     * REGRA DE NEGÓCIO: Um veículo locado não pode estar disponível durante o período da locação.
     */
    private int calcularOcupacao(CategoriaCodigo categoriaCodigo, PeriodoLocacao periodo) {
        // Contar reservas ativas conflitantes
        long reservasConflitantes = reservaRepositorio.listar().stream()
                .filter(reserva -> reserva.getCategoria().equals(categoriaCodigo))
                .filter(reserva -> reserva.getStatus().ativa())
                .filter(reserva -> periodosConflitantes(reserva.getPeriodo(), periodo))
                .count();

        // Contar locações ativas conflitantes
        long locacoesConflitantes = locacaoRepositorio.listarLocacoes().stream()
                .filter(locacao -> locacao.getReserva().getCategoria().equals(categoriaCodigo))
                .filter(locacao -> locacao.getStatus() == StatusLocacao.ATIVA)
                .filter(locacao -> periodosConflitantes(locacao.getReserva().getPeriodo(), periodo))
                .count();

        return Math.toIntExact(reservasConflitantes + locacoesConflitantes);
    }

	private boolean periodosConflitantes(PeriodoLocacao existente, PeriodoLocacao desejado) {
		LocalDateTime inicioExistente = existente.getRetirada();
		LocalDateTime fimExistente = existente.getDevolucao();
		LocalDateTime inicioNovo = desejado.getRetirada();
		LocalDateTime fimNovo = desejado.getDevolucao();
		return !inicioNovo.isAfter(fimExistente) && !fimNovo.isBefore(inicioExistente);
	}

	private void registrarClienteSeNecessario(Cliente cliente) {
		var documento = cliente.getCpfOuCnpj();
		var existente = clienteRepositorio.buscarPorDocumento(documento);
		existente.ifPresentOrElse(c -> {}, () -> clienteRepositorio.salvar(cliente));
	}

	/**
	 * Valida se um veículo específico está disponível no período solicitado.
	 * REGRA DE NEGÓCIO: Um veículo não pode ser reservado se já existe uma reserva ativa
	 * ou locação ativa para o mesmo veículo no período solicitado.
	 */
	private void validarDisponibilidadeVeiculo(String placaVeiculo, PeriodoLocacao periodo) {
		// Verificar reservas ativas do mesmo veículo no período
		boolean temReservaConflitante = reservaRepositorio.listarPorVeiculo(placaVeiculo).stream()
				.filter(reserva -> reserva.getStatus().ativa())
				.anyMatch(reserva -> periodosConflitantes(reserva.getPeriodo(), periodo));
		
		if (temReservaConflitante) {
			throw new IllegalStateException("O veículo já está reservado para outro cliente no período solicitado");
		}
		
		// Verificar locações ativas do mesmo veículo no período
		boolean temLocacaoConflitante = locacaoRepositorio.listarLocacoes().stream()
				.filter(locacao -> locacao.getStatus() == StatusLocacao.ATIVA)
				.filter(locacao -> locacao.getVeiculo().getPlaca().equals(placaVeiculo))
				.anyMatch(locacao -> periodosConflitantes(locacao.getReserva().getPeriodo(), periodo));
		
		if (temLocacaoConflitante) {
			throw new IllegalStateException("O veículo já está locado para outro cliente no período solicitado");
		}
	}

	private Categoria obterCategoria(CategoriaCodigo categoria) {
		return categoriaRepositorio.buscarPorCodigo(categoria)
				.orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada: " + categoria));
	}
}
