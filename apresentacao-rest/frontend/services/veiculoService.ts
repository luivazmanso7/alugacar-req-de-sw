import api from "@/lib/api";

export interface VeiculoResumo {
  placa: string;
  modelo: string;
  categoria: string;
  cidade: string;
  diaria: number;
  status: string;
}

export const veiculoService = {
  // Buscar veículo por placa
  async buscarPorPlaca(placa: string): Promise<VeiculoResumo> {
    const response = await api.get(`/veiculos/${placa}`);
    return response.data;
  },

  // Buscar veículos disponíveis por cidade e categoria (opcional)
  async buscarDisponiveis(
    cidade: string,
    categoria?: string
  ): Promise<VeiculoResumo[]> {
    const params: any = { cidade };
    if (categoria) {
      params.categoria = categoria;
    }
    const response = await api.get("/veiculos/disponiveis", { params });
    return response.data;
  },
};
