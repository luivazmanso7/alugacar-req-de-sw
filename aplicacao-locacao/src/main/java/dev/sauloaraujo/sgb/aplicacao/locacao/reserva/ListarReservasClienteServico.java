package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;

/**
 * Serviço de aplicação para listar reservas de um cliente.
 */
@Service
public class ListarReservasClienteServico {
    
    private final ReservaRepositorio reservaRepositorio;
    
    public ListarReservasClienteServico(ReservaRepositorio reservaRepositorio) {
        this.reservaRepositorio = notNull(reservaRepositorio, "Repositório de reserva é obrigatório");
    }
    
    /**
     * Lista todas as reservas de um cliente.
     * 
     * @param cpfOuCnpj CPF ou CNPJ do cliente
     * @return lista de resumos das reservas do cliente
     */
    @Transactional(readOnly = true)
    public List<ReservaResumo> listarPorCliente(String cpfOuCnpj) {
        notNull(cpfOuCnpj, "CPF/CNPJ do cliente é obrigatório");
        
        List<Reserva> reservas = reservaRepositorio.listarPorCliente(cpfOuCnpj);
        
        return reservas.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte entidade de domínio para DTO de resumo.
     */
    private ReservaResumo toResumo(Reserva reserva) {
        return new ReservaResumo(
                reserva.getCodigo(),
                reserva.getCategoria().name(),
                reserva.getCidadeRetirada(),
                reserva.getPeriodo().getRetirada(),
                reserva.getPeriodo().getDevolucao(),
                reserva.getValorEstimado(),
                reserva.getStatus().name(),
                reserva.getCliente().getNome(),
                reserva.getCliente().getCpfOuCnpj()
        );
    }
}

