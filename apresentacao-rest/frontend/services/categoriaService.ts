export interface CategoriaResumo {
  codigo: string;
  nome: string;
  descricao: string;
  diaria: number;
  quantidadeDisponivel: number;
}

export const categoriaService = {
  // Listar todas as categorias (usa API route do Next.js)
  async listar(): Promise<CategoriaResumo[]> {
    const response = await fetch("/api/categorias", {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Erro ao listar categorias");
    }

    return await response.json();
  },

  // Buscar categoria por código (usa API route do Next.js)
  async buscarPorCodigo(codigo: string): Promise<CategoriaResumo> {
    const response = await fetch(`/api/categorias/${codigo}`, {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Erro ao buscar categoria");
    }

    return await response.json();
  },
};
