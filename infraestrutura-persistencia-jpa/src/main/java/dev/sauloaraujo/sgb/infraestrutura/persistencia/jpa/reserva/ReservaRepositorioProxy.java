package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.reserva;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;

@Repository
@Primary
public class ReservaRepositorioProxy implements ReservaRepositorio {

    private final ReservaRepositorio reservaRepositorioReal;
    private final Map<String, Reserva> cache = new ConcurrentHashMap<>();

    public ReservaRepositorioProxy(@Qualifier("reservaRepositorioReal") ReservaRepositorio reservaRepositorioReal) {
        this.reservaRepositorioReal = reservaRepositorioReal;
    }

    @Override
    public void salvar(Reserva reserva) {
        reservaRepositorioReal.salvar(reserva);
        if (reserva != null && reserva.getCodigo() != null) {
            cache.put(reserva.getCodigo(), reserva);
            System.out.println("[PROXY] Atualizando cache após salvar reserva " + reserva.getCodigo());
        }
    }

    @Override
    public Optional<Reserva> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }

        var emCache = cache.get(codigo);
        if (emCache != null) {
            System.out.println("[PROXY] Retornando do Cache para código " + codigo);
            return Optional.of(emCache);
        }

        var resultado = reservaRepositorioReal.buscarPorCodigo(codigo);
        resultado.ifPresent(reserva -> {
            cache.put(codigo, reserva);
            System.out.println("[PROXY] Salvando no Cache reserva " + codigo);
        });

        return resultado;
    }

    @Override
    public java.util.List<Reserva> listar() {
        // Para listagem, delegamos diretamente ao repositório real.
        return reservaRepositorioReal.listar();
    }
}

