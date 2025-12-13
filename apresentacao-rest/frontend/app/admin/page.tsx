"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { adminAuthService } from "@/services/adminAuthService";

export default function AdminPage() {
  const router = useRouter();

  useEffect(() => {
    const adminNome = adminAuthService.getAdminNome();
    if (!adminNome) {
      router.push("/admin/login");
    }
  }, [router]);

  const handleLogout = async () => {
    await adminAuthService.logout();
    router.push("/admin/login");
  };

  const adminNome = adminAuthService.getAdminNome();

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <h1 className="text-2xl font-bold text-blue-600">
              AlugaCar - Admin
            </h1>
            <div className="flex items-center gap-4">
              <span className="text-gray-700">Olá, {adminNome}</span>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                Sair
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-white rounded-lg shadow-md p-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">
            Painel Administrativo
          </h2>
          <p className="text-gray-600">
            Login realizado com sucesso! Bem-vindo ao painel administrativo.
          </p>
        </div>
      </main>
    </div>
  );
}
