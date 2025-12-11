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
}

export interface CriarReservaRequest {
  categoriaCodigo: string;
  cidadeRetirada: string;
  periodo: {
    dataRetirada: string;
    dataDevolucao: string;
  };
  placaVeiculo: string; // Placa do veículo específico a ser reservado
  // Cliente vem da sessão HTTP (não precisa enviar)
}

export const reservaService = {
  // Buscar reserva por código
  async buscarPorCodigo(codigo: string): Promise<ReservaResponse> {
    const response = await api.get(`/reservas/${codigo}`);
    return response.data;
  },

  // Criar nova reserva (usa API route do Next.js que faz proxy para o backend)
  async criar(reserva: CriarReservaRequest): Promise<ReservaResponse> {
    const response = await fetch("/api/reservas", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include", // Importante para cookies
      body: JSON.stringify(reserva),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Erro ao criar reserva");
    }

    return await response.json();
  },

  // Listar minhas reservas (do cliente logado) - usa API route do Next.js
  async listarMinhas(): Promise<ReservaResponse[]> {
    const response = await fetch("/api/reservas/minhas", {
      method: "GET",
      credentials: "include", // Importante para cookies
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

  // Listar todas as reservas (admin) - usa API route do Next.js
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

  // Cancelar reserva (do cliente logado) - usa API route do Next.js
  async cancelar(codigoReserva: string): Promise<CancelarReservaResponse> {
    const response = await fetch(`/api/reservas/${codigoReserva}`, {
      method: "DELETE",
      credentials: "include", // Importante para cookies
    });

    if (!response.ok) {
      // Verificar se a resposta é JSON antes de tentar parsear
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
        // Se não for JSON, ler como texto para ver o erro
        const errorText = await response.text();
        console.error(
          "Erro não-JSON do servidor:",
          errorText.substring(0, 200)
        );
        throw new Error(
          `Erro ${response.status}: ${
            response.statusText || "Erro ao cancelar reserva"
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
