"use client";

import { useState, useEffect } from "react";
import { reservaService, ReservaResponse } from "@/services/reservaService";
import {
  Search,
  Filter,
  X,
  Calendar,
  User,
  MapPin,
  DollarSign,
  CheckCircle,
  XCircle,
  Clock,
} from "lucide-react";

/**
 * Página de busca e administração de reservas para administradores.
 * Respeita DDD: apenas apresenta dados, sem lógica de negócio.
 */
export default function BuscarReservasPage() {
  const [reservas, setReservas] = useState<ReservaResponse[]>([]);
  const [reservasFiltradas, setReservasFiltradas] = useState<ReservaResponse[]>(
    []
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filtros
  const [busca, setBusca] = useState("");
  const [filtroStatus, setFiltroStatus] = useState<string>("TODOS");
  const [filtroCategoria, setFiltroCategoria] = useState<string>("TODOS");

  useEffect(() => {
    carregarReservas();
  }, []);

  useEffect(() => {
    aplicarFiltros();
  }, [reservas, busca, filtroStatus, filtroCategoria]);

  const carregarReservas = async () => {
    try {
      setLoading(true);
      setError(null);
      const dados = await reservaService.listarTodas();
      setReservas(dados);
      setReservasFiltradas(dados);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Erro ao carregar reservas"
      );
    } finally {
      setLoading(false);
    }
  };

  const aplicarFiltros = () => {
    let filtradas = [...reservas];

    // Filtro de busca (código, cliente, documento)
    if (busca.trim()) {
      const buscaLower = busca.toLowerCase();
      filtradas = filtradas.filter(
        (r) =>
          r.codigo.toLowerCase().includes(buscaLower) ||
          r.clienteNome.toLowerCase().includes(buscaLower) ||
          r.clienteDocumento.includes(busca)
      );
    }

    // Filtro de status
    if (filtroStatus !== "TODOS") {
      filtradas = filtradas.filter((r) => r.status === filtroStatus);
    }

    // Filtro de categoria
    if (filtroCategoria !== "TODOS") {
      filtradas = filtradas.filter((r) => r.categoria === filtroCategoria);
    }

    setReservasFiltradas(filtradas);
  };

  const limparFiltros = () => {
    setBusca("");
    setFiltroStatus("TODOS");
    setFiltroCategoria("TODOS");
  };

  const formatarData = (data: string) => {
    return new Date(data).toLocaleDateString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const formatarMoeda = (valor: number) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(valor);
  };

  const getStatusBadge = (status: string) => {
    const statusMap: Record<
      string,
      { label: string; className: string; icon: React.ReactNode }
    > = {
      ATIVA: {
        label: "Ativa",
        className: "bg-green-100 text-green-800",
        icon: <CheckCircle className="w-4 h-4" />,
      },
      CANCELADA: {
        label: "Cancelada",
        className: "bg-red-100 text-red-800",
        icon: <XCircle className="w-4 h-4" />,
      },
      CONCLUIDA: {
        label: "Concluída",
        className: "bg-blue-100 text-blue-800",
        icon: <CheckCircle className="w-4 h-4" />,
      },
    };

    const statusInfo = statusMap[status] || {
      label: status,
      className: "bg-gray-100 text-gray-800",
      icon: <Clock className="w-4 h-4" />,
    };

    return (
      <span
        className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${statusInfo.className}`}
      >
        {statusInfo.icon}
        {statusInfo.label}
      </span>
    );
  };

  const categoriasUnicas = Array.from(
    new Set(reservas.map((r) => r.categoria))
  ).sort();
  const statusUnicos = Array.from(
    new Set(reservas.map((r) => r.status))
  ).sort();

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 px-8 py-6">
        <h1 className="text-3xl font-semibold text-gray-900">
          Buscar Reservas
        </h1>
        <p className="text-gray-500 mt-1">
          Gerencie e consulte todas as reservas do sistema
        </p>
      </header>

      {/* Main Content */}
      <main className="px-8 py-6">
        {/* Filtros */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
              <Filter className="w-5 h-5" />
              Filtros
            </h2>
            {(busca ||
              filtroStatus !== "TODOS" ||
              filtroCategoria !== "TODOS") && (
              <button
                onClick={limparFiltros}
                className="text-sm text-gray-600 hover:text-gray-900 flex items-center gap-1"
              >
                <X className="w-4 h-4" />
                Limpar filtros
              </button>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Busca */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Buscar
              </label>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  placeholder="Código, cliente ou documento..."
                  value={busca}
                  onChange={(e) => setBusca(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            {/* Filtro Status */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Status
              </label>
              <select
                value={filtroStatus}
                onChange={(e) => setFiltroStatus(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="TODOS">Todos</option>
                {statusUnicos.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </div>

            {/* Filtro Categoria */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Categoria
              </label>
              <select
                value={filtroCategoria}
                onChange={(e) => setFiltroCategoria(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="TODOS">Todas</option>
                {categoriasUnicas.map((categoria) => (
                  <option key={categoria} value={categoria}>
                    {categoria}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Resultados */}
        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-gray-900">
                Reservas ({reservasFiltradas.length})
              </h2>
              {loading && (
                <div className="text-sm text-gray-500">Carregando...</div>
              )}
            </div>
          </div>

          {error && (
            <div className="px-6 py-4 bg-red-50 border-b border-red-200">
              <p className="text-red-800">{error}</p>
            </div>
          )}

          {loading ? (
            <div className="px-6 py-12 text-center">
              <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
              <p className="mt-2 text-gray-500">Carregando reservas...</p>
            </div>
          ) : reservasFiltradas.length === 0 ? (
            <div className="px-6 py-12 text-center">
              <p className="text-gray-500">Nenhuma reserva encontrada</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Código
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Cliente
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Categoria
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Período
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Cidade
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Valor
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {reservasFiltradas.map((reserva) => (
                    <tr key={reserva.codigo} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">
                          {reserva.codigo}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2">
                          <User className="w-4 h-4 text-gray-400" />
                          <div>
                            <div className="text-sm font-medium text-gray-900">
                              {reserva.clienteNome}
                            </div>
                            <div className="text-sm text-gray-500">
                              {reserva.clienteDocumento}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">
                          {reserva.categoria}
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2 text-sm text-gray-900">
                          <Calendar className="w-4 h-4 text-gray-400" />
                          <div>
                            <div>{formatarData(reserva.dataRetirada)}</div>
                            <div className="text-gray-500">
                              até {formatarData(reserva.dataDevolucao)}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center gap-1 text-sm text-gray-900">
                          <MapPin className="w-4 h-4 text-gray-400" />
                          {reserva.cidadeRetirada}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center gap-1 text-sm font-medium text-gray-900">
                          <DollarSign className="w-4 h-4 text-gray-400" />
                          {formatarMoeda(reserva.valorEstimado)}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {getStatusBadge(reserva.status)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
