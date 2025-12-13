"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import { reservaService, ReservaResponse } from "@/services/reservaService";
import { authService } from "@/services/authService";
import {
  Calendar,
  MapPin,
  DollarSign,
  Tag,
  Save,
  ArrowLeft,
} from "lucide-react";

export default function AlterarReservaPage() {
  const router = useRouter();
  const params = useParams();
  const codigoReserva = params?.codigo as string;

  const [reserva, setReserva] = useState<ReservaResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const [dataRetirada, setDataRetirada] = useState("");
  const [dataDevolucao, setDataDevolucao] = useState("");

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

        const dataRetiradaDate = new Date(reservaData.dataRetirada);
        const dataDevolucaoDate = new Date(reservaData.dataDevolucao);

        setDataRetirada(dataRetiradaDate.toISOString().slice(0, 16));
        setDataDevolucao(dataDevolucaoDate.toISOString().slice(0, 16));
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

  const handleAlterar = async () => {
    if (!reserva) return;

    if (!dataRetirada || !dataDevolucao) {
      setError("Por favor, preencha ambas as datas");
      return;
    }

    const dataRetiradaDate = new Date(dataRetirada);
    const dataDevolucaoDate = new Date(dataDevolucao);
    const agora = new Date();

    if (dataRetiradaDate < agora) {
      setError("A data de retirada não pode ser no passado");
      return;
    }

    if (dataDevolucaoDate <= dataRetiradaDate) {
      setError("A data de devolução deve ser posterior à data de retirada");
      return;
    }

    if (
      !confirm(
        "Tem certeza que deseja alterar o período desta reserva? O valor será recalculado."
      )
    ) {
      return;
    }

    try {
      setSalvando(true);
      setError(null);
      setSuccess(null);

      const reservaAtualizada = await reservaService.alterarPeriodo(
        reserva.codigo,
        {
          dataRetirada: dataRetiradaDate.toISOString(),
          dataDevolucao: dataDevolucaoDate.toISOString(),
        }
      );

      setReserva(reservaAtualizada);
      setSuccess(
        "Período da reserva alterado com sucesso! O valor foi recalculado."
      );

      setTimeout(() => {
        router.push("/alugar/minhas-reservas");
      }, 2000);
    } catch (err: any) {
      setError(err.message || "Erro ao alterar período da reserva");
    } finally {
      setSalvando(false);
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
            Alterar Período da Reserva
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
                <p className="text-xs text-gray-500">Valor Estimado Atual</p>
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
        </div>

        {/* Formulário de Alteração */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            Novo Período
          </h2>

          {error && (
            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
              {success}
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label
                htmlFor="dataRetirada"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                Nova Data de Retirada *
              </label>
              <input
                type="datetime-local"
                id="dataRetirada"
                value={dataRetirada}
                onChange={(e) => setDataRetirada(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
              {reserva && (
                <p className="mt-1 text-xs text-gray-500">
                  Atual: {formatarData(reserva.dataRetirada)}
                </p>
              )}
            </div>

            <div>
              <label
                htmlFor="dataDevolucao"
                className="block text-sm font-medium text-gray-700 mb-2"
              >
                Nova Data de Devolução *
              </label>
              <input
                type="datetime-local"
                id="dataDevolucao"
                value={dataDevolucao}
                onChange={(e) => setDataDevolucao(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
              />
              {reserva && (
                <p className="mt-1 text-xs text-gray-500">
                  Atual: {formatarData(reserva.dataDevolucao)}
                </p>
              )}
            </div>
          </div>

          <div className="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-4">
            <p className="text-sm text-blue-800">
              <strong>Importante:</strong> Ao alterar o período, o valor
              estimado da reserva será recalculado automaticamente com base nas
              novas datas e na diária da categoria.
            </p>
          </div>

          <div className="mt-6 flex gap-4">
            <button
              onClick={() => router.push("/alugar/minhas-reservas")}
              className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              onClick={handleAlterar}
              disabled={salvando}
              className="flex-1 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
            >
              {salvando ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  Salvando...
                </>
              ) : (
                <>
                  <Save className="w-4 h-4" />
                  Salvar Alterações
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
