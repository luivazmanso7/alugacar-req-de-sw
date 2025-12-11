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
};
