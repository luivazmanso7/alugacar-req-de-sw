import api from "@/lib/api";

export interface CategoriaResumo {
  codigo: string;
  nome: string;
  descricao: string;
  diaria: number;
  quantidadeDisponivel: number;
}

export const categoriaService = {
  // Listar todas as categorias
  async listar(): Promise<CategoriaResumo[]> {
    const response = await api.get("/categorias");
    return response.data;
  },

  // Buscar categoria por código
  async buscarPorCodigo(codigo: string): Promise<CategoriaResumo> {
    const response = await api.get(`/categorias/${codigo}`);
    return response.data;
  },
};
