"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { adminAuthService } from "@/services/adminAuthService";
import { reservaService } from "@/services/reservaService";

interface ReservaDetalhes {
  codigo: string;
  categoria: string;
  cidadeRetirada: string;
  dataRetirada: string;
  dataDevolucao: string;
  valorEstimado: number;
  status: string;
  clienteNome: string;
  clienteDocumento: string;
  placaVeiculo: string;
}

export default function AdminRetiradaPage() {
  const router = useRouter();
  const [codigoReserva, setCodigoReserva] = useState("");
  const [reserva, setReserva] = useState<ReservaDetalhes | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Campos do formulário de retirada
  const [placaVeiculo, setPlacaVeiculo] = useState("");
  const [cnhCondutor, setCnhCondutor] = useState("");
  const [dataHoraRetirada, setDataHoraRetirada] = useState("");
  const [quilometragemSaida, setQuilometragemSaida] = useState("");
  const [nivelTanqueSaida, setNivelTanqueSaida] = useState("CHEIO");
  const [observacoes, setObservacoes] = useState("");

  // Verificar autenticação
  useEffect(() => {
    const adminNome = adminAuthService.getAdminNome();
    if (!adminNome) {
      router.push("/admin/login");
    }
  }, [router]);

  const buscarReserva = async () => {
    if (!codigoReserva.trim()) {
      setError("Por favor, informe o código da reserva");
      return;
    }

    setLoading(true);
    setError(null);
    setReserva(null);
    setSuccess(null);

    try {
      const reservaEncontrada = await reservaService.buscarPorCodigo(
        codigoReserva.trim()
      );
      setReserva(reservaEncontrada);

      // Preencher campos automáticos
      if (reservaEncontrada.status !== "ATIVA") {
        setError(
          `Esta reserva não está ATIVA. Status atual: ${reservaEncontrada.status}`
        );
        setReserva(null);
        return;
      }

      // Preencher placa do veículo se disponível
      if (reservaEncontrada.placaVeiculo) {
        setPlacaVeiculo(reservaEncontrada.placaVeiculo);
      }

      // Preencher data/hora atual como padrão
      const agora = new Date();
      const dataHoraFormatada = agora.toISOString().slice(0, 16);
      setDataHoraRetirada(dataHoraFormatada);
    } catch (err: any) {
      setError(
        err.message || "Erro ao buscar reserva. Verifique o código informado."
      );
      setReserva(null);
    } finally {
      setLoading(false);
    }
  };

  const confirmarRetirada = async () => {
    if (!reserva) {
      setError("Busque uma reserva primeiro");
      return;
    }

    // Validações
    if (!placaVeiculo.trim()) {
      setError("A placa do veículo é obrigatória");
      return;
    }

    if (!cnhCondutor.trim()) {
      setError("A CNH do condutor é obrigatória");
      return;
    }

    if (!dataHoraRetirada) {
      setError("A data e hora da retirada são obrigatórias");
      return;
    }

    if (!quilometragemSaida || parseInt(quilometragemSaida) < 0) {
      setError(
        "A quilometragem de saída é obrigatória e deve ser maior ou igual a zero"
      );
      return;
    }

    if (!nivelTanqueSaida) {
      setError("O nível do tanque é obrigatório");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      // Converter data/hora para o formato ISO esperado pelo backend
      const dataHoraISO = new Date(dataHoraRetirada).toISOString();

      const response = await fetch(
        `/api/reservas/${reserva.codigo}/confirmar-retirada`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            placaVeiculo: placaVeiculo.trim(),
            cnhCondutor: cnhCondutor.trim(),
            dataHoraRetirada: dataHoraISO,
            quilometragemSaida: parseInt(quilometragemSaida),
            nivelTanqueSaida: nivelTanqueSaida,
            observacoes: observacoes.trim() || "",
          }),
        }
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || errorData.error || "Erro ao confirmar retirada"
        );
      }

      setSuccess(
        "Retirada confirmada com sucesso! A reserva foi atualizada para EM_ANDAMENTO."
      );

      // Limpar formulário após 3 segundos
      setTimeout(() => {
        setReserva(null);
        setCodigoReserva("");
        setPlacaVeiculo("");
        setCnhCondutor("");
        setDataHoraRetirada("");
        setQuilometragemSaida("");
        setNivelTanqueSaida("CHEIO");
        setObservacoes("");
        setSuccess(null);
      }, 3000);
    } catch (err: any) {
      setError(err.message || "Erro ao confirmar retirada");
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
                Confirmar Retirada
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
            Confirmar Retirada de Veículo
          </h1>
          <p className="text-gray-600 mb-6">
            Busque a reserva pelo código e preencha os dados da vistoria de
            saída.
          </p>

          {/* Busca de Reserva */}
          <div className="flex gap-4 mb-6">
            <input
              type="text"
              value={codigoReserva}
              onChange={(e) => setCodigoReserva(e.target.value.toUpperCase())}
              placeholder="Digite o código da reserva (ex: RES-1C42593D)"
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              onKeyPress={(e) => e.key === "Enter" && buscarReserva()}
            />
            <button
              onClick={buscarReserva}
              disabled={loading || !codigoReserva.trim()}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
            >
              {loading ? "Buscando..." : "Buscar"}
            </button>
          </div>

          {/* Mensagens de Erro/Sucesso */}
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

          {/* Detalhes da Reserva */}
          {reserva && (
            <div className="mb-6 bg-blue-50 border border-blue-200 rounded-lg p-4">
              <h2 className="text-lg font-semibold text-gray-900 mb-3">
                Detalhes da Reserva
              </h2>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="font-medium text-gray-700">Código:</span>
                  <span className="ml-2 text-gray-900">{reserva.codigo}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Status:</span>
                  <span className="ml-2 text-gray-900">{reserva.status}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Cliente:</span>
                  <span className="ml-2 text-gray-900">
                    {reserva.clienteNome}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Documento:</span>
                  <span className="ml-2 text-gray-900">
                    {reserva.clienteDocumento}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Categoria:</span>
                  <span className="ml-2 text-gray-900">
                    {reserva.categoria}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Cidade:</span>
                  <span className="ml-2 text-gray-900">
                    {reserva.cidadeRetirada}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">
                    Placa do Veículo:
                  </span>
                  <span className="ml-2 text-gray-900 font-semibold">
                    {reserva.placaVeiculo}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">
                    Data Retirada:
                  </span>
                  <span className="ml-2 text-gray-900">
                    {new Date(reserva.dataRetirada).toLocaleDateString("pt-BR")}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">
                    Data Devolução:
                  </span>
                  <span className="ml-2 text-gray-900">
                    {new Date(reserva.dataDevolucao).toLocaleDateString(
                      "pt-BR"
                    )}
                  </span>
                </div>
                <div className="col-span-2">
                  <span className="font-medium text-gray-700">
                    Valor Estimado:
                  </span>
                  <span className="ml-2 text-gray-900">
                    R$ {reserva.valorEstimado.toFixed(2)}
                  </span>
                </div>
              </div>
            </div>
          )}

          {/* Formulário de Dados da Retirada */}
          {reserva && (
            <div className="bg-gray-50 rounded-lg p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">
                Dados da Retirada
              </h2>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label
                    htmlFor="placaVeiculo"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Placa do Veículo *
                  </label>
                  <input
                    type="text"
                    id="placaVeiculo"
                    value={placaVeiculo}
                    onChange={(e) =>
                      setPlacaVeiculo(e.target.value.toUpperCase())
                    }
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="ABC1234"
                    required
                  />
                </div>

                <div>
                  <label
                    htmlFor="cnhCondutor"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    CNH do Condutor *
                  </label>
                  <input
                    type="text"
                    id="cnhCondutor"
                    value={cnhCondutor}
                    onChange={(e) => setCnhCondutor(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="12345678901"
                    required
                  />
                </div>

                <div>
                  <label
                    htmlFor="dataHoraRetirada"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Data e Hora da Retirada *
                  </label>
                  <input
                    type="datetime-local"
                    id="dataHoraRetirada"
                    value={dataHoraRetirada}
                    onChange={(e) => setDataHoraRetirada(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                </div>

                <div>
                  <label
                    htmlFor="quilometragemSaida"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Quilometragem de Saída *
                  </label>
                  <input
                    type="number"
                    id="quilometragemSaida"
                    value={quilometragemSaida}
                    onChange={(e) => setQuilometragemSaida(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="0"
                    min="0"
                    required
                  />
                </div>

                <div>
                  <label
                    htmlFor="nivelTanqueSaida"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Nível do Tanque *
                  </label>
                  <select
                    id="nivelTanqueSaida"
                    value={nivelTanqueSaida}
                    onChange={(e) => setNivelTanqueSaida(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  >
                    <option value="CHEIO">Cheio</option>
                    <option value="MEIO">Meio</option>
                    <option value="UM_QUARTO">Um Quarto</option>
                    <option value="RESERVA">Reserva</option>
                    <option value="VAZIO">Vazio</option>
                  </select>
                </div>

                <div className="md:col-span-2">
                  <label
                    htmlFor="observacoes"
                    className="block text-sm font-medium text-gray-700 mb-2"
                  >
                    Observações
                  </label>
                  <textarea
                    id="observacoes"
                    value={observacoes}
                    onChange={(e) => setObservacoes(e.target.value)}
                    rows={3}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Anotações sobre a vistoria de saída..."
                  />
                </div>
              </div>

              <div className="mt-6 flex justify-end gap-4">
                <button
                  onClick={() => {
                    setReserva(null);
                    setCodigoReserva("");
                    setPlacaVeiculo("");
                    setCnhCondutor("");
                    setDataHoraRetirada("");
                    setQuilometragemSaida("");
                    setNivelTanqueSaida("CHEIO");
                    setObservacoes("");
                    setError(null);
                    setSuccess(null);
                  }}
                  className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Limpar
                </button>
                <button
                  onClick={confirmarRetirada}
                  disabled={loading}
                  className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
                >
                  {loading ? "Processando..." : "Confirmar Retirada"}
                </button>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
