"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { reservaService, CriarReservaRequest } from "@/services/reservaService";
import { authService } from "@/services/authService";
import { ArrowLeft, Calendar, MapPin, Car, DollarSign } from "lucide-react";

interface ReservarFormProps {
  placa: string;
  categoria: string;
  cidade: string;
  diaria: number;
  modelo: string;
  dataRetiradaParam?: string;
  dataDevolucaoParam?: string;
}

export default function ReservarForm({
  placa,
  categoria,
  cidade,
  diaria,
  modelo,
  dataRetiradaParam = "",
  dataDevolucaoParam = "",
}: ReservarFormProps) {
  const router = useRouter();

  // Estado do formulário
  const [formData, setFormData] = useState({
    dataRetirada: dataRetiradaParam || "",
    dataDevolucao: dataDevolucaoParam || "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [reservaCriada, setReservaCriada] = useState<any>(null);
  const [clienteNome, setClienteNome] = useState<string | null>(null);

  // Verificar se está logado ao montar o componente
  useEffect(() => {
    const verificarAutenticacao = async () => {
      // Verificar se há nome do cliente no localStorage (indica login anterior)
      const nome = authService.getClienteNome();
      if (!nome) {
        console.log(
          "Nenhum nome encontrado no localStorage, redirecionando para login"
        );
        // Redirecionar para login se não houver nome no localStorage
        router.push("/alugar/login");
        return;
      }

      console.log("Nome encontrado no localStorage:", nome);

      // Definir o nome do cliente imediatamente
      setClienteNome(nome);

      // NÃO verificar sessão aqui - apenas confiar no localStorage
      // A verificação real será feita pelo backend quando criar a reserva
      // Isso evita redirecionamentos desnecessários se o cookie não estiver sendo enviado corretamente
      console.log("Autenticação verificada via localStorage, continuando...");
    };

    verificarAutenticacao();
  }, [router]);

  const calcularValorTotal = () => {
    if (!formData.dataRetirada || !formData.dataDevolucao) return 0;
    const inicio = new Date(formData.dataRetirada);
    const fim = new Date(formData.dataDevolucao);
    const dias = Math.ceil(
      (fim.getTime() - inicio.getTime()) / (1000 * 60 * 60 * 24)
    );
    return dias > 0 ? dias * diaria : 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Validar datas
      if (!formData.dataRetirada || !formData.dataDevolucao) {
        throw new Error(
          "Por favor, selecione as datas de retirada e devolução"
        );
      }

      const dataRetirada = new Date(formData.dataRetirada);
      const dataDevolucao = new Date(formData.dataDevolucao);

      if (dataDevolucao <= dataRetirada) {
        throw new Error(
          "A data de devolução deve ser posterior à data de retirada"
        );
      }

      // Criar requisição (cliente vem da sessão HTTP)
      const request: CriarReservaRequest = {
        categoriaCodigo: categoria.toUpperCase(),
        cidadeRetirada: cidade,
        periodo: {
          dataRetirada: dataRetirada.toISOString(),
          dataDevolucao: dataDevolucao.toISOString(),
        },
        placaVeiculo: placa, // Placa do veículo específico
      };

      const reserva = await reservaService.criar(request);
      setReservaCriada(reserva);
    } catch (err: any) {
      console.error("Erro ao criar reserva:", err);
      setError(
        err.response?.data?.message ||
          err.message ||
          "Erro ao criar reserva. Verifique os dados e tente novamente."
      );
    } finally {
      setLoading(false);
    }
  };

  const formatarPreco = (valor: number) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(valor);
  };

  const formatarData = (dataISO: string) => {
    return new Date(dataISO).toLocaleDateString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  // Se a reserva foi criada, mostrar página de sucesso
  if (reservaCriada) {
    return (
      <div className="min-h-screen bg-gray-50 py-12">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="bg-white rounded-lg shadow-lg p-8">
            <div className="text-center mb-8">
              <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-green-100 mb-4">
                <svg
                  className="h-8 w-8 text-green-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M5 13l4 4L19 7"
                  />
                </svg>
              </div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                Reserva Confirmada!
              </h1>
              <p className="text-gray-600">
                Sua reserva foi criada com sucesso
              </p>
            </div>

            <div className="border-t border-gray-200 pt-6 space-y-4">
              <div className="flex justify-between">
                <span className="text-gray-600">Código da Reserva:</span>
                <span className="font-semibold text-gray-900">
                  {reservaCriada.codigo}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Veículo:</span>
                <span className="font-semibold text-gray-900">
                  {modelo} - {categoria}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Cidade de Retirada:</span>
                <span className="font-semibold text-gray-900">
                  {reservaCriada.cidadeRetirada}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Data de Retirada:</span>
                <span className="font-semibold text-gray-900">
                  {formatarData(reservaCriada.dataRetirada)}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Data de Devolução:</span>
                <span className="font-semibold text-gray-900">
                  {formatarData(reservaCriada.dataDevolucao)}
                </span>
              </div>
              <div className="flex justify-between pt-4 border-t border-gray-200">
                <span className="text-lg font-semibold text-gray-900">
                  Valor Estimado:
                </span>
                <span className="text-xl font-bold text-blue-600">
                  {formatarPreco(reservaCriada.valorEstimado)}
                </span>
              </div>
            </div>

            <div className="mt-8 flex gap-4">
              <button
                onClick={() => router.push("/alugar")}
                className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-3 px-6 rounded-lg transition-colors"
              >
                Fazer Nova Reserva
              </button>
              <button
                onClick={() => router.push(`/reservas/${reservaCriada.codigo}`)}
                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors"
              >
                Ver Detalhes da Reserva
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  const hoje = new Date().toISOString().split("T")[0];
  const minDataDevolucao = formData.dataRetirada || hoje;

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Botão Voltar */}
        <button
          onClick={() => router.back()}
          className="mb-6 flex items-center text-gray-600 hover:text-gray-900"
        >
          <ArrowLeft className="w-5 h-5 mr-2" />
          Voltar
        </button>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Resumo do Veículo */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-md p-6 sticky top-4">
              <h2 className="text-xl font-bold text-gray-900 mb-4">
                Resumo da Reserva
              </h2>

              <div className="space-y-4">
                <div>
                  <div className="flex items-center text-sm text-gray-600 mb-1">
                    <Car className="w-4 h-4 mr-2" />
                    Veículo
                  </div>
                  <p className="font-semibold text-gray-900">{modelo}</p>
                  <p className="text-sm text-gray-600">{categoria}</p>
                  <p className="text-xs text-gray-500 mt-1">Placa: {placa}</p>
                </div>

                <div>
                  <div className="flex items-center text-sm text-gray-600 mb-1">
                    <MapPin className="w-4 h-4 mr-2" />
                    Cidade
                  </div>
                  <p className="font-semibold text-gray-900">{cidade}</p>
                </div>

                <div>
                  <div className="flex items-center text-sm text-gray-600 mb-1">
                    <DollarSign className="w-4 h-4 mr-2" />
                    Diária
                  </div>
                  <p className="font-semibold text-gray-900">
                    {formatarPreco(diaria)}
                  </p>
                </div>

                {formData.dataRetirada && formData.dataDevolucao && (
                  <div className="pt-4 border-t border-gray-200">
                    <div className="flex justify-between mb-2">
                      <span className="text-sm text-gray-600">Período:</span>
                      <span className="text-sm font-semibold text-gray-900">
                        {Math.ceil(
                          (new Date(formData.dataDevolucao).getTime() -
                            new Date(formData.dataRetirada).getTime()) /
                            (1000 * 60 * 60 * 24)
                        )}{" "}
                        dia(s)
                      </span>
                    </div>
                    <div className="flex justify-between pt-2 border-t border-gray-200">
                      <span className="font-semibold text-gray-900">
                        Total:
                      </span>
                      <span className="text-lg font-bold text-blue-600">
                        {formatarPreco(calcularValorTotal())}
                      </span>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Formulário */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-lg shadow-md p-8">
              <div className="mb-6">
                <h1 className="text-2xl font-bold text-gray-900 mb-2">
                  Dados da Reserva
                </h1>
                {clienteNome && (
                  <p className="text-sm text-gray-600">
                    Reservando como:{" "}
                    <span className="font-semibold">{clienteNome}</span>
                  </p>
                )}
              </div>

              {error && (
                <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Datas */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label
                      htmlFor="dataRetirada"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      <Calendar className="w-4 h-4 inline mr-1" />
                      Data de Retirada *
                    </label>
                    <input
                      type="date"
                      id="dataRetirada"
                      value={formData.dataRetirada}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          dataRetirada: e.target.value,
                        })
                      }
                      min={hoje}
                      required
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>

                  <div>
                    <label
                      htmlFor="dataDevolucao"
                      className="block text-sm font-medium text-gray-700 mb-2"
                    >
                      <Calendar className="w-4 h-4 inline mr-1" />
                      Data de Devolução *
                    </label>
                    <input
                      type="date"
                      id="dataDevolucao"
                      value={formData.dataDevolucao}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          dataDevolucao: e.target.value,
                        })
                      }
                      min={minDataDevolucao}
                      required
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>
                </div>

                {/* Botões */}
                <div className="flex gap-4 pt-6 border-t border-gray-200">
                  <button
                    type="button"
                    onClick={() => router.back()}
                    className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-3 px-6 rounded-lg transition-colors"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    disabled={loading}
                    className="flex-1 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-semibold py-3 px-6 rounded-lg transition-colors"
                  >
                    {loading ? "Processando..." : "Confirmar Reserva"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
