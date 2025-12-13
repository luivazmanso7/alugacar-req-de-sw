"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { adminAuthService } from "@/services/adminAuthService";

interface LocacaoDetalhes {
  codigo: string;
  reservaCodigo: string;
  veiculoPlaca: string;
  veiculoModelo: string;
  clienteNome: string;
  diasPrevistos: number;
  valorDiaria: number;
  status: string;
}

interface FaturamentoResponse {
  valorTotal: number;
  valorDiarias: number;
  valorAtraso: number;
  valorMulta: number;
  valorTaxas: number;
}

export default function AdminDevolucaoPage() {
  const router = useRouter();
  const [locacoesAtivas, setLocacoesAtivas] = useState<LocacaoDetalhes[]>([]);
  const [locacoesFiltradas, setLocacoesFiltradas] = useState<LocacaoDetalhes[]>(
    []
  );
  const [filtroCpf, setFiltroCpf] = useState("");
  const [locacaoSelecionada, setLocacaoSelecionada] =
    useState<LocacaoDetalhes | null>(null);
  const [loading, setLoading] = useState(false);
  const [loadingLista, setLoadingLista] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [faturamento, setFaturamento] = useState<FaturamentoResponse | null>(
    null
  );

  const [quilometragem, setQuilometragem] = useState("");
  const [combustivel, setCombustivel] = useState("CHEIO");
  const [possuiAvarias, setPossuiAvarias] = useState(false);
  const [dataDevolucao, setDataDevolucao] = useState("");

  useEffect(() => {
    const adminNome = adminAuthService.getAdminNome();
    if (!adminNome) {
      router.push("/admin/login");
    } else {
      carregarLocacoesAtivas();
    }
  }, [router]);

  useEffect(() => {
    if (filtroCpf.trim()) {
      filtrarPorCpf(filtroCpf.trim());
    } else {
      setLocacoesFiltradas(locacoesAtivas);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filtroCpf, locacoesAtivas]);

  const carregarLocacoesAtivas = async () => {
    setLoadingLista(true);
    setError(null);

    try {
      const response = await fetch(`/api/locacoes/em-andamento`, {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message ||
            errorData.error ||
            "Erro ao carregar locações em andamento"
        );
      }

      const locacoes = await response.json();
      setLocacoesAtivas(locacoes);
      setLocacoesFiltradas(locacoes);
    } catch (err: any) {
      setError(err.message || "Erro ao carregar locações em andamento");
    } finally {
      setLoadingLista(false);
    }
  };

  const filtrarPorCpf = async (cpf: string) => {
    setLoadingLista(true);
    setError(null);

    try {
      const response = await fetch(`/api/locacoes/cliente/${cpf}`, {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || errorData.error || "Erro ao filtrar locações"
        );
      }

      const locacoes = await response.json();
      const locacoesAtivasFiltradas = locacoes.filter(
        (l: LocacaoDetalhes) => l.status === "EM_ANDAMENTO"
      );
      setLocacoesFiltradas(locacoesAtivasFiltradas);
    } catch (err: any) {
      setError(err.message || "Erro ao filtrar locações");
      setLocacoesFiltradas([]);
    } finally {
      setLoadingLista(false);
    }
  };

  const selecionarLocacao = (locacao: LocacaoDetalhes) => {
    setLocacaoSelecionada(locacao);
    setError(null);
    setSuccess(null);
    setFaturamento(null);
    setQuilometragem("");
    setCombustivel("CHEIO");
    setPossuiAvarias(false);
    // Definir data/hora atual como padrão
    const agora = new Date();
    const dataHoraISO = agora.toISOString().slice(0, 16); // Formato: YYYY-MM-DDTHH:mm
    setDataDevolucao(dataHoraISO);
  };

  const processarDevolucao = async () => {
    if (!locacaoSelecionada) {
      setError("Selecione uma locação primeiro");
      return;
    }

    if (!quilometragem.trim() || parseInt(quilometragem) < 0) {
      setError(
        "A quilometragem é obrigatória e deve ser maior ou igual a zero"
      );
      return;
    }

    if (!combustivel.trim()) {
      setError("O nível de combustível é obrigatório");
      return;
    }

    if (!dataDevolucao.trim()) {
      setError("A data e hora de devolução são obrigatórias");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);
    setFaturamento(null);

    try {
      // Converter data/hora local para ISO 8601
      const dataDevolucaoISO = new Date(dataDevolucao).toISOString();

      const response = await fetch(
        `/api/locacoes/${locacaoSelecionada.codigo}/processar-devolucao`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            quilometragem: parseInt(quilometragem),
            combustivel: combustivel,
            possuiAvarias: possuiAvarias,
            dataDevolucao: dataDevolucaoISO,
          }),
        }
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || errorData.error || "Erro ao processar devolução"
        );
      }

      const faturamentoData = await response.json();
      setFaturamento(faturamentoData);
      setSuccess("Devolução processada com sucesso! A locação foi finalizada.");

      await carregarLocacoesAtivas();

      // Não limpar automaticamente - deixar o usuário fechar manualmente
    } catch (err: any) {
      setError(err.message || "Erro ao processar devolução");
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
                Processar Devolução
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
            Processar Devolução de Veículo
          </h1>
          <p className="text-gray-600 mb-6">
            Selecione uma locação em andamento e preencha os dados da vistoria
            de devolução.
          </p>

          {/* Filtro por CPF */}
          <div className="mb-6">
            <label
              htmlFor="filtroCpf"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Filtrar por CPF/CNPJ (Opcional)
            </label>
            <div className="flex gap-4">
              <input
                type="text"
                id="filtroCpf"
                value={filtroCpf}
                onChange={(e) => setFiltroCpf(e.target.value)}
                placeholder="Digite o CPF ou CNPJ do cliente"
                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              {filtroCpf && (
                <button
                  onClick={() => {
                    setFiltroCpf("");
                    setLocacoesFiltradas(locacoesAtivas);
                  }}
                  className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                >
                  Limpar Filtro
                </button>
              )}
            </div>
          </div>

          {/* Lista de Locações Ativas */}
          <div className="mb-6">
            <div className="flex justify-between items-center mb-3">
              <h2 className="text-lg font-semibold text-gray-900">
                Locações em Andamento ({locacoesFiltradas.length})
              </h2>
              <button
                onClick={carregarLocacoesAtivas}
                disabled={loadingLista}
                className="px-4 py-2 text-sm bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
              >
                {loadingLista ? "Atualizando..." : "Atualizar Lista"}
              </button>
            </div>
            {loadingLista ? (
              <div className="text-center py-8 text-gray-500">
                Carregando locações...
              </div>
            ) : locacoesFiltradas.length === 0 ? (
              <div className="text-center py-8 text-gray-500 bg-gray-50 rounded-lg">
                {filtroCpf
                  ? "Nenhuma locação ativa encontrada para este CPF/CNPJ"
                  : "Nenhuma locação em andamento no momento"}
              </div>
            ) : (
              <div className="space-y-2 max-h-96 overflow-y-auto">
                {locacoesFiltradas.map((locacao) => (
                  <div
                    key={locacao.codigo}
                    onClick={() => selecionarLocacao(locacao)}
                    className={`p-4 border rounded-lg cursor-pointer transition-colors ${
                      locacaoSelecionada?.codigo === locacao.codigo
                        ? "border-blue-500 bg-blue-50"
                        : "border-gray-200 hover:border-gray-300 hover:bg-gray-50"
                    }`}
                  >
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                      <div>
                        <span className="font-medium text-gray-700">
                          Código:
                        </span>
                        <span className="ml-2 text-gray-900 font-semibold">
                          {locacao.codigo}
                        </span>
                      </div>
                      <div>
                        <span className="font-medium text-gray-700">
                          Cliente:
                        </span>
                        <span className="ml-2 text-gray-900">
                          {locacao.clienteNome}
                        </span>
                      </div>
                      <div>
                        <span className="font-medium text-gray-700">
                          Placa:
                        </span>
                        <span className="ml-2 text-gray-900 font-semibold">
                          {locacao.veiculoPlaca}
                        </span>
                      </div>
                      <div>
                        <span className="font-medium text-gray-700">
                          Modelo:
                        </span>
                        <span className="ml-2 text-gray-900">
                          {locacao.veiculoModelo}
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

          {locacaoSelecionada && (
            <div className="mb-6 bg-blue-50 border border-blue-200 rounded-lg p-4">
              <h2 className="text-lg font-semibold text-gray-900 mb-3">
                Locação Selecionada
              </h2>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="font-medium text-gray-700">Código:</span>
                  <span className="ml-2 text-gray-900 font-semibold">
                    {locacaoSelecionada.codigo}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Status:</span>
                  <span className="ml-2 text-gray-900">
                    {locacaoSelecionada.status}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Cliente:</span>
                  <span className="ml-2 text-gray-900">
                    {locacaoSelecionada.clienteNome}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Placa:</span>
                  <span className="ml-2 text-gray-900 font-semibold">
                    {locacaoSelecionada.veiculoPlaca}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Modelo:</span>
                  <span className="ml-2 text-gray-900">
                    {locacaoSelecionada.veiculoModelo}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">
                    Dias Previstos:
                  </span>
                  <span className="ml-2 text-gray-900">
                    {locacaoSelecionada.diasPrevistos}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">
                    Valor Diária:
                  </span>
                  <span className="ml-2 text-gray-900">
                    R$ {locacaoSelecionada.valorDiaria.toFixed(2)}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Reserva:</span>
                  <span className="ml-2 text-gray-900">
                    {locacaoSelecionada.reservaCodigo}
                  </span>
                </div>
              </div>
            </div>
          )}

          {locacaoSelecionada &&
            locacaoSelecionada.status === "EM_ANDAMENTO" && (
              <div className="bg-gray-50 rounded-lg p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">
                  Dados da Vistoria de Devolução
                </h2>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label
                      htmlFor="quilometragem"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      Quilometragem *
                    </label>
                    <input
                      type="number"
                      id="quilometragem"
                      value={quilometragem}
                      onChange={(e) => setQuilometragem(e.target.value)}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="0"
                      min="0"
                      required
                    />
                  </div>

                  <div>
                    <label
                      htmlFor="combustivel"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      Nível de Combustível *
                    </label>
                    <select
                      id="combustivel"
                      value={combustivel}
                      onChange={(e) => setCombustivel(e.target.value)}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      required
                    >
                      <option value="CHEIO">Cheio</option>
                      <option value="TRES_QUARTOS">3/4</option>
                      <option value="MEIO">Meio</option>
                      <option value="UM_QUARTO">1/4</option>
                      <option value="RESERVA">Reserva</option>
                      <option value="VAZIO">Vazio</option>
                    </select>
                  </div>

                  <div>
                    <label
                      htmlFor="dataDevolucao"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      Data e Hora de Devolução *
                    </label>
                    <input
                      type="datetime-local"
                      id="dataDevolucao"
                      value={dataDevolucao}
                      onChange={(e) => setDataDevolucao(e.target.value)}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      required
                    />
                    <p className="mt-1 text-xs text-gray-500">
                      A multa por atraso será calculada automaticamente
                      comparando esta data com a data prevista de devolução da
                      reserva.
                    </p>
                  </div>

                  <div className="md:col-span-2">
                    <label className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        checked={possuiAvarias}
                        onChange={(e) => setPossuiAvarias(e.target.checked)}
                        className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                      />
                      <span className="text-sm font-medium text-gray-700">
                        Veículo possui avarias
                      </span>
                    </label>
                  </div>
                </div>

                {faturamento && (
                  <div className="mt-6 bg-green-50 border border-green-200 rounded-lg p-4 relative">
                    <button
                      onClick={() => {
                        setFaturamento(null);
                        setSuccess(null);
                        setLocacaoSelecionada(null);
                        setQuilometragem("");
                        setCombustivel("CHEIO");
                        setPossuiAvarias(false);
                      }}
                      className="absolute top-2 right-2 text-gray-500 hover:text-gray-700 transition-colors"
                      title="Fechar faturamento"
                    >
                      <svg
                        className="w-5 h-5"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M6 18L18 6M6 6l12 12"
                        />
                      </svg>
                    </button>
                    <h3 className="text-lg font-semibold text-gray-900 mb-3 pr-6">
                      Faturamento Calculado
                    </h3>
                    <div className="space-y-2 text-sm">
                      <div className="flex justify-between">
                        <span className="text-gray-700">Diárias:</span>
                        <span className="text-gray-900 font-medium">
                          R$ {faturamento.valorDiarias.toFixed(2)}
                        </span>
                      </div>
                      {faturamento.valorAtraso > 0 && (
                        <div className="flex justify-between">
                          <span className="text-gray-700">Atraso:</span>
                          <span className="text-orange-600 font-medium">
                            R$ {faturamento.valorAtraso.toFixed(2)}
                          </span>
                        </div>
                      )}
                      {faturamento.valorMulta > 0 && (
                        <div className="flex justify-between">
                          <span className="text-gray-700">
                            Multa por Atraso:
                          </span>
                          <span className="text-red-600 font-medium">
                            R$ {faturamento.valorMulta.toFixed(2)}
                          </span>
                        </div>
                      )}
                      {faturamento.valorTaxas > 0 && (
                        <div className="flex justify-between">
                          <span className="text-gray-700">Taxas:</span>
                          <span className="text-gray-900 font-medium">
                            R$ {faturamento.valorTaxas.toFixed(2)}
                          </span>
                        </div>
                      )}
                      <div className="flex justify-between pt-2 border-t border-green-300 mt-3">
                        <span className="font-semibold text-gray-900 text-base">
                          Total:
                        </span>
                        <span className="font-bold text-xl text-green-700">
                          R$ {faturamento.valorTotal.toFixed(2)}
                        </span>
                      </div>
                    </div>
                  </div>
                )}

                <div className="mt-6 flex justify-end gap-4">
                  {!faturamento && (
                    <>
                      <button
                        onClick={() => {
                          setLocacaoSelecionada(null);
                          setQuilometragem("");
                          setCombustivel("CHEIO");
                          setPossuiAvarias(false);
                          setFaturamento(null);
                          setError(null);
                          setSuccess(null);
                        }}
                        className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                      >
                        Limpar Seleção
                      </button>
                      <button
                        onClick={processarDevolucao}
                        disabled={loading}
                        className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
                      >
                        {loading ? "Processando..." : "Processar Devolução"}
                      </button>
                    </>
                  )}
                  {faturamento && (
                    <button
                      onClick={() => {
                        setFaturamento(null);
                        setSuccess(null);
                        setLocacaoSelecionada(null);
                        setQuilometragem("");
                        setCombustivel("CHEIO");
                        setPossuiAvarias(false);
                      }}
                      className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    >
                      Nova Devolução
                    </button>
                  )}
                </div>
              </div>
            )}

          {locacaoSelecionada &&
            locacaoSelecionada.status !== "EM_ANDAMENTO" && (
              <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-lg text-yellow-800">
                <p>
                  Esta locação não pode ser processada. Status atual:{" "}
                  <span className="font-semibold">
                    {locacaoSelecionada.status}
                  </span>
                  . Apenas locações com status EM_ANDAMENTO podem ser
                  devolvidas.
                </p>
              </div>
            )}
        </div>
      </main>
    </div>
  );
}
