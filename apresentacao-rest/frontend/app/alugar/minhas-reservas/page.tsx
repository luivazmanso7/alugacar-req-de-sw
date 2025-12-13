"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import {
  reservaService,
  ReservaResponse,
  CancelarReservaResponse,
} from "@/services/reservaService";
import { authService } from "@/services/authService";
import {
  Calendar,
  MapPin,
  DollarSign,
  Tag,
  User,
  FileText,
  X,
  Edit,
  Filter,
} from "lucide-react";

type FiltroStatus =
  | "TODAS"
  | "ATIVA"
  | "CONFIRMADA"
  | "CANCELADA"
  | "FINALIZADA"
  | "CONCLUIDA";

export default function MinhasReservasPage() {
  const router = useRouter();
  const [reservas, setReservas] = useState<ReservaResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filtroStatus, setFiltroStatus] = useState<FiltroStatus>("TODAS");

  useEffect(() => {
    const carregarReservas = async () => {
      // Verificar se está logado
      const nome = authService.getClienteNome();
      if (!nome) {
        router.push("/alugar/login");
        return;
      }

      try {
        setLoading(true);
        setError(null);
        const minhasReservas = await reservaService.listarMinhas();
        setReservas(minhasReservas);
      } catch (err: any) {
        if (err.message === "Não autenticado" || err.message?.includes("401")) {
          router.push("/alugar/login");
        } else {
          setError(err.message || "Erro ao carregar suas reservas");
        }
      } finally {
        setLoading(false);
      }
    };

    carregarReservas();
  }, [router]);

  const formatarData = (dataISO: string) => {
    const data = new Date(dataISO);
    return data.toLocaleDateString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const formatarMoeda = (valor: number | string) => {
    const numValor = typeof valor === "string" ? parseFloat(valor) : valor;
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(numValor);
  };

  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case "CONFIRMADA":
      case "ATIVA":
        return "bg-green-100 text-green-800";
      case "PENDENTE":
        return "bg-yellow-100 text-yellow-800";
      case "CANCELADA":
        return "bg-red-100 text-red-800";
      case "FINALIZADA":
      case "CONCLUIDA":
        return "bg-blue-100 text-blue-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status.toUpperCase()) {
      case "CONFIRMADA":
      case "ATIVA":
        return "Confirmada";
      case "PENDENTE":
        return "Pendente";
      case "CANCELADA":
        return "Cancelada";
      case "FINALIZADA":
      case "CONCLUIDA":
        return "Finalizada";
      default:
        return status;
    }
  };

  // Função para ordenar reservas: ativas primeiro, depois por data de retirada
  const ordenarReservas = (reservas: ReservaResponse[]): ReservaResponse[] => {
    return [...reservas].sort((a, b) => {
      const statusA = a.status.toUpperCase();
      const statusB = b.status.toUpperCase();

      // Prioridade: ATIVA/CONFIRMADA primeiro
      const isAtivaA = statusA === "ATIVA" || statusA === "CONFIRMADA";
      const isAtivaB = statusB === "ATIVA" || statusB === "CONFIRMADA";

      if (isAtivaA && !isAtivaB) return -1;
      if (!isAtivaA && isAtivaB) return 1;

      // Se ambas são ativas ou ambas não são, ordenar por data de retirada (mais recente primeiro)
      const dataRetiradaA = new Date(a.dataRetirada).getTime();
      const dataRetiradaB = new Date(b.dataRetirada).getTime();
      return dataRetiradaB - dataRetiradaA;
    });
  };

  // Função para filtrar reservas por status
  const filtrarReservas = (reservas: ReservaResponse[]): ReservaResponse[] => {
    if (filtroStatus === "TODAS") {
      return ordenarReservas(reservas);
    }

    const reservasFiltradas = reservas.filter((reserva) => {
      const status = reserva.status.toUpperCase();
      switch (filtroStatus) {
        case "ATIVA":
          return status === "ATIVA";
        case "CONFIRMADA":
          return status === "CONFIRMADA";
        case "CANCELADA":
          return status === "CANCELADA";
        case "FINALIZADA":
          return status === "FINALIZADA";
        case "CONCLUIDA":
          return status === "CONCLUIDA";
        default:
          return true;
      }
    });

    return ordenarReservas(reservasFiltradas);
  };

  const reservasFiltradasEOrdenadas = filtrarReservas(reservas);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 p-8">
        <div className="max-w-7xl mx-auto">
          <h1 className="text-3xl font-bold text-gray-900 mb-6">
            Minhas Reservas
          </h1>
          <div className="bg-white rounded-lg shadow-md p-8 text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Carregando suas reservas...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 p-8">
        <div className="max-w-7xl mx-auto">
          <h1 className="text-3xl font-bold text-gray-900 mb-6">
            Minhas Reservas
          </h1>
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Minhas Reservas
          </h1>
          <p className="text-gray-600">
            Visualize todas as suas reservas de veículos
          </p>
        </div>

        {/* Filtro por Status */}
        {reservas.length > 0 && (
          <div className="mb-6 bg-white rounded-lg shadow-md p-4">
            <div className="flex items-center gap-4 flex-wrap">
              <div className="flex items-center gap-2 text-gray-700">
                <Filter className="w-5 h-5" />
                <span className="font-semibold">Filtrar por status:</span>
              </div>
              <div className="flex flex-wrap gap-2">
                {(
                  [
                    "TODAS",
                    "ATIVA",
                    "CONFIRMADA",
                    "CANCELADA",
                    "FINALIZADA",
                    "CONCLUIDA",
                  ] as FiltroStatus[]
                ).map((status) => (
                  <button
                    key={status}
                    onClick={() => setFiltroStatus(status)}
                    className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                      filtroStatus === status
                        ? "bg-blue-600 text-white"
                        : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                    }`}
                  >
                    {status === "TODAS" ? "Todas" : getStatusLabel(status)}
                  </button>
                ))}
              </div>
              <div className="ml-auto text-sm text-gray-600">
                {reservasFiltradasEOrdenadas.length} de {reservas.length}{" "}
                reserva(s)
              </div>
            </div>
          </div>
        )}

        {/* Lista de Reservas */}
        {reservas.length === 0 ? (
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <FileText className="w-16 h-16 text-gray-400 mx-auto mb-4" />
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              Nenhuma reserva encontrada
            </h2>
            <p className="text-gray-600 mb-6">
              Você ainda não possui reservas. Que tal fazer uma agora?
            </p>
            <button
              onClick={() => router.push("/alugar")}
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors"
            >
              Fazer uma Reserva
            </button>
          </div>
        ) : reservasFiltradasEOrdenadas.length === 0 ? (
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <FileText className="w-16 h-16 text-gray-400 mx-auto mb-4" />
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              Nenhuma reserva encontrada
            </h2>
            <p className="text-gray-600 mb-6">
              Não há reservas com o status selecionado.
            </p>
            <button
              onClick={() => setFiltroStatus("TODAS")}
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors"
            >
              Ver Todas as Reservas
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {reservasFiltradasEOrdenadas.map((reserva) => (
              <div
                key={reserva.codigo}
                className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300"
              >
                <div className="p-6">
                  {/* Header do Card */}
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-xl font-bold text-gray-900 mb-1">
                        Reserva {reserva.codigo}
                      </h3>
                      <p className="text-sm text-gray-500">
                        Criada em {formatarData(reserva.dataRetirada)}
                      </p>
                    </div>
                    <span
                      className={`px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(
                        reserva.status
                      )}`}
                    >
                      {getStatusLabel(reserva.status)}
                    </span>
                  </div>

                  {/* Informações da Reserva */}
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
                    {/* Categoria */}
                    <div className="flex items-center text-gray-700">
                      <Tag className="w-5 h-5 mr-2 text-blue-600" />
                      <div>
                        <p className="text-xs text-gray-500">Categoria</p>
                        <p className="font-semibold">{reserva.categoria}</p>
                      </div>
                    </div>

                    {/* Cidade */}
                    <div className="flex items-center text-gray-700">
                      <MapPin className="w-5 h-5 mr-2 text-blue-600" />
                      <div>
                        <p className="text-xs text-gray-500">Cidade</p>
                        <p className="font-semibold">
                          {reserva.cidadeRetirada}
                        </p>
                      </div>
                    </div>

                    {/* Valor */}
                    <div className="flex items-center text-gray-700">
                      <DollarSign className="w-5 h-5 mr-2 text-blue-600" />
                      <div>
                        <p className="text-xs text-gray-500">Valor Estimado</p>
                        <p className="font-semibold text-green-600">
                          {formatarMoeda(reserva.valorEstimado)}
                        </p>
                      </div>
                    </div>

                    {/* Cliente */}
                    <div className="flex items-center text-gray-700">
                      <User className="w-5 h-5 mr-2 text-blue-600" />
                      <div>
                        <p className="text-xs text-gray-500">Cliente</p>
                        <p className="font-semibold">{reserva.clienteNome}</p>
                      </div>
                    </div>
                  </div>

                  {/* Período */}
                  <div className="border-t border-gray-200 pt-4 mb-4">
                    <div className="flex items-center text-gray-700 mb-2">
                      <Calendar className="w-5 h-5 mr-2 text-blue-600" />
                      <span className="font-semibold">Período de Locação</span>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 ml-7">
                      <div>
                        <p className="text-xs text-gray-500 mb-1">
                          Data de Retirada
                        </p>
                        <p className="font-semibold">
                          {formatarData(reserva.dataRetirada)}
                        </p>
                      </div>
                      <div>
                        <p className="text-xs text-gray-500 mb-1">
                          Data de Devolução
                        </p>
                        <p className="font-semibold">
                          {formatarData(reserva.dataDevolucao)}
                        </p>
                      </div>
                    </div>
                  </div>

                  {/* Botões de Ação */}
                  {reserva.status.toUpperCase() === "ATIVA" ||
                  reserva.status.toUpperCase() === "CONFIRMADA" ? (
                    <div className="border-t border-gray-200 pt-4">
                      <div className="flex flex-wrap gap-3">
                        <button
                          onClick={() =>
                            router.push(
                              `/alugar/reservas/${reserva.codigo}/alterar`
                            )
                          }
                          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
                        >
                          <Edit className="w-4 h-4" />
                          Alterar Período
                        </button>
                        <button
                          onClick={() =>
                            router.push(
                              `/alugar/reservas/${reserva.codigo}/cancelar`
                            )
                          }
                          className="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
                        >
                          <X className="w-4 h-4" />
                          Cancelar Reserva
                        </button>
                      </div>
                    </div>
                  ) : null}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Mensagem de erro global */}
        {error && (
          <div className="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            {error}
          </div>
        )}
      </div>
    </div>
  );
}
