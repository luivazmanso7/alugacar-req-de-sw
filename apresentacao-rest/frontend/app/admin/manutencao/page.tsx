"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { adminAuthService } from "@/services/adminAuthService";

interface VeiculoManutencao {
  placa: string;
  modelo: string;
  categoria: string;
  cidade: string;
  diaria: number;
  status: string;
  manutencaoPrevista: string | null;
  manutencaoNota: string | null;
}

export default function AdminManutencaoPage() {
  const router = useRouter();
  const [veiculos, setVeiculos] = useState<VeiculoManutencao[]>([]);
  const [veiculoSelecionado, setVeiculoSelecionado] =
    useState<VeiculoManutencao | null>(null);
  const [loading, setLoading] = useState(false);
  const [loadingLista, setLoadingLista] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [previsaoTermino, setPrevisaoTermino] = useState("");
  const [motivo, setMotivo] = useState("");

  useEffect(() => {
    const adminNome = adminAuthService.getAdminNome();
    if (!adminNome) {
      router.push("/admin/login");
    } else {
      carregarVeiculos();
    }
  }, [router]);

  const carregarVeiculos = async () => {
    setLoadingLista(true);
    setError(null);

    try {
      const response = await fetch(`/api/veiculos/precisam-manutencao`, {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message ||
            errorData.error ||
            "Erro ao carregar veículos que precisam de manutenção"
        );
      }

      const data = await response.json();
      setVeiculos(data);
    } catch (err: any) {
      setError(
        err.message || "Erro ao carregar veículos que precisam de manutenção"
      );
    } finally {
      setLoadingLista(false);
    }
  };

  const selecionarVeiculo = (veiculo: VeiculoManutencao) => {
    setVeiculoSelecionado(veiculo);
    setError(null);
    setSuccess(null);
    setPrevisaoTermino("");
    setMotivo("");
  };

  const agendarManutencao = async () => {
    if (!veiculoSelecionado) {
      setError("Selecione um veículo primeiro");
      return;
    }

    if (!previsaoTermino.trim()) {
      setError("A data prevista de término é obrigatória");
      return;
    }

    if (!motivo.trim()) {
      setError("O motivo da manutenção é obrigatório");
      return;
    }

    const dataPrevisao = new Date(previsaoTermino);
    if (dataPrevisao <= new Date()) {
      setError("A data prevista deve ser uma data futura");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const response = await fetch(
        `/api/veiculos/${veiculoSelecionado.placa}/agendar-manutencao`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            previsaoTermino: dataPrevisao.toISOString(),
            motivo: motivo.trim(),
          }),
        }
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || errorData.error || "Erro ao agendar manutenção"
        );
      }

      setSuccess("Manutenção agendada com sucesso!");
      setVeiculoSelecionado(null);
      setPrevisaoTermino("");
      setMotivo("");
      await carregarVeiculos();
    } catch (err: any) {
      setError(err.message || "Erro ao agendar manutenção");
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    await adminAuthService.logout();
    router.push("/admin/login");
  };

  const adminNome = adminAuthService.getAdminNome();

  if (!adminNome) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <p>Redirecionando para o login...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="flex items-center gap-4">
              <button
                onClick={() => router.push("/admin")}
                className="text-2xl font-bold text-blue-600 hover:text-blue-700"
              >
                AlugaCar Admin
              </button>
              <span className="text-gray-400">|</span>
              <span className="text-gray-700 font-medium">
                Agendar Manutenção
              </span>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-gray-700">Olá, {adminNome}</span>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
              >
                Sair
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">
            Agendar Manutenção de Veículos
          </h1>
          <p className="text-gray-600 mb-6">
            Veículos que foram enviados para manutenção devido a avarias e
            precisam ter a manutenção agendada.
          </p>

          <div className="mb-6">
            <div className="flex justify-between items-center mb-3">
              <h2 className="text-lg font-semibold text-gray-900">
                Veículos que Precisam de Manutenção ({veiculos.length})
              </h2>
              <button
                onClick={carregarVeiculos}
                disabled={loadingLista}
                className="px-4 py-2 text-sm bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
              >
                {loadingLista ? "Atualizando..." : "Atualizar Lista"}
              </button>
            </div>
            {loadingLista ? (
              <div className="text-center py-8 text-gray-500">
                Carregando veículos...
              </div>
            ) : veiculos.length === 0 ? (
              <div className="text-center py-8 text-gray-500 bg-gray-50 rounded-lg">
                Nenhum veículo precisa de manutenção no momento
              </div>
            ) : (
              <div className="space-y-2 max-h-96 overflow-y-auto">
                {veiculos.map((veiculo) => (
                  <div
                    key={veiculo.placa}
                    onClick={() => selecionarVeiculo(veiculo)}
                    className={`p-4 border rounded-lg cursor-pointer transition-colors ${
                      veiculoSelecionado?.placa === veiculo.placa
                        ? "border-blue-500 bg-blue-50"
                        : "border-gray-200 hover:border-gray-300 hover:bg-gray-50"
                    }`}
                  >
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                      <div>
                        <span className="font-medium text-gray-700">
                          Placa:
                        </span>
                        <span className="ml-2 text-gray-900 font-semibold">
                          {veiculo.placa}
                        </span>
                      </div>
                      <div>
                        <span className="font-medium text-gray-700">
                          Modelo:
                        </span>
                        <span className="ml-2 text-gray-900">
                          {veiculo.modelo}
                        </span>
                      </div>
                      <div>
                        <span className="font-medium text-gray-700">
                          Categoria:
                        </span>
                        <span className="ml-2 text-gray-900">
                          {veiculo.categoria}
                        </span>
                      </div>
                      <div>
                        <span className="font-medium text-gray-700">
                          Cidade:
                        </span>
                        <span className="ml-2 text-gray-900">
                          {veiculo.cidade}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-6 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
              {success}
            </div>
          )}

          {veiculoSelecionado && (
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-3">
                Veículo Selecionado
              </h2>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="font-medium text-gray-700">Placa:</span>
                  <span className="ml-2 text-gray-900 font-semibold">
                    {veiculoSelecionado.placa}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Modelo:</span>
                  <span className="ml-2 text-gray-900">
                    {veiculoSelecionado.modelo}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Categoria:</span>
                  <span className="ml-2 text-gray-900">
                    {veiculoSelecionado.categoria}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Cidade:</span>
                  <span className="ml-2 text-gray-900">
                    {veiculoSelecionado.cidade}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Diária:</span>
                  <span className="ml-2 text-gray-900">
                    R$ {veiculoSelecionado.diaria.toFixed(2)}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Status:</span>
                  <span className="ml-2 text-gray-900">
                    {veiculoSelecionado.status}
                  </span>
                </div>
              </div>
            </div>
          )}

          {veiculoSelecionado && (
            <div className="bg-gray-50 rounded-lg p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">
                Dados do Agendamento
              </h2>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label
                    htmlFor="previsaoTermino"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Data Prevista de Término *
                  </label>
                  <input
                    type="datetime-local"
                    id="previsaoTermino"
                    value={previsaoTermino}
                    onChange={(e) => setPrevisaoTermino(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                </div>

                <div className="md:col-span-2">
                  <label
                    htmlFor="motivo"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Motivo da Manutenção *
                  </label>
                  <textarea
                    id="motivo"
                    value={motivo}
                    onChange={(e) => setMotivo(e.target.value)}
                    rows={3}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Descreva o motivo da manutenção..."
                    required
                  />
                </div>
              </div>

              <div className="mt-6 flex justify-end gap-4">
                <button
                  onClick={() => {
                    setVeiculoSelecionado(null);
                    setPrevisaoTermino("");
                    setMotivo("");
                    setError(null);
                    setSuccess(null);
                  }}
                  className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  onClick={agendarManutencao}
                  disabled={loading}
                  className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
                >
                  {loading ? "Agendando..." : "Agendar Manutenção"}
                </button>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
