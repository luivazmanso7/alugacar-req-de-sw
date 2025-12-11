"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { VeiculoResumo } from "@/services/veiculoService";
import { CategoriaResumo } from "@/services/categoriaService";
import { veiculoService } from "@/services/veiculoService";
import { categoriaService } from "@/services/categoriaService";
import BuscaVeiculos from "@/components/veiculo/BuscaVeiculos";
import VeiculoCard from "@/components/veiculo/VeiculoCard";

export default function AlugarPage() {
  const router = useRouter();
  const [veiculos, setVeiculos] = useState<VeiculoResumo[]>([]);
  const [categorias, setCategorias] = useState<CategoriaResumo[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [buscaRealizada, setBuscaRealizada] = useState(false);
  const [filtros, setFiltros] = useState<{
    cidade?: string;
    categoria?: string;
    dataRetirada?: string;
    dataDevolucao?: string;
  }>({});

  useEffect(() => {
    // Carregar categorias ao montar o componente
    categoriaService
      .listar()
      .then(setCategorias)
      .catch((err) => {
        console.error("Erro ao carregar categorias:", err);
        setError("Erro ao carregar categorias");
      });
  }, []);

  const handleBuscar = async (
    cidade: string,
    categoria?: string,
    dataRetirada?: string,
    dataDevolucao?: string
  ) => {
    setLoading(true);
    setError(null);
    setFiltros({ cidade, categoria, dataRetirada, dataDevolucao });

    try {
      const resultado = await veiculoService.buscarDisponiveis(
        cidade,
        categoria
      );
      setVeiculos(resultado);
      setBuscaRealizada(true);
    } catch (err: any) {
      console.error("Erro ao buscar veículos:", err);
      setError(
        err.response?.data?.message ||
          "Erro ao buscar veículos. Tente novamente."
      );
      setVeiculos([]);
    } finally {
      setLoading(false);
    }
  };

  const handleReservar = (veiculo: VeiculoResumo) => {
    // Passar os dados do veículo e filtros para a página de reserva
    const params = new URLSearchParams({
      placa: veiculo.placa,
      categoria: veiculo.categoria,
      cidade: veiculo.cidade,
      diaria: veiculo.diaria.toString(),
      modelo: veiculo.modelo,
      ...(filtros.dataRetirada && { dataRetirada: filtros.dataRetirada }),
      ...(filtros.dataDevolucao && { dataDevolucao: filtros.dataDevolucao }),
    });
    router.push(`/alugar/reservar?${params.toString()}`);
  };

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      {/* Conteúdo Principal */}
      <div className="max-w-7xl mx-auto">
        {/* Formulário de Busca */}
        <BuscaVeiculos
          onBuscar={handleBuscar}
          categorias={categorias}
          loading={loading}
        />

        {/* Mensagem de Erro */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
            {error}
          </div>
        )}

        {/* Resultados */}
        {buscaRealizada && !loading && (
          <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">
              Veículos Disponíveis
              {veiculos.length > 0 && (
                <span className="text-lg font-normal text-gray-600 ml-2">
                  ({veiculos.length} encontrado
                  {veiculos.length !== 1 ? "s" : ""})
                </span>
              )}
            </h2>

            {veiculos.length === 0 ? (
              <div className="bg-white rounded-lg shadow-md p-8 text-center">
                <p className="text-gray-600 text-lg">
                  Nenhum veículo disponível encontrado para os critérios
                  selecionados.
                </p>
                <p className="text-gray-500 mt-2">
                  Tente alterar os filtros de busca.
                </p>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {veiculos.map((veiculo) => (
                  <VeiculoCard
                    key={veiculo.placa}
                    veiculo={veiculo}
                    onReservar={handleReservar}
                  />
                ))}
              </div>
            )}
          </div>
        )}

        {/* Seção de Destaque (quando não há busca) */}
        {!buscaRealizada && !loading && (
          <div>
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-2xl font-bold text-gray-900">
                  Veículos em Destaque
                </h2>
                <p className="text-gray-600 mt-1">
                  Escolha entre nossa frota variada
                </p>
              </div>
              <button className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 flex items-center">
                <svg
                  className="w-5 h-5 mr-2"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"
                  />
                </svg>
                Filtros
              </button>
            </div>
            <div className="bg-white rounded-lg shadow-md p-8 text-center">
              <p className="text-gray-600 text-lg">
                Faça uma busca para ver os veículos disponíveis.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
