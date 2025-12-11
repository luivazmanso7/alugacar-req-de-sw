import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para buscar veículo por placa.
 * Faz proxy para o backend Spring Boot.
 * Rota pública (não requer autenticação).
 */
export async function GET(
  request: NextRequest,
  context: { params: Promise<{ placa: string }> }
) {
  try {
    const params = await context.params;
    const placa = params.placa;

    // Fazer requisição para o backend Spring Boot
    const backendUrl = `http://localhost:8080/api/v1/veiculos/${placa}`;
    const response = await fetch(backendUrl, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error("Erro do backend ao buscar veículo:", errorText);
      return NextResponse.json(
        { error: "Erro ao buscar veículo" },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error: any) {
    console.error("Erro ao buscar veículo:", error);
    return NextResponse.json(
      { error: "Erro ao buscar veículo", message: error.message },
      { status: 500 }
    );
  }
}
