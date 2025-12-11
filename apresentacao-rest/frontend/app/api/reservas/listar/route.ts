import { NextRequest, NextResponse } from "next/server";

/**
 * API Route para listar todas as reservas (admin).
 * Faz proxy para o backend Spring Boot.
 * Respeita DDD: apenas faz proxy, sem lógica de negócio.
 */
export async function GET(request: NextRequest) {
  try {
    const backendUrl =
      process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
    const url = `${backendUrl}/api/v1/reservas`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    });

    if (!response.ok) {
      const error = await response
        .json()
        .catch(() => ({ message: "Erro ao listar reservas" }));
      return NextResponse.json(
        { error: error.message || "Erro ao listar reservas" },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error("Erro ao listar reservas:", error);
    return NextResponse.json(
      { error: "Erro ao conectar com o servidor" },
      { status: 500 }
    );
  }
}
