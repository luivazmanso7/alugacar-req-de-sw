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
  AlertCircle,
} from "lucide-react";

export default function MinhasReservasPage() {
  const router = useRouter();
  const [reservas, setReservas] = useState<ReservaResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [cancelandoCodigo, setCancelandoCodigo] = useState<string | null>(null);

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
        console.error("Erro ao carregar reservas:", err);
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

  const podeCancelar = (reserva: ReservaResponse) => {
    // Só pode cancelar reservas ATIVAS/CONFIRMADAS
    const status = reserva.status.toUpperCase();
    if (status !== "ATIVA" && status !== "CONFIRMADA") {
      return false;
    }

    // Verificar se há pelo menos 12 horas antes da retirada
    const dataRetirada = new Date(reserva.dataRetirada);
    const agora = new Date();
    const horasRestantes =
      (dataRetirada.getTime() - agora.getTime()) / (1000 * 60 * 60);

    return horasRestantes >= 12;
  };

  const handleCancelar = async (codigoReserva: string) => {
    if (!confirm("Tem certeza que deseja cancelar esta reserva?")) {
      return;
    }

    try {
      setCancelandoCodigo(codigoReserva);
      setError(null);

      await reservaService.cancelar(codigoReserva);

      // Recarregar lista de reservas
      const minhasReservas = await reservaService.listarMinhas();
      setReservas(minhasReservas);
    } catch (err: any) {
      console.error("Erro ao cancelar reserva:", err);
      setError(err.message || "Erro ao cancelar reserva");
    } finally {
      setCancelandoCodigo(null);
    }
  };

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
        ) : (
          <div className="space-y-4">
            {reservas.map((reserva) => (
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

                  {/* Botão de Cancelar */}
                  {podeCancelar(reserva) ? (
                    <div className="border-t border-gray-200 pt-4">
                      <button
                        onClick={() => handleCancelar(reserva.codigo)}
                        disabled={cancelandoCodigo === reserva.codigo}
                        className="w-full md:w-auto bg-red-600 hover:bg-red-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-semibold py-2 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
                      >
                        {cancelandoCodigo === reserva.codigo ? (
                          <>
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                            Cancelando...
                          </>
                        ) : (
                          <>
                            <X className="w-4 h-4" />
                            Cancelar Reserva
                          </>
                        )}
                      </button>
                    </div>
                  ) : reserva.status.toUpperCase() !== "CANCELADA" &&
                    reserva.status.toUpperCase() !== "CONCLUIDA" &&
                    reserva.status.toUpperCase() !== "FINALIZADA" ? (
                    <div className="border-t border-gray-200 pt-4">
                      <div className="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded-lg flex items-start gap-2">
                        <AlertCircle className="w-5 h-5 mt-0.5 flex-shrink-0" />
                        <div>
                          <p className="font-semibold text-sm mb-1">
                            Cancelamento não disponível
                          </p>
                          <p className="text-xs">
                            O cancelamento só é permitido com pelo menos 12
                            horas de antecedência antes da data de retirada.
                          </p>
                        </div>
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
