"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { adminAuthService } from "@/services/adminAuthService";
import { StatsCard } from "@/components/dashboard/StatsCard";
import { QuickActionCard } from "@/components/dashboard/QuickActionCard";
import { ReservasTable } from "@/components/dashboard/ReservasTable";
import { DevolucoesTable } from "@/components/dashboard/DevolucoesTable";
import { Calendar, Key, RotateCcw, Car, Search } from "lucide-react";

export default function DashboardPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [adminNome, setAdminNome] = useState<string | null>(null);

  useEffect(() => {
    const nome = adminAuthService.getAdminNome();
    if (!nome) {
      router.push("/admin/login");
      return;
    }
    setAdminNome(nome);
    setLoading(false);
  }, [router]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando...</p>
        </div>
      </div>
    );
  }

  if (!adminNome) {
    return null; // Será redirecionado pelo useEffect
  }

  const stats = {
    reservasDoDia: 2,
    reservasDoDiaChange: "+12% vs ontem",
    retiradasPendentes: 2,
    devolucoesPendentes: 2,
    veiculosAlugados: 2,
  };

  const proximasRetiradas = [
    {
      codigo: "RES-2024-001",
      cliente: {
        nome: "João Silva Santos",
        telefone: "123.456.789-00",
      },
      periodo: "10 dez - 15 dez",
      categoria: "Econômico",
    },
    {
      codigo: "RES-2024-005",
      cliente: {
        nome: "Maria Oliveira Costa",
        telefone: "987.654.321-00",
      },
      periodo: "20 dez - 25 dez",
      categoria: "Econômico",
    },
  ];

  const devolucoesPrevistas = [
    {
      codigo: "RES-2024-002",
      cliente: {
        nome: "Maria Oliveira Costa",
        telefone: "987.654.321-00",
      },
      periodo: "08 dez - 12 dez",
      categoria: "Executivo",
    },
    {
      codigo: "RES-2024-003",
      cliente: {
        nome: "Pedro Henrique Lima",
        telefone: "",
      },
      periodo: "05 dez - 10 dez",
      categoria: "SUV",
    },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 px-8 py-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-semibold text-gray-900">
              Olá, {adminNome}!
            </h1>
            <p className="text-gray-500 mt-1">Aqui está o resumo do seu dia</p>
          </div>
          <button
            onClick={async () => {
              await adminAuthService.logout();
              router.push("/admin/login");
            }}
            className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
          >
            Sair
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className="px-8 py-6 space-y-8">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <StatsCard
            title="Reservas do Dia"
            value={stats.reservasDoDia}
            change={stats.reservasDoDiaChange}
            icon={<Calendar className="w-6 h-6" />}
            bgColor="bg-blue-50"
            iconColor="text-blue-600"
          />
          <StatsCard
            title="Retiradas Pendentes"
            value={stats.retiradasPendentes}
            icon={<Key className="w-6 h-6" />}
            bgColor="bg-amber-50"
            iconColor="text-amber-600"
          />
          <StatsCard
            title="Devoluções Pendentes"
            value={stats.devolucoesPendentes}
            icon={<RotateCcw className="w-6 h-6" />}
            bgColor="bg-emerald-50"
            iconColor="text-emerald-600"
          />
          <StatsCard
            title="Veículos Alugados"
            value={stats.veiculosAlugados}
            icon={<Car className="w-6 h-6" />}
            bgColor="bg-gray-50"
            iconColor="text-gray-600"
          />
        </div>

        {/* Quick Actions */}
        <div>
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            Ações Rápidas
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <QuickActionCard
              title="Buscar Reserva"
              description="Pesquise por código ou CPF do cliente"
              icon={<Search className="w-6 h-6" />}
              bgColor="bg-cyan-600"
              href="/reservas/buscar"
            />
            <QuickActionCard
              title="Processar Retirada"
              description="Inicie uma nova retirada de veículo"
              icon={<Key className="w-6 h-6" />}
              bgColor="bg-gray-100"
              textColor="text-gray-900"
              href="/admin/retirada"
            />
            <QuickActionCard
              title="Processar Devolução"
              description="Finalize uma locação ativa"
              icon={<RotateCcw className="w-6 h-6" />}
              bgColor="bg-gray-100"
              textColor="text-gray-900"
              href="/devolucao"
            />
          </div>
        </div>

        {/* Tables Section */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Próximas Retiradas */}
          <ReservasTable
            title="Próximas Retiradas"
            data={proximasRetiradas}
            linkText="Ver todas"
            linkHref="/retiradas"
          />

          {/* Devoluções Previstas */}
          <DevolucoesTable
            title="Devoluções Previstas"
            data={devolucoesPrevistas}
            linkText="Ver todas"
            linkHref="/devolucoes"
          />
        </div>
      </main>
    </div>
  );
}
