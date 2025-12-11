import api from "@/lib/api";

export interface LoginRequest {
  login: string;
  senha: string;
}

export interface LoginResponse {
  clienteNome: string;
  clienteDocumento: string;
  clienteEmail: string;
}

export const authService = {
  // Fazer login (usa API route do Next.js que faz proxy para o backend)
  async login(login: string, senha: string): Promise<LoginResponse> {
    try {
      // Usar API route do Next.js para evitar problemas de CORS no navegador
      // O Next.js faz proxy para o backend e repassa o cookie corretamente
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Importante para cookies
        body: JSON.stringify({ login, senha }),
      });

      if (!response.ok) {
        let errorMessage = "Erro ao fazer login";
        try {
          const error = await response.json();
          errorMessage = error.message || error.error || errorMessage;
        } catch {
          // Se não conseguir parsear JSON, usar status
          errorMessage = `Erro ${response.status}: ${response.statusText}`;
        }
        throw new Error(errorMessage);
      }

      const data: LoginResponse = await response.json();

      // Armazenar nome do cliente no localStorage para exibição
      if (data.clienteNome) {
        localStorage.setItem("clienteNome", data.clienteNome);
      }

      return data;
    } catch (error: any) {
      // Se for erro de rede (Failed to fetch), dar mensagem mais clara
      if (error.message === "Failed to fetch" || error.name === "TypeError") {
        throw new Error(
          "Não foi possível conectar ao servidor. Verifique se o Spring Boot está rodando na porta 8080."
        );
      }
      throw error;
    }
  },

  // Fazer logout (usa API route do Next.js)
  async logout(): Promise<void> {
    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch (error) {
      // Ignorar erros no logout
    }
    localStorage.removeItem("clienteNome");
  },

  // Obter nome do cliente (armazenado no localStorage após login)
  getClienteNome(): string | null {
    return localStorage.getItem("clienteNome");
  },
};
