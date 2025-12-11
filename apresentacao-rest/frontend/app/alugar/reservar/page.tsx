"use client";

import { Suspense } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import ReservarForm from "./ReservarForm";

function ReservarContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  // Dados do veículo da URL
  const placa = searchParams.get("placa") || "";
  const categoria = searchParams.get("categoria") || "";
  const cidade = searchParams.get("cidade") || "";
  const diaria = parseFloat(searchParams.get("diaria") || "0");
  const modelo = searchParams.get("modelo") || "";
  const dataRetiradaParam = searchParams.get("dataRetirada") || "";
  const dataDevolucaoParam = searchParams.get("dataDevolucao") || "";

  if (!placa || !categoria) {
    router.push("/alugar");
    return null;
  }

  return (
    <ReservarForm
      placa={placa}
      categoria={categoria}
      cidade={cidade}
      diaria={diaria}
      modelo={modelo}
      dataRetiradaParam={dataRetiradaParam}
      dataDevolucaoParam={dataDevolucaoParam}
    />
  );
}

export default function ReservarPage() {
  return (
    <Suspense
      fallback={
        <div className="min-h-screen bg-gray-50 flex items-center justify-center">
          Carregando...
        </div>
      }
    >
      <ReservarContent />
    </Suspense>
  );
}
