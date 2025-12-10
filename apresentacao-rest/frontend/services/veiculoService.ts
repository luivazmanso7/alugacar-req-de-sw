import api from '@/lib/api';
import { Veiculo, StatusVeiculo, CategoriaVeiculo } from '@/types';

export const veiculoService = {
  // Listar todos os veículos
  async listar(): Promise<Veiculo[]> {
    const response = await api.get('/veiculos');
    return response.data;
  },

  // Buscar veículo por ID
  async buscarPorId(id: string): Promise<Veiculo> {
    const response = await api.get(`/veiculos/${id}`);
    return response.data;
  },

  // Buscar veículos disponíveis por categoria
  async buscarDisponiveis(categoria?: CategoriaVeiculo): Promise<Veiculo[]> {
    const params = categoria ? { categoria } : {};
    const response = await api.get('/veiculos/disponiveis', { params });
    return response.data;
  },

  // Buscar veículo por placa
  async buscarPorPlaca(placa: string): Promise<Veiculo> {
    const response = await api.get(`/veiculos/placa/${placa}`);
    return response.data;
  },

  // Atualizar status do veículo
  async atualizarStatus(id: string, status: StatusVeiculo): Promise<Veiculo> {
    const response = await api.patch(`/veiculos/${id}/status`, { status });
    return response.data;
  },

  // Registrar quilometragem
  async registrarQuilometragem(id: string, quilometragem: number): Promise<Veiculo> {
    const response = await api.patch(`/veiculos/${id}/quilometragem`, { quilometragem });
    return response.data;
  },
};
