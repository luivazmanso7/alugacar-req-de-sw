package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import static org.apache.commons.lang3.Validate.notNull;

import org.springframework.stereotype.Service;

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
                cliente.getCpfOuCnpj());
    }
}

