package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;

@Service
public class BuscarReservaServico {

    private final ReservaRepositorio reservaRepositorio;

    public BuscarReservaServico(ReservaRepositorio reservaRepositorio) {
        this.reservaRepositorio = notNull(reservaRepositorio, "Repositório de reserva não pode ser nulo");
    }

    public ReservaResumo buscar(String codigo) {
        var reserva = reservaRepositorio.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));

        return toResumo(reserva);
    }

    /**
     * Lista todas as reservas.
     * Delega para o repositório de domínio e converte para DTOs.
     * 
     * NOTA: O repositório já filtra reservas inválidas durante a conversão,
     * garantindo que apenas entidades de domínio válidas sejam retornadas.
     * Isso respeita DDD: validação técnica fica na infraestrutura.
     * 
     * @return lista de resumos de reservas válidas
     */
    public List<ReservaResumo> listarTodas() {
        var reservas = reservaRepositorio.listar();
        return reservas.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }

    /**
     * Converte entidade de domínio para DTO de resumo.
     * Respeita DDD: conversão acontece na camada de aplicação.
     */
    private ReservaResumo toResumo(Reserva reserva) {
        var periodo = reserva.getPeriodo();
        var cliente = reserva.getCliente();

        return new ReservaResumo(
                reserva.getCodigo(),
                reserva.getCategoria().name(),
                reserva.getCidadeRetirada(),
                periodo.getRetirada(),
                periodo.getDevolucao(),
                reserva.getValorEstimado(),
                reserva.getStatus().name(),
                cliente.getNome(),
                cliente.getCpfOuCnpj(),
                reserva.getPlacaVeiculo());
    }
}

