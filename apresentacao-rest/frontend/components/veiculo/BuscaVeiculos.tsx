"use client";

import { useState, FormEvent } from "react";
import { Search, Calendar, MapPin } from "lucide-react";
import { VeiculoResumo } from "@/services/veiculoService";
import { CategoriaResumo } from "@/services/categoriaService";

interface BuscaVeiculosProps {
  onBuscar: (
    cidade: string,
    categoria?: string,
    dataRetirada?: string,
    dataDevolucao?: string
  ) => void;
  categorias: CategoriaResumo[];
  loading?: boolean;
}

export default function BuscaVeiculos({
  onBuscar,
  categorias,
  loading = false,
}: BuscaVeiculosProps) {
  const [cidade, setCidade] = useState("");
  const [categoria, setCategoria] = useState("");
  const [dataRetirada, setDataRetirada] = useState("");
  const [dataDevolucao, setDataDevolucao] = useState("");

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (cidade.trim()) {
      onBuscar(
        cidade.trim(),
        categoria || undefined,
        dataRetirada || undefined,
        dataDevolucao || undefined
      );
    }
  };

  // Data mínima é hoje
  const hoje = new Date().toISOString().split("T")[0];

  return (
    <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
      <h2 className="text-2xl font-bold text-gray-900 mb-6">
        Alugue o Carro Perfeito
      </h2>
      <p className="text-gray-600 mb-6">
        Reserve online de forma rápida e fácil
      </p>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Cidade */}
          <div>
            <label
              htmlFor="cidade"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              <MapPin className="w-4 h-4 inline mr-1" />
              Onde você está?
            </label>
            <input
              type="text"
              id="cidade"
              value={cidade}
              onChange={(e) => setCidade(e.target.value)}
              placeholder="Digite a cidade"
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          {/* Data de Retirada */}
          <div>
            <label
              htmlFor="dataRetirada"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              <Calendar className="w-4 h-4 inline mr-1" />
              Data de Retirada
            </label>
            <input
              type="date"
              id="dataRetirada"
              value={dataRetirada}
              onChange={(e) => setDataRetirada(e.target.value)}
              min={hoje}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          {/* Data de Devolução */}
          <div>
            <label
              htmlFor="dataDevolucao"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              <Calendar className="w-4 h-4 inline mr-1" />
              Data de Devolução
            </label>
            <input
              type="date"
              id="dataDevolucao"
              value={dataDevolucao}
              onChange={(e) => setDataDevolucao(e.target.value)}
              min={dataRetirada || hoje}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          {/* Categoria */}
          <div>
            <label
              htmlFor="categoria"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              Categoria (Opcional)
            </label>
            <select
              id="categoria"
              value={categoria}
              onChange={(e) => setCategoria(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">Todas</option>
              {categorias.map((cat) => (
                <option key={cat.codigo} value={cat.codigo}>
                  {cat.nome}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Botões */}
        <div className="flex gap-4 pt-4">
          <button
            type="submit"
            disabled={loading || !cidade.trim()}
            className="flex-1 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-semibold py-3 px-6 rounded-lg transition-colors duration-200 flex items-center justify-center"
          >
            <Search className="w-5 h-5 mr-2" />
            {loading ? "Buscando..." : "Buscar Veículos"}
          </button>
        </div>
      </form>
    </div>
  );
}
