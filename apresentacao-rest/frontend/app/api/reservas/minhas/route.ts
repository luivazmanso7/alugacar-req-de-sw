import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para listar reservas do cliente logado.
 * Faz proxy para o backend Spring Boot passando os cookies de sessão.
 */
export async function GET(request: NextRequest) {
  try {
    // Obter cookie de sessão do request
    const sessionCookie = request.cookies.get("JSESSIONID");

    // Log de todos os cookies recebidos para debug
    const allCookies = request.cookies.getAll();
    console.log(
      "Todos os cookies recebidos:",
      allCookies.map((c) => c.name)
    );
    console.log(
      "Cookie JSESSIONID recebido:",
      sessionCookie?.value
        ? `SIM (${sessionCookie.value.substring(0, 20)}...)`
        : "NÃO"
    );

    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      console.warn("Requisição sem cookie JSESSIONID - retornando 401");
      return NextResponse.json({ error: "Não autenticado" }, { status: 401 });
    }

    // Fazer requisição para o backend Spring Boot
    const backendUrl = "http://localhost:8080/api/v1/reservas/minhas";
    const response = await fetch(backendUrl, {
      method: "GET",
      headers: {
        Cookie: cookieHeader,
      },
      credentials: "include", // Importante para cookies
    });

    // Se o backend retornar erro de autenticação, repassar
    if (response.status === 401 || response.status === 403) {
      return NextResponse.json(
        { error: "Não autenticado" },
        { status: response.status }
      );
    }

    // Se não for sucesso, tentar parsear JSON ou retornar erro genérico
    if (!response.ok) {
      try {
        const errorData = await response.json();
        return NextResponse.json(errorData, { status: response.status });
      } catch {
        return NextResponse.json(
          { error: "Erro ao listar reservas" },
          { status: response.status }
        );
      }
    }

    const data = await response.json();

    return NextResponse.json(data, {
      status: response.status,
    });
  } catch (error: any) {
    console.error("Erro ao listar reservas:", error);
    return NextResponse.json(
      { error: "Erro ao listar reservas", message: error.message },
      { status: 500 }
    );
  }
}
