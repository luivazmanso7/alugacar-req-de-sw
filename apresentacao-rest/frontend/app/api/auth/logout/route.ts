import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para logout.
 * Faz proxy para o backend Spring Boot e remove o cookie de sessão.
 */
export async function POST(request: NextRequest) {
  try {
    // Obter cookie de sessão do request
    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    // Fazer requisição para o backend Spring Boot
    const backendUrl = "http://localhost:8080/api/v1/auth/logout";
    const response = await fetch(backendUrl, {
      method: "POST",
      headers: {
        ...(cookieHeader && { Cookie: cookieHeader }),
      },
    });

    // Criar resposta Next.js
    const nextResponse = NextResponse.json(
      { message: "Logout realizado com sucesso" },
      { status: response.status }
    );

    // Remover cookie de sessão
    nextResponse.cookies.delete("JSESSIONID");

    return nextResponse;
  } catch (error: any) {
    console.error("Erro ao fazer logout:", error);
    return NextResponse.json(
      { error: "Erro ao fazer logout", message: error.message },
      { status: 500 }
    );
  }
}
