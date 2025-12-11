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
  cliente: {
    nome: string;
    cpfOuCnpj: string;
    cnh: string;
    email: string;
    login: string;
    senha: string;
  };
}

export const reservaService = {
  // Buscar reserva por código
  async buscarPorCodigo(codigo: string): Promise<ReservaResponse> {
    const response = await api.get(`/reservas/${codigo}`);
    return response.data;
  },

  // Criar nova reserva
  async criar(reserva: CriarReservaRequest): Promise<ReservaResponse> {
    const response = await api.post("/reservas", reserva);
    return response.data;
  },
};
