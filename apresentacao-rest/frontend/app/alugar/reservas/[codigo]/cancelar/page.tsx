"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
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
  X,
  ArrowLeft,
  AlertCircle,
  CheckCircle,
} from "lucide-react";

export default function CancelarReservaPage() {
  const router = useRouter();
  const params = useParams();
  const codigoReserva = params?.codigo as string;

  const [reserva, setReserva] = useState<ReservaResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [cancelando, setCancelando] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<CancelarReservaResponse | null>(null);

  useEffect(() => {
    const carregarReserva = async () => {
      const nome = authService.getClienteNome();
      if (!nome) {
        router.push("/alugar/login");
        return;
      }

      if (!codigoReserva) {
        setError("Código da reserva não informado");
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError(null);
        const reservaData = await reservaService.buscarPorCodigo(codigoReserva);
        setReserva(reservaData);
      } catch (err: any) {
        if (err.message === "Não autenticado" || err.message?.includes("401")) {
          router.push("/alugar/login");
        } else {
          setError(err.message || "Erro ao carregar reserva");
        }
      } finally {
        setLoading(false);
      }
    };

    carregarReserva();
  }, [codigoReserva, router]);

  const handleCancelar = async () => {
    if (!reserva) return;

    if (!confirm("Tem certeza que deseja cancelar esta reserva?")) {
      return;
    }

    try {
      setCancelando(true);
      setError(null);
      setSuccess(null);

      const resultado = await reservaService.cancelar(reserva.codigo);
      setSuccess(resultado);

      setTimeout(() => {
        router.push("/alugar/minhas-reservas");
      }, 3000);
    } catch (err: any) {
      setError(err.message || "Erro ao cancelar reserva");
    } finally {
      setCancelando(false);
    }
  };

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

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 p-8">
        <div className="max-w-4xl mx-auto">
          <div className="bg-white rounded-lg shadow-md p-8 text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Carregando reserva...</p>
          </div>
        </div>
      </div>
    );
  }

  if (!reserva) {
    return (
      <div className="min-h-screen bg-gray-50 p-8">
        <div className="max-w-4xl mx-auto">
          <div className="bg-white rounded-lg shadow-md p-8">
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error || "Reserva não encontrada"}
            </div>
            <button
              onClick={() => router.push("/alugar/minhas-reservas")}
              className="mt-4 px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
            >
              Voltar para Minhas Reservas
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="mb-6">
          <button
            onClick={() => router.push("/alugar/minhas-reservas")}
            className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-4 transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
            Voltar para Minhas Reservas
          </button>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Cancelar Reserva
          </h1>
          <p className="text-gray-600">
            Reserva: <span className="font-semibold">{reserva.codigo}</span>
          </p>
        </div>

        {/* Informações da Reserva */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            Informações da Reserva
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex items-center text-gray-700">
              <Tag className="w-5 h-5 mr-2 text-blue-600" />
              <div>
                <p className="text-xs text-gray-500">Categoria</p>
                <p className="font-semibold">{reserva.categoria}</p>
              </div>
            </div>
            <div className="flex items-center text-gray-700">
              <MapPin className="w-5 h-5 mr-2 text-blue-600" />
              <div>
                <p className="text-xs text-gray-500">Cidade</p>
                <p className="font-semibold">{reserva.cidadeRetirada}</p>
              </div>
            </div>
            <div className="flex items-center text-gray-700">
              <DollarSign className="w-5 h-5 mr-2 text-blue-600" />
              <div>
                <p className="text-xs text-gray-500">Valor Estimado</p>
                <p className="font-semibold text-green-600">
                  {formatarMoeda(reserva.valorEstimado)}
                </p>
              </div>
            </div>
            <div className="flex items-center text-gray-700">
              <Calendar className="w-5 h-5 mr-2 text-blue-600" />
              <div>
                <p className="text-xs text-gray-500">Status</p>
                <p className="font-semibold">{reserva.status}</p>
              </div>
            </div>
          </div>

          <div className="mt-4 pt-4 border-t border-gray-200">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p className="text-xs text-gray-500 mb-1">Data de Retirada</p>
                <p className="font-semibold">
                  {formatarData(reserva.dataRetirada)}
                </p>
              </div>
              <div>
                <p className="text-xs text-gray-500 mb-1">Data de Devolução</p>
                <p className="font-semibold">
                  {formatarData(reserva.dataDevolucao)}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Confirmação de Cancelamento */}
        {!success && (
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
              <div className="flex items-start gap-2">
                <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
                <div>
                  <p className="font-semibold text-red-900 mb-1">
                    Atenção: Esta ação não pode ser desfeita
                  </p>
                  <p className="text-sm text-red-800">
                    Ao cancelar esta reserva, ela será marcada como cancelada e
                    não poderá ser reativada. Uma tarifa de cancelamento pode
                    ser aplicada conforme a política da empresa.
                  </p>
                </div>
              </div>
            </div>

            {error && (
              <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
                {error}
              </div>
            )}

            <div className="flex gap-4">
              <button
                onClick={() => router.push("/alugar/minhas-reservas")}
                className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Voltar
              </button>
              <button
                onClick={handleCancelar}
                disabled={cancelando}
                className="flex-1 px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
              >
                {cancelando ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    Cancelando...
                  </>
                ) : (
                  <>
                    <X className="w-4 h-4" />
                    Confirmar Cancelamento
                  </>
                )}
              </button>
            </div>
          </div>
        )}

        {/* Sucesso */}
        {success && (
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="bg-green-50 border border-green-200 rounded-lg p-6">
              <div className="flex items-start gap-3 mb-4">
                <CheckCircle className="w-6 h-6 text-green-600 mt-0.5 flex-shrink-0" />
                <div>
                  <h3 className="text-lg font-semibold text-green-900 mb-2">
                    Reserva Cancelada com Sucesso!
                  </h3>
                  <p className="text-sm text-green-800">
                    A reserva {success.codigoReserva} foi cancelada.
                  </p>
                </div>
              </div>

              <div className="bg-white rounded-lg p-4 mb-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-xs text-gray-500 mb-1">Status</p>
                    <p className="font-semibold text-gray-900">
                      {success.status}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 mb-1">
                      Tarifa de Cancelamento
                    </p>
                    <p className="font-semibold text-green-600">
                      {formatarMoeda(success.tarifaCancelamento)}
                    </p>
                  </div>
                </div>
              </div>

              <p className="text-sm text-gray-600 mb-4">
                Você será redirecionado para a página de minhas reservas em
                alguns segundos...
              </p>

              <button
                onClick={() => router.push("/alugar/minhas-reservas")}
                className="w-full px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Ir para Minhas Reservas
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
