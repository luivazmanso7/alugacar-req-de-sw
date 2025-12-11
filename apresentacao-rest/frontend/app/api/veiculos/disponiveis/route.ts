import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para buscar veículos disponíveis.
 * Faz proxy para o backend Spring Boot.
 * Rota pública (não requer autenticação).
 */
export async function GET(request: NextRequest) {
  try {
    // Obter parâmetros da query string
    const searchParams = request.nextUrl.searchParams;
    const cidade = searchParams.get("cidade");
    const categoria = searchParams.get("categoria");

    // Construir URL do backend com parâmetros
    const backendUrl = new URL(
      "http://localhost:8080/api/v1/veiculos/disponiveis"
    );
    if (cidade) {
      backendUrl.searchParams.set("cidade", cidade);
    }
    if (categoria) {
      backendUrl.searchParams.set("categoria", categoria);
    }

    const response = await fetch(backendUrl.toString(), {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error("Erro do backend ao buscar veículos:", errorText);
      return NextResponse.json(
        { error: "Erro ao buscar veículos disponíveis" },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error: any) {
    console.error("Erro ao buscar veículos:", error);
    return NextResponse.json(
      { error: "Erro ao buscar veículos", message: error.message },
      { status: 500 }
    );
  }
}
