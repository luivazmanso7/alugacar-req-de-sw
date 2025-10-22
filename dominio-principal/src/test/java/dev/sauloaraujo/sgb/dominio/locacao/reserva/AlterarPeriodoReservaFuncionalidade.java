package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ChecklistVistoria;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ContratoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ProcessarRetiradaCommand;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class AlterarPeriodoReservaFuncionalidade extends AlugacarFuncionalidade {
    private final Map<CategoriaCodigo, BigDecimal> valoresDiaria = new HashMap<>();
    private final Map<CategoriaCodigo, Integer> quantidadesCategoria = new HashMap<>();
    private Reserva reservaReplanejada;
    private Reserva reservaCriada;
    private Veiculo veiculoAgendado;
    private RuntimeException erro;
    private ReservaCancelamentoServico.ResultadoCancelamento cancelamentoResultado;
    private ContratoLocacao contratoGerado;

    @Dado("que o catalogo de categorias esta limpo")
    public void que_o_catalogo_de_categorias_esta_limpo() {
        limparContexto();
        valoresDiaria.clear();
        quantidadesCategoria.clear();
        reservaReplanejada = null;
        reservaCriada = null;
        veiculoAgendado = null;
        erro = null;
        cancelamentoResultado = null;
        contratoGerado = null;
    }

    @E("que nao existem reservas cadastradas")
    public void que_nao_existem_reservas_cadastradas() {
        // contexto já limpo
    }

    @E("que nao existem contratos de locacao ativos")
    public void que_nao_existem_contratos_de_locacao_ativos() {
        // contexto já limpo
    }

    @Dado("a categoria {string} com diaria base {int}")
    public void a_categoria_com_diaria_base(String codigo, Integer diaria) {
        var categoriaCodigo = CategoriaCodigo.fromTexto(codigo);
        valoresDiaria.put(categoriaCodigo, BigDecimal.valueOf(diaria));
        quantidadesCategoria.putIfAbsent(categoriaCodigo, 10);
        registrarCategoria(categoriaCodigo);
    }

    @E("existem {int} veiculos disponiveis da categoria {string}")
    public void existem_veiculos_disponiveis_da_categoria(Integer quantidade, String codigoCategoria) {
        var categoriaCodigo = CategoriaCodigo.fromTexto(codigoCategoria);
        quantidadesCategoria.put(categoriaCodigo, quantidade);
        registrarCategoria(categoriaCodigo);
        var diaria = valoresDiaria.getOrDefault(categoriaCodigo, BigDecimal.valueOf(100));
        criarVeiculosDisponiveis(categoriaCodigo, quantidade, diaria);
    }

    @E("existem {int} reservas ativas da categoria {string} de {string} ate {string}")
    public void existem_reservas_ativas_da_categoria(Integer quantidade, String codigoCategoria, String inicio,
            String fim) {
        var categoriaCodigo = CategoriaCodigo.fromTexto(codigoCategoria);
        for (int indice = 0; indice < quantidade; indice++) {
            var periodo = new PeriodoLocacao(LocalDateTime.parse(inicio), LocalDateTime.parse(fim));
            var codigo = "ATV-" + codigoCategoria + "-" + indice;
            var valorDiaria = valoresDiaria.getOrDefault(categoriaCodigo, BigDecimal.valueOf(100));
            var reserva = new Reserva(codigo, categoriaCodigo, "São Paulo", periodo,
                    valorDiaria.multiply(BigDecimal.valueOf(periodo.dias())), StatusReserva.ATIVA);
            repositorio.salvar(reserva);
        }
    }

    @E("existe uma reserva confirmada {string} da categoria {string} de {string} ate {string} para o cliente {string}")
    public void existe_uma_reserva_confirmada(String codigoReserva, String codigoCategoria, String inicio, String fim,
            String cliente) {
        var categoriaCodigo = CategoriaCodigo.fromTexto(codigoCategoria);
        registrarCategoriaSeNecessario(categoriaCodigo);
        var periodo = new PeriodoLocacao(LocalDateTime.parse(inicio), LocalDateTime.parse(fim));
        var diaria = valoresDiaria.getOrDefault(categoriaCodigo, BigDecimal.valueOf(100));
        var reserva = new Reserva(codigoReserva, categoriaCodigo, "São Paulo", periodo,
                diaria.multiply(BigDecimal.valueOf(periodo.dias())), StatusReserva.ATIVA);
        repositorio.salvar(reserva);
    }

    @E("existe um veiculo elegivel para manutencao da categoria {string} com placa {string}")
    public void existe_um_veiculo_elegivel_para_manutencao(String codigoCategoria, String placa) {
        criarVeiculoIndividual(codigoCategoria, placa, StatusVeiculo.DISPONIVEL);
    }

    @E("existe um veiculo com manutencao pendente da categoria {string} com placa {string}")
    public void existe_um_veiculo_com_manutencao_pendente_da_categoria(String codigoCategoria, String placa) {
        criarVeiculoIndividual(codigoCategoria, placa, StatusVeiculo.EM_MANUTENCAO);
    }

    @E("existe um veiculo disponivel da categoria {string} com placa {string}")
    public void existe_um_veiculo_disponivel_da_categoria(String codigoCategoria, String placa) {
        criarVeiculoIndividual(codigoCategoria, placa, StatusVeiculo.DISPONIVEL);
    }

    @E("a reserva {string} foi convertida em locacao com o veiculo {string} na data {string} com odometro {int} e combustivel {int}")
    public void reserva_convertida_em_locacao(String codigoReserva, String placa, String dataRetirada, Integer odometro,
            Integer combustivel) {
        var reserva = repositorio.buscarPorCodigo(codigoReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
        var veiculo = repositorio.buscarPorPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        veiculo.locar();
        repositorio.salvar(veiculo);

        reserva.concluir();
        repositorio.salvar(reserva);

        var locacao = new Locacao("LOCACAO-" + codigoReserva, reserva, veiculo,
                Math.toIntExact(reserva.diasReservados()), veiculo.getDiaria(),
                new ChecklistVistoria(odometro, combustivel.toString(), false));
        repositorio.salvar(locacao);
    }

    @Quando("eu altero a reserva {string} para o periodo de {string} ate {string}")
    public void eu_altero_a_reserva_para_o_periodo(String codigoReserva, String inicio, String fim) {
        var novoPeriodo = new PeriodoLocacao(LocalDateTime.parse(inicio), LocalDateTime.parse(fim));
        try {
            reservaReplanejada = reservaReplanejamentoServico.replanejar(codigoReserva, novoPeriodo);
        } catch (RuntimeException ex) {
            erro = ex;
        }
    }

    @Quando("eu solicito o cancelamento da reserva {string} em {string}")
    public void eu_solicito_o_cancelamento_da_reserva_em(String codigoReserva, String data) {
        try {
            cancelamentoResultado = reservaCancelamentoServico.cancelar(codigoReserva, LocalDateTime.parse(data));
        } catch (RuntimeException ex) {
            erro = ex;
        }
    }

    @Quando("eu agendo manutencao para o veiculo {string} com previsao {string} e motivo {string}")
    public void eu_agendo_manutencao_para_o_veiculo(String placa, String previsao, String motivo) {
        try {
            veiculoAgendado = manutencaoServico.agendar(placa, LocalDateTime.parse(previsao), motivo);
        } catch (RuntimeException ex) {
            erro = ex;
        }
    }

    @Quando("eu crio uma reserva da categoria {string} de {string} ate {string}")
    public void eu_crio_uma_reserva_da_categoria(String categoria, String inicio, String fim,
            io.cucumber.datatable.DataTable dados) {
        var categoriaCodigo = CategoriaCodigo.fromTexto(categoria);
        var periodo = new PeriodoLocacao(LocalDateTime.parse(inicio), LocalDateTime.parse(fim));
        erro = null;
        reservaCriada = null;
        try {
            reservaCriada = reservaServico.criarReserva("RES-DIN-" + System.nanoTime(), categoriaCodigo,
                    extrairCidade(dados), periodo);
        } catch (RuntimeException ex) {
            erro = ex;
        }
    }

    @Quando("eu tento criar uma reserva da categoria {string} de {string} ate {string}")
    public void eu_tento_criar_uma_reserva_da_categoria(String categoria, String inicio, String fim,
            io.cucumber.datatable.DataTable dados) {
        var categoriaCodigo = CategoriaCodigo.fromTexto(categoria);
        var periodo = new PeriodoLocacao(LocalDateTime.parse(inicio), LocalDateTime.parse(fim));
        erro = null;
        reservaCriada = null;
        try {
            reservaCriada = reservaServico.criarReserva("RES-DIN-" + System.nanoTime(), categoriaCodigo,
                    extrairCidade(dados), periodo);
        } catch (RuntimeException ex) {
            erro = ex;
        }
    }

    @Quando("eu confirmo a retirada da reserva {string} com o veiculo {string}")
    public void eu_confirmo_a_retirada_da_reserva_com_o_veiculo(String codigoReserva, String placa,
            io.cucumber.datatable.DataTable dados) {
        var registro = dados.asMaps().get(0);
        var combustivel = registro.get("combustivel");
        var odometro = Integer.parseInt(registro.get("odometro"));
        try {
            var command = ProcessarRetiradaCommand.builder().codigoReserva(codigoReserva)
                    .codigoLocacao("LOC-" + codigoReserva).placaVeiculo(placa).documentosValidos(true)
                    .combustivel(combustivel).quilometragem(odometro).build();
            contratoGerado = retiradaServico.processar(command);
        } catch (RuntimeException ex) {
            erro = ex;
        }
    }

    @Entao("o periodo da reserva deve ser atualizado para {string} ate {string}")
    public void o_periodo_da_reserva_deve_ser_atualizado(String inicioEsperado, String fimEsperado) {
        assertNotNull(reservaReplanejada);
        assertEquals(LocalDateTime.parse(inicioEsperado), reservaReplanejada.getPeriodo().getRetirada());
        assertEquals(LocalDateTime.parse(fimEsperado), reservaReplanejada.getPeriodo().getDevolucao());
    }

    @E("o valor total da reserva replanejada deve ser maior que {int}")
    public void o_valor_total_da_reserva_replanejada_deve_ser_maior_que(Integer valorMinimo) {
        assertNotNull(reservaReplanejada);
        assertTrue(reservaReplanejada.getValorEstimado().compareTo(BigDecimal.valueOf(valorMinimo)) > 0);
    }

    @Entao("o cancelamento e realizado com tarifa {double}")
    public void o_cancelamento_e_realizado_com_tarifa(Double tarifaEsperada) {
        assertNotNull(cancelamentoResultado);
        assertEquals(0, cancelamentoResultado.tarifa().compareTo(BigDecimal.valueOf(tarifaEsperada)));
    }

    @Entao("o veiculo {string} deve ficar em manutencao com nota {string}")
    public void o_veiculo_deve_ficar_em_manutencao(String placa, String nota) {
        var veiculo = repositorio.buscarPorPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        assertEquals(StatusVeiculo.EM_MANUTENCAO, veiculo.getStatus());
        assertEquals(nota, veiculo.getManutencaoNota());
    }

    @Entao("o contrato de locacao e criado para o veiculo {string}")
    public void o_contrato_de_locacao_e_criado_para_o_veiculo(String placa) {
        assertNotNull(contratoGerado);
        assertEquals(placa, contratoGerado.placaVeiculo());
    }

    @Entao("a reserva {string} passa a ter o status {string}")
    public void a_reserva_passa_a_ter_o_status(String codigoReserva, String statusEsperado) {
        var reserva = repositorio.buscarPorCodigo(codigoReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
        assertEquals(statusEsperado, reserva.getStatus().name());
    }

    @Entao("a reserva e criada com sucesso")
    public void a_reserva_e_criada_com_sucesso() {
        assertNotNull(reservaCriada);
    }

    @Entao("o valor final deve ser maior que {int}")
    public void o_valor_final_deve_ser_maior_que(Integer valorMinimo) {
        assertNotNull(reservaCriada);
        assertTrue(reservaCriada.getValorEstimado().compareTo(BigDecimal.valueOf(valorMinimo)) > 0);
    }

    @Entao("ocorre um erro de negocio com a mensagem {string}")
    public void ocorre_um_erro_de_negocio_com_a_mensagem(String mensagem) {
        assertNotNull(erro);
        assertEquals(mensagem, erro.getMessage());
    }

    private void registrarCategoria(CategoriaCodigo categoriaCodigo) {
        var diaria = valoresDiaria.getOrDefault(categoriaCodigo, BigDecimal.valueOf(100));
        var quantidade = quantidadesCategoria.getOrDefault(categoriaCodigo, 0);
        var categoria = new Categoria(categoriaCodigo, categoriaCodigo.name(),
                "Categoria " + categoriaCodigo.name(), diaria, List.of("Modelo"), Math.max(quantidade, 1));
        catalogoServico.registrarCategoria(categoria);
    }

    private void registrarCategoriaSeNecessario(CategoriaCodigo categoriaCodigo) {
        if (!quantidadesCategoria.containsKey(categoriaCodigo)) {
            quantidadesCategoria.put(categoriaCodigo, 1);
        }
        registrarCategoria(categoriaCodigo);
    }

    private void criarVeiculosDisponiveis(CategoriaCodigo categoriaCodigo, int quantidade, BigDecimal diaria) {
        for (int indice = 0; indice < quantidade; indice++) {
            var placa = categoriaCodigo.name() + "-" + (indice + 1);
            var veiculo = new Veiculo(placa, "Modelo " + categoriaCodigo.name(), categoriaCodigo, "São Paulo", diaria,
                    StatusVeiculo.DISPONIVEL);
            repositorio.salvar(veiculo);
        }
    }

    private void criarVeiculoIndividual(String codigoCategoria, String placa, StatusVeiculo status) {
        var categoriaCodigo = CategoriaCodigo.fromTexto(codigoCategoria);
        quantidadesCategoria.putIfAbsent(categoriaCodigo, 1);
        registrarCategoriaSeNecessario(categoriaCodigo);
        var diaria = valoresDiaria.getOrDefault(categoriaCodigo, BigDecimal.valueOf(100));
        var veiculo = new Veiculo(placa, "Modelo " + categoriaCodigo, categoriaCodigo, "São Paulo", diaria, status);
        repositorio.salvar(veiculo);
    }

    private String extrairCidade(io.cucumber.datatable.DataTable dados) {
        return dados.asMaps().get(0).getOrDefault("cidade", "São Paulo");
    }
}
