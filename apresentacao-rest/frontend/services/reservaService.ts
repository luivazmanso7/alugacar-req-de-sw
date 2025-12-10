import api from "@/lib/api";
import { Reserva, StatusReserva } from "@/types";

export const reservaService = {
  // Buscar todas as reservas
  async listar(): Promise<Reserva[]> {
    const response = await api.get("/reservas");
    return response.data;
  },

  // Buscar reserva por código
  async buscarPorCodigo(codigo: string): Promise<Reserva> {
    const response = await api.get(`/reservas/codigo/${codigo}`);
    return response.data;
  },

  // Buscar reserva por CPF do cliente
  async buscarPorCpf(cpf: string): Promise<Reserva[]> {
    const response = await api.get(`/reservas/cliente/${cpf}`);
    return response.data;
  },

  // Criar nova reserva
  async criar(reserva: Partial<Reserva>): Promise<Reserva> {
    const response = await api.post("/reservas", reserva);
    return response.data;
  },

  // Alterar reserva
  async alterar(id: string, reserva: Partial<Reserva>): Promise<Reserva> {
    const response = await api.put(`/reservas/${id}`, reserva);
    return response.data;
  },

  // Cancelar reserva
  async cancelar(id: string): Promise<void> {
    await api.delete(`/reservas/${id}`);
  },

  // Confirmar retirada
  async confirmarRetirada(reservaId: string, veiculoId: string): Promise<any> {
    const response = await api.post(
      `/reservas/${reservaId}/confirmar-retirada`,
      {
        veiculoId,
      }
    );
    return response.data;
  },

  // Buscar próximas retiradas
  async proximasRetiradas(): Promise<Reserva[]> {
    const response = await api.get("/reservas/proximas-retiradas");
    return response.data;
  },

  // Buscar devoluções previstas
  async devolucoesPrevistas(): Promise<Reserva[]> {
    const response = await api.get("/reservas/devolucoes-previstas");
    return response.data;
  },
};
