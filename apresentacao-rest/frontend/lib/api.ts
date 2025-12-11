import axios from "axios";

const api = axios.create({
  baseURL: "/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Importante para enviar/receber cookies de sessão
});

// Interceptor para tratar erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirecionar para login se não autenticado
      localStorage.removeItem("clienteNome");
      // Não redirecionar automaticamente, deixar o componente decidir
    }
    return Promise.reject(error);
  }
);

export default api;
