import api from "@/lib/api";

export interface ReservaResponse {
  codigo: string;
  categoria: string;
  cidadeRetirada: string;
  dataRetirada: string;
  dataDevolucao: string;
  valorEstimado: number;
  status: string;
  clienteNome: string;
  clienteDocumento: string;
  placaVeiculo: string;
}

export interface CriarReservaRequest {
  categoriaCodigo: string;
  cidadeRetirada: string;
  periodo: {
    dataRetirada: string;
    dataDevolucao: string;
  };
  placaVeiculo: string;
}

export const reservaService = {
  async buscarPorCodigo(codigo: string): Promise<ReservaResponse> {
    const response = await fetch(`/api/reservas/${codigo}`, {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({}));
      throw new Error(error.message || error.error || "Erro ao buscar reserva");
    }

    return await response.json();
  },

  async criar(reserva: CriarReservaRequest): Promise<ReservaResponse> {
    const response = await fetch("/api/reservas", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(reserva),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Erro ao criar reserva");
    }

    return await response.json();
  },

  async listarMinhas(): Promise<ReservaResponse[]> {
    const response = await fetch("/api/reservas/minhas", {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      if (response.status === 401) {
        throw new Error("Não autenticado");
      }
      const error = await response.json();
      throw new Error(error.message || "Erro ao listar reservas");
    }

    return await response.json();
  },

  async listarTodas(): Promise<ReservaResponse[]> {
    const response = await fetch("/api/reservas/listar", {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || "Erro ao listar reservas");
    }

    return await response.json();
  },

  async cancelar(codigoReserva: string): Promise<CancelarReservaResponse> {
    const response = await fetch(`/api/reservas/${codigoReserva}`, {
      method: "DELETE",
      credentials: "include",
    });

    if (!response.ok) {
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        const error = await response.json();
        if (response.status === 401) {
          throw new Error("Não autenticado");
        }
        if (response.status === 403) {
          throw new Error("Você não tem permissão para cancelar esta reserva");
        }
        throw new Error(
          error.message || error.error || "Erro ao cancelar reserva"
        );
      } else {
        const errorText = await response.text();
        throw new Error(
          `Erro ${response.status}: ${
            response.statusText || "Erro ao cancelar reserva"
          }`
        );
      }
    }

    return await response.json();
  },

  async alterarPeriodo(
    codigoReserva: string,
    novoPeriodo: { dataRetirada: string; dataDevolucao: string }
  ): Promise<ReservaResponse> {
    const response = await fetch(`/api/reservas/${codigoReserva}/periodo`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        dataRetirada: novoPeriodo.dataRetirada,
        dataDevolucao: novoPeriodo.dataDevolucao,
      }),
    });

    if (!response.ok) {
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        const error = await response.json();
        if (response.status === 401) {
          throw new Error("Não autenticado");
        }
        if (response.status === 403) {
          throw new Error("Você não tem permissão para alterar esta reserva");
        }
        throw new Error(
          error.message || error.error || "Erro ao alterar reserva"
        );
      } else {
        const errorText = await response.text();
        throw new Error(
          `Erro ${response.status}: ${
            response.statusText || "Erro ao alterar reserva"
          }`
        );
      }
    }

    return await response.json();
  },
};

export interface CancelarReservaResponse {
  codigoReserva: string;
  status: string;
  tarifaCancelamento: number;
}
