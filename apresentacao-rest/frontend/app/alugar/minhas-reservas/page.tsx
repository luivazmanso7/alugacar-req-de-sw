"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { reservaService, ReservaResponse } from "@/services/reservaService";
import { authService } from "@/services/authService";
import {
  Calendar,
  MapPin,
  DollarSign,
  Tag,
  User,
  FileText,
} from "lucide-react";

export default function MinhasReservasPage() {
  const router = useRouter();
  const [reservas, setReservas] = useState<ReservaResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
        return "bg-green-100 text-green-800";
      case "PENDENTE":
        return "bg-yellow-100 text-yellow-800";
      case "CANCELADA":
        return "bg-red-100 text-red-800";
      case "FINALIZADA":
        return "bg-blue-100 text-blue-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status.toUpperCase()) {
      case "CONFIRMADA":
        return "Confirmada";
      case "PENDENTE":
        return "Pendente";
      case "CANCELADA":
        return "Cancelada";
      case "FINALIZADA":
        return "Finalizada";
      default:
        return status;
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
                  <div className="border-t border-gray-200 pt-4">
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
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
