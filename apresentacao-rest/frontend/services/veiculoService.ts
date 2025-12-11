export interface VeiculoResumo {
  placa: string;
  modelo: string;
  categoria: string;
  cidade: string;
  diaria: number;
  status: string;
}

export const veiculoService = {
  // Buscar veículo por placa (usa API route do Next.js)
  async buscarPorPlaca(placa: string): Promise<VeiculoResumo> {
    const response = await fetch(`/api/veiculos/${placa}`, {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Erro ao buscar veículo");
    }

    return await response.json();
  },

  // Buscar veículos disponíveis por cidade e categoria (opcional) - usa API route do Next.js
  async buscarDisponiveis(
    cidade: string,
    categoria?: string
  ): Promise<VeiculoResumo[]> {
    const params = new URLSearchParams({ cidade });
    if (categoria) {
      params.set("categoria", categoria);
    }

    const response = await fetch(
      `/api/veiculos/disponiveis?${params.toString()}`,
      {
        method: "GET",
        credentials: "include",
      }
    );

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Erro ao buscar veículos disponíveis");
    }

    return await response.json();
  },
};
