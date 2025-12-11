import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para listar categorias.
 * Faz proxy para o backend Spring Boot.
 * Rota pública (não requer autenticação).
 */
export async function GET(request: NextRequest) {
  try {
    // Fazer requisição para o backend Spring Boot
    const backendUrl = "http://localhost:8080/api/v1/categorias";
    const response = await fetch(backendUrl, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error("Erro do backend ao listar categorias:", errorText);
      return NextResponse.json(
        { error: "Erro ao listar categorias" },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error: any) {
    console.error("Erro ao listar categorias:", error);
    return NextResponse.json(
      { error: "Erro ao listar categorias", message: error.message },
      { status: 500 }
    );
  }
}
