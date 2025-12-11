package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ChecklistVistoria;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.patio.Patio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.CategoriaJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.ChecklistVistoriaJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.ClienteJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.LocacaoJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.PatioJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.PeriodoLocacaoJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.ReservaJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.VeiculoJpa;

/**
 * Componente responsável pelo mapeamento entre objetos de domínio e entidades JPA.
 * Utiliza ModelMapper com conversores customizados para garantir compatibilidade total.
 */
@Component
public class JpaMapeador extends ModelMapper {

	@PersistenceContext
	private EntityManager entityManager;

	public JpaMapeador() {
		var configuracao = getConfiguration();
		configuracao.setFieldMatchingEnabled(true);
		configuracao.setFieldAccessLevel(AccessLevel.PRIVATE);
		// Desabilitar mapeamento automático de propriedades aninhadas
		// Isso força o uso de conversores customizados quando disponíveis
		configuracao.setSkipNullEnabled(true);

		configurarConversoresAuditoria();
		configurarConversoresCliente();
		configurarConversoresCategoria();
		configurarConversoresVeiculo();
		configurarConversoresReserva();
		// IMPORTANTE: Registrar conversor de Locacao ANTES de Reserva para garantir prioridade
		// Mas na verdade, Reserva já está registrado antes, então Locacao vai sobrescrever
		configurarConversoresLocacao();
		configurarConversoresPatio();
		configurarConversoresPeriodo();
		configurarConversoresChecklist();
	}

	private void configurarConversoresAuditoria() {
		// TODO: Auditoria temporariamente desabilitada
		/*
		addConverter(new AbstractConverter<AuditoriaJpa, Auditoria>() {
			@Override
			protected Auditoria convert(AuditoriaJpa source) {
				if (source == null) {
					return null;
				}
				// Usa construtor de reconstrução (do banco)
				return new Auditoria(
					source.getId(),
					source.getDataHora(),
					source.getOperacao(),
					source.getDetalhes(),
					source.getUsuario()
				);
			}
		});

		addConverter(new AbstractConverter<Auditoria, AuditoriaJpa>() {
			@Override
			protected AuditoriaJpa convert(Auditoria source) {
				if (source == null) {
					return null;
				}
				var jpa = new AuditoriaJpa();
				jpa.setId(source.getId());
				jpa.setDataHora(source.getDataHora());
				jpa.setOperacao(source.getOperacao());
				jpa.setDetalhes(source.getDetalhes());
				jpa.setUsuario(source.getUsuario());
				return jpa;
			}
		});
		*/
	}

	private void configurarConversoresCliente() {
		addConverter(new AbstractConverter<ClienteJpa, Cliente>() {
			@Override
			protected Cliente convert(ClienteJpa source) {
				if (source == null) {
					return null;
				}
				// Usar construtor de reconstrução com Credenciais e StatusCliente
				var credenciais = new dev.sauloaraujo.sgb.dominio.locacao.cliente.Credenciais(
					source.getLogin(), 
					source.getSenhaHash()
				);
				var status = dev.sauloaraujo.sgb.dominio.locacao.cliente.StatusCliente.valueOf(source.getStatus());
				
				return new Cliente(
					source.getNome(), 
					source.getCpfOuCnpj(), 
					source.getCnh(), 
					source.getEmail(),
					credenciais,
					status
				);
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
				jpa.setLogin(source.getCredenciais().getLogin());
				jpa.setSenhaHash(source.getCredenciais().getSenhaCriptografada());
				jpa.setStatus(source.getStatus().name());
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

				// Validar período
				var periodoJpa = source.getPeriodo();
				if (periodoJpa == null) {
					throw new IllegalStateException("ReservaJpa sem período: " + source.getCodigo());
				}
				
				var periodo = new PeriodoLocacao(periodoJpa.getRetirada(), periodoJpa.getDevolucao());
				var cliente = map(source.getCliente(), Cliente.class);
				var categoria = CategoriaCodigo.fromTexto(source.getCategoria());

				// Validar placaVeiculo
				// NOTA: Após migração completa, todas as reservas devem ter placa_veiculo preenchido.
				// O conversor não faz consultas ao banco (respeita DDD 100%).
				// Se a reserva não tiver placa válida, lança erro imediatamente.
				String placaVeiculo = source.getPlacaVeiculo();
				
				// Validar que a placa está presente e válida
				if (placaVeiculo == null || placaVeiculo.isBlank() || 
				    placaVeiculo.equals("MIGRAR") || placaVeiculo.equals("TEMP")) {
					throw new IllegalStateException(
							"ReservaJpa sem placaVeiculo válida (codigo: " + source.getCodigo() + 
							", status: " + source.getStatus() + 
							", placaVeiculo: " + source.getPlacaVeiculo() + 
							"). Reservas devem ter placa do veículo. Os dados devem estar migrados corretamente no banco de dados."
					);
				}

				return new Reserva(source.getCodigo(), categoria, source.getCidadeRetirada(),
						periodo, source.getValorEstimado(), source.getStatus(), cliente, placaVeiculo);
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
				jpa.setPlacaVeiculo(source.getPlacaVeiculo());

				var periodoJpa = new PeriodoLocacaoJpa();
				periodoJpa.setRetirada(source.getPeriodo().getRetirada());
				periodoJpa.setDevolucao(source.getPeriodo().getDevolucao());
				jpa.setPeriodo(periodoJpa);

				// Respeitando DDD: Não fazer consultas ao banco no conversor
				// Usar getReference() para criar referência lazy (não consulta o banco)
				// Se a entidade não existir, o Hibernate lançará erro ao salvar
				var clienteJpa = entityManager.getReference(ClienteJpa.class, source.getCliente().getCpfOuCnpj());
				jpa.setCliente(clienteJpa);

				return jpa;
			}
		});
	}

	private void configurarConversoresLocacao() {
		// IMPORTANTE: Registrar o conversor ANTES de qualquer outro para garantir prioridade
		addConverter(new AbstractConverter<LocacaoJpa, Locacao>() {
			@Override
			protected Locacao convert(LocacaoJpa source) {
				if (source == null) {
					return null;
				}

				try {
					// Validações básicas
					if (source.getCodigo() == null || source.getCodigo().isBlank()) {
						throw new IllegalStateException("LocacaoJpa sem código");
					}
					
					if (source.getDiasPrevistos() <= 0) {
						throw new IllegalStateException("LocacaoJpa com diasPrevistos inválido: " + source.getDiasPrevistos());
					}
					
					if (source.getValorDiaria() == null) {
						throw new IllegalStateException("LocacaoJpa sem valorDiaria");
					}
					
					if (source.getVeiculo() == null) {
						throw new IllegalStateException("LocacaoJpa sem veículo: " + source.getCodigo());
					}

					// Converter reserva, mas se ela não tiver placaVeiculo (reserva antiga),
					// usar a placa do veículo da locação
					var reservaJpa = source.getReserva();
					if (reservaJpa == null) {
						throw new IllegalStateException("LocacaoJpa sem reserva: " + source.getCodigo());
					}
					
					// Validar campos críticos da reserva antes de converter
					if (reservaJpa.getPeriodo() == null) {
						throw new IllegalStateException("ReservaJpa sem período: " + reservaJpa.getCodigo());
					}
					
					if (reservaJpa.getCliente() == null) {
						throw new IllegalStateException("ReservaJpa sem cliente: " + reservaJpa.getCodigo());
					}
					
					// IMPORTANTE: Se a reserva não tem placaVeiculo (reserva antiga), 
					// usar a placa do veículo da locação ANTES de converter
					String placaVeiculoReserva = reservaJpa.getPlacaVeiculo();
					if (placaVeiculoReserva == null || placaVeiculoReserva.isBlank() || placaVeiculoReserva.equals("MIGRAR")) {
						// Criar uma cópia da reserva JPA com a placa do veículo da locação
						// Isso garante que o conversor de ReservaJpa -> Reserva receba uma placa válida
						var reservaJpaComPlaca = new ReservaJpa();
						reservaJpaComPlaca.setCodigo(reservaJpa.getCodigo());
						reservaJpaComPlaca.setCategoria(reservaJpa.getCategoria());
						reservaJpaComPlaca.setCidadeRetirada(reservaJpa.getCidadeRetirada());
						reservaJpaComPlaca.setPeriodo(reservaJpa.getPeriodo());
						reservaJpaComPlaca.setValorEstimado(reservaJpa.getValorEstimado());
						reservaJpaComPlaca.setStatus(reservaJpa.getStatus());
						reservaJpaComPlaca.setCliente(reservaJpa.getCliente());
						
						// Usar placa do veículo da locação (garantir que não é null)
						String placaVeiculoLocacao = source.getVeiculo().getPlaca();
						if (placaVeiculoLocacao == null || placaVeiculoLocacao.isBlank()) {
							throw new IllegalStateException("Veículo da locação sem placa: " + source.getCodigo());
						}
						reservaJpaComPlaca.setPlacaVeiculo(placaVeiculoLocacao);
						
						// Substituir a referência para usar a cópia com placa válida
						reservaJpa = reservaJpaComPlaca;
					}

					// Converter reserva MANUALMENTE (sem usar ModelMapper recursivo)
					// Isso evita que o ModelMapper chame o conversor de Reserva diretamente,
					// que não tem acesso ao contexto da locação para obter a placa do veículo
					Reserva reserva;
					try {
						// Garantir que a placa está definida antes de converter
						String placaFinal = reservaJpa.getPlacaVeiculo();
						if (placaFinal == null || placaFinal.isBlank() || placaFinal.equals("MIGRAR")) {
							placaFinal = source.getVeiculo().getPlaca();
							if (placaFinal == null || placaFinal.isBlank()) {
								throw new IllegalStateException("Não foi possível determinar placa do veículo para reserva: " + reservaJpa.getCodigo());
							}
							reservaJpa.setPlacaVeiculo(placaFinal);
						}
						
						// Converter manualmente os componentes da reserva
						var periodoJpa = reservaJpa.getPeriodo();
						if (periodoJpa == null) {
							throw new IllegalStateException("ReservaJpa sem período: " + reservaJpa.getCodigo());
						}
						var periodo = new PeriodoLocacao(periodoJpa.getRetirada(), periodoJpa.getDevolucao());
						
						var cliente = map(reservaJpa.getCliente(), Cliente.class);
						if (cliente == null) {
							throw new IllegalStateException("Falha ao converter ClienteJpa para Cliente na reserva: " + reservaJpa.getCodigo());
						}
						
						var categoria = CategoriaCodigo.fromTexto(reservaJpa.getCategoria());
						
						// Criar Reserva diretamente (sem usar ModelMapper para evitar recursão)
						reserva = new Reserva(
								reservaJpa.getCodigo(),
								categoria,
								reservaJpa.getCidadeRetirada(),
								periodo,
								reservaJpa.getValorEstimado(),
								reservaJpa.getStatus(),
								cliente,
								placaFinal
						);
					} catch (Exception e) {
						throw new IllegalStateException("Erro ao converter ReservaJpa para Reserva (codigo: " + reservaJpa.getCodigo() + "): " + e.getMessage(), e);
					}

					// Converter veículo
					Veiculo veiculo;
					try {
						veiculo = map(source.getVeiculo(), Veiculo.class);
						if (veiculo == null) {
							throw new IllegalStateException("Falha ao converter VeiculoJpa para Veiculo (retornou null)");
						}
					} catch (Exception e) {
						throw new IllegalStateException("Erro ao converter VeiculoJpa para Veiculo (placa: " + source.getVeiculo().getPlaca() + "): " + e.getMessage(), e);
					}
					
					// Tratar vistoriaRetirada null ou com campos null
					ChecklistVistoria vistoriaRetirada;
					var vistoriaRetiradaJpa = source.getVistoriaRetirada();
					if (vistoriaRetiradaJpa == null) {
						// Criar vistoria padrão se não existir (para locações antigas)
						vistoriaRetirada = new ChecklistVistoria(0, "CHEIO", false);
					} else {
						// Converter vistoria, tratando campos null
						int quilometragem = vistoriaRetiradaJpa.getQuilometragem() != null 
								? vistoriaRetiradaJpa.getQuilometragem() 
								: 0;
						String combustivel = vistoriaRetiradaJpa.getCombustivel() != null 
								? vistoriaRetiradaJpa.getCombustivel() 
								: "CHEIO";
						boolean possuiAvarias = vistoriaRetiradaJpa.getPossuiAvarias() != null 
								? vistoriaRetiradaJpa.getPossuiAvarias() 
								: false;
						vistoriaRetirada = new ChecklistVistoria(quilometragem, combustivel, possuiAvarias);
					}

					// Criar Locacao
					Locacao locacao;
					try {
						locacao = new Locacao(source.getCodigo(), reserva, veiculo,
								source.getDiasPrevistos(), source.getValorDiaria(), vistoriaRetirada);
					} catch (Exception e) {
						throw new IllegalStateException(
								"Erro ao criar Locacao (codigo: " + source.getCodigo() + 
								", diasPrevistos: " + source.getDiasPrevistos() + 
								", valorDiaria: " + source.getValorDiaria() + "): " + e.getMessage(), 
								e
						);
					}

					// Registrar vistoria de devolução se existir
					if (source.getVistoriaDevolucao() != null) {
						var vistoriaDevolucaoJpa = source.getVistoriaDevolucao();
						int kmDevolucao = vistoriaDevolucaoJpa.getQuilometragem() != null 
								? vistoriaDevolucaoJpa.getQuilometragem() 
								: 0;
						String combustivelDevolucao = vistoriaDevolucaoJpa.getCombustivel() != null 
								? vistoriaDevolucaoJpa.getCombustivel() 
								: "CHEIO";
						boolean avariasDevolucao = vistoriaDevolucaoJpa.getPossuiAvarias() != null 
								? vistoriaDevolucaoJpa.getPossuiAvarias() 
								: false;
						var vistoriaDevolucao = new ChecklistVistoria(kmDevolucao, combustivelDevolucao, avariasDevolucao);
						locacao.registrarDevolucao(vistoriaDevolucao);
					}

					return locacao;
				} catch (IllegalStateException e) {
					// Re-lançar IllegalStateException sem wrappear
					throw e;
				} catch (Exception e) {
					throw new IllegalStateException(
							"Erro ao converter LocacaoJpa para Locacao (codigo: " + source.getCodigo() + "): " + e.getMessage(), 
							e
					);
				}
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

				// Respeitando DDD: Não fazer consultas ao banco no conversor
				// Usar getReference() para criar referências lazy (não consulta o banco)
				// Se as entidades não existirem, o Hibernate lançará erro ao salvar
				var reservaJpa = entityManager.getReference(ReservaJpa.class, source.getReserva().getCodigo());
				jpa.setReserva(reservaJpa);

				var veiculoJpa = entityManager.getReference(VeiculoJpa.class, source.getVeiculo().getPlaca());
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
