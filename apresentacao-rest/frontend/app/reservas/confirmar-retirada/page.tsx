'use client';

import { useState } from 'react';
import Link from 'next/link';
import axios from 'axios';

interface Reserva {
  id: number;
  clienteNome: string;
  veiculoModelo: string;
  dataInicio: string;
  dataFim: string;
  status: string;
}

export default function ConfirmarRetiradaPage() {
  const [reservaId, setReservaId] = useState('');
  const [reserva, setReserva] = useState<Reserva | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [observacoes, setObservacoes] = useState('');

  const buscarReserva = async () => {
    if (!reservaId) {
      setError('Por favor, informe o ID da reserva');
      return;
    }

    setLoading(true);
    setError('');
    setReserva(null);

    try {
      const response = await axios.get(`/api/reservas/${reservaId}`);
      setReserva(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar reserva');
    } finally {
      setLoading(false);
    }
  };

  const confirmarRetirada = async () => {
    if (!reserva) return;

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await axios.post(`/api/reservas/${reserva.id}/confirmar-retirada`, {
        observacoes
      });
      setSuccess('Retirada confirmada com sucesso! Contrato gerado.');
      // Limpar formulário após 2 segundos
      setTimeout(() => {
        setReservaId('');
        setReserva(null);
        setObservacoes('');
        setSuccess('');
      }, 2000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao confirmar retirada');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-green-50 to-white">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <Link href="/" className="text-2xl font-bold text-blue-600">
              AlugaCar
            </Link>
            <h2 className="text-lg font-semibold text-gray-700">
              Confirmar Retirada
            </h2>
          </div>
        </div>
      </nav>

      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-white rounded-lg shadow-md p-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-8">
            Confirmar Retirada e Gerar Contrato
          </h1>

          {/* Buscar Reserva */}
          <div className="mb-8">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              ID da Reserva
            </label>
            <div className="flex gap-4">
              <input
                type="text"
                value={reservaId}
                onChange={(e) => setReservaId(e.target.value)}
                placeholder="Digite o ID da reserva"
                className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent"
              />
              <button
                onClick={buscarReserva}
                disabled={loading}
                className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
              >
                {loading ? 'Buscando...' : 'Buscar'}
              </button>
            </div>
          </div>

          {/* Mensagens de Erro */}
          {error && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
              <p className="text-red-800">{error}</p>
            </div>
          )}

          {/* Mensagens de Sucesso */}
          {success && (
            <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-md">
              <p className="text-green-800">{success}</p>
            </div>
          )}

          {/* Detalhes da Reserva */}
          {reserva && (
            <div className="space-y-6">
              <div className="border-t border-gray-200 pt-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">
                  Detalhes da Reserva
                </h2>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-600">Cliente</p>
                    <p className="text-lg font-medium text-gray-900">
                      {reserva.clienteNome}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Veículo</p>
                    <p className="text-lg font-medium text-gray-900">
                      {reserva.veiculoModelo}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Data de Início</p>
                    <p className="text-lg font-medium text-gray-900">
                      {new Date(reserva.dataInicio).toLocaleDateString('pt-BR')}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Data de Fim</p>
                    <p className="text-lg font-medium text-gray-900">
                      {new Date(reserva.dataFim).toLocaleDateString('pt-BR')}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Status</p>
                    <p className="text-lg font-medium text-gray-900">
                      <span className={`px-3 py-1 rounded-full text-sm ${
                        reserva.status === 'CONFIRMADA' 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-yellow-100 text-yellow-800'
                      }`}>
                        {reserva.status}
                      </span>
                    </p>
                  </div>
                </div>
              </div>

              {/* Observações */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Observações da Retirada (opcional)
                </label>
                <textarea
                  value={observacoes}
                  onChange={(e) => setObservacoes(e.target.value)}
                  rows={4}
                  placeholder="Ex: Verificado estado geral do veículo, tanque cheio, sem avarias..."
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
              </div>

              {/* Botão de Confirmação */}
              <div className="flex gap-4 pt-4">
                <button
                  onClick={confirmarRetirada}
                  disabled={loading || reserva.status === 'EM_ANDAMENTO'}
                  className="flex-1 px-6 py-3 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors font-semibold"
                >
                  {loading ? 'Processando...' : 'Confirmar Retirada e Gerar Contrato'}
                </button>
                <Link
                  href="/"
                  className="px-6 py-3 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors font-semibold"
                >
                  Cancelar
                </Link>
              </div>

              {reserva.status === 'EM_ANDAMENTO' && (
                <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-md">
                  <p className="text-yellow-800">
                    Esta reserva já foi confirmada anteriormente.
                  </p>
                </div>
              )}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
