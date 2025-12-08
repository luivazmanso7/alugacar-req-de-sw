package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

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
public class JpaMapeador extends ModelMapper {

	@Autowired
	private ReservaJpaRepository reservaRepositorio;

	@Autowired
	private VeiculoJpaRepository veiculoRepositorio;

	@Autowired
	private ClienteJpaRepository clienteRepositorio;

	public JpaMapeador() {
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
				if (source == null) {
					return null;
				}
				return new Cliente(source.getNome(), source.getCpfOuCnpj(), source.getCnh(), source.getEmail());
			}
		});

		addConverter(new AbstractConverter<Cliente, ClienteJpa>() {
			@Override
			protected ClienteJpa convert(Cliente source) {
				if (source == null) {
					return null;
				}
				var jpa = new ClienteJpa();
				jpa.setCpfOuCnpj(source.getCpfOuCnpj());
				jpa.setNome(source.getNome());
				jpa.setCnh(source.getCnh());
				jpa.setEmail(source.getEmail());
				return jpa;
			}
		});
	}

	private void configurarConversoresCategoria() {
		addConverter(new AbstractConverter<CategoriaJpa, Categoria>() {
			@Override
			protected Categoria convert(CategoriaJpa source) {
				if (source == null) {
					return null;
				}

				var modelosLista = source.getModelosExemplo() != null && !source.getModelosExemplo().isBlank()
						? List.of(source.getModelosExemplo().split(","))
						: List.<String>of();

				var codigo = CategoriaCodigo.fromTexto(source.getCodigo());

				return new Categoria(codigo, source.getNome(), source.getDescricao(),
						source.getDiaria(), modelosLista, source.getQuantidadeDisponivel());
			}
		});

		addConverter(new AbstractConverter<Categoria, CategoriaJpa>() {
			@Override
			protected CategoriaJpa convert(Categoria source) {
				if (source == null) {
					return null;
				}
				var jpa = new CategoriaJpa();
				jpa.setCodigo(source.getCodigo().name());
				jpa.setNome(source.getNome());
				jpa.setDescricao(source.getDescricao());
				jpa.setDiaria(source.getDiaria());
				jpa.setModelosExemplo(String.join(",", source.getModelosExemplo()));
				jpa.setQuantidadeDisponivel(source.getQuantidadeDisponivel());
				return jpa;
			}
		});
	}

	private void configurarConversoresVeiculo() {
		addConverter(new AbstractConverter<VeiculoJpa, Veiculo>() {
			@Override
			protected Veiculo convert(VeiculoJpa source) {
				if (source == null) {
					return null;
				}

				var categoria = CategoriaCodigo.fromTexto(source.getCategoria());
				var veiculo = new Veiculo(source.getPlaca(), source.getModelo(), categoria,
						source.getCidade(), source.getDiaria(), source.getStatus());

				if (source.getManutencaoPrevista() != null) {
					veiculo.agendarManutencao(source.getManutencaoPrevista(), source.getManutencaoNota());
				}

				// O pátio é reconstruído automaticamente pelo domínio com base na cidade.
				return veiculo;
			}
		});

		addConverter(new AbstractConverter<Veiculo, VeiculoJpa>() {
			@Override
			protected VeiculoJpa convert(Veiculo source) {
				if (source == null) {
					return null;
				}
				var jpa = new VeiculoJpa();
				jpa.setPlaca(source.getPlaca());
				jpa.setModelo(source.getModelo());
				jpa.setCategoria(source.getCategoria().name());
				jpa.setCidade(source.getCidade());
				jpa.setDiaria(source.getDiaria());
				jpa.setStatus(source.getStatus());
				jpa.setManutencaoPrevista(source.getManutencaoPrevista());
				jpa.setManutencaoNota(source.getManutencaoNota());

				if (source.getPatio() != null) {
					var patioJpa = new PatioJpa();
					patioJpa.setCodigo(source.getPatio().getCodigo());
					patioJpa.setLocalizacao(source.getPatio().getCidade());
					jpa.setPatio(patioJpa);
				}

				return jpa;
			}
		});
	}

	private void configurarConversoresReserva() {
		addConverter(new AbstractConverter<ReservaJpa, Reserva>() {
			@Override
			protected Reserva convert(ReservaJpa source) {
				if (source == null) {
					return null;
				}

				var periodoJpa = source.getPeriodo();
				var periodo = new PeriodoLocacao(periodoJpa.getRetirada(), periodoJpa.getDevolucao());
				var cliente = map(source.getCliente(), Cliente.class);
				var categoria = CategoriaCodigo.fromTexto(source.getCategoria());

				return new Reserva(source.getCodigo(), categoria, source.getCidadeRetirada(),
						periodo, source.getValorEstimado(), source.getStatus(), cliente);
			}
		});

		addConverter(new AbstractConverter<Reserva, ReservaJpa>() {
			@Override
			protected ReservaJpa convert(Reserva source) {
				if (source == null) {
					return null;
				}
				var jpa = new ReservaJpa();
				jpa.setCodigo(source.getCodigo());
				jpa.setCategoria(source.getCategoria().name());
				jpa.setCidadeRetirada(source.getCidadeRetirada());
				jpa.setValorEstimado(source.getValorEstimado());
				jpa.setStatus(source.getStatus());

				var periodoJpa = new PeriodoLocacaoJpa();
				periodoJpa.setRetirada(source.getPeriodo().getRetirada());
				periodoJpa.setDevolucao(source.getPeriodo().getDevolucao());
				jpa.setPeriodo(periodoJpa);

				var clienteJpa = clienteRepositorio.findById(source.getCliente().getCpfOuCnpj())
						.orElseGet(() -> map(source.getCliente(), ClienteJpa.class));
				jpa.setCliente(clienteJpa);

				return jpa;
			}
		});
	}

	private void configurarConversoresLocacao() {
		addConverter(new AbstractConverter<LocacaoJpa, Locacao>() {
			@Override
			protected Locacao convert(LocacaoJpa source) {
				if (source == null) {
					return null;
				}

				var reserva = map(source.getReserva(), Reserva.class);
				var veiculo = map(source.getVeiculo(), Veiculo.class);
				var vistoriaRetirada = map(source.getVistoriaRetirada(), ChecklistVistoria.class);

				var locacao = new Locacao(source.getCodigo(), reserva, veiculo,
						source.getDiasPrevistos(), source.getValorDiaria(), vistoriaRetirada);

				if (source.getVistoriaDevolucao() != null) {
					locacao.registrarDevolucao(map(source.getVistoriaDevolucao(), ChecklistVistoria.class));
				}

				return locacao;
			}
		});

		addConverter(new AbstractConverter<Locacao, LocacaoJpa>() {
			@Override
			protected LocacaoJpa convert(Locacao source) {
				if (source == null) {
					return null;
				}
				var jpa = new LocacaoJpa();
				jpa.setCodigo(source.getCodigo());
				jpa.setDiasPrevistos(source.getDiasPrevistos());
				jpa.setValorDiaria(source.getValorDiaria());
				jpa.setStatus(source.getStatus());

				var reservaJpa = reservaRepositorio.findById(source.getReserva().getCodigo())
						.orElseGet(() -> map(source.getReserva(), ReservaJpa.class));
				jpa.setReserva(reservaJpa);

				var veiculoJpa = veiculoRepositorio.findById(source.getVeiculo().getPlaca())
						.orElseGet(() -> map(source.getVeiculo(), VeiculoJpa.class));
				jpa.setVeiculo(veiculoJpa);

				jpa.setVistoriaRetirada(map(source.getVistoriaRetirada(), ChecklistVistoriaJpa.class));
				if (source.getVistoriaDevolucao() != null) {
					jpa.setVistoriaDevolucao(map(source.getVistoriaDevolucao(), ChecklistVistoriaJpa.class));
				}

				return jpa;
			}
		});
	}

	private void configurarConversoresPatio() {
		addConverter(new AbstractConverter<PatioJpa, Patio>() {
			@Override
			protected Patio convert(PatioJpa source) {
				if (source == null) {
					return null;
				}
				return new Patio(source.getCodigo(), source.getLocalizacao());
			}
		});

		addConverter(new AbstractConverter<Patio, PatioJpa>() {
			@Override
			protected PatioJpa convert(Patio source) {
				if (source == null) {
					return null;
				}
				var jpa = new PatioJpa();
				jpa.setCodigo(source.getCodigo());
				jpa.setLocalizacao(source.getCidade());
				return jpa;
			}
		});
	}

	private void configurarConversoresPeriodo() {
		addConverter(new AbstractConverter<PeriodoLocacaoJpa, PeriodoLocacao>() {
			@Override
			protected PeriodoLocacao convert(PeriodoLocacaoJpa source) {
				if (source == null) {
					return null;
				}
				return new PeriodoLocacao(source.getRetirada(), source.getDevolucao());
			}
		});

		addConverter(new AbstractConverter<PeriodoLocacao, PeriodoLocacaoJpa>() {
			@Override
			protected PeriodoLocacaoJpa convert(PeriodoLocacao source) {
				if (source == null) {
					return null;
				}
				var jpa = new PeriodoLocacaoJpa();
				jpa.setRetirada(source.getRetirada());
				jpa.setDevolucao(source.getDevolucao());
				return jpa;
			}
		});
	}

	private void configurarConversoresChecklist() {
		addConverter(new AbstractConverter<ChecklistVistoriaJpa, ChecklistVistoria>() {
			@Override
			protected ChecklistVistoria convert(ChecklistVistoriaJpa source) {
				if (source == null || source.getQuilometragem() == null) {
					return null;
				}
				return new ChecklistVistoria(
						source.getQuilometragem(),
						source.getCombustivel(),
						Boolean.TRUE.equals(source.getPossuiAvarias()));
			}
		});

		addConverter(new AbstractConverter<ChecklistVistoria, ChecklistVistoriaJpa>() {
			@Override
			protected ChecklistVistoriaJpa convert(ChecklistVistoria source) {
				if (source == null) {
					return null;
				}
				var jpa = new ChecklistVistoriaJpa();
				jpa.setQuilometragem(source.quilometragem());
				jpa.setCombustivel(source.combustivel());
				jpa.setPossuiAvarias(source.possuiAvarias());
				return jpa;
			}
		});
	}

	/**
	 * Mapeia uma lista de objetos usando o ModelMapper.
	 */
	public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream()
				.map(element -> map(element, targetClass))
				.collect(Collectors.toList());
	}

	/**
	 * Mapeia uma lista usando TypeToken.
	 */
	public <S, T> List<T> mapList(List<S> source, TypeToken<List<T>> typeToken) {
		return map(source, typeToken.getType());
	}
}
