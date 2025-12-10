import api from "@/lib/api";
import { DashboardStats } from "@/types";

export const dashboardService = {
  // Buscar estatísticas do dashboard
  async getStats(): Promise<DashboardStats> {
    const response = await api.get("/dashboard/stats");
    return response.data;
  },

  // Buscar resumo do dia
  async getResumoDia(): Promise<any> {
    const response = await api.get("/dashboard/resumo-dia");
    return response.data;
  },
};
