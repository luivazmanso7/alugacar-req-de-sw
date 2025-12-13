import { NextRequest, NextResponse } from "next/server";

export async function GET(request: NextRequest) {
  try {
    const backendUrl =
      process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
    const url = `${backendUrl}/api/v1/reservas`;

    const sessionCookie = request.cookies.get("JSESSIONID");
    const cookieHeader = sessionCookie
      ? `JSESSIONID=${sessionCookie.value}`
      : "";

    if (!cookieHeader) {
      return NextResponse.json(
        { error: "Não autenticado. Faça login como administrador." },
        { status: 401 }
      );
    }

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Cookie: cookieHeader,
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
    return NextResponse.json(
      { error: "Erro ao conectar com o servidor" },
      { status: 500 }
    );
  }
}
