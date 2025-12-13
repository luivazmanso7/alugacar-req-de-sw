import api from "@/lib/api";

export interface LoginAdminRequest {
  login: string;
  senha: string;
}

export interface LoginAdminResponse {
  nome: string;
  email: string;
}

export const adminAuthService = {
  async login(login: string, senha: string): Promise<LoginAdminResponse> {
    try {
      const response = await fetch("/api/admin/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ login, senha }),
      });

      if (!response.ok) {
        let errorMessage = "Erro ao fazer login";
        try {
          const error = await response.json();
          errorMessage = error.message || error.error || errorMessage;
        } catch {
          errorMessage = `Erro ${response.status}: ${response.statusText}`;
        }
        throw new Error(errorMessage);
      }

      const data: LoginAdminResponse = await response.json();

      if (data.nome) {
        localStorage.setItem("adminNome", data.nome);
      }

      return data;
    } catch (error: any) {
      if (error.message === "Failed to fetch" || error.name === "TypeError") {
        throw new Error(
          "Não foi possível conectar ao servidor. Verifique se o Spring Boot está rodando na porta 8080."
        );
      }
      throw error;
    }
  },

  async logout(): Promise<void> {
    try {
      await fetch("/api/admin/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch (error) {}
    localStorage.removeItem("adminNome");
  },

  getAdminNome(): string | null {
    return localStorage.getItem("adminNome");
  },
};
