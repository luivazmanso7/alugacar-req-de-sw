package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ChecklistVistoria;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.patio.Patio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;

/**
 * Componente responsável pelo mapeamento entre objetos de domínio e entidades JPA.
 * Utiliza ModelMapper com conversores customizados para garantir compatibilidade total.
 */
@Component
class JpaMapeador extends ModelMapper {

	@Autowired
	private ReservaJpaRepository reservaRepositorio;

	@Autowired
	private VeiculoJpaRepository veiculoRepositorio;

	@Autowired
	private ClienteJpaRepository clienteRepositorio;

	JpaMapeador() {
		var configuracao = getConfiguration();
		configuracao.setFieldMatchingEnabled(true);
		configuracao.setFieldAccessLevel(AccessLevel.PRIVATE);

		configurarConversoresCliente();
		configurarConversoresCategoria();
		configurarConversoresVeiculo();
		configurarConversoresReserva();
		configurarConversoresLocacao();
		configurarConversoresPatio();
		configurarConversoresPeriodo();
		configurarConversoresChecklist();
	}

	private void configurarConversoresCliente() {
		addConverter(new AbstractConverter<ClienteJpa, Cliente>() {
			@Override
			protected Cliente convert(ClienteJpa source) {
				return new Cliente(source.nome, source.cpfOuCnpj, source.cnh, source.email);
			}
		});

		addConverter(new AbstractConverter<Cliente, ClienteJpa>() {
			@Override
			protected ClienteJpa convert(Cliente source) {
				var jpa = new ClienteJpa();
				jpa.cpfOuCnpj = source.getCpfOuCnpj();
				jpa.nome = source.getNome();
				jpa.cnh = source.getCnh();
				jpa.email = source.getEmail();
				return jpa;
			}
		});
	}

	private void configurarConversoresCategoria() {
		addConverter(new AbstractConverter<CategoriaJpa, Categoria>() {
			@Override
			protected Categoria convert(CategoriaJpa source) {
				var modelosLista = source.modelosExemplo != null 
					? List.of(source.modelosExemplo.split(","))
					: List.<String>of();
				return new Categoria(source.codigo, source.nome, source.descricao, 
					source.diaria, modelosLista, source.quantidadeDisponivel);
			}
		});

		addConverter(new AbstractConverter<Categoria, CategoriaJpa>() {
			@Override
			protected CategoriaJpa convert(Categoria source) {
				var jpa = new CategoriaJpa();
				jpa.codigo = source.getCodigo();
				jpa.nome = source.getNome();
				jpa.descricao = source.getDescricao();
				jpa.diaria = source.getDiaria();
				jpa.modelosExemplo = String.join(",", source.getModelosExemplo());
				jpa.quantidadeDisponivel = source.getQuantidadeDisponivel();
				return jpa;
			}
		});
	}

	private void configurarConversoresVeiculo() {
		addConverter(new AbstractConverter<VeiculoJpa, Veiculo>() {
			@Override
			protected Veiculo convert(VeiculoJpa source) {
				var veiculo = new Veiculo(source.placa, source.modelo, source.categoria, 
					source.cidade, source.diaria, source.status);
				
				if (source.patio != null && source.patio.codigo != null) {
					var patio = new Patio(source.patio.codigo, source.patio.cidade);
					// Usar reflexão ou método setter se disponível
				}
				
				if (source.manutencaoPrevista != null) {
					veiculo.agendarManutencao(source.manutencaoPrevista, source.manutencaoNota);
				}
				
				return veiculo;
			}
		});

		addConverter(new AbstractConverter<Veiculo, VeiculoJpa>() {
			@Override
			protected VeiculoJpa convert(Veiculo source) {
				var jpa = new VeiculoJpa();
				jpa.placa = source.getPlaca();
				jpa.modelo = source.getModelo();
				jpa.categoria = source.getCategoria();
				jpa.cidade = source.getCidade();
				jpa.diaria = source.getDiaria();
				jpa.status = source.getStatus();
				jpa.manutencaoPrevista = source.getManutencaoPrevista();
				jpa.manutencaoNota = source.getManutencaoNota();
				
				if (source.getPatio() != null) {
					var patioJpa = new PatioJpa();
					patioJpa.codigo = source.getPatio().getCodigo();
					patioJpa.cidade = source.getPatio().getCidade();
					jpa.patio = patioJpa;
				}
				
				return jpa;
			}
		});
	}

	private void configurarConversoresReserva() {
		addConverter(new AbstractConverter<ReservaJpa, Reserva>() {
			@Override
			protected Reserva convert(ReservaJpa source) {
				var periodo = new PeriodoLocacao(source.periodo.retirada, source.periodo.devolucao);
				var cliente = map(source.cliente, Cliente.class);
				
				return new Reserva(source.codigo, source.categoria, source.cidadeRetirada, 
					periodo, source.valorEstimado, source.status, cliente);
			}
		});

		addConverter(new AbstractConverter<Reserva, ReservaJpa>() {
			@Override
			protected ReservaJpa convert(Reserva source) {
				var jpa = new ReservaJpa();
				jpa.codigo = source.getCodigo();
				jpa.categoria = source.getCategoria();
				jpa.cidadeRetirada = source.getCidadeRetirada();
				jpa.valorEstimado = source.getValorEstimado();
				jpa.status = source.getStatus();
				
				var periodoJpa = new PeriodoLocacaoJpa();
				periodoJpa.retirada = source.getPeriodo().getRetirada();
				periodoJpa.devolucao = source.getPeriodo().getDevolucao();
				jpa.periodo = periodoJpa;
				
				jpa.cliente = clienteRepositorio.findById(source.getCliente().getCpfOuCnpj())
					.orElseGet(() -> map(source.getCliente(), ClienteJpa.class));
				
				return jpa;
			}
		});
	}

	private void configurarConversoresLocacao() {
		addConverter(new AbstractConverter<LocacaoJpa, Locacao>() {
			@Override
			protected Locacao convert(LocacaoJpa source) {
				var reserva = map(source.reserva, Reserva.class);
				var veiculo = map(source.veiculo, Veiculo.class);
				var vistoriaRetirada = map(source.vistoriaRetirada, ChecklistVistoria.class);
				
				return new Locacao(source.codigo, reserva, veiculo, source.diasPrevistos, 
					source.valorDiaria, vistoriaRetirada);
			}
		});

		addConverter(new AbstractConverter<Locacao, LocacaoJpa>() {
			@Override
			protected LocacaoJpa convert(Locacao source) {
				var jpa = new LocacaoJpa();
				jpa.codigo = source.getCodigo();
				jpa.diasPrevistos = source.getDiasPrevistos();
				jpa.valorDiaria = source.getValorDiaria();
				jpa.status = source.getStatus();
				
				jpa.reserva = reservaRepositorio.findById(source.getReserva().getCodigo())
					.orElseGet(() -> map(source.getReserva(), ReservaJpa.class));
				
				jpa.veiculo = veiculoRepositorio.findById(source.getVeiculo().getPlaca())
					.orElseGet(() -> map(source.getVeiculo(), VeiculoJpa.class));
				
				jpa.vistoriaRetirada = map(source.getVistoriaRetirada(), ChecklistVistoriaJpa.class);
				
				if (source.getVistoriaDevolucao() != null) {
					jpa.vistoriaDevolucao = map(source.getVistoriaDevolucao(), ChecklistVistoriaJpa.class);
				}
				
				return jpa;
			}
		});
	}

	private void configurarConversoresPatio() {
		addConverter(new AbstractConverter<PatioJpa, Patio>() {
			@Override
			protected Patio convert(PatioJpa source) {
				return new Patio(source.codigo, source.cidade);
			}
		});

		addConverter(new AbstractConverter<Patio, PatioJpa>() {
			@Override
			protected PatioJpa convert(Patio source) {
				var jpa = new PatioJpa();
				jpa.codigo = source.getCodigo();
				jpa.cidade = source.getCidade();
				return jpa;
			}
		});
	}

	private void configurarConversoresPeriodo() {
		addConverter(new AbstractConverter<PeriodoLocacaoJpa, PeriodoLocacao>() {
			@Override
			protected PeriodoLocacao convert(PeriodoLocacaoJpa source) {
				return new PeriodoLocacao(source.retirada, source.devolucao);
			}
		});

		addConverter(new AbstractConverter<PeriodoLocacao, PeriodoLocacaoJpa>() {
			@Override
			protected PeriodoLocacaoJpa convert(PeriodoLocacao source) {
				var jpa = new PeriodoLocacaoJpa();
				jpa.retirada = source.getRetirada();
				jpa.devolucao = source.getDevolucao();
				return jpa;
			}
		});
	}

	private void configurarConversoresChecklist() {
		addConverter(new AbstractConverter<ChecklistVistoriaJpa, ChecklistVistoria>() {
			@Override
			protected ChecklistVistoria convert(ChecklistVistoriaJpa source) {
				if (source == null || source.quilometragem == null) {
					return null;
				}
				return new ChecklistVistoria(source.quilometragem, source.combustivel, source.possuiAvarias);
			}
		});

		addConverter(new AbstractConverter<ChecklistVistoria, ChecklistVistoriaJpa>() {
			@Override
			protected ChecklistVistoriaJpa convert(ChecklistVistoria source) {
				if (source == null) {
					return null;
				}
				var jpa = new ChecklistVistoriaJpa();
				jpa.quilometragem = source.quilometragem();
				jpa.combustivel = source.combustivel();
				jpa.possuiAvarias = source.possuiAvarias();
				return jpa;
			}
		});
	}

	/**
	 * Mapeia uma lista de objetos JPA para objetos de domínio.
	 */
	public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream()
			.map(element -> map(element, targetClass))
			.collect(Collectors.toList());
	}

	/**
	 * Mapeia uma lista de objetos JPA para objetos de domínio usando TypeToken.
	 */
	public <S, T> List<T> mapList(List<S> source, TypeToken<List<T>> typeToken) {
		return map(source, typeToken.getType());
	}
}
