"use client";

import { Car, MapPin, DollarSign } from "lucide-react";
import { VeiculoResumo } from "@/services/veiculoService";

interface VeiculoCardProps {
  veiculo: VeiculoResumo;
  onReservar: (veiculo: VeiculoResumo) => void;
}

export default function VeiculoCard({ veiculo, onReservar }: VeiculoCardProps) {
  const formatarPreco = (valor: number) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(valor);
  };

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300">
      {/* Imagem do veículo - placeholder */}
      <div className="h-48 bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center">
        <Car className="w-24 h-24 text-gray-400" />
      </div>

      {/* Conteúdo do card */}
      <div className="p-6">
        {/* Categoria */}
        <div className="mb-2">
          <span className="inline-block px-3 py-1 text-xs font-semibold text-blue-600 bg-blue-100 rounded-full">
            {veiculo.categoria}
          </span>
        </div>

        {/* Modelo */}
        <h3 className="text-xl font-bold text-gray-900 mb-2">
          {veiculo.modelo}
        </h3>

        {/* Placa */}
        <p className="text-sm text-gray-500 mb-4">Placa: {veiculo.placa}</p>

        {/* Informações */}
        <div className="space-y-2 mb-4">
          <div className="flex items-center text-sm text-gray-600">
            <MapPin className="w-4 h-4 mr-2" />
            <span>{veiculo.cidade}</span>
          </div>
          <div className="flex items-center text-sm text-gray-600">
            <DollarSign className="w-4 h-4 mr-2" />
            <span className="font-semibold">
              {formatarPreco(veiculo.diaria)}/dia
            </span>
          </div>
        </div>

        {/* Botão de reservar */}
        <button
          onClick={() => onReservar(veiculo)}
          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200"
        >
          Reservar
        </button>
      </div>
    </div>
  );
}
